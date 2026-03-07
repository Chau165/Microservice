package org.example.warehouseservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.warehouseservice.dto.requestDTO.RequestRequestDto;
import org.example.warehouseservice.dto.responseDTO.RequestResponseDto;
import org.example.warehouseservice.entity.Request;
import org.example.warehouseservice.mapper.RequestMapper;
import org.example.warehouseservice.repository.RequestRepository;
import org.example.warehouseservice.service.RequestImportService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestImportServiceImpl implements RequestImportService {
    private final RequestRepository requestRepository;

    @Override
    public RequestResponseDto createRequest(RequestRequestDto requestRequestDto) {
        Request request = RequestMapper.INSTANCE.toEntity(requestRequestDto);
        return RequestMapper.INSTANCE.toRequestResponseDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public void deleteRequestImport(Long id) {
        requestRepository.updateStatusById(id, "REJECTED");
    }
}
