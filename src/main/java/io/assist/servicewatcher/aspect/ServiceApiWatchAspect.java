package io.assist.servicewatcher.aspect;

import io.assist.servicewatcher.vo.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Aspect
@RequiredArgsConstructor
@Slf4j
public class ServiceApiWatchAspect {
    @PostConstruct
    private void postConstruct() {
        log.info("Activate ServiceApiWatch Aspect");
    }

    @Before(value = "@within(io.assist.servicewatcher.annotation.ApiWatch) && !@annotation(io.assist.servicewatcher.annotation.ApiSkip)")
    public void before() {
        setRequestAttribute("isWatchApi", true);
    }

    @AfterThrowing(value = "@within(io.assist.servicewatcher.annotation.ApiWatch) && !@annotation(io.assist.servicewatcher.annotation.ApiSkip)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        log.trace("after throw from --> {}", joinPoint.getSignature().getName());
        setRequestAttribute("origin", getRequestURI());
        setRequestAttribute("exception", ApiException.builder()
                .type(exception.getClass().getName())
                .message(exception.getMessage())
                .build());
    }

    @Around("@within(io.assist.servicewatcher.annotation.ApiWatch) && !@annotation(io.assist.servicewatcher.annotation.ApiSkip)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startAt = System.currentTimeMillis();
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            long endAt = System.currentTimeMillis();
            setRequestAttribute("duration", endAt - startAt);
        }
    }

    private String getRequestURI() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getRequestURI();
    }

    private void setRequestAttribute(String name, Object value) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        request.setAttribute(name, value);
    }
}
