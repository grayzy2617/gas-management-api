package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.gaspro.api.dto.response.ShellInventoryResponse;
import vn.gaspro.api.mapper.ShellInventoryMapper;
import vn.gaspro.api.repository.ShellInventoryRepository;
import vn.gaspro.api.service.ShellInventoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShellInventoryServiceImpl implements ShellInventoryService {

    private final ShellInventoryRepository shellInventoryRepository;
    private final ShellInventoryMapper shellInventoryMapper;

    @Override
    public List<ShellInventoryResponse> getAllShellInventories() {
        return shellInventoryRepository.findAll().stream()
                .map(shellInventoryMapper::toShellInventoryResponse)
                .collect(Collectors.toList());
    }
}
