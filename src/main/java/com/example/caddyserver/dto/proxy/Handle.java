package com.example.caddyserver.dto.proxy;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class Handle {
    public String handler;
    public Header headers;
    public Upstream[] upstreams;

    public static class Upstream {
        public String dial;
    }
}
