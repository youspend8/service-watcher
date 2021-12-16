package io.assist.servicewatcher.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {
    public static final LocalDateTime NOW = LocalDateTime.now(ZoneId.systemDefault());
}
