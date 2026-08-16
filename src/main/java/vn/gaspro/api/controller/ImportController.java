package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.request.ImportReceiptRequest;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.ImportReceiptResponse;
import vn.gaspro.api.service.ImportService;

@RestController
@RequestMapping("/api/v1/admin/imports")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> createImportReceipt(
            @Valid @RequestBody ImportReceiptRequest request) {
        return ResponseEntity.ok(ApiResponse.success(importService.createImportReceipt(request)));
    }
}
