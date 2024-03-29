package com.example.caddyserver.dto;

import com.example.caddyserver.exception.BizException;
import com.example.caddyserver.config.ConfigType;
import com.example.caddyserver.exception.ExceptionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
public class DomainDto {

    @Getter
    @Setter
    public ConfigType type;

    @Getter
    @Setter
    @NotNull(message = "domain is required")
    public String domain;

    @Getter
    @Setter
    @NotNull(message = "ip is required, eg: 127.0.0.1")
    public String ip;

    @Getter
    @Setter
    @NotNull(message = "port is required, eg: 8080")
    public String port;

    @Getter
    @Setter
    @NotNull(message = "name is required")
    public String name;

    @Getter
    @Setter
    public String root;

    @Getter
    @Setter
    public String respond;

    @Getter
    @Setter
    @NotNull(message = "larkAuth is required, true or false")
    public Boolean larkAuth;

    @Override
    public String toString() {
        return "DomainDto{" +
                "type=" + type +
                ", domain='" + domain + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", name='" + name + '\'' +
                ", root='" + root + '\'' +
                ", respond='" + respond + '\'' +
                ", larkAuth=" + larkAuth +
                '}';
    }
}
