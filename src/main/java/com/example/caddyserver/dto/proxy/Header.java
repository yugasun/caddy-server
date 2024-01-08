package com.example.caddyserver.dto.proxy;

import java.util.Map;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class Header {
    public Request request;

    public static class Request {
        public Map<String, String[]> set;
    }
}
