package io.cjc.backend.service;

import io.cjc.backend.dto.UserProfileDTO;
import io.cjc.backend.entity.User;
import io.cjc.backend.enums.UserRole;
import io.cjc.backend.repository.UserRepository;
import io.cjc.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${file.upload.base-url:http://localhost:8080/api/uploads}")
    private String uploadBaseUrl;

    @Transactional
    public String register(String username, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        
        userRepository.save(user);
        
        return jwtTokenProvider.generateToken(user.getId(), username, role.name(), user.getMerchantId());
    }

    @Transactional(readOnly = true)
    public String login(String username, String password) {
        log.debug("登录请求 - 用户名: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("登录失败 - 用户不存在: {}", username);
                    return new RuntimeException("用户名或密码错误");
                });
        
        log.debug("找到用户: {}, 角色: {}", user.getUsername(), user.getRole());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("登录失败 - 密码错误: {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        log.info("登录成功 - 用户: {}", username);
        return jwtTokenProvider.generateToken(user.getId(), username, user.getRole().name(), user.getMerchantId());
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return convertToUserProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateUserProfile(String username, String name, String phone, String avatar) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (name != null) {
            user.setName(name);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (avatar != null) {
            user.setAvatar(normalizeAvatarPath(avatar));
        }
        
        userRepository.save(user);
        
        return convertToUserProfileDTO(user);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserProfileDTO convertToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(buildAvatarUrl(user.getAvatar()));
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private String normalizeAvatarPath(String avatar) {
        if (avatar == null) {
            return null;
        }

        String trimmed = avatar.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        String path = trimmed;
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            try {
                URI uri = URI.create(trimmed);
                if (uri.getPath() != null && !uri.getPath().isBlank()) {
                    path = uri.getPath();
                }
            } catch (IllegalArgumentException ex) {
                return trimmed;
            }
        } else if (uploadBaseUrl != null && !uploadBaseUrl.isBlank() && trimmed.startsWith(uploadBaseUrl)) {
            path = trimmed.substring(uploadBaseUrl.length());
        }

        if (path.startsWith("/api/uploads/")) {
            path = path.substring("/api/uploads/".length());
        } else if (path.startsWith("/uploads/")) {
            path = path.substring("/uploads/".length());
        } else if (path.startsWith("uploads/")) {
            path = path.substring("uploads/".length());
        } else if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    private String buildAvatarUrl(String avatarPath) {
        if (avatarPath == null) {
            return null;
        }

        String trimmed = avatarPath.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        String base = uploadBaseUrl == null ? "" : uploadBaseUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String path = trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
        if (base.isEmpty()) {
            return path;
        }

        return base + "/" + path;
    }
}
