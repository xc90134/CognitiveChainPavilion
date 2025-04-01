package org.chainpavilion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 
 * 提供一些简单的API接口用于测试系统是否正常工作
 * 
 * @author 知链智阁团队
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试公开接口
     * 
     * @return 欢迎信息
     */
    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "欢迎访问知链智阁公开API");
        response.put("status", "success");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }

    /**
     * 测试需要认证的接口
     * 
     * @return 认证成功信息
     */
    @GetMapping("/private")
    public Map<String, String> privateEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "您已成功认证，欢迎访问知链智阁私有API");
        response.put("status", "success");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
} 