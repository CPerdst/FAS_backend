package com.l1Akr.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CommomStaticFunc {

    public static LocalDate LocalDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
