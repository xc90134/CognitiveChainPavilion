package org.chainpavilion.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rStatisticsAction")
public class StatisticsController {

    @GetMapping
    public Map<String, Object> handleStatistics() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", "统计数据");
        return response;
    }
}