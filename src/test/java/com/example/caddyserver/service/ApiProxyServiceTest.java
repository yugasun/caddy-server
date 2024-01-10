package com.example.caddyserver.service;

import com.example.caddyserver.dto.proxy.MatchDto;
import com.example.caddyserver.dto.proxy.RouteDto;
import com.example.caddyserver.dto.proxy.UpstreamDto;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringJUnitConfig
@SpringBootTest
public class ApiProxyServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void shouldGetUpstreams() {
        // Given
        String caddyApi = "https://example.com";
        String apiPath = "/reverse_proxy/upstreams";
        ApiProxyService apiProxyService = new ApiProxyService();
        apiProxyService.setCaddyApi(caddyApi);
        apiProxyService.setRestTemplate(restTemplate);

        List<UpstreamDto> upstreamDtos = new ArrayList<>();
        upstreamDtos.add(new UpstreamDto("upstream1"));
        upstreamDtos.add(new UpstreamDto("upstream2"));

        ResponseEntity<List<UpstreamDto>> responseEntity = ResponseEntity.ok(upstreamDtos);
        when(restTemplate.exchange(eq(caddyApi + apiPath), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        Map<String, List<UpstreamDto>> result = apiProxyService.getUpstreams();

        // Then
        assertEquals(upstreamDtos, result.get("result"));
        verify(restTemplate, times(1)).exchange(eq(caddyApi + apiPath), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    public void shouldGetRoutes() {
        // Given
        String caddyApi = "https://example.com";
        String routesPath = "/config/apps/http/servers/srv0/routes";
        ApiProxyService apiProxyService = new ApiProxyService();
        apiProxyService.setCaddyApi(caddyApi);
        apiProxyService.setRestTemplate(restTemplate);

        List<RouteDto> routeDtos = new ArrayList<>();
        routeDtos.add(new RouteDto());

        ResponseEntity<List<RouteDto>> responseEntity = ResponseEntity.ok(routeDtos);
        when(restTemplate.exchange(eq(caddyApi + routesPath), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        Map<String, List<RouteDto>> result = apiProxyService.getRoutes();

        // Then
        assertEquals(routeDtos, result.get("result"));
        verify(restTemplate, times(1)).exchange(eq(caddyApi + routesPath), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    public void shouldGetHosts() {
        // Given
        String caddyApi = "https://example.com";
        String routesPath = "/config/apps/http/servers/srv0/routes";
        ApiProxyService apiProxyService = new ApiProxyService();
        apiProxyService.setCaddyApi(caddyApi);
        apiProxyService.setRestTemplate(restTemplate);

        List<RouteDto> routeDtos = new ArrayList<>();
        RouteDto routeDto1 = new RouteDto();
        routeDto1.setMatch(new MatchDto[]{new MatchDto()});
        routeDto1.getMatch()[0].setHost(new String[]{"example1.com"});
        RouteDto routeDto2 = new RouteDto();
        routeDto2.setMatch(new MatchDto[]{new MatchDto()});
        routeDto2.getMatch()[0].setHost(new String[]{"example2.com"});
        routeDtos.add(routeDto1);
        routeDtos.add(routeDto2);

        ResponseEntity<List<RouteDto>> responseEntity = ResponseEntity.ok(routeDtos);
        when(restTemplate.exchange(eq(caddyApi + routesPath), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // When
        Map<String, List<String>> result = apiProxyService.getHosts();

        // Then
        List<String> expectedHosts = List.of("example1.com", "example2.com");
        assertEquals(expectedHosts, result.get("result"));
        verify(restTemplate, times(1)).exchange(eq(caddyApi + routesPath), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    public void shouldMatchValidString() {
        // given
        String domain = "example.com";
        String input = "###START### example.com { some text } ###END###\n";

        // when
        String regex = "###START###\\s*(" + domain + ")\\s*\\{[^#]*?}\\s*###END###\\s";
        boolean isMatch = input.matches(regex);

        // then
        assertTrue(isMatch);
    }

    @Test
    public void shouldNotMatchInvalidString() {
        // given
        String domain = "example.com";
        String input = "###START### example.org { some text } ###END###\n";

        // when
        String regex = "###START###\\s*(" + domain + ")\\s*\\{[^#]*?}\\s*###END###\\s";
        boolean isMatch = input.matches(regex);

        // then
        assertFalse(isMatch);
    }

    @Test
    public void shouldNotMatchStringWithMissingStartTag() {
        // given
        String domain = "example.com";
        String input = "example.com { some text } ###END###\n";

        // when
        String regex = "###START###\\s*(" + domain + ")\\s*\\{[^#]*?}\\s*###END###\\s";
        boolean isMatch = input.matches(regex);

        // then
        assertFalse(isMatch);
    }

    @Test
    public void shouldNotMatchStringWithMissingEndTag() {
        // given
        String domain = "example.com";
        String input = "###START### example.com { some text }";

        // when
        String regex = "###START###\\s*(" + domain + ")\\s*\\{[^#]*?}\\s*###END###\\s";
        boolean isMatch = input.matches(regex);

        // then
        assertFalse(isMatch);
    }
}