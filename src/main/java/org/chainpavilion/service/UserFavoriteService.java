package org.chainpavilion.service;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.UserFavorite;
import org.chainpavilion.repository.ResourceRepository;
import org.chainpavilion.repository.UserFavoriteRepository;
import org.chainpavilion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户收藏服务类
 */
@Service
public class UserFavoriteService {
    
    @Autowired
    private UserFavoriteRepository userFavoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    /**
     * 添加收藏
     */
    @Transactional
    public UserFavorite addFavorite(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new RuntimeException("资源不存在"));
        
        // 检查是否已收藏
        if (userFavoriteRepository.existsByUserAndResource(user, resource)) {
            throw new RuntimeException("已经收藏过该资源");
        }
        
        // 创建新收藏
        UserFavorite favorite = new UserFavorite(user, resource);
        
        // 更新资源收藏数
        resource.setFavoriteCount(resource.getFavoriteCount() + 1);
        resourceRepository.save(resource);
        
        return userFavoriteRepository.save(favorite);
    }
    
    /**
     * 取消收藏
     */
    @Transactional
    public void removeFavorite(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new RuntimeException("资源不存在"));
        
        Optional<UserFavorite> favorite = userFavoriteRepository.findByUserAndResource(user, resource);
        if (favorite.isPresent()) {
            userFavoriteRepository.delete(favorite.get());
            
            // 更新资源收藏数
            int count = resource.getFavoriteCount();
            resource.setFavoriteCount(count > 0 ? count - 1 : 0);
            resourceRepository.save(resource);
        }
    }
    
    /**
     * 查询用户是否已收藏资源
     */
    public boolean hasUserFavorited(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElse(null);
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        
        if (user == null || resource == null) {
            return false;
        }
        
        return userFavoriteRepository.existsByUserAndResource(user, resource);
    }
    
    /**
     * 获取用户收藏列表
     */
    public Page<UserFavorite> getUserFavorites(Long userId, Pageable pageable) {
        return userFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
} 