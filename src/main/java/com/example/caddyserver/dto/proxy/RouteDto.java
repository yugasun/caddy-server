package com.example.caddyserver.dto.proxy;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class RouteDto {
    @Getter
    @Setter
    public Object[] handle;
    @Getter
    @Setter
    public MatchDto[] match;

    @Getter
    @Setter
    public Boolean terminal;
}
