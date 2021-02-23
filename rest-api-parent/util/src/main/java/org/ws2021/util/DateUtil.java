package org.ws2021.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date toDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }
    
    public static Date now() {
        return toDate(LocalDateTime.now());
    }
    
    public static Date nowPlusHours(int hours) {
        return toDate(LocalDateTime.now().plusHours(hours));
    }
    
    public static Date plusHours(Date source, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source == null ? new Date() : source);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
    
    public static java.sql.Date sqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }
    
    public static Timestamp sqlTimestamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }
}
