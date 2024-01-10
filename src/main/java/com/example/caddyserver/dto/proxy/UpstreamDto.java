package com.example.caddyserver.dto.proxy;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yugasun
 * @date 2024/1/8
 **/
public class UpstreamDto {
    @Getter
    @Setter
    public String address;
    @Getter
    @Setter
    public Number num_requests;
    @Getter
    @Setter
    public Number fails;

    public UpstreamDto(String address) {
        this.address = address;
        this.num_requests = 0;
        this.fails = 0;
    }
}
