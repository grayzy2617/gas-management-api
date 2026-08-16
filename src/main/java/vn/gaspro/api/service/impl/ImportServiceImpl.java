package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gaspro.api.dto.request.ImportItemRequest;
import vn.gaspro.api.dto.request.ImportReceiptRequest;
import vn.gaspro.api.dto.response.ImportReceiptResponse;
import vn.gaspro.api.entity.ImportReceipt;
import vn.gaspro.api.entity.ImportReceiptItem;
import vn.gaspro.api.entity.Product;
import vn.gaspro.api.entity.Supplier;
import vn.gaspro.api.enums.PaymentStatus;
import vn.gaspro.api.enums.ProductStatus;
import vn.gaspro.api.enums.ReceiptType;
import vn.gaspro.api.mapper.ImportMapper;
import vn.gaspro.api.repository.ImportReceiptItemRepository;
import vn.gaspro.api.repository.ImportReceiptRepository;
import vn.gaspro.api.repository.ProductRepository;
import vn.gaspro.api.repository.ShellInventoryRepository;
import vn.gaspro.api.repository.SupplierRepository;
import vn.gaspro.api.service.ImportService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ImportReceiptRepository importReceiptRepository;
    private final ImportReceiptItemRepository importReceiptItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ShellInventoryRepository shellInventoryRepository;
    private final ImportMapper importMapper;

    @Override
    @Transactional
    public ImportReceiptResponse createImportReceipt(ImportReceiptRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.SUPPLIER_NOT_EXISTED));

        // 1. Tính toán Total Amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ImportReceiptItem> itemsToSave = new ArrayList<>();

        for (ImportItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.PRODUCT_NOT_EXISTED));

            BigDecimal totalPrice = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(totalPrice);

            ImportReceiptItem item = ImportReceiptItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(totalPrice)
                    .build();
            itemsToSave.add(item);

            // Cập nhật tồn kho
            product.setStockQuantity(product.getStockQuantity() + itemReq.getQuantity());
            if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
                product.setStatus(ProductStatus.ACTIVE);
            }
            productRepository.save(product);
        }

        // 2. Tính toán Debt Amount
        BigDecimal amountPaid = request.getAmountPaid();
        if (amountPaid.compareTo(totalAmount) > 0) {
            throw new RuntimeException("AMOUNT_PAID_EXCEEDS_TOTAL");
        }

        BigDecimal debtAmount = totalAmount.subtract(amountPaid);

        PaymentStatus paymentStatus;
        if (debtAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = PaymentStatus.PAID;
        } else if (amountPaid.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = PaymentStatus.DEBT;
        } else {
            paymentStatus = PaymentStatus.PARTIAL;
        }

        // Cập nhật công nợ nhà cung cấp
        if (debtAmount.compareTo(BigDecimal.ZERO) > 0) {
            supplier.setDebtBalance(supplier.getDebtBalance().add(debtAmount));
            supplierRepository.save(supplier);
        }

        // 3. Lưu Import Receipt
        String createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
        String receiptCode = "IMP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ImportReceipt receipt = ImportReceipt.builder()
                .receiptCode(receiptCode)
                .supplier(supplier)
                .receiptDate(LocalDateTime.now())
                .type(ReceiptType.STANDARD_IMPORT)
                .totalAmount(totalAmount)
                .amountPaid(amountPaid)
                .debtAmount(debtAmount)
                .paymentStatus(paymentStatus)
                .note(request.getNote())
                .createdBy(createdBy)
                .build();

        ImportReceipt savedReceipt = importReceiptRepository.save(receipt);

        // 4. Liên kết và lưu Items
        for (ImportReceiptItem item : itemsToSave) {
            item.setReceipt(savedReceipt);
        }
        importReceiptItemRepository.saveAll(itemsToSave);

        savedReceipt.setItems(itemsToSave);
        return importMapper.toImportReceiptResponse(savedReceipt);
    }

    @Override
    @Transactional
    public ImportReceiptResponse createShellExchangeReceipt(vn.gaspro.api.dto.request.ShellExchangeRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.SUPPLIER_NOT_EXISTED));

        Product fullProduct = productRepository.findById(request.getFullProductId())
                .orElseThrow(() -> new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.PRODUCT_NOT_EXISTED));

        if (!fullProduct.getBrand().getId().equals(request.getShellBrandId())) {
            throw new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.BRAND_MISMATCH);
        }

        vn.gaspro.api.entity.ShellInventory shellInventory = shellInventoryRepository.findByBrandId(request.getShellBrandId())
                .orElseThrow(() -> new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.RESOURCE_NOT_FOUND));

        if (shellInventory.getEmptyQuantity() < request.getExportedShellQuantity()) {
            throw new vn.gaspro.api.exception.AppException(vn.gaspro.api.enums.ErrorCode.INSUFFICIENT_SHELL_STOCK);
        }

        // 1. Trừ tồn kho vỏ
        shellInventory.setEmptyQuantity(shellInventory.getEmptyQuantity() - request.getExportedShellQuantity());

        // 2. Cộng tồn kho bình đầy
        fullProduct.setStockQuantity(fullProduct.getStockQuantity() + request.getImportedCylinderQuantity());
        if (fullProduct.getStatus() == ProductStatus.OUT_OF_STOCK) {
            fullProduct.setStatus(ProductStatus.ACTIVE);
        }
        productRepository.save(fullProduct);

        // 3. Tạo Import Receipt
        String createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
        String receiptCode = "EXC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String autoNote = String.format("Đối lưu: Xuất %d vỏ rỗng %s, Nhập %d bình đầy %s. %s",
                request.getExportedShellQuantity(), fullProduct.getBrand().getName(),
                request.getImportedCylinderQuantity(), fullProduct.getName(),
                request.getNote() != null ? request.getNote() : "");

        ImportReceipt receipt = ImportReceipt.builder()
                .receiptCode(receiptCode)
                .supplier(supplier)
                .receiptDate(LocalDateTime.now())
                .type(ReceiptType.SHELL_EXCHANGE)
                .totalAmount(BigDecimal.ZERO)
                .amountPaid(BigDecimal.ZERO)
                .debtAmount(BigDecimal.ZERO)
                .paymentStatus(PaymentStatus.PAID) // Không ghi nợ tiền trong lệnh đối lưu thuần túy
                .note(autoNote)
                .createdBy(createdBy)
                .build();

        ImportReceipt savedReceipt = importReceiptRepository.save(receipt);

        // 4. Lưu Item (Chỉ ghi nhận bình đầy nhập vào)
        ImportReceiptItem item = ImportReceiptItem.builder()
                .receipt(savedReceipt)
                .product(fullProduct)
                .quantity(request.getImportedCylinderQuantity())
                .unitPrice(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .build();

        importReceiptItemRepository.save(item);
        
        // Load lại items
        savedReceipt.setItems(java.util.List.of(item));

        return importMapper.toImportReceiptResponse(savedReceipt);
    }
}
