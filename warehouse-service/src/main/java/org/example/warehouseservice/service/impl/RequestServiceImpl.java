package org.example.warehouseservice.service.impl;

import org.example.warehouseservice.dto.responseDTO.RequestResponseDto;
import org.example.warehouseservice.entity.Item;
import org.example.warehouseservice.entity.Request;
import org.example.warehouseservice.entity.RequestHistory;
import org.example.warehouseservice.mapper.RequestMapper;
import org.example.warehouseservice.repository.RequestHistoryRepository;
import org.example.warehouseservice.repository.ItemRepository;
import org.example.warehouseservice.repository.RequestRepository;
import org.example.warehouseservice.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestHistoryRepository historyRepository;

    @Override
    public Page<RequestResponseDto> getRequestsWithFilters(String type, String status, String fromDate, String toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Request> requests = requestRepository.findByFilters(type, status, fromDate, toDate, pageable);
        return requests.map(RequestMapper.INSTANCE::toRequestResponseDto);
    }

    @Override
    @Transactional
    public RequestResponseDto updateStatus(String id, String status, String reason) {

        Request request = requestRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Request already processed");
        }

        // REJECT REQUEST
        if (status.equalsIgnoreCase("REJECTED")) {

            request.setStatus("REJECTED");
            request.setRejectReason(reason);
            request.setHandledBy(1L);
            request.setUpdatedDate(LocalDateTime.now());
        }

        // ACCEPT REQUEST
        else if (status.equalsIgnoreCase("ACCEPTED")) {

            request.setStatus("ACCEPTED");
            request.setHandledBy(1L);
            request.setUpdatedDate(LocalDateTime.now());

            List<Item> items = itemRepository.findItemByName(request.getItemName());

            if (items.isEmpty()) {
                throw new RuntimeException("Item not found: " + request.getItemName());
            }

            Item item = items.get(0);

            if (request.getRequestType().equalsIgnoreCase("IMPORT")) {

                item.setQuantity(item.getQuantity() + request.getQuantity().intValue());

            } else if (request.getRequestType().equalsIgnoreCase("EXPORT")) {

                if (item.getQuantity() < request.getQuantity()) {
                    throw new RuntimeException("Not enough stock");
                }

                item.setQuantity(item.getQuantity() - request.getQuantity().intValue());
            }

            itemRepository.save(item);

            RequestHistory history = new RequestHistory();
            history.setRequest(request);
            history.setItem(item);
            history.setCompletedDate(LocalDateTime.now());

            historyRepository.save(history);
        }

        else {
            throw new RuntimeException("Status must be ACCEPTED or REJECTED");
        }

        return RequestMapper.INSTANCE.toRequestResponseDto(requestRepository.save(request));
    }
}