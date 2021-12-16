package io.assist.servicewatcher.vo;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiEventResponse {
    private String contentType;
    private Long bytes;
    private HttpStatus httpStatus;
}
