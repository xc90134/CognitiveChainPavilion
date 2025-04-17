package org.chainpavilion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 
 * 配置Web MVC相关设置，包括：
 * - 跨域资源共享(CORS)配置
 * - 其他Web MVC相关配置
 * 
 * @author 知链智阁团队
 */
@Configuration

public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域资源共享
     * <p>
     * 允许前端应用从不同域名/端口访问API
     *
     * @param registry CORS配置注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加favicon处理器
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);  // 添加缓存

        // 根路径映射
        registry.addResourceHandler("/")
                .addResourceLocations("classpath:/static/");

        // 添加静态资源处理器
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}