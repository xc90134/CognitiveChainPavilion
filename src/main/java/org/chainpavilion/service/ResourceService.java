package org.chainpavilion.service;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.repository.ResourceRepository;
import org.chainpavilion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 资源服务类
 * 
 * 提供学习资源相关的业务逻辑处理，包括：
 * - 资源的基本CRUD操作
 * - 资源搜索功能
 * - 资源收藏管理
 * 
 * @author 知链智阁团队
 */
@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有资源
     * 
     * @return 所有资源的列表
     */
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    /**
     * 根据ID获取资源
     * 
     * @param id 资源ID
     * @return 找到的资源对象，如果不存在则返回null
     */
    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id).orElse(null);
    }

    /**
     * 创建新资源
     * 
     * @param resource 包含资源信息的资源对象
     * @return 保存后的资源对象（包含生成的ID）
     */
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    /**
     * 根据分类获取资源列表
     * 
     * @param category 资源分类
     * @return 该分类下的所有资源列表
     */
    public List<Resource> getResourcesByCategory(String category) {
        return resourceRepository.findByCategory(category);
    }
    
    /**
     * 根据关键词搜索资源
     * 
     * @param keyword 搜索关键词
     * @return 匹配的资源列表
     */
    public List<Resource> searchResourcesByKeyword(String keyword) {
        // 简单实现：在标题和描述中搜索关键词
        String lowerKeyword = keyword.toLowerCase();
        return resourceRepository.findAll().stream()
                .filter(resource -> 
                    resource.getTitle().toLowerCase().contains(lowerKeyword) || 
                    resource.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 增强版资源搜索
     * 支持关键词与分类组合查询
     */
    /*
    public List<Resource> searchResources(String keyword, String category) {
        if (keyword != null && category != null) {
            return resourceRepository.findByTitleContainingAndCategory(keyword, category);
        } else if (keyword != null) {
            return resourceRepository.findByTitleContaining(keyword);
        } else {
            return resourceRepository.findByCategory(category);
        }
    }
    */
    /**
     * 删除资源
     * 
     * @param id 要删除的资源ID
     */
    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }
    
    /**
     * 用户收藏资源
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否成功收藏（已收藏的资源返回false）
     */
    @Transactional
    public boolean favoriteResource(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElse(null);
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        
        if (user == null || resource == null) {
            return false;
        }
        
        if (user.getResources() == null) {
            user.setResources(new HashSet<>());
        }
        
        // 检查是否已经收藏
        if (user.getResources().contains(resource)) {
            return false;
        }
        
        user.getResources().add(resource);
        userRepository.save(user);
        return true;
    }
    
    /**
     * 用户取消收藏资源
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否成功取消收藏（未收藏的资源返回false）
     */
    @Transactional
    public boolean unfavoriteResource(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElse(null);
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        
        if (user == null || resource == null || user.getResources() == null) {
            return false;
        }
        
        // 检查是否已经收藏
        if (!user.getResources().contains(resource)) {
            return false;
        }
        
        user.getResources().remove(resource);
        userRepository.save(user);
        return true;
    }
    
    /**
     * 获取用户收藏的资源列表
     * 
     * @param userId 用户ID
     * @return 用户收藏的资源列表
     */
    public List<Resource> getFavoriteResources(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getResources() == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(user.getResources());
    }
}