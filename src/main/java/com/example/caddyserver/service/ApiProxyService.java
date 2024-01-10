package com.example.caddyserver.service;

import com.example.caddyserver.config.ConfigType;
import com.example.caddyserver.dto.DomainDto;
import com.example.caddyserver.dto.proxy.RouteDto;
import com.example.caddyserver.dto.proxy.UpstreamDto;
import lombok.Setter;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
@Service
public class ApiProxyService {
    private final Logger logger = LoggerFactory.getLogger(ApiProxyService.class);

    @Setter
    @Value("${mode:prod}")
    private String mode;

    @Setter
    @Value("${caddy.api}")
    private String caddyApi;

    @Setter
    @Autowired
    private RestTemplate restTemplate;

    @Setter
    @Value("${caddy.file.path}")
    private String caddyfilePath;

    @Setter
    @Value("${caddy.reload.command:echo reload}")
    private String CADDY_RELOAD_COMMAND;

    private final String ROUTES_PATH = "/apps/http/servers/srv0/routes";

    public Map<String, List<UpstreamDto>> getUpstreams() {
        return getStringListMap(caddyApi, "/reverse_proxy/upstreams");
    }


    public <T> Map<String, List<T>> getStringListMap(String caddyApi, String apiPath) {
        String url = caddyApi + apiPath;
        logger.info("get upstreams from caddy api: {}", url);
        ResponseEntity<List<T>> upstreamDtos = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<T>>() {
        });
        List<T> body = upstreamDtos.getBody();
        return Map.of("result", body);
    }

    public Map<String, List<RouteDto>> getRoutes() {
        String url = caddyApi + "/config" + ROUTES_PATH;
        logger.info("get routes from caddy api: {}", url);
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

    public Map<String, Object> getConfig(@RequestParam String path) {
        String url = caddyApi + "/config" + path;
        logger.info("get config from caddy api: {}", url);
        ResponseEntity<Object> configs = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Object>() {
        });
        Object body = configs.getBody();
        return Map.of("result", body);
    }

    public Map<String, String> getCaddyfile() {
        logger.info("get caddy file");
        return Map.of("result", caddyfilePath);
    }

    public String getCaddyfileContent() {
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

    public String reloadConfig() {
        logger.info("reload config");
        // exec command
        String command = CADDY_RELOAD_COMMAND;
        try {
            logger.info("exec command: {}", command);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logger.info("exec command success");
        } catch (IOException | InterruptedException e) {
            logger.error("exec command error", e);
            return "error";
        }

        return "success";
    }

    public boolean isDev() {
        logger.info("mode: {}", mode);
        return mode != null && (mode.equals("dev") || mode.equals("test"));
    }

    public Boolean deleteFromCaddyfile(String domain) {
        String regex = "###START###\\s*(" + domain + ")\\s*\\{[^#]*?}\\s*###END###\\s";
        String content = getCaddyfileContent();

        content = content.replaceAll(regex, "");

        return writeToCaddyfile(content);
    }

    public Boolean isDomainExist(String domain) {
        boolean isExist = false;
        if (isDev()) {
            logger.info("Debug mode, skip check domain exist");
        } else {
            // check domain exist
            List<String> domains = getHosts().get("result");
            if (isDomainExist(domain)) {
                isExist = domains != null && domains.contains(domain);
            }
        }

        return isExist;
    }

    public Boolean updateCaddyfile(DomainDto domainDto) {
        // initialize domain config
        String newConfig = getCaddyConfig(domainDto);
        // add domain config string to caddyfile
        String content = getCaddyfileContent();
        content += newConfig;

        // write caddyfile
        return writeToCaddyfile(content);
    }

    public Boolean writeToCaddyfile(String content) {
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

    public String getCaddyConfig(DomainDto domainDto) {
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
}
