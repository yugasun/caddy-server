package com.example.caddyserver.service;

import com.example.caddyserver.dto.proxy.RouteDto;
import com.example.caddyserver.dto.proxy.UpstreamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
@Component
public class ApiProxyService {
    private final Logger logger = LoggerFactory.getLogger(ApiProxyService.class);

    @Value("${caddy.api}")
    private String caddyApi;

    private final String ROUTES_PATH = "/apps/http/servers/srv0/routes";

    public Map<String, List<UpstreamDto>> getUpstreams() {
        return getStringListMap(caddyApi, "/reverse_proxy/upstreams");
    }


    public <T> Map<String, List<T>> getStringListMap(String caddyApi, String apiPath) {
        String url = caddyApi + apiPath;
        logger.debug("get upstreams from caddy api: {}", url);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<T>> upstreamDtos = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<T>>() {
        });
        List<T> body = upstreamDtos.getBody();
        return Map.of("result", body);
    }

    public Map<String, List<RouteDto>> getRoutes() {
        String url = caddyApi + "/config" + ROUTES_PATH;
        logger.debug("get routes from caddy api: {}", url);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<RouteDto>> upstreamDtos = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RouteDto>>() {
        });
        List<RouteDto> body = upstreamDtos.getBody();
        return Map.of("result", body);
    }

    public Map<String, List<String>> getHosts() {
        List<RouteDto> routes = getRoutes().get("result");
        // iterate routes, get domains from match host
        List<String> hosts = new ArrayList<>();
        for (RouteDto route : routes) {
            String host = route.match[0].host[0];
            logger.debug("hosts: {}", hosts);
            hosts.add(host);
        }

        return Map.of("result", hosts);
    }

    public Map<String, String> getConfig(@RequestParam String path) {
        String url = caddyApi + "/config" + path;
        logger.debug("get config from caddy api: {}", url);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> upstreamDtos = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        });
        String body = upstreamDtos.getBody();
        return Map.of("result", body);
    }
}
