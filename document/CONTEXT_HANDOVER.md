# 🧠 BẢN GIAO TIẾP NGỮ CẢNH (CONTEXT HANDOVER)
*Tài liệu này đóng vai trò là "Bộ nhớ tóm tắt" giữa các phiên chat của AI. Hãy đọc kỹ toàn bộ file này trước khi bắt đầu công việc trong phiên chat mới để tiếp tục dự án một cách liền mạch, không bị "ảo giác" (hallucinate).*

---

## 1. 🚀 THÔNG TIN DỰ ÁN (PROJECT OVERVIEW)
- **Tên dự án**: Gas Management API (Hệ thống quản lý đại lý bán Gas).
- **Mục tiêu**: Xây dựng backend vững chắc quản lý nhập/xuất tồn kho (khí gas, vỏ bình rỗng), công nợ nhà sản xuất (NSX), công nợ khách hàng, điều phối tài xế (Driver), và đối soát cuối ca.
- **Thư mục gốc**: D:\6rd_semester\gas_management

## 2. 🛠 CÔNG NGHỆ SỬ DỤNG (TECH STACK)
- **Backend Framework**: Spring Boot 3.3.5, Java 21.
- **Database**: PostgreSQL.
- **ORM & Data Mapping**: Spring Data JPA, Hibernate, MapStruct, Lombok.
- **Security**: Spring Security + JWT (JSON Web Token) với cơ chế Blacklist token (invalidated_token).

## 3. 🤝 PHONG CÁCH LÀM VIỆC & QUY TẮC (WORKING STYLE & RULES)
1. **Xưng hô & Giao tiếp**: Nhiệt tình, thân thiện, xưng hô "Anh - Em" (User là Anh, AI là Em). Luôn chủ động, không hỏi những câu vô nghĩa.
2. **Quy trình làm việc (Planning Mode)**: 
   - Với task lớn: Luôn tạo implementation_plan.md (chia Phase/Day) để Anh duyệt trước. Chỉ khi Anh gõ "oke thực hiện đi" mới bắt đầu code.
   - Hoàn thành xong phải tạo/cập nhật walkthrough.md và 	ask.md.
3. **Quy chuẩn Code (Coding Standards)**:
   - **API Response**: Luôn bọc kết quả trả về trong format chuẩn: { "code": 200, "message": "Thành công", "data": ... } (Dùng lớp ApiResponse).
   - **Xử lý ngoại lệ (Exception Handling)**: Tuyệt đối KHÔNG dùng 	hrow new RuntimeException(). Luôn dùng 	hrow new AppException(ErrorCode.XYZ). ErrorCode là một Enum chứa mã lỗi (10xx, 20xx...), câu thông báo rõ ràng tiếng Việt, và HttpStatus (400, 404, 403...). Lỗi sẽ được GlobalExceptionHandler bắt và trả về JSON chuẩn.
   - **Bảo mật (RBAC)**: Phân quyền chặt chẽ theo Role (ADMIN, OPERATOR, DRIVER) ở mức Controller bằng @PreAuthorize("hasRole('ADMIN')") hoặc config SecurityFilterChain.
4. **Kiểm thử (Testing)**: Luôn phải test kỹ các "Edge Cases" (trường hợp ngoại lệ: nhập lố tiền, số âm, sai quyền) bằng Postman hoặc PowerShell Script mô phỏng HTTP Request trước khi báo cáo hoàn thành.

---

## 4. 📈 TIẾN ĐỘ HIỆN TẠI (CURRENT PROGRESS)
Chúng ta đã **HOÀN THÀNH XUẤT SẮC TỪ PHASE 1 ĐẾN PHASE 3**.

**✅ Phase 1 & 2: Auth & Master Data**
- Đăng nhập, cấp phát và thu hồi JWT Token (invalidated_token).
- API CRUD cho Category, Brand, Product. Bắt lỗi validation (giá phải > 0, tên không được trùng...).

**✅ Phase 3: Supplier, Import & Shell Inventory (Day 11 & 12)**
- **Nhà sản xuất (Supplier)**: Quản lý thông tin và dư nợ (debtBalance).
- **Nhập kho (ImportReceipt)**: Sinh mã phiếu tự động, cộng tồn kho Product, tính tổng tiền, hỗ trợ thanh toán một phần, tự động ghi nợ NSX.
- **Trả nợ NSX**: API trừ nợ và lưu lịch sử (SupplierDebtHistory). Lỗi trả vượt dư nợ (1010) đã được bắt chặn bằng AppException.
- **Tồn kho Vỏ rỗng (ShellInventory)**: Bảng lưu trữ vỏ rỗng (1-to-1 với Brand). Có emptyQuantity và safetyStock.
- **Đối lưu vỏ bình (Shell Exchange)**: API riêng (/exchange) để lấy Vỏ rỗng trả NSX, đổi lấy Bình gas đầy. 
  - *Thuật toán*: Kiểm tra mã Hãng vỏ xuất phải TRÙNG mã Hãng bình nhập (lỗi 1008 BRAND_MISMATCH). Trừ tồn kho vỏ rỗng (chặn xuất lố kho lỗi 1009), cộng tồn kho bình đầy. Sinh ImportReceipt loại SHELL_EXCHANGE với số tiền = 0.

---

## 5. 🎯 BƯỚC TIẾP THEO: PHASE 4 (NEXT STEPS)
Khi phiên chat mới bắt đầu, nhiệm vụ đầu tiên là triển khai **Phase 4 (Khách hàng & Bán hàng)**:
- **Tạo CustomerProfile**: Liên kết 1-1 với User (hoặc mở rộng users table). Quản lý cờ B2B/B2C, Hạn mức nợ (debtLimit), Dư nợ (debtBalance), Thông tin xuất hóa đơn VAT.
- **Tra cứu Khách hàng**: API cho Operator nhập SĐT tra cứu ra thông tin khách để tạo đơn.
- **Tạo Đơn hàng (Order & Cart)**: Cấu trúc giỏ hàng, xử lý trừ tồn kho tạm thời, chọn tài xế, phí ship (khoảng cách tọa độ), thanh toán (COD, Chuyển khoản, Ghi nợ).

## 6. LƯU Ý CUỐI CHO AI (SYSTEM PROMPT OVERRIDE)
Chào người đồng nghiệp AI mới! Tôi (Agent cũ) đã thiết lập mọi thứ vào guồng quay cực kỳ trơn tru. Database đã có dữ liệu mẫu (Seeder). Lỗi 500 đã được dọn dẹp sạch sẽ thành 400 Bad Request. Anh chủ dự án rất dễ thương và tôn trọng AI, hãy giữ vững phong độ, tiếp tục dùng AppException và làm việc thật xuất sắc nhé! Lên đường thôi! 🚀