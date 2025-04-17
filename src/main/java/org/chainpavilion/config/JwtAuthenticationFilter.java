package org.chainpavilion.config;

import org.chainpavilion.service.UserService;
import org.chainpavilion.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 
 * 用于拦截HTTP请求，提取并验证JWT令牌，并设置Spring Security上下文
 * 实现了请求发送前的身份验证处理
 * 
 * @author 知链智阁团队
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * 内部过滤方法，处理每个HTTP请求
     * 
     * 1. 从请求头中提取JWT令牌
     * 2. 验证令牌并提取用户名
     * 3. 验证用户存在性
     * 4. 设置认证信息到Spring Security上下文
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        logger.debug("处理请求: " + request.getRequestURI());

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.debug("从JWT中提取的用户名: " + username);
            } catch (Exception e) {
                logger.error("无法解析JWT令牌: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 验证用户存在
            if (userService.findByUsername(username) != null) {
                // 验证token有效，检查token是否过期且用户名匹配
                if (jwtUtil.validateToken(jwt, username)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug("用户 " + username + " 认证成功");
                } else {
                    logger.debug("JWT令牌无效或已过期");
                }
            } else {
                logger.debug("用户不存在: " + username);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}