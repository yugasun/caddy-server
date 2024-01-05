package com.example.caddyserver.dto;

import com.example.caddyserver.ApiError;
import com.example.caddyserver.ApiException;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
public class DomainDto implements ValidateDto {

    public String domain;

    public String ip;

    public String port;

    public String name;

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