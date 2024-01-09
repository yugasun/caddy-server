package com.example.caddyserver.controller;

import com.example.caddyserver.config.ConfigType;
import com.example.caddyserver.dto.DomainDto;
import com.example.caddyserver.service.ApiProxyService;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

    @Value("${caddy.reload.command:echo reload}")
    private String CADDY_RELOAD_COMMAND;

    @Value("${caddy.file.path}")
    private String caddyfilePath;

    @PostMapping("/domain")
    public Map<String, String> addDomain(@RequestBody DomainDto domainDto) {
        logger.info("add domain: {}", domainDto);

        domainDto.name = domainDto.name != null && !domainDto.name.isEmpty() ? domainDto.name : domainDto.domain;

        // check domain exist
        if (isDomainExist(domainDto.domain)) {
            return Map.of("result", "domain exist");
        }

        Boolean updateResult = updateCaddyfile(domainDto);
        if (!updateResult) {
            reloadConfig();
        }
        String result = updateResult ? reloadConfig() : "error";
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
        return Map.of("result", reloadConfig());
    }

    @GetMapping("/caddy/file")
    public Map<String, String> getCaddyFile() {
        logger.info("get caddy file");
        return Map.of("result", caddyfilePath);
    }

    private Boolean isDomainExist(String domain) {
        List<String> domains = apiProxyService.getHosts().get("result");
        return domains != null && domains.contains(domain);
    }

    private Boolean updateCaddyfile(DomainDto domainDto) {
        // initialize domain config
        String newConfig = getCaddyConfig(domainDto);
        // add domain config string to caddyfile
        String content = getCaddyfileContent();
        content += newConfig;

        // write caddyfile
        Resource resource = new FileSystemResource(caddyfilePath);
        try {
            FileCopyUtils.copy(content.getBytes(StandardCharsets.UTF_8), resource.getFile());
            logger.info("write caddyfile success");
            return true;
        } catch (IOException e) {
            logger.error("write caddyfile error", e);
            return false;
        }
    }

    private String getCaddyConfig(DomainDto domainDto) {
        Map<String, String> valuesMap = new HashMap<>();
        switch (domainDto.type) {
            case RESERVE_PROXY -> {
                valuesMap.put("domain", domainDto.domain);
                valuesMap.put("ip", domainDto.ip);
                valuesMap.put("port", domainDto.port);
                valuesMap.put("name", domainDto.name);
                valuesMap.put("larkAuth", domainDto.larkAuth ? "import larkoauth" : "");
            }
            case CUSTOM_RESPOND -> {
                valuesMap.put("domain", domainDto.domain);
                valuesMap.put("respond", domainDto.respond);
                valuesMap.put("name", domainDto.name);
                valuesMap.put("larkAuth", domainDto.larkAuth ? "import larkoauth" : "");
            }
            case FILE_SERVER -> {
                valuesMap.put("domain", domainDto.domain);
                valuesMap.put("root", domainDto.root);
                valuesMap.put("name", domainDto.name);
                valuesMap.put("larkAuth", domainDto.larkAuth ? "import larkoauth" : "");
            }
            default -> {
                logger.error("unknown domain type: {}", domainDto.type);
            }
        }
        ;

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String resolvedString = sub.replace(ConfigType.getConfig(domainDto.type));
        return resolvedString;
    }

    private String getCaddyfileContent() {
        Resource resource = new FileSystemResource(caddyfilePath);
        // if file not exit create it
        if (!resource.exists()) {
            try {
                resource.getFile().createNewFile();
            } catch (IOException e) {
                logger.error("create caddyfile error", e);
                return "";
            }
        }
        try (InputStream is = resource.getInputStream()) {
            // 读取和处理资源内容
            byte[] data = FileCopyUtils.copyToByteArray(is);
            String content = new String(data, StandardCharsets.UTF_8);
            logger.debug("caddyfile content: {}", content);
            return content;
        } catch (IOException e) {
            logger.error("get domain error", e);
            return "";
        }
    }

    private String getResourcePathPrefix() {
        Resource resource = new ClassPathResource("application.yml");
        try {
            return resource.getFile().getParentFile().getAbsolutePath();
        } catch (IOException e) {
            logger.error("get resource path prefix error", e);
            return "";
        }
    }

    private String reloadConfig() {
        logger.info("reload config");
        // exec command
        String command = CADDY_RELOAD_COMMAND;
        logger.info("exec command: {}", command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logger.info("exec command success");
        } catch (IOException | InterruptedException e) {
            logger.error("exec command error", e);
            return "error";
        }

        return "success";
    }
}
