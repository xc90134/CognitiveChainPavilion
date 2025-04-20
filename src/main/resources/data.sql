-- 初始化用户数据
INSERT INTO users (id, username, email, password, role) 
VALUES 
(1, 'admin', 'admin@zhilianzhige.com', '$2a$10$X7InfJHuLxQw5C/8B4Z9UePjk8SYpP/blvT.chmj5Vz1mVJPA26Tu', 'ADMIN'),
(2, 'test', 'test@zhilianzhige.com', '$2a$10$RvgDJhhLlzakrE/Xul/ureQxVVpKrXnQnD/tmlsY89AUWc2jcUl9a', 'USER');

-- 初始化资源数据（根据Resource实体类）
INSERT INTO resources (id, title, description, url, category, cover_image, source_website, view_count, favorite_count) 
VALUES 
(1, '机器学习入门教程', '适合机器学习初学者的教程，包含基本概念和实践案例', 'https://example.com/ml-intro', 'AI', null, '知链智阁', 120, 45),
(2, 'Spring Boot开发指南', '从零开始学习Spring Boot框架的完整教程', 'https://example.com/spring-boot', 'PROGRAMMING', null, '知链智阁', 210, 78),
(3, 'UI设计原则与实践', '现代UI设计的核心原则和最佳实践', 'https://example.com/ui-design', 'DESIGN', null, '知链智阁', 98, 36),
(4, '数据结构与算法基础', '计算机科学必备的数据结构与算法知识', 'https://example.com/algorithms', 'PROGRAMMING', null, '知链智阁', 178, 64),
(5, '人工智能与伦理', '探讨AI技术发展中的伦理问题与挑战', 'https://example.com/ai-ethics', 'AI', null, '知链智阁', 87, 29);

-- 更新创建者关系
UPDATE resources SET created_by = 1 WHERE id IN (1, 2, 3);
UPDATE resources SET created_by = 2 WHERE id IN (4, 5);

-- 用户收藏关系
INSERT INTO user_favorites (id, user_id, resource_id) 
VALUES 
(1, 1, 2),
(2, 1, 3),
(3, 2, 1),
(4, 2, 2);

-- 用户活动记录
INSERT INTO user_activities (id, user_id, resource_id, activity_type) 
VALUES 
(1, 1, 1, 'VIEW'),
(2, 1, 2, 'VIEW'),
(3, 1, 2, 'FAVORITE'),
(4, 1, 3, 'VIEW'),
(5, 1, 3, 'FAVORITE'),
(6, 2, 1, 'VIEW'),
(7, 2, 1, 'FAVORITE'),
(8, 2, 2, 'VIEW'),
(9, 2, 2, 'FAVORITE'),
(10, 2, 4, 'VIEW'); 