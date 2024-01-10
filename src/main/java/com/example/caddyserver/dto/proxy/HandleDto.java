package com.example.caddyserver.dto.proxy;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class HandleDto {
    public String handler;
    public HeaderDto headers;
    public Upstream[] upstreams;

    public static class Upstream {
        public String dial;
    }
}
