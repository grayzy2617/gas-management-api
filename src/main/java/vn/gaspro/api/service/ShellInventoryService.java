package vn.gaspro.api.service;

import vn.gaspro.api.dto.response.ShellInventoryResponse;

import java.util.List;

public interface ShellInventoryService {
    List<ShellInventoryResponse> getAllShellInventories();
}
