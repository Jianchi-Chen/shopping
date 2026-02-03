package io.cjc.backend.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPrincipal {
    private String username;
    private String role;
    private String merchantId;
}
