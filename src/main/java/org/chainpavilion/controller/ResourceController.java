package org.chainpavilion.controller;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.service.ResourceService;
import org.chainpavilion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

/**
 * 学习资源控制器
 * 
 * 提供资源相关的API接口，包括：
 * - 资源的CRUD操作
 * - 资源分类查询
 * - 资源搜索
 * - 资源收藏管理
 * 
 * @author 知链智阁团队
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserService userService;

    /**
     * 创建新资源接口
     * 
     * @param resource 包含资源信息的资源对象
     * @return 创建成功的资源信息
     */
    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.createResource(resource));
    }

    /**
     * 获取所有资源接口
     * 
     * @return 所有资源的列表
     */
    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    /**
     * 根据分类获取资源接口
     * 
     * @param category 资源分类
     * @return 该分类下的所有资源列表
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Resource>> getResourcesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(resourceService.getResourcesByCategory(category));
    }

    /**
     * 搜索资源接口
     * 
     * @param keyword 搜索关键词
     * @return 匹配的资源列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<Resource>> searchResources(@RequestParam String keyword) {
        return ResponseEntity.ok(resourceService.searchResourcesByKeyword(keyword));
    }
    
    /**
     * 根据ID获取资源接口
     * 
     * @param id 资源ID
     * @return 资源详细信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id);
        if (resource != null) {
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除资源接口
     * 
     * @param id 要删除的资源ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 收藏资源接口
     * 
     * @param id 要收藏的资源ID
     * @param principal 当前登录的用户主体
     * @return 操作结果
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> favoriteResource(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        Resource resource = resourceService.getResourceById(id);
        
        if (user == null || resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean result = resourceService.favoriteResource(user.getId(), id);
        if (result) {
            return ResponseEntity.ok().body("收藏成功");
        } else {
            return ResponseEntity.badRequest().body("已经收藏过该资源");
        }
    }
    
    /**
     * 取消收藏资源接口
     * 
     * @param id 要取消收藏的资源ID
     * @param principal 当前登录的用户主体
     * @return 操作结果
     */
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> unfavoriteResource(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("用户未登录");
        }
        
        User user = userService.findByUsername(principal.getName());
        Resource resource = resourceService.getResourceById(id);
        
        if (user == null || resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean result = resourceService.unfavoriteResource(user.getId(), id);
        if (result) {
            return ResponseEntity.ok().body("取消收藏成功");
        } else {
            return ResponseEntity.badRequest().body("未收藏该资源");
        }
    }
    
    /**
     * 获取用户收藏的资源列表接口
     * 
     * @param principal 当前登录的用户主体
     * @return 用户收藏的资源列表
     */
    @GetMapping("/favorites")
    public ResponseEntity<List<Resource>> getFavoriteResources(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(null);
        }
        
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(resourceService.getFavoriteResources(user.getId()));
    }
}