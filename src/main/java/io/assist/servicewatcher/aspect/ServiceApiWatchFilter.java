package io.assist.servicewatcher.aspect;

import io.assist.servicewatcher.dispatcher.ServiceWatcherEventDispatcher;
import io.assist.servicewatcher.vo.ApiEvent;
import io.assist.servicewatcher.vo.ApiEventRequest;
import io.assist.servicewatcher.vo.ApiEventResponse;
import io.assist.servicewatcher.vo.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ServiceApiWatchFilter implements Filter {
    private final ServiceWatcherEventDispatcher dispatcher;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(servletRequest, servletResponse);
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            if (isWatchApi(request)) {
                ApiEvent apiEvent = createApiEvent(request, response);
                dispatcher.dispatch(apiEvent);
            }
        } catch (Exception e) {
            log.error("Service watcher event dispatching exception --> {}", e.getMessage());
        };
    }

    private boolean isWatchApi(HttpServletRequest request) {
        return request.getAttribute("isWatchApi") != null;
    }

    private boolean isExcept(HttpServletRequest request) {
        return Objects.nonNull(request.getAttribute("exception"));
    }

    private ApiEvent createApiEvent(HttpServletRequest request, HttpServletResponse response) {
        return ApiEvent.builder()
                .duration((long) request.getAttribute("duration"))
                .request(ApiEventRequest.builder()
                        .uri(isExcept(request) ? request.getAttribute("origin").toString() : request.getRequestURI())
                        .contentType(request.getContentType())
                        .method(request.getMethod())
                        .userAgent(request.getHeader("User-Agent"))
                        .referrer(request.getHeader("Referer"))
                        .build())
                .response(ApiEventResponse.builder()
                        .httpStatus(HttpStatus.valueOf(response.getStatus()))
                        .contentType(response.getContentType())
                        .build())
                .exception(isExcept(request) ? (ApiException) request.getAttribute("exception") : null)
                .build();
    }
}
