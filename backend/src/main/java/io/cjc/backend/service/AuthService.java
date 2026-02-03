package io.cjc.backend.service;

import io.cjc.backend.entity.User;
import io.cjc.backend.enums.UserRole;
import io.cjc.backend.repository.UserRepository;
import io.cjc.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
        
        return jwtTokenProvider.generateToken(username, role.name(), user.getMerchantId());
    }

    @Transactional(readOnly = true)
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return jwtTokenProvider.generateToken(username, user.getRole().name(), user.getMerchantId());
    }
}
