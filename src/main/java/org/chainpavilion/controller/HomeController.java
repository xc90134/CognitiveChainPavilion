package org.chainpavilion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器，处理根路径请求
 */
@RestController
public class HomeController {
    
    /**
     * 返回API服务状态信息
     * @return 服务运行状态信息
     */
    @GetMapping("/")
    public String home() {
        return "知链智阁API项目正在运行 - Cognitive Chain Pavilion API is running";
    }
} 