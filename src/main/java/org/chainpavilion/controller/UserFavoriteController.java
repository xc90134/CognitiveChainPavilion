package org.chainpavilion.controller;

import org.chainpavilion.model.User;
import org.chainpavilion.model.UserFavorite;
import org.chainpavilion.service.UserActivityService;
import org.chainpavilion.service.UserFavoriteService;
import org.chainpavilion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户收藏控制器
 */
@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {
    
    @Autowired
    private UserFavoriteService userFavoriteService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserActivityService userActivityService;
    
    /**
     * 添加收藏
     */
    @PostMapping("/{resourceId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long resourceId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        
        try {
            UserFavorite favorite = userFavoriteService.addFavorite(user.getId(), resourceId);
            
            // 记录收藏活动
            userActivityService.recordFavoriteActivity(user.getId(), resourceId);
            
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long resourceId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        
        userFavoriteService.removeFavorite(user.getId(), resourceId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 检查资源是否已收藏
     */
    @GetMapping("/check/{resourceId}")
    public ResponseEntity<?> checkFavorite(@PathVariable Long resourceId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        
        boolean favorited = userFavoriteService.hasUserFavorited(user.getId(), resourceId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("favorited", favorited);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取用户的收藏列表
     */
    @GetMapping
    public ResponseEntity<?> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<UserFavorite> favorites = userFavoriteService.getUserFavorites(user.getId(), pageRequest);
        return ResponseEntity.ok(favorites);
    }
} 