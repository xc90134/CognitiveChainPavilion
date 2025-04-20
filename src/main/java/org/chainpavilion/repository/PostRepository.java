package org.chainpavilion.repository;

import org.chainpavilion.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子数据访问接口
 * 
 * 提供论坛帖子相关的数据库操作功能，包括：
 * - 基本的CRUD操作（继承自JpaRepository）
 * - 根据分类查询帖子
 * - 自定义查询方法（分类、关键词等）
 * - 热门帖子查询
 * 
 * @author 知链智阁团队
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * 根据分类查询帖子
     * 
     * @param category 帖子分类
     * @param pageable 分页参数
     * @return 分页帖子列表
     */
    Page<Post> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据关键词搜索帖子（标题或内容包含关键词）
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页帖子列表
     */
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据分类和关键词搜索帖子
     * 
     * @param category 帖子分类
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页帖子列表
     */
    @Query("SELECT p FROM Post p WHERE (:category IS NULL OR p.category = :category) " +
           "AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByCategoryAndKeyword(
            @Param("category") String category, 
            @Param("keyword") String keyword, 
            Pageable pageable);
    
    /**
     * 获取热门帖子（浏览量最高）
     * 
     * @return 热门帖子列表
     */
    List<Post> findTop10ByOrderByViewCountDesc();
    
    /**
     * 获取最新帖子
     * 
     * @return 最新帖子列表
     */
    List<Post> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根据作者ID查找帖子
     * 
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 分页帖子列表
     */
    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId")
    Page<Post> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
} 