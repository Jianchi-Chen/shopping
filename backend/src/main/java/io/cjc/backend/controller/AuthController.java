package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.enums.UserRole;
import io.cjc.backend.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(new TokenResponse(token));
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
}
