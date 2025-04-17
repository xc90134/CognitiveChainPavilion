package org.chainpavilion.service;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.enums.ResourceCategory;
import org.chainpavilion.repository.ResourceRepository;
import org.chainpavilion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Optional<Resource> resource = resourceRepository.findById(id);
        if (resource.isPresent()) {
            Resource r = resource.get();
            // 增加浏览量
            r.setViewCount(r.getViewCount() + 1);
            return resourceRepository.save(r);
        }
        return null;
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
    public Page<Resource> getResourcesByCategory(String category, Pageable pageable) {
        return resourceRepository.findByCategory(ResourceCategory.valueOf(category), pageable);
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
        
        if (user.getFavoriteResources() == null) {
            user.setFavoriteResources(new HashSet<>());
        }
        
        // 检查是否已经收藏
        if (user.getFavoriteResources().contains(resource)) {
            return false;
        }
        
        user.getFavoriteResources().add(resource);
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
        
        if (user == null || resource == null || user.getFavoriteResources() == null) {
            return false;
        }
        
        // 检查是否已经收藏
        if (!user.getFavoriteResources().contains(resource)) {
            return false;
        }
        
        user.getFavoriteResources().remove(resource);
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
        if (user == null || user.getFavoriteResources() == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(user.getFavoriteResources());
    }

    /**
     * 分页获取所有资源
     * 
     * @param pageable 分页参数
     * @return 分页资源列表
     */
    public Page<Resource> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    
    /**
     * 搜索资源
     */
    public Page<Resource> searchResources(String category, String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            if (category == null || category.trim().isEmpty()) {
                return getAllResources(pageable);
            } else {
                return getResourcesByCategory(category, pageable);
            }
        }
        
        if (category == null || category.trim().isEmpty()) {
            return resourceRepository.searchByKeyword(keyword, pageable);
        } else {
            return resourceRepository.searchByCategoryAndKeyword(category, keyword, pageable);
        }
    }
    
    /**
     * 添加资源
     */
    @Transactional
    public Resource addResource(Resource resource, User user) {
        resource.setCreatedBy(user);
        resource.setViewCount(0);
        resource.setFavoriteCount(0);
        return resourceRepository.save(resource);
    }
    
    /**
     * 获取热门资源
     */
    public List<Resource> getPopularResources() {
        return resourceRepository.findTop10ByOrderByViewCountDesc();
    }
    
    /**
     * 获取最新资源
     */
    public List<Resource> getLatestResources() {
        return resourceRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    /**
     * 根据用户查询收藏的资源（分页版本）
     * 
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 该用户收藏的资源分页列表
     */
    public Page<Resource> findFavoritesByUser(User user, Pageable pageable) {
        return resourceRepository.findFavoritesByUser(user, pageable);
    }
    
    /**
     * 获取用户创建的资源
     */
    public Page<Resource> getUserResources(Long userId, Pageable pageable) {
        return resourceRepository.findByCreatedById(userId, pageable);
    }
    
    /**
     * 获取各分类的资源数量统计
     * 
     * @return 分类及其对应的资源数量
     */
    public Map<ResourceCategory, Long> getCategoryCounts() {
        return resourceRepository.getCategoryCounts();
    }
}