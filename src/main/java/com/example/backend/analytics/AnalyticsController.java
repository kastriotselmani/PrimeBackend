package com.example.backend.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService svc;

    @GetMapping("/task1") public Object task1() { return svc.task1(); }
    @GetMapping("/task2") public Object task2() { return svc.task2(); }
    @GetMapping("/task3") public Object task3() { return svc.task3(); }

}

