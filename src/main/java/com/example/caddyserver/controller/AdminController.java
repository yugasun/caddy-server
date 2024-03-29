package com.example.caddyserver.controller;

import com.example.caddyserver.dto.ApiResponse;
import com.example.caddyserver.dto.DeleteDomainDto;
import com.example.caddyserver.dto.DomainDto;
import com.example.caddyserver.exception.ExceptionEnum;
import com.example.caddyserver.service.ApiProxyService;
import jakarta.validation.Valid;
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
    public ApiResponse addDomain(@Valid @RequestBody DomainDto domainDto) {
        logger.info("add domain: {}", domainDto);

        domainDto.name = domainDto.name != null && !domainDto.name.isEmpty() ? domainDto.name : domainDto.domain;

        // check domain exist
        if (apiProxyService.isDomainExist(domainDto.domain)) {
            return ApiResponse.error(ExceptionEnum.DOMAIN_EXISTED);
        }

        Boolean updateResult = apiProxyService.updateCaddyfile(domainDto);
        String result = updateResult ? apiProxyService.reloadConfig() : "error";
        return ApiResponse.success(result);
    }

    @DeleteMapping("/domain")
    public ApiResponse deleteDomain(@RequestBody DeleteDomainDto domainDto) {
        if (!apiProxyService.isDev()) {
            // check domain exist
            ApiResponse checkResult = checkDomainExist(domainDto.domain);
            if (checkResult != null) {
                return checkResult;
            }
        }

        logger.info("delete domain: {}", domainDto.domain);
        Boolean updateResult = apiProxyService.deleteFromCaddyfile(domainDto.domain);
        String result = updateResult ? apiProxyService.reloadConfig() : "error";
        return ApiResponse.success(result);
    }

    private ApiResponse checkDomainExist(String domain) {
        if (!apiProxyService.isDomainExist(domain)) {
            return ApiResponse.error(ExceptionEnum.DOMAIN_NOT_EXIST);
        }
        return null;
    }

    @PutMapping("/domain")
    public ApiResponse updateDomain(@Valid @RequestBody DomainDto domainDto) {
        logger.info("update domain: {}", domainDto);

        // check domain exist
        ApiResponse checkResult = checkDomainExist(domainDto.domain);
        if (checkResult != null) {
            return checkResult;
        }

        Boolean updateResult = apiProxyService.deleteFromCaddyfile(domainDto.domain);
        if (!updateResult) {
            return ApiResponse.error(ExceptionEnum.DELETE_DOMAIN_ERROR);
        }
        updateResult = apiProxyService.updateCaddyfile(domainDto);
        String result = updateResult ? apiProxyService.reloadConfig() : "error";
        if (result == "error") {
            return ApiResponse.error(ExceptionEnum.UPDATE_DOMAIN_ERROR);
        }
        return ApiResponse.success(result);
    }

    @GetMapping("/domain")
    public Map<String, List<String>> getDomain() {
        logger.info("get domain");
        return apiProxyService.getHosts();
    }

    @GetMapping("/reload")
    public ApiResponse reload() {
        logger.info("reload");
        String result = apiProxyService.reloadConfig();
        return ApiResponse.success(result);
    }

    @GetMapping("/caddy/file")
    public ApiResponse getCaddyFile() {
        logger.info("get caddy file");
        String content = apiProxyService.getCaddyfileContent();
        return ApiResponse.success(content);
    }
}
