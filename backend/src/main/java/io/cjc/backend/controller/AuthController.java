package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.dto.UserProfileDTO;
import io.cjc.backend.enums.UserRole;
import io.cjc.backend.security.UserPrincipal;
import io.cjc.backend.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@RequestBody RegisterRequest request) {
        String token = authService.register(
                request.getUsername(), 
                request.getPassword(), 
                request.getRole() != null ? request.getRole() : UserRole.USER
        );
        return ApiResponse.success(new TokenResponse(token));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("收到登录请求 - 用户名: {}, 密码长度: {}", request.getUsername(), 
                 request.getPassword() != null ? request.getPassword().length() : 0);
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            UserProfileDTO userInfo = authService.getCurrentUserProfile(request.getUsername());
            log.info("登录成功，返回 token 和用户信息");
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setId(userInfo.getId());
            response.setUsername(userInfo.getUsername());
            response.setName(userInfo.getName());
            response.setEmail(userInfo.getEmail());
            response.setPhone(userInfo.getPhone());
            response.setAvatar(userInfo.getAvatar());
            response.setRole(userInfo.getRole());
            response.setMerchantId(userInfo.getMerchantId());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未登录"));
        }
        UserProfileDTO userProfile = authService.getCurrentUserProfile(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userProfile));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileDTO> updateCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateProfileRequest request) {
        if (principal == null) {
            return ApiResponse.error(401, "未登录");
        }
        UserProfileDTO userProfile = authService.updateUserProfile(
                principal.getUsername(),
                request.getName(),
                request.getPhone(),
                request.getAvatar()
        );
        return ApiResponse.success(userProfile);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ChangePasswordRequest request) {
        if (principal == null) {
            return ApiResponse.error(401, "未登录");
        }
        authService.changePassword(principal.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success(null);
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private UserRole role;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String token;
        
        public TokenResponse(String token) {
            this.token = token;
        }
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String id;
        private String username;
        private String name;
        private String email;
        private String phone;
        private String avatar;
        private String role;
        private String merchantId;  // 商家ID（仅商家用户有值）
    }

    @Data
    public static class UpdateProfileRequest {
        private String name;
        private String phone;
        private String avatar;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
