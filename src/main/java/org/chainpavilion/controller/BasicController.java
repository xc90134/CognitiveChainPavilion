package org.chainpavilion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
    
    @GetMapping("/index")
    public String welcome() {
        return "欢迎访问知链智阁知识服务平台！";
    }
}