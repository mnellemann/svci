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


    static MeasurementItem detectMeasurementItem(String name, Number value) {

        String rename = name.substring(0, name.length() - 3);
        MeasurementType type;
        MeasurementUnit unit;

        if(value instanceof Long) {
            type = MeasurementType.COUNTER;
        } else if(value instanceof Double) {
            type = MeasurementType.GAUGE;
        } else {
            type = MeasurementType.INFO;
        }

        if(name.endsWith("_mb")) {
            unit = MeasurementUnit.MB;
        } else if(name.endsWith("_tb")) {
            unit = MeasurementUnit.TB;
        } else if(name.endsWith("_ms")) {
            unit = MeasurementUnit.MS;
        } else if(name.endsWith("_io")) {
            unit = MeasurementUnit.IO;
        } else {
            unit = MeasurementUnit.NONE;
            rename = name;
        }

        return new MeasurementItem(type, unit, rename, value);
    }

}
