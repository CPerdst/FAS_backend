package com.l1Akr.common.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        List<Integer> list = new ArrayList<>();
        Integer year = src.getYear();
        Integer month = src.getMonthValue();
        Integer day = src.getDayOfMonth();
        Integer hour = src.getHour();
        Integer minute = src.getMinute();
        Integer second = src.getSecond();
        list.add(year);
        list.add(month);
        list.add(day);
        list.add(hour);
        list.add(minute);
        list.add(second);
        return context.serialize(list);
    }

}
