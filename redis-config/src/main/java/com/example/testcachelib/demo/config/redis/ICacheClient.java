package com.example.testcachelib.demo.config.redis;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ICacheClient {
    Boolean remove(String key);

    void removeAll(Collection<String> keys);

    String get(String key);

    <T> T get(String key, Class<T> tClass);

    <T> T get(String key, TypeReference<T> typeReference);

    Long increment(String key, Integer amount);

    Long decrement(String key, Integer amount);

    <T> void add(String key, T value);

    boolean set(String key, String value);

    <T> boolean set(String key, T value);

    <T> Boolean replace(String key, T value);

    <T> boolean add(String key, T value, Date expiresAt);

    boolean add(String key, String value, Date expiresAt);

    <T> boolean set(String key, T value, Date expiresAt);

    boolean set(String key, String value, Date expiresAt);

    <T> boolean replace(String key, T value, Date expiresAt);

    <T> boolean add(String key, T value, int expiresIn, TimeUnit timeUnit);

    boolean add(String key, String value, int expiresIn, TimeUnit timeUnit);

    <T> boolean set(String key, T value, int expiresIn, TimeUnit timeUnit);

    boolean set(String key, String value, int expiresIn, TimeUnit timeUnit);

    <T> boolean replace(String key, T value, int expiresIn, TimeUnit timeUnit);

    void flushAll();

    <T> Map<String, T> getAll(Collection<String> keys, Class<T> tClass);

    <T> Map<String, T> getAll(Collection<String> keys, TypeReference<T> typeReference);

    /**
     * 读多个
     *
     * @param keys
     * @return
     */
    Map<String, String> getAll(Collection<String> keys);

    <T> void setAll(Map<String, T> values);

    long time();
}