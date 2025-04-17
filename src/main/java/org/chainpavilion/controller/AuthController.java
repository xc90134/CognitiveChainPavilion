package org.chainpavilion.controller;

import org.chainpavilion.model.User;
import org.chainpavilion.repository.UserRepository;
import org.chainpavilion.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * 提供用户认证相关的API接口：
 * - 用户登录
 * - 用户注册
 * 
 * @author 知链智阁团队
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求（包含用户名和密码）
     * @return 登录成功后的JWT令牌和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            String token = jwtTokenProvider.generateToken(authentication);

            // 获取用户信息
            User user = userRepository.findByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            
            response.put("user", userMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "登录失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求（包含用户名、邮箱和密码）
     * @return 注册成功后的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        try {
            String username = registerRequest.get("username");
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");

            // 检查用户名是否已存在
            if (userRepository.existsByUsername(username)) {
                Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "用户名已存在");
            return ResponseEntity.badRequest().body(errorResponse);
            }

            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(email)) {
                Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "邮箱已被注册");
            return ResponseEntity.badRequest().body(errorResponse);
            }

            // 创建新用户
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("ROLE_USER");

            User savedUser = userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "注册失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}