package org.chainpavilion.controller;

import org.chainpavilion.util.JwtUtil;
import org.chainpavilion.model.User;
import org.chainpavilion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 
 * 提供用户相关的API接口，包括：
 * - 用户注册
 * - 用户登录
 * - 获取用户个人资料
 * - 更新用户个人资料
 * 
 * @author 知链智阁团队
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用户注册接口
     * 
     * @param user 包含注册信息的用户对象
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * 用户登录接口
     * 
     * @param user 包含登录凭据的用户对象
     * @return 包含JWT令牌的响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(user.getUsername());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", jwt);
        return ResponseEntity.ok(tokenMap);
    }
    
    /**
     * 获取用户个人资料接口
     * 
     * @param principal 当前登录的用户主体
     * @return 用户个人资料信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                // 不返回敏感信息
                user.setPassword(null);
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.badRequest().body("用户未登录或不存在");
    }
    
    /**
     * 更新用户个人资料接口
     * 
     * @param userUpdate 包含更新信息的用户对象
     * @param principal 当前登录的用户主体
     * @return 更新后的用户个人资料
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User userUpdate, Principal principal) {
        if (principal != null) {
            User updatedUser = userService.updateUser(principal.getName(), userUpdate);
            if (updatedUser != null) {
                updatedUser.setPassword(null);
                return ResponseEntity.ok(updatedUser);
            }
        }
        return ResponseEntity.badRequest().body("用户未登录或更新失败");
    }
}