package com.example.caddyserver.dto;

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
