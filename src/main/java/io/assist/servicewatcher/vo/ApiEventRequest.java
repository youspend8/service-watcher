package io.assist.servicewatcher.vo;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiEventRequest {
    private String method;
    private String uri;
    private String contentType;
    private String userAgent;
    private String referrer;
}
