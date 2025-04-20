package org.chainpavilion.controller;

import org.chainpavilion.model.User;
import org.chainpavilion.model.UserActivity;
import org.chainpavilion.service.UserActivityService;
import org.chainpavilion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 用户活动控制器
 */
@RestController
@RequestMapping("/api/activities")
public class UserActivityController {
    
    @Autowired
    private UserActivityService userActivityService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户活动列表
     */
    @GetMapping
    public ResponseEntity<?> getUserActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
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
        
        Page<UserActivity> activities;
        if (type != null && !type.isEmpty()) {
            activities = userActivityService.getUserActivitiesByType(user.getId(), type, pageRequest);
        } else {
            activities = userActivityService.getUserActivities(user.getId(), pageRequest);
        }
        
        return ResponseEntity.ok(activities);
    }
    
    /**
     * 获取用户浏览历史
     */
    @GetMapping("/views")
    public ResponseEntity<?> getUserViewHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserActivity> viewHistory = userActivityService.getUserActivitiesByType(
                user.getId(), UserActivityService.ACTIVITY_VIEW, pageRequest);
        
        return ResponseEntity.ok(viewHistory);
    }
} 