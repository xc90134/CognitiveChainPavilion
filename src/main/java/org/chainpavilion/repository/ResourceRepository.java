package org.chainpavilion.repository;

import org.chainpavilion.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资源数据访问接口
 * 
 * 提供学习资源相关的数据库操作功能，包括：
 * - 基本的CRUD操作（继承自JpaRepository）
 * - 根据分类查询资源
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
}