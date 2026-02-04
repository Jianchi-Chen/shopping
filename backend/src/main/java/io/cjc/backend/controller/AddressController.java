package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.dto.AddressDTO;
import io.cjc.backend.security.UserPrincipal;
import io.cjc.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addresses")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressDTO>> getUserAddresses(@AuthenticationPrincipal UserPrincipal principal) {
        List<AddressDTO> addresses = addressService.getUserAddresses(principal.getUserId());
        return ApiResponse.success(addresses);
    }

    @PostMapping
    public ApiResponse<AddressDTO> createAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AddressDTO dto) {
        AddressDTO result = addressService.createAddress(principal.getUserId(), dto);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressDTO> updateAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @RequestBody AddressDTO dto) {
        AddressDTO result = addressService.updateAddress(principal.getUserId(), id, dto);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        addressService.deleteAddress(principal.getUserId(), id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/default")
    public ApiResponse<AddressDTO> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        AddressDTO result = addressService.setDefaultAddress(principal.getUserId(), id);
        return ApiResponse.success(result);
    }
}
