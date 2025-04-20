package com.cognitive.chain.pavilion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/login")
    public String login() {
        // TODO: 实现登录逻辑
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register() {
        // TODO: 实现注册逻辑
        return "redirect:/login";
    }
}