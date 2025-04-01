package org.chainpavilion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 
 * 配置应用的安全性设置，包括：
 * - 请求授权规则
 * - 会话管理
 * - 过滤器链配置
 * - 认证管理器
 * - 密码编码器
 * 
 * @author 知链智阁团队
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * 
     * 定义了HTTP请求的安全规则：
     * - 禁用CSRF保护（使用JWT方式认证）
     * - 设置请求授权规则
     * - 配置无状态会话管理
     * - 添加JWT认证过滤器
     * 
     * @param http HTTP安全构建器
     * @return 配置好的安全过滤器链
     * @throws Exception 配置过程中的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/index.html", "/static/**", "/public/**", "/css/**", "/js/**", "/images/**", "/api/auth/**", "/api/resources/**", "/api/test/public", "/error").permitAll() // 允许公开访问的资源和API
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 配置密码编码器
     * 
     * 使用BCrypt强哈希函数加密密码
     * 
     * @return 密码编码器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置认证管理器
     * 
     * 用于处理认证请求
     * 
     * @param authenticationConfiguration 认证配置
     * @return 认证管理器实例
     * @throws Exception 配置过程中的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}