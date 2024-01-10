package com.example.caddyserver.dto;

import com.example.caddyserver.ApiError;
import com.example.caddyserver.ApiException;
import com.example.caddyserver.config.ConfigType;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
public class DeleteDomainDto implements ValidateDto {

    public String domain;

    @Override
    public void validate() {
    }
}
