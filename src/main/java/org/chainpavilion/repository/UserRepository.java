package org.chainpavilion.repository;

import org.chainpavilion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问接口
 * 
 * 提供用户相关的数据库操作功能，包括：
 * - 基本的CRUD操作（继承自JpaRepository）
 * - 根据用户名查询用户
 * 
 * @author 知链智阁团队
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 找到的用户对象，如果不存在则返回null
     */
    User findByUsername(String username);
}