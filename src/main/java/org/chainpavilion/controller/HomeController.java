package org.chainpavilion.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 首页控制器，处理根路径请求
 */
@RestController
public class HomeController {
    
    /**
     * 返回index.html的内容
     * @return index.html的内容
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        try {
            ClassPathResource resource = new ClassPathResource("static/index.html");
            Path path = resource.getFile().toPath();
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Error loading index.html: " + e.getMessage();
        }
    }
}