package com.example.caddyserver.controller;

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

    private static final String DOMAIN_CONFIG_TEMPLATE = """

            ${domain} {
                import favicon
                import encode
                import cache
                import logs ${name}
                ${larkAuth}

                handle /* {
                    reverse_proxy http://${ip}:${port} {
                        header_up Host {host}
                        header_up X-Real-IP {remote_host}
                    }
                }
            }
            """;

    @PostMapping("/domain")
    public Map<String, String> addDomain(@RequestBody DomainDto domainDto) {
        logger.info("add domain: {}", domainDto);

        domainDto.name = domainDto.name != null && !domainDto.name.isEmpty() ? domainDto.name : domainDto.domain;

        // check domain exist
        if (isDomainExist(domainDto.domain)) {
            return Map.of("result", "domain exist");
        }

        Boolean updateResult = updateCaddyfile(domainDto.domain, domainDto.ip, domainDto.port, domainDto.name, domainDto.larkAuth);
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

    private Boolean updateCaddyfile(String domain, String ip, String port, String name, Boolean isLarkAuth) {
        // initialize domain config
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("domain", domain);
        valuesMap.put("ip", ip);
        valuesMap.put("port", port);
        valuesMap.put("name", name);
        valuesMap.put("larkAuth", isLarkAuth ? "import larkoauth" : "");
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String resolvedString = sub.replace(DOMAIN_CONFIG_TEMPLATE);

        // add domain config string to caddyfile
        String content = getCaddyfileContent();
        content += resolvedString;

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
        // exec shell command
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
