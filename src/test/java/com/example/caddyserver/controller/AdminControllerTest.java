package com.example.caddyserver.controller;

import com.example.caddyserver.dto.ApiResponse;
import com.example.caddyserver.dto.DomainDto;
import com.example.caddyserver.service.ApiProxyService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminControllerTest {

    @Mock
    private ApiProxyService apiProxyService;

    @InjectMocks
    private AdminController adminController;

    @Test
    public void shouldAddDomainSuccessfully() {
        // given
        DomainDto domainDto = new DomainDto();
        domainDto.setDomain("example.com");
        domainDto.setName("example");
        domainDto.setIp("127.0.0.1");
        domainDto.setPort("8080");
        domainDto.setRoot("/home/example");
        domainDto.setRespond("Hello, World!");
        domainDto.setLarkAuth(true);

        when(apiProxyService.isDomainExist(domainDto.getDomain())).thenReturn(false);
        when(apiProxyService.updateCaddyfile(domainDto)).thenReturn(true);
        when(apiProxyService.reloadConfig()).thenReturn("success");

        // when
        ApiResponse result = adminController.addDomain(domainDto);

        // then
        assertEquals("Success", result.getMessage());
        verify(apiProxyService, times(1)).isDomainExist(domainDto.getDomain());
        verify(apiProxyService, times(1)).updateCaddyfile(domainDto);
        verify(apiProxyService, times(1)).reloadConfig();
    }

    @Test
    public void shouldReturnDomainExistError() {
        // given
        DomainDto domainDto = new DomainDto();
        domainDto.setDomain("example.com");

        when(apiProxyService.isDomainExist(domainDto.getDomain())).thenReturn(true);

        // when
        ApiResponse result = adminController.addDomain(domainDto);

        // then
        assertEquals("Domain Existed", result.getMessage());
        verify(apiProxyService, times(1)).isDomainExist(domainDto.getDomain());
        verify(apiProxyService, never()).updateCaddyfile(domainDto);
        verify(apiProxyService, never()).reloadConfig();
    }

    @Test
    public void shouldReturnSuccessWhenUpdateCaddyfileSuccess() {
        // given
        DomainDto domainDto = new DomainDto();
        domainDto.setDomain("example.com");
        domainDto.setName("example");
        domainDto.setIp("127.0.0.1");
        domainDto.setPort("8081");
        domainDto.setRoot("/home/example");
        domainDto.setRespond("Hello, World!");
        domainDto.setLarkAuth(true);

        when(apiProxyService.isDomainExist(domainDto.getDomain())).thenReturn(true);
        when(apiProxyService.updateCaddyfile(domainDto)).thenReturn(true);
        when(apiProxyService.reloadConfig()).thenReturn("success");
        when(apiProxyService.deleteFromCaddyfile(domainDto.getDomain())).thenReturn(true);

        // when
        ApiResponse result = adminController.updateDomain(domainDto);

        // then
        assertEquals("Success", result.getMessage());
        verify(apiProxyService, times(1)).isDomainExist(domainDto.getDomain());
        verify(apiProxyService, times(1)).updateCaddyfile(domainDto);
        verify(apiProxyService, times(1)).reloadConfig();
    }
}
