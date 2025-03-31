package org.chainpavilion.service;

import org.chainpavilion.model.User;
import org.chainpavilion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 * 
 * 提供用户相关的业务逻辑处理，包括：
 * - 用户注册
 * - 用户信息查询
 * - 用户信息更新
 * 
 * @author 知链智阁团队
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册新用户
     * 
     * @param user 包含用户信息的用户对象
     * @return 保存后的用户对象（包含生成的ID）
     */
    public User registerUser(User user) {
        // 对密码进行加密处理
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 找到的用户对象，如果不存在则返回null
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 更新用户信息
     * 
     * @param username 要更新的用户名
     * @param userUpdate 包含更新信息的用户对象
     * @return 更新后的用户对象，如果用户不存在则返回null
     */
    public User updateUser(String username, User userUpdate) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            // 更新基本信息
            if (userUpdate.getEmail() != null) {
                existingUser.setEmail(userUpdate.getEmail());
            }
            
            // 如果需要更新密码并且提供了新密码
            if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
            }
            
            return userRepository.save(existingUser);
        }
        return null;
    }
}