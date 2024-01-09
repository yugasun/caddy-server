package com.example.caddyserver.controller;

import com.example.caddyserver.dto.proxy.RouteDto;
import com.example.caddyserver.dto.proxy.UpstreamDto;
import com.example.caddyserver.service.ApiProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
@RestController()
@RequestMapping("/api")
public class ApiProxyController {
    private final Logger logger = LoggerFactory.getLogger(ApiProxyController.class);

    private final ApiProxyService apiProxyService;

    public ApiProxyController(ApiProxyService apiProxyService) {
        this.apiProxyService = apiProxyService;
    }

    @GetMapping("/reverse_proxy/upstreams")
    public Map<String, List<UpstreamDto>> getUpstreams() {
        return apiProxyService.getUpstreams();
    }

    @GetMapping("/routes")
    public Map<String, List<RouteDto>> getRoutes() {
        return apiProxyService.getRoutes();
    }

    @GetMapping("/hosts")
    public Map<String, List<String>> getHosts() {
        return apiProxyService.getHosts();
    }

    @GetMapping("/config")
    public Map<String, String> getConfig(@RequestParam String path) {
        return apiProxyService.getConfig(path);
    }
}
