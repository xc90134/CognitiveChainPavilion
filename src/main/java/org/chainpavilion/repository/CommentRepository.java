package org.chainpavilion.repository;

import org.chainpavilion.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 评论数据访问接口
 * 
 * 提供评论相关的数据库操作功能，包括：
 * - 基本的CRUD操作（继承自JpaRepository）
 * - 根据帖子ID查询评论
 * - 根据用户ID查询评论
 * 
 * @author 知链智阁团队
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 根据帖子ID查询评论，按时间倒序排列
     * 
     * @param postId 帖子ID
     * @param pageable 分页参数
     * @return 分页评论列表
     */
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);
    
    /**
     * 根据用户ID查询该用户发表的所有评论
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页评论列表
     */
    @Query("SELECT c FROM Comment c WHERE c.author.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计指定帖子的评论数量
     * 
     * @param postId 帖子ID
     * @return 评论数量
     */
    long countByPostId(Long postId);
} 