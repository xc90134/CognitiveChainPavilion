package org.chainpavilion.repository;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户收藏数据访问接口
 */
@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    /**
     * 查找用户的收藏资源（分页）
     */
    Page<UserFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 检查用户是否已收藏特定资源
     */
    boolean existsByUserAndResource(User user, Resource resource);
    
    /**
     * 查找用户对特定资源的收藏
     */
    Optional<UserFavorite> findByUserAndResource(User user, Resource resource);
    
    /**
     * 统计资源被收藏数量
     */
    long countByResourceId(Long resourceId);
} 