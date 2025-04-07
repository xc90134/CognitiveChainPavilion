package org.chainpavilion.controller;

import org.chainpavilion.model.User;
import org.chainpavilion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 用户个人资料控制器
 * 
 * 提供用户相关的API接口，包括：
 * - 获取用户个人资料
 * - 更新用户个人资料
 * 
 * @author 知链智阁团队
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

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