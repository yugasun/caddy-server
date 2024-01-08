package com.example.caddyserver.dto.proxy;

import lombok.Getter;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class RouteDto {
    public Object[] handle;
    public Match[] match;

    public Boolean terminal;
}
