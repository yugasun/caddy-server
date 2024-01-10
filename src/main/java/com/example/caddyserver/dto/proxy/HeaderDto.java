package com.example.caddyserver.dto.proxy;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class HeaderDto {
    @Getter
    @Setter
    public Request request;

    public static class Request {
        public Map<String, String[]> set;
    }
}
