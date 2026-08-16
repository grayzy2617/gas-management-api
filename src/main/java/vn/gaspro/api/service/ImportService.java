package vn.gaspro.api.service;

import vn.gaspro.api.dto.request.ImportReceiptRequest;
import vn.gaspro.api.dto.response.ImportReceiptResponse;

public interface ImportService {
    ImportReceiptResponse createImportReceipt(ImportReceiptRequest request);
}
