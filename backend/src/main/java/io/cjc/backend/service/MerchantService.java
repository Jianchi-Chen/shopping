package io.cjc.backend.service;

import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.MerchantDTO;
import io.cjc.backend.entity.Merchant;
import io.cjc.backend.enums.MerchantStatus;
import io.cjc.backend.repository.MerchantRepository;
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
public class MerchantService {
    
    private final MerchantRepository merchantRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public PageResponse<MerchantDTO> getMerchants(
            Integer page, Integer pageSize,
            String keyword, MerchantStatus status) {
        
        Pageable pageable = PageRequest.of(
                page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        
        Page<Merchant> merchantPage = merchantRepository.findByFilters(keyword, status, pageable);
        
        List<MerchantDTO> dtoList = merchantPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(page, pageSize, merchantPage.getTotalElements(), dtoList);
    }

    @Transactional
    public MerchantDTO updateStatus(String id, MerchantStatus status) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        
        merchant.setStatus(status);
        Merchant saved = merchantRepository.save(merchant);
        
        return toDTO(saved);
    }

    private MerchantDTO toDTO(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setShopId(merchant.getShopId());
        dto.setShopName(merchant.getShopName());
        dto.setName(merchant.getShopName());  // name映射自shopName
        dto.setOwnerName(merchant.getOwnerName());
        dto.setContactName(merchant.getOwnerName());  // contactName映射自ownerName
        dto.setContactPhone(merchant.getContactPhone());
        dto.setStatus(merchant.getStatus());
        dto.setCreatedAt(merchant.getCreatedAt().format(DATE_FORMATTER));
        return dto;
    }
}
