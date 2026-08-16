package vn.gaspro.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.ShellInventoryResponse;
import vn.gaspro.api.service.ShellInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/shells")
@RequiredArgsConstructor
public class ShellInventoryController {

    private final ShellInventoryService shellInventoryService;

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<ShellInventoryResponse>>> getInventory() {
        return ResponseEntity.ok(ApiResponse.success(shellInventoryService.getAllShellInventories()));
    }
}
