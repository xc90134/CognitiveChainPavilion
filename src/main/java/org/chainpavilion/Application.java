package org.chainpavilion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 知链智阁（CognitiveChainPavilion）应用启动类
 * 
 * 这是一个集学习资源聚合、社区交流、学习激励于一体的综合学习平台
 * 
 * 功能包括：
 * 1. 用户系统：注册、登录、个人中心
 * 2. 资源管理：多源学习资源整合、分类展示
 * 3. 搜索功能：关键词搜索和资源分类筛选
 * 4. 收藏功能：用户收藏和管理学习资源
 * 
 * @author 知链智阁团队
 * @version 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.chainpavilion.repository")
@EntityScan(basePackages = "org.chainpavilion.model")
public class Application {
    /**
     * 应用程序入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}