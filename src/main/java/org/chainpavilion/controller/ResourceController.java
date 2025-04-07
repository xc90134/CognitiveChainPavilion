package org.chainpavilion.controller;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.enums.ResourceCategory;
import org.chainpavilion.repository.ResourceRepository;
import org.chainpavilion.repository.UserRepository;
import org.chainpavilion.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
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

    /**
     * 获取资源列表
     * 
     * @param pageable 分页参数
     * @param category 资源分类（可选）
     * @param keyword 搜索关键词（可选）
     * @return 分页资源列表
     */
    @GetMapping
    public ResponseEntity<Page<Resource>> getResources(
            Pageable pageable,
            @RequestParam(required = false) ResourceCategory category,
            @RequestParam(required = false) String keyword) {
        
        Page<Resource> resources;
        
        // 获取当前用户
        User currentUser = getCurrentUser();
        
        if (category != null && keyword != null && !keyword.isEmpty()) {
            // 按分类和关键词搜索
            resources = resourceService.findByCategoryAndKeyword(category, keyword, pageable);
        } else if (category != null) {
            // 只按分类搜索
            resources = resourceService.findByCategory(category, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // 只按关键词搜索
            resources = resourceService.findByKeyword(keyword, pageable);
        } else {
            // 获取所有资源
            resources = resourceService.findAll(pageable);
        }
        
        // 如果用户已登录，标记哪些资源已被收藏
        if (currentUser != null) {
            resources.getContent().forEach(resource -> {
                resource.setFavorited(currentUser.getFavoriteResources().contains(resource));
            });
        }
        
        return ResponseEntity.ok(resources);
    }

    /**
     * 获取资源详情
     * 
     * @param id 资源ID
     * @return 资源详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getResource(@PathVariable Long id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        
        if (resource.isPresent()) {
            // 增加浏览量
            Resource r = resource.get();
            r.setViewCount(r.getViewCount() + 1);
            resourceRepository.save(r);
            
            // 如果用户已登录，标记该资源是否已被收藏
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                r.setFavorited(currentUser.getFavoriteResources().contains(r));
            }
            
            return ResponseEntity.ok(r);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建资源
     * 
     * @param resource 资源信息
     * @return 创建的资源
     */
    @PostMapping
    public ResponseEntity<?> createResource(@RequestBody Resource resource) {
        // 设置创建时间
        resource.setCreatedAt(new Date());
        resource.setUpdatedAt(new Date());
        
        // 设置创建者
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            resource.setCreatedBy(currentUser);
        }
        
        // 初始化计数器
        resource.setViewCount(0);
        resource.setFavoriteCount(0);
        
        Resource savedResource = resourceRepository.save(resource);
        return ResponseEntity.ok(savedResource);
    }

    /**
     * 更新资源
     * 
     * @param id 资源ID
     * @param resource 更新的资源信息
     * @return 更新后的资源
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        Optional<Resource> existingResource = resourceRepository.findById(id);
        
        if (existingResource.isPresent()) {
            Resource r = existingResource.get();
            
            // 检查权限
            User currentUser = getCurrentUser();
            if (currentUser == null || (r.getCreatedBy() != null && !r.getCreatedBy().getId().equals(currentUser.getId()))) {
                return ResponseEntity.status(403).body("您没有权限修改该资源");
            }
            
            // 更新字段
            r.setTitle(resource.getTitle());
            r.setDescription(resource.getDescription());
            r.setUrl(resource.getUrl());
            r.setCategory(resource.getCategory());
            r.setCoverImage(resource.getCoverImage());
            r.setUpdatedAt(new Date());
            
            Resource updatedResource = resourceRepository.save(r);
            return ResponseEntity.ok(updatedResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除资源
     * 
     * @param id 资源ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        Optional<Resource> existingResource = resourceRepository.findById(id);
        
        if (existingResource.isPresent()) {
            Resource r = existingResource.get();
            
            // 检查权限
            User currentUser = getCurrentUser();
            if (currentUser == null || (r.getCreatedBy() != null && !r.getCreatedBy().getId().equals(currentUser.getId()))) {
                return ResponseEntity.status(403).body("您没有权限删除该资源");
            }
            
            resourceRepository.delete(r);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
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
