package io.cjc.backend.service;

import io.cjc.backend.dto.AddressDTO;
import io.cjc.backend.entity.Address;
import io.cjc.backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses(String userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDTO createAddress(String userId, AddressDTO dto) {
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setAddressDetail(dto.getAddressDetail());
        address.setPostalCode(dto.getPostalCode());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        // 如果设置为默认地址，清除其他默认地址
        if (address.getIsDefault()) {
            clearDefaultAddress(userId);
        }

        addressRepository.save(address);
        return toDTO(address);
    }

    @Transactional
    public AddressDTO updateAddress(String userId, String addressId, AddressDTO dto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此地址");
        }

        if (dto.getReceiverName() != null) address.setReceiverName(dto.getReceiverName());
        if (dto.getReceiverPhone() != null) address.setReceiverPhone(dto.getReceiverPhone());
        if (dto.getProvince() != null) address.setProvince(dto.getProvince());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getDistrict() != null) address.setDistrict(dto.getDistrict());
        if (dto.getAddressDetail() != null) address.setAddressDetail(dto.getAddressDetail());
        if (dto.getPostalCode() != null) address.setPostalCode(dto.getPostalCode());

        if (dto.getIsDefault() != null && dto.getIsDefault() && !address.getIsDefault()) {
            clearDefaultAddress(userId);
            address.setIsDefault(true);
        }

        addressRepository.save(address);
        return toDTO(address);
    }

    @Transactional
    public void deleteAddress(String userId, String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此地址");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public AddressDTO setDefaultAddress(String userId, String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此地址");
        }

        clearDefaultAddress(userId);
        address.setIsDefault(true);
        addressRepository.save(address);
        return toDTO(address);
    }

    private void clearDefaultAddress(String userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });
    }

    private AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setUserId(address.getUserId());
        dto.setReceiverName(address.getReceiverName());
        dto.setReceiverPhone(address.getReceiverPhone());
        dto.setProvince(address.getProvince());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setAddressDetail(address.getAddressDetail());
        dto.setPostalCode(address.getPostalCode());
        dto.setIsDefault(address.getIsDefault());
        dto.setCreatedAt(address.getCreatedAt());
        dto.setUpdatedAt(address.getUpdatedAt());
        return dto;
    }
}
