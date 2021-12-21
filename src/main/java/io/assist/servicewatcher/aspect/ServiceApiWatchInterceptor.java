package io.assist.servicewatcher.aspect;

import io.assist.servicewatcher.dispatcher.ServiceWatcherEventDispatcher;
import io.assist.servicewatcher.vo.ApiEvent;
import io.assist.servicewatcher.vo.ApiEventRequest;
import io.assist.servicewatcher.vo.ApiEventResponse;
import io.assist.servicewatcher.vo.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Deprecated
public class ServiceApiWatchInterceptor implements HandlerInterceptor, WebMvcConfigurer {
    private final ServiceWatcherEventDispatcher dispatcher;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (isWatchApi(request)) {
            ApiEvent apiEvent = createApiEvent(request, response);
            dispatcher.dispatch(apiEvent);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this)
                .addPathPatterns("/**/*");
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
                        .build())
                .response(ApiEventResponse.builder()
                        .httpStatus(HttpStatus.valueOf(response.getStatus()))
                        .contentType(response.getContentType())
                        .build())
                .exception(isExcept(request) ? (ApiException) request.getAttribute("exception") : null)
                .build();
    }
}
