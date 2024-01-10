package com.example.caddyserver.controller;

import com.example.caddyserver.dto.DeleteDomainDto;
import com.example.caddyserver.dto.DomainDto;
import com.example.caddyserver.service.ApiProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private ApiProxyService apiProxyService;

    @PostMapping("/domain")
    public Map<String, String> addDomain(@RequestBody DomainDto domainDto) {
        logger.info("add domain: {}", domainDto);

        domainDto.name = domainDto.name != null && !domainDto.name.isEmpty() ? domainDto.name : domainDto.domain;

        // check domain exist
        if (apiProxyService.isDomainExist(domainDto.domain)) {
            return Map.of("result", "domain exist");
        }

        Boolean updateResult = apiProxyService.updateCaddyfile(domainDto);
        String result = updateResult ? apiProxyService.reloadConfig() : "error";
        return Map.of("result", result);
    }

    @DeleteMapping("/domain")
    public Map<String, String> deleteDomain(@RequestBody DeleteDomainDto domainDto) {
        if (!apiProxyService.isDev()) {
            // check domain exist
            if (!apiProxyService.isDomainExist(domainDto.domain)) {
                return Map.of("result", "domain not exist");
            }
        }

        logger.info("delete domain: {}", domainDto.domain);
        Boolean updateResult = apiProxyService.deleteFromCaddyfile(domainDto.domain);
        String result = updateResult ? apiProxyService.reloadConfig() : "error";
        return Map.of("result", result);
    }

    @PutMapping("/domain")
    public Map<String, String> updateDomain(@RequestBody DomainDto domainDto) {
        logger.info("update domain: {}", domainDto);

        // check domain exist
        if (!apiProxyService.isDomainExist(domainDto.domain)) {
            return Map.of("result", "domain not exist");
        }

        Boolean updateResult = apiProxyService.deleteFromCaddyfile(domainDto.domain);
        if (!updateResult) {
            return Map.of("result", "error");
        }
        updateResult = apiProxyService.updateCaddyfile(domainDto);
        String result = updateResult ? apiProxyService.reloadConfig() : "error";
        return Map.of("result", result);
    }

    @GetMapping("/domain")
    public Map<String, List<String>> getDomain() {
        logger.info("get domain");
        return apiProxyService.getHosts();
    }

    @GetMapping("/reload")
    public Map<String, String> reload() {
        logger.info("reload");
        return Map.of("result", apiProxyService.reloadConfig());
    }

    @GetMapping("/caddy/file")
    public Map<String, String> getCaddyFile() {
        return apiProxyService.getCaddyfile();
    }
}
