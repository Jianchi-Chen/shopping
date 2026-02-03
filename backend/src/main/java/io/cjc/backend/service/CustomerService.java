package io.cjc.backend.service;

import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.CustomerDTO;
import io.cjc.backend.entity.Customer;
import io.cjc.backend.enums.CustomerStatus;
import io.cjc.backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public PageResponse<CustomerDTO> getCustomers(
            Integer page, Integer pageSize,
            String keyword, CustomerStatus status) {
        
        Pageable pageable = PageRequest.of(
                page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        
        Page<Customer> customerPage = customerRepository.findByFilters(keyword, status, pageable);
        
        List<CustomerDTO> dtoList = customerPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(page, pageSize, customerPage.getTotalElements(), dtoList);
    }

    @Transactional
    public CustomerDTO updateStatus(String id, CustomerStatus status) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        customer.setStatus(status);
        Customer saved = customerRepository.save(customer);
        
        return toDTO(saved);
    }

    private CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setStatus(customer.getStatus());
        dto.setOrderCount(customer.getOrderCount());
        dto.setTotalSpent(customer.getTotalSpent());
        dto.setCreatedAt(customer.getCreatedAt().format(DATE_FORMATTER));
        return dto;
    }
}
