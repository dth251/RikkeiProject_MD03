package ra.edu.util;

import lombok.extern.slf4j.Slf4j;

import java.util.TimeZone;

@Slf4j
public class TimezoneUtil {

    private static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";

    public static void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
        log.info("Application timezone has been set to: {}", DEFAULT_TIMEZONE);
    }
}
