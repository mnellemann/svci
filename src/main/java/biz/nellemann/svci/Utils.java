package biz.nellemann.svci;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    static public Instant parseDateTime(String stringDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return parseDateTime(stringDate, zoneId);
    }


    static public Instant parseDateTime(String stringDate, ZoneId zoneId) {

        if(stringDate == null) {
            System.err.println("parseDateTime() - null input");
            return Instant.now();
        }

        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);

        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
    }

}
