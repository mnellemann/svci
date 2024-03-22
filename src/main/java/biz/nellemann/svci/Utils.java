package biz.nellemann.svci;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    static public Instant parseDateTime(String stringDate) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return instant;
    }

}
