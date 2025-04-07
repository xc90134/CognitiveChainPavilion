package org.chainpavilion.repository;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.enums.ResourceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 资源数据访问接口
 * 
 * 提供学习资源相关的数据库操作功能，包括：
 * - 基本的CRUD操作（继承自JpaRepository）
 * - 根据分类查询资源
 * - 自定义查询方法（分类、关键词、收藏等）
 * - 统计查询
 * 
 * @author 知链智阁团队
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    /**
     * 根据分类查找资源列表
     * 
     * @param category 资源分类名称
     * @return 该分类下的所有资源列表
     */
    List<Resource> findByCategory(String category);

    /**
     * 根据分类查询资源
     * 
     * @param category 资源分类
     * @param pageable 分页参数
     * @return 分页资源列表
     */
    Page<Resource> findByCategory(ResourceCategory category, Pageable pageable);
    
    /**
     * 根据关键词搜索资源（标题或描述包含关键词）
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页资源列表
     */
    @Query("SELECT r FROM Resource r WHERE r.title LIKE %:keyword% OR r.description LIKE %:keyword%")
    Page<Resource> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据分类和关键词搜索资源
     * 
     * @param category 资源分类
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页资源列表
     */
    @Query("SELECT r FROM Resource r WHERE r.category = :category AND (r.title LIKE %:keyword% OR r.description LIKE %:keyword%)")
    Page<Resource> findByCategoryAndKeyword(@Param("category") ResourceCategory category, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 查询用户收藏的资源
     * 
     * @param user 用户
     * @param pageable 分页参数
     * @return 分页收藏资源列表
     */
    @Query("SELECT r FROM Resource r JOIN r.favoritedBy u WHERE u = :user")
    Page<Resource> findFavoritesByUser(@Param("user") User user, Pageable pageable);
    
    /**
     * 获取各分类的资源数量统计
     * 
     * @return 分类及其对应的资源数量
     */
    @Query("SELECT r.category, COUNT(r) FROM Resource r GROUP BY r.category")
    Map<ResourceCategory, Long> getCategoryCounts();
}