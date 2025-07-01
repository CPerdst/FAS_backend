package com.l1Akr.common.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.l1Akr.common.serializer.LocalDateTimeSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class WsMessage<T> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .create();

    Integer code;

    String message;

    T data;

    public WsMessage(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    static class WsMessageBuilder<T> {
        Integer code;

        String message;

        T data;

        public WsMessageBuilder(Integer code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public WsMessageBuilder<T> setCode(Integer code) {
            this.code = code;
            return this;
        }

        public WsMessageBuilder<T> setMessage(String message) {
            this.message = message;
            return this;
        }

        public WsMessageBuilder<T> setData(T data) {
            this.data = data;
            return this;
        }

        public WsMessage<T> build() {
            return new WsMessage<>(0, message, data);
        }
    }

    public String parse() {
        return gson.toJson(this);
    }

    public static <T> WsMessage<T> stringify(String message, Class<T> clazz) {
        Type type = TypeToken.getParameterized(WsMessage.class, clazz).getType();
        return gson.fromJson(message, type);
    }

}
