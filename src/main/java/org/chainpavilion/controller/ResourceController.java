package org.chainpavilion.controller;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.enums.ResourceCategory;
import org.chainpavilion.repository.ResourceRepository;
import org.chainpavilion.repository.UserRepository;
import org.chainpavilion.service.ResourceService;
import org.chainpavilion.service.UserActivityService;
import org.chainpavilion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 资源控制器
 * 
 * 提供资源相关的API接口：
 * - 资源列表查询
 * - 资源详情获取
 * - 资源创建/编辑/删除
 * - 资源分类统计
 * - 资源搜索
 * - 资源收藏
 * 
 * @author 知链智阁团队
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserActivityService userActivityService;

    /**
     * 获取资源列表（分页+排序）
     */
    @GetMapping
    public ResponseEntity<?> getResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<Resource> resources;
        if (keyword != null && !keyword.isEmpty()) {
            resources = resourceService.searchResources(category, keyword, pageRequest);
        } else if (category != null && !category.isEmpty()) {
            resources = resourceService.getResourcesByCategory(category, pageRequest);
        } else {
            resources = resourceService.getAllResources(pageRequest);
        }
        
        return ResponseEntity.ok(resources);
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable Long id, Principal principal) {
        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 记录浏览活动
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                userActivityService.recordViewActivity(user.getId(), id);
            }
        }
        
        return ResponseEntity.ok(resource);
    }

    /**
     * 添加资源
     */
    @PostMapping
    public ResponseEntity<?> addResource(@RequestBody Resource resource, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        
        Resource savedResource = resourceService.addResource(resource, user);
        return ResponseEntity.ok(savedResource);
    }

    /**
     * 获取热门资源
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularResources() {
        List<Resource> resources = resourceService.getPopularResources();
        return ResponseEntity.ok(resources);
    }

    /**
     * 获取最新资源
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestResources() {
        List<Resource> resources = resourceService.getLatestResources();
        return ResponseEntity.ok(resources);
    }

    /**
     * 获取资源分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        // 这里简化处理，返回固定的分类列表
        Map<String, String> categories = new HashMap<>();
        categories.put("programming", "编程");
        categories.put("ai", "人工智能");
        categories.put("design", "设计");
        categories.put("business", "商业");
        categories.put("language", "语言学习");
        
        return ResponseEntity.ok(categories);
    }

    /**
     * 获取资源分类统计
     * 
     * @return 各分类的资源数量
     */
    @GetMapping("/categories/count")
    public ResponseEntity<?> getCategoryCounts() {
        return ResponseEntity.ok(resourceService.getCategoryCounts());
    }

    /**
     * 收藏资源
     * 
     * @param id 资源ID
     * @return 操作结果
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> favoriteResource(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        
        Optional<Resource> resource = resourceRepository.findById(id);
        if (!resource.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource r = resource.get();
        
        // 添加到用户收藏
        if (!currentUser.getFavoriteResources().contains(r)) {
            currentUser.getFavoriteResources().add(r);
            
            // 更新收藏计数
            r.setFavoriteCount(r.getFavoriteCount() + 1);
            resourceRepository.save(r);
            
            userRepository.save(currentUser);
        }
        
        return ResponseEntity.ok().build();
    }

    /**
     * 取消收藏资源
     * 
     * @param id 资源ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> unfavoriteResource(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        
        Optional<Resource> resource = resourceRepository.findById(id);
        if (!resource.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource r = resource.get();
        
        // 从用户收藏中移除
        if (currentUser.getFavoriteResources().contains(r)) {
            currentUser.getFavoriteResources().remove(r);
            
            // 更新收藏计数
            if (r.getFavoriteCount() > 0) {
                r.setFavoriteCount(r.getFavoriteCount() - 1);
                resourceRepository.save(r);
            }
            
            userRepository.save(currentUser);
        }
        
        return ResponseEntity.ok().build();
    }

    /**
     * 获取当前登录用户的收藏资源
     * 
     * @param pageable 分页参数
     * @return 分页收藏资源列表
     */
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoriteResources(Pageable pageable) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        
        Page<Resource> favorites = resourceService.findFavoritesByUser(currentUser, pageable);
        
        // 标记所有资源为已收藏
        favorites.getContent().forEach(r -> r.setFavorited(true));
        
        return ResponseEntity.ok(favorites);
    }
    
    /**
     * 获取当前登录用户
     * 
     * @return 当前用户，如未登录则返回null
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return userRepository.findByUsername(username);
    }
}


