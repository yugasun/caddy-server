package com.example.caddyserver.dto;

import com.example.caddyserver.ApiError;
import com.example.caddyserver.ApiException;
import com.example.caddyserver.config.ConfigType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
public class DomainDto implements ValidateDto {

    @Getter
    @Setter
    public ConfigType type;

    @Getter
    @Setter
    public String domain;

    @Getter
    @Setter
    public String ip;

    @Getter
    @Setter
    public String port;

    @Getter
    @Setter
    public String name;

    @Getter
    @Setter
    public String root;

    @Getter
    @Setter
    public String respond;

    @Getter
    @Setter
    public Boolean larkAuth;

    @Override
    public void validate() {
        if (domain == null || domain.isEmpty()) {
            throw new ApiException(ApiError.PARAMETER_INVALID, "domain", "Must specify domain.");
        }

        if (ip == null || ip.isEmpty()) {
            throw new ApiException(ApiError.PARAMETER_INVALID, "ip", "Must specify ip.");
        }

        if (port == null || port.isEmpty()) {
            throw new ApiException(ApiError.PARAMETER_INVALID, "port", "Must specify port.");
        }
    }
}
