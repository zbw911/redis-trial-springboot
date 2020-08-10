package com.example.testcachelib.demo.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create by  zhangbaowei on 2018/8/21 12:48.
 */
//@Component
public class RedisClientImpl implements RedisClient, ICacheClient {

    private static final int Success = 1;

    ValueOperations<String, String> redisValue;

    SetOperations<String, String> redisSet;

    HashOperations<String, String, String> redisHash;

    ListOperations<String, String> redisList;

    ZSetOperations<String, String> zSetOperations;
    //    @Autowired
    private RedisTemplate redisTemplateclient;

    public RedisClientImpl(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplateclient = redisTemplateclient(redisConnectionFactory);
        this.redisValue = redisTemplateclient.opsForValue();
        this.redisSet = redisTemplateclient.opsForSet();
        this.redisHash = redisTemplateclient.opsForHash();
        this.redisList = redisTemplateclient.opsForList();
        this.zSetOperations = redisTemplateclient.opsForZSet();
    }

    private static ObjectMapper getMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private static <T> String serializeToString(T value) {
        try {
            return getMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <T> T deserializeFromString(String json, Class<T> tClass) {
        try {
            if (json == null || json.equals("")) {
                return null;
            }
            return getMapper().readValue(json, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <T> T deserializeFromString(String json, TypeReference<T> typeReference) {
        try {
            if (json == null) {
                return null;
            }
            return getMapper().readValue(json, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        if (keys == null || values == null) {
            return null;
        }
        if (keys.size() == 0) {
            return null;
        }
        if (keys.size() != values.size()) {
            throw new RuntimeException("key value 不相等");
        }

        Map<K, V> map = new LinkedHashMap<>(values.size());

        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);
            V value = values.get(i);

            map.put(key, value);
        }
        return map;
    }

    private static <K, V> Map<K, V> zipToMap(Collection<K> keys, Collection<V> values) {

        List<K> listkey = keys.stream().collect(Collectors.toList());
        List<V> listvalue = values.stream().collect(Collectors.toList());

        return zipToMap(listkey, listvalue);
    }

    private static double getLexicalScore(String value) {
        if (value == null || value.equals("")) {
            return 0;
        }

        double lexicalValue = 0;
        if (value.length() >= 1) {
            lexicalValue += value.charAt(0) * (int) Math.pow(256, 3);
        }

        if (value.length() >= 2) {
            lexicalValue += value.charAt(1) * (int) Math.pow(256, 2);
        }

        if (value.length() >= 3) {
            lexicalValue += value.charAt(2) * (int) Math.pow(256, 1);
        }

        if (value.length() >= 4) {
            lexicalValue += value.charAt(3);
        }

        return lexicalValue;
    }

    private static Map<String, Double> convertTypedTupleToMap(Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        Map<String, Double> map = new LinkedHashMap<>();

        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            map.put(typedTuple.getValue(), typedTuple.getScore());
        }
        return map;
    }

    private static String getSearchStart(String start) {

        return start == null
                ? "-"
                : indexOfAny(start, Arrays.asList("[", "(", "-")) != 0
                ? "[" + start
                : start;
    }

    private static String getSearchEnd(String end) {
        return end == null
                ? "+"
                : indexOfAny(end, Arrays.asList("[", "(", "+")) != 0
                ? "[" + end
                : end;
    }

    private static int indexOfAny(String source, List<String> pattern) {
        for (String s : pattern) {
            if (source.indexOf(s) >= 0) {
                return source.indexOf(s);
            }
        }
        return -1;
    }

    private RedisTemplate redisTemplateclient(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //下面这行代码如果会有什么问题呢？
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Override
    public boolean ping() {
        Object execute = redisTemplateclient.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                return connection.ping();
            }
        });

        // redisTemplateclient.getConnectionFactory().getConnection().ping();

        if (execute == null) {
            return false;
        }

        return ((String) execute).equals("PONG");
    }

    @Override
    public Long getExpire(String key, final TimeUnit timeUnit) {
        return redisTemplateclient.getExpire(key, timeUnit);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplateclient.getExpire(key);
    }

    @Override
    public Set<String> getAllKeys() {
        Set<String> keys = redisTemplateclient.keys("*");
        return keys;
    }

    @Override
    public void setAll(Collection<String> keys, Collection<String> values) {

        Map<String, String> map = zipToMap(keys, values);
        if (map == null) {
            return;
        }
        redisValue.multiSet(map);
    }

    @Override
    public void setValues(Map<String, String> map) {
        redisValue.multiSet(map);
    }

    @Override
    public void setValue(String key, String value) {
        redisValue.set(key, value);
    }

    @Override
    public void setValue(String key, String value, int expireIn, TimeUnit timeUnit) {
        redisValue.set(key, value, expireIn, timeUnit);
    }

    @Override
    public Boolean setValueIfNotExists(String key, String value) {
        return redisValue.setIfAbsent(key, value);
    }

    @Override
    public String getValue(String key) {
        return redisValue.get(key);
    }

    @Override
    public String getAndSetValue(String key, String value) {
        return redisValue.getAndSet(key, value);
    }

    @Override
    public List<String> getValues(List<String> keys) {
        return redisValue.multiGet(keys);
    }

    @Override
    public <T> List<T> getValues(List<String> keys, Class<T> tClass) {

        List<String> strings = redisValue.multiGet(keys);

        Stream<T> tStream = strings.stream().filter(x -> x != null && x.length() > 0).map(x -> deserializeFromString(x, tClass));

        return tStream.collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getValuesMap(List<String> keys) {
        List<String> strings = redisValue.multiGet(keys);
        return zipToMap(keys, strings);
    }

    @Override
    public <T> Map<String, T> getValuesMap(List<String> keys, Class<T> tClass) {
        List<String> strings = redisValue.multiGet(keys);
        List<T> valueList = strings.stream().map(x -> deserializeFromString(x, tClass)).collect(Collectors.toList());

        return zipToMap(keys, valueList);
    }

    @Override
    public Integer appendToValue(String key, String value) {
        return redisValue.append(key, value);
    }

    @Override
    public void renameKey(String fromName, String toName) {
        redisTemplateclient.rename(fromName, toName);
    }

    @Override
    public Boolean containsKey(String key) {
        return redisTemplateclient.hasKey(key);
    }

    @Override
    public boolean removeEntry(Collection<String> args) {
        return redisTemplateclient.delete(args) == 1;
    }

    @Override
    public Long incrementValue(String key) {
        return redisValue.increment(key, 1L);
    }

    @Override
    public Set<String> searchKeys(String pattern) {
        Set<String> keys = redisTemplateclient.keys(pattern);

        return keys;
    }

    @Override
    public String type(String key) {
        DataType type = redisTemplateclient.type(key);
        return type.toString();
    }

    @Override
    public RedisKeyType getEntryType(String key) {

        String type = type(key);
        switch (type.toLowerCase()) {
            case "none":
                return RedisKeyType.None;
            case "string":
                return RedisKeyType.String;
            case "set":
                return RedisKeyType.Set;
            case "list":
                return RedisKeyType.List;
            case "zset":
                return RedisKeyType.SortedSet;
            case "hash":
                return RedisKeyType.Hash;
        }
        throw new ArithmeticException("Invalid Type '" + type + "'");
    }

    @Override
    public Long incrementValueBy(String key, int count) {
        return redisValue.increment(key, count);
    }

    @Override
    public Long incrementValueBy(String key, long count) {
        return redisValue.increment(key, count);
    }

    @Override
    public double incrementValueBy(String key, double count) {
        return redisValue.increment(key, count);
    }

    @Override
    public Long decrementValue(String key) {
        return redisValue.increment(key, -1);
    }

    @Override
    public Long decrementValueBy(String key, int count) {
        return redisValue.increment(key, -count);
    }

    @Override
    public String getRandomKey() {
        return redisValue.getOperations().randomKey();
    }

    @Override
    public Boolean expireEntryIn(String key, long expireIn, TimeUnit timeUnit) {

        return redisTemplateclient.expire(key, expireIn, timeUnit);
    }

    @Override
    public Boolean expireEntryIn(Collection<String> keys, long expireIn, TimeUnit timeUnit) {
        long rawTimeout = TimeoutUtils.toMillis(expireIn, timeUnit);
        redisTemplateclient.executePipelined((RedisCallback) connection -> {
            for (String value : keys) {
                byte[] key = redisTemplateclient.getKeySerializer().serialize(value);
                connection.pExpire(key, rawTimeout);
            }
            return null;
        });
        return true;
    }

    @Override
    public Boolean expireEntryAt(String key, Date expireAt) {
        return redisTemplateclient.expireAt(key, expireAt);
    }

    @Override
    public Boolean expireEntryAt(Collection<String> keys, Date expireAt) {
        long rawTimeout = expireAt.getTime();
        redisTemplateclient.executePipelined((RedisCallback) connection -> {
            for (String value : keys) {
                byte[] key = redisTemplateclient.getKeySerializer().serialize(value);
                connection.pExpireAt(key, rawTimeout);
            }
            return null;
        });
        return true;
    }

    @Override
    public Iterable<AbstractMap.SimpleEntry<String, Double>> scanAllSortedSetItems(String setId, String pattern, int pageSize) {

        Cursor<ZSetOperations.TypedTuple<String>> scan = zSetOperations.scan(setId, ScanOptions.scanOptions().count(pageSize).match(pattern).build());
        ArrayList<AbstractMap.SimpleEntry<String, Double>> simpleEntries = new ArrayList<>();

        while (scan.hasNext()) {
            ZSetOperations.TypedTuple<String> next = scan.next();
            simpleEntries.add(new AbstractMap.SimpleEntry<>(next.getValue(), next.getScore()));
        }

        return simpleEntries;
    }

    @Override
    public Iterable<AbstractMap.SimpleEntry<String, String>> scanAllHashEntries(String hashId, String pattern, int pageSize) {

        Cursor<Map.Entry<String, String>> scan = redisHash.scan(hashId, ScanOptions.scanOptions().count(pageSize).match(pattern).build());

        ArrayList<AbstractMap.SimpleEntry<String, String>> simpleEntries = new ArrayList<>();

        while (scan.hasNext()) {
            Map.Entry<String, String> next = scan.next();
            simpleEntries.add(new AbstractMap.SimpleEntry(next.getKey(), next.getValue()));
        }

        return simpleEntries;
    }

    @Override
    public void addRangeToSet(String setId, List<String> items) {

        redisSet.getOperations().executePipelined((RedisCallback) connnetct -> {
            for (String item : items) {
                connnetct.sAdd(redisTemplateclient.getKeySerializer().serialize(setId),
                        redisTemplateclient.getKeySerializer().serialize(item));
            }
            return null;
        });
    }

    @Override
    public void removeItemFromSet(String setId, String item) {
        redisSet.remove(setId, item);
    }

    @Override
    public Set<String> getAllItemsFromSet(String setId) {
        return redisSet.members(setId);
    }

    @Override
    public void addItemToSet(String setId, String item) {
        redisSet.add(setId, item);
    }

    @Override
    public void moveBetweenSets(String fromSetId, String toSetId, String item) {
        redisSet.move(fromSetId, item, toSetId);
    }

    @Override
    public String popItemFromSet(String setId) {
        return redisSet.pop(setId);
    }

    @Override
    public List<String> popItemsFromSet(String setId, int count) {
        return redisSet.pop(setId, count);
    }

    @Override
    public Boolean isSetContainsItem(String setId, String item) {
        return redisSet.isMember(setId, item);
    }

    @Override
    public void storeIntersectFromSets(String intoSetId, Collection<String> setIds) {
        redisSet.intersectAndStore(setIds.stream().findFirst().get(), setIds.stream().skip(0).collect(Collectors.toList()), intoSetId);
    }

    @Override
    public Long getSetCount(String setId) {
        return redisSet.size(setId);
    }

    @Override
    public void storeUnionFromSets(String intoSetId, Collection<String> setIds) {
        redisSet.union(intoSetId, (setIds));
    }

    @Override
    public Set<String> getIntersectFromSets(Collection<String> setIds) {

        return redisSet.intersect(setIds.stream().findFirst().get(), setIds.stream().skip(1).collect(Collectors.toList()));
    }

    @Override
    public void storeDifferencesFromSet(String intoSetId, String fromSetId, Collection<String> withSetIds) {
        redisSet.differenceAndStore(fromSetId, (withSetIds), intoSetId);
    }

    @Override
    public Set<String> getUnionFromSets(Collection<String> setIds) {
        return redisSet.union(setIds.stream().findFirst().get(), (setIds).stream().skip(1).collect(Collectors.toList()));
    }

    @Override
    public Set<String> getDifferencesFromSet(String fromSetId, Collection<String> withSetIds) {
        return redisSet.difference(fromSetId, (withSetIds));
    }

    @Override
    public void addItemToList(String listId, String value) {
        redisList.rightPush(listId, value);
    }

    @Override
    public void addRangeToList(String listId, List<String> values) {
        redisList.getOperations().executePipelined((RedisCallback) connection -> {
            byte[] serialize = redisTemplateclient.getKeySerializer().serialize(listId);
            for (int i = 0; i < values.size(); i++) {
                connection.rPush(serialize, redisTemplateclient.getValueSerializer().serialize(values.get(i)));
            }
            return null;
        });

//        for (int i = 0; i < values.size(); i++) {
//            redisTemplateclient.opsForList().rightPush(listId, values.get(i));
//        }

    }

    @Override
    public String getRandomItemFromSet(String setId) {
        return redisSet.randomMember(setId);
    }

    @Override
    public List<String> getAllItemsFromList(String listId) {
        return redisList.range(listId, 0, -1);
    }

    @Override
    public List<String> getRangeFromList(String listId, int startingFrom, int endingAt) {
        return redisList.range(listId, startingFrom, endingAt);
    }

    @Override
    public void prependItemToList(String listId, String value) {
        redisList.leftPush(listId, value);
    }

    @Override
    public void prependRangeToList(String listId, List<String> values) {
        redisList.getOperations().executePipelined((RedisCallback) connection -> {
            byte[] serialize = redisTemplateclient.getKeySerializer().serialize(listId);
            for (int i = 0; i < values.size(); i++) {
                connection.lPush(serialize, redisTemplateclient.getValueSerializer().serialize(values.get(i)));
            }
            return null;
        });
    }

    @Override
    public void removeAllFromList(String listId) {
        redisList.trim(listId, -1, 0);
    }

    @Override
    public Set<String> getRangeFromSortedList(String listId, int startingFrom, int endingAt) {
        return zSetOperations.range(listId, startingFrom, endingAt);
    }

    @Override
    public String removeStartFromList(String listId) {
        return redisList.leftPop(listId);
    }

    @Override
    public String blockingRemoveStartFromList(String listId, int timeOut, TimeUnit timeUnit) {
        return redisList.leftPop(listId, timeOut, timeUnit);
    }

    @Override
    public void trimList(String listId, int keepStartingFrom, int keepEndingAt) {
        redisList.trim(listId, keepStartingFrom, keepEndingAt);
    }

    @Override
    public String removeEndFromList(String listId) {
        return redisList.rightPop(listId);
    }

    @Override
    public Long removeItemFromList(String listId, String value) {
        return redisList.remove(listId, 0, value);
    }

    @Override
    public Long removeItemFromList(String listId, String value, int noOfMatches) {
        return redisList.remove(listId, noOfMatches, value);
    }

    @Override
    public void setItemInList(String listId, int listIndex, String value) {
        redisList.set(listId, listIndex, value);
    }

    @Override
    public void enqueueItemOnList(String listId, String value) {
        redisList.leftPush(listId, value);
    }

    @Override
    public Long getListCount(String listId) {
        return redisList.size(listId);
    }

    @Override
    public String getItemFromList(String listId, int listIndex) {
        return redisList.index(listId, listIndex);
    }

    @Override
    public String dequeueItemFromList(String listId) {
        return redisList.rightPop(listId);
    }

    @Override
    public String blockingDequeueItemFromList(String listId, int timeOut, TimeUnit timeUnit) {
        return redisList.rightPop(listId, timeOut, timeUnit);
    }

    @Override
    public void pushItemToList(String listId, String value) {
        redisList.leftPush(listId, value);
    }

    @Override
    public String popItemFromList(String listId) {
        return redisList.rightPop(listId);
    }

    @Override
    public String blockingPopItemFromList(String listId, int timeOut, TimeUnit timeUnit) {
        return redisList.rightPop(listId, timeOut, timeUnit);
    }

    @Override
    public String popAndPushItemBetweenLists(String fromListId, String toListId) {
        return redisList.rightPopAndLeftPush(fromListId, toListId);
    }

    @Override
    public String blockingPopAndPushItemBetweenLists(String fromListId, String toListId, int timeOut, TimeUnit timeUnit) {
        return redisList.rightPopAndLeftPush(fromListId, toListId, timeOut, timeUnit);
    }

    @Override
    public Boolean addItemToSortedSet(String setId, String value) {
        return zSetOperations.add(setId, value, getLexicalScore(value));
    }

    @Override
    public Boolean addItemToSortedSet(String setId, String value, double score) {
        return zSetOperations.add(setId, value, score);
    }

    @Override
    public Boolean addRangeToSortedSet(String setId, List<String> values, double score) {
        zSetOperations.getOperations().executePipelined((RedisCallback) connection -> {
            for (String value : values) {
                connection.zAdd(redisTemplateclient.getKeySerializer().serialize(setId), score, redisTemplateclient.getValueSerializer().serialize(value));
            }
            return null;
        });
        return true;
    }

    @Override
    public Boolean addRangeToSortedSet(String setId, List<String> values, long score) {

        return addRangeToSortedSet(setId, values, (double) score);
//        zSetOperations.getOperations().executePipelined((RedisCallback) connection -> {
//            for (String value : values) {
//                connection.zAdd(redisTemplateclient.getKeySerializer().serialize(setId), score, redisTemplateclient.getValueSerializer().serialize(value));
//            }
//            return null;
//        });
//        return true;
    }

    @Override
    public Boolean removeItemFromSortedSet(String setId, String value) {

        Long remove = zSetOperations.remove(setId, value);
        if (remove == null) {

        }
        return remove == Success;
    }

    @Override
    public Long removeItemsFromSortedSet(String setId, List<String> values) {
        return zSetOperations.remove(setId, values.toArray());
    }

    @Override
    public String popItemWithLowestScoreFromSortedSet(String setId) {

        Set<String> range = zSetOperations.range(setId, 0, 1);
        if (range == null || range.size() == 0) {
            return null;
        }

        zSetOperations.remove(setId, range.toArray()[0].toString());
        return range.toArray()[0].toString();
    }

    @Override
    public String popItemWithHighestScoreFromSortedSet(String setId) {
        Set<String> range = zSetOperations.reverseRange(setId, 0, 1);
        if (range == null || range.size() == 0) {
            return null;
        }

        zSetOperations.remove(setId, range.toArray()[0].toString());
        return range.toArray()[0].toString();
    }

    @Override
    public boolean sortedSetContainsItem(String setId, String value) {
        Long rank = zSetOperations.rank(setId, value);
        if (rank == null) {
            return false;
        }
        return rank != -1;
    }

    @Override
    public Double incrementItemInSortedSet(String setId, String value, double incrementBy) {
        return zSetOperations.incrementScore(setId, value, incrementBy);
    }

    @Override
    public Double incrementItemInSortedSet(String setId, String value, long incrementBy) {
        return zSetOperations.incrementScore(setId, value, incrementBy);
    }

    @Override
    public Long getItemIndexInSortedSet(String setId, String value) {
        return zSetOperations.rank(setId, value);
    }

    @Override
    public Long getItemIndexInSortedSetDesc(String setId, String value) {
        return zSetOperations.reverseRank(setId, value);
    }

    @Override
    public Set<String> getAllItemsFromSortedSet(String setId) {
        return zSetOperations.range(setId, 0, -1);
    }

    @Override
    public Set<String> getAllItemsFromSortedSetDesc(String setId) {
        return zSetOperations.reverseRange(setId, 0, -1);
    }

    @Override
    public Set<String> getRangeFromSortedSet(String setId, int fromRank, int toRank) {
        return zSetOperations.range(setId, fromRank, toRank);
    }

    @Override
    public Set<String> getRangeFromSortedSetDesc(String setId, int fromRank, int toRank) {
        return zSetOperations.reverseRange(setId, fromRank, toRank);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSet(String setId, int fromRank, int toRank) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeWithScores(setId, fromRank, toRank);

        Map<String, Double> map = convertTypedTupleToMap(typedTuples);
        return map;
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetDesc(String setId, int fromRank, int toRank) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(setId, fromRank, toRank);

        Map<String, Double> map = convertTypedTupleToMap(typedTuples);
        return map;
    }

    @Override
    public Set<String> getRangeFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);
        return zSetOperations.rangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Set<String> getRangeFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore, int skip, int take) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        return zSetOperations.rangeByScore(setId, fromScore, toScore, skip, take);
    }

    @Override
    public Set<String> getRangeFromSortedSetByLowestScore(String setId, double fromScore, double toScore) {
        return zSetOperations.rangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Set<String> getRangeFromSortedSetByLowestScore(String setId, long fromScore, long toScore) {
        return zSetOperations.rangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Set<String> getRangeFromSortedSetByLowestScore(String setId, double fromScore, double toScore, int skip, int take) {
        return zSetOperations.rangeByScore(setId, fromScore, toScore, skip, take);
    }

    @Override
    public Set<String> getRangeFromSortedSetByLowestScore(String setId, long fromScore, long toScore, int skip, int take) {
        return zSetOperations.rangeByScore(setId, fromScore, toScore, skip, take);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(setId, fromScore, toScore);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore, int skip, int take) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(setId, fromScore, toScore, skip, take);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, double fromScore, double toScore) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(setId, fromScore, toScore);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, long fromScore, long toScore) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(setId, fromScore, toScore);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, double fromScore, double toScore, int skip, int take) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(setId, fromScore, toScore, skip, take);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, long fromScore, long toScore, int skip, int take) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(setId, fromScore, toScore, skip, take);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Set<String> getRangeFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        return zSetOperations.reverseRangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Set<String> getRangeFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore, int skip, int take) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        return zSetOperations.reverseRangeByScore(setId, fromScore, toScore, skip, take);
    }

    @Override
    public Set<String> getRangeFromSortedSetByHighestScore(String setId, double fromScore, double toScore) {
        return zSetOperations.reverseRangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Set<String> getRangeFromSortedSetByHighestScore(String setId, long fromScore, long toScore) {
        return zSetOperations.reverseRangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Set<String> getRangeFromSortedSetByHighestScore(String setId, double fromScore, double toScore, int skip, int take) {
        return zSetOperations.reverseRangeByScore(setId, fromScore, toScore, skip, take);
    }

    @Override
    public Set<String> getRangeFromSortedSetByHighestScore(String setId, long fromScore, long toScore, int skip, int take) {
        return zSetOperations.reverseRangeByScore(setId, fromScore, toScore, skip, take);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeByScoreWithScores(setId, fromScore, toScore);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore, int skip, int take) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeByScoreWithScores(setId, fromScore, toScore, skip, take);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, double fromScore, double toScore) {

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeByScoreWithScores(setId, fromScore, toScore);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, long fromScore, long toScore) {

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeByScoreWithScores(setId, fromScore, toScore);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, double fromScore, double toScore, int skip, int take) {

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeByScoreWithScores(setId, fromScore, toScore, skip, take);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, long fromScore, long toScore, int skip, int take) {

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeByScoreWithScores(setId, fromScore, toScore, skip, take);

        return convertTypedTupleToMap(typedTuples);
    }

    @Override
    public Long removeRangeFromSortedSet(String setId, int minRank, int maxRank) {
        return zSetOperations.removeRange(setId, minRank, maxRank);
    }

    @Override
    public Long removeRangeFromSortedSetByScore(String setId, double fromScore, double toScore) {
        return zSetOperations.removeRangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Long removeRangeFromSortedSetByScore(String setId, long fromScore, long toScore) {
        return zSetOperations.removeRangeByScore(setId, fromScore, toScore);
    }

    @Override
    public Long getSortedSetCount(String setId) {
        return zSetOperations.zCard(setId);
    }

    @Override
    public Long getSortedSetCount(String setId, String fromStringScore, String toStringScore) {
        double fromScore = getLexicalScore(fromStringScore);
        double toScore = getLexicalScore(toStringScore);
        return zSetOperations.count(setId, fromScore, toScore);
    }

    @Override
    public Long getSortedSetCount(String setId, long fromScore, long toScore) {
        return zSetOperations.count(setId, fromScore, toScore);
    }

    @Override
    public Long getSortedSetCount(String setId, double fromScore, double toScore) {
        return zSetOperations.count(setId, fromScore, toScore);
    }

    @Override
    public Double getItemScoreInSortedSet(String setId, String value) {
        return zSetOperations.score(setId, value);
    }

    @Override
    public Long storeIntersectFromSortedSets(String intoSetId, Collection<String> setIds) {
        return zSetOperations.intersectAndStore(setIds.stream().findFirst().get(), setIds.stream().skip(1).collect(Collectors.toList()), intoSetId);
    }

    @Override
    public Long storeUnionFromSortedSets(String intoSetId, Collection<String> setIds) {
        return zSetOperations.unionAndStore(setIds.stream().findFirst().get(), setIds.stream().skip(1).collect(Collectors.toList()), intoSetId);
    }

    @Override
    public Set<String> searchSortedSet(String setId, String start, String end, int skip, int take) {
        start = getSearchStart(start);
        end = getSearchEnd(end);
//
//        var ret = base.ZRangeByLex(setId, start, end, skip, take);
//        return ret.ToStringList();
        RedisZSetCommands.Range range = RedisZSetCommands.Range.range().gte(start).lt(end);
        RedisZSetCommands.Limit limit = RedisZSetCommands.Limit.limit().offset(skip).count(take);
        return zSetOperations.rangeByLex(setId, range, limit);
    }

    @Override
    public Boolean hashContainsEntry(String hashId, String key) {
        return redisHash.hasKey(hashId, key);
    }

    @Override
    public void setEntryInHash(String hashId, String key, String value) {
        redisHash.put(hashId, key, value);
    }

    @Override
    public Boolean setEntryInHashIfNotExists(String hashId, String key, String value) {
        return redisHash.putIfAbsent(hashId, key, value);
    }

    @Override
    public void setRangeInHash(String hashId, Map<String, String> keyValuePairs) {
        redisHash.putAll(hashId, keyValuePairs);
    }

//    @Override
//    public void setRangeInHash(String hashId, Map<String, Object> keyValuePairs) {
//
//        redisHash.putAll(hashId, keyValuePairs);
//    }

    @Override
    public Long incrementValueInHash(String hashId, String key, int incrementBy) {
        return redisHash.increment(hashId, key, incrementBy);
    }

    @Override
    public Double incrementValueInHash(String hashId, String key, double incrementBy) {
        return redisHash.increment(hashId, key, incrementBy);
    }

    @Override
    public String getValueFromHash(String hashId, String key) {
        return redisHash.get(hashId, key);
    }

    @Override
    public List<String> getValuesFromHash(String hashId, Collection<String> keys) {
        return redisHash.multiGet(hashId, (keys));
    }

    @Override
    public boolean removeEntryFromHash(String hashId, String key) {
        return redisHash.delete(hashId, key) == 1;
    }

    @Override
    public boolean removeEntryFromHash(String hashId, List<String> keys) {
        return redisHash.delete(hashId, keys.toArray()) == 1;
    }

    @Override
    public Long getHashCount(String hashId) {
        return redisHash.size(hashId);
    }

    @Override
    public Set<String> getHashKeys(String hashId) {
        return redisHash.keys(hashId);
    }

    @Override
    public List<String> getHashValues(String hashId) {
        return redisHash.values(hashId);
    }

    @Override
    public Map<String, String> getHashEntries(String hashId) {
        return redisHash.entries(hashId);
    }

//

    @Override
    public Boolean remove(String key) {
        return redisTemplateclient.delete(key);
    }

    @Override
    public void removeAll(Collection<String> keys) {
        redisTemplateclient.delete(keys);
    }

    @Override
    public String get(String key) {
        String s = redisValue.get(key);
        if (s == null) {
            return null;
        }
        return s;
    }

    @Override
    public <T> T get(String key, Class<T> tClass) {
        String s = redisValue.get(key);
        if (s == null) {
            return null;
        }
        return deserializeFromString(s, tClass);
    }

    @Override
    public <T> T get(String key, TypeReference<T> typeReference) {
        String s = redisValue.get(key);
        if (s == null) {
            return null;
        }
        return deserializeFromString(s, typeReference);
    }

    @Override
    public Long increment(String key, Integer amount) {
        return redisValue.increment(key, amount);
    }

    @Override
    public Long decrement(String key, Integer amount) {
        return redisValue.increment(key, -amount);
    }

    @Override
    public <T> void add(String key, T value) {
        redisValue.set(key, serializeToString(value));
    }

    @Override
    public <T> boolean add(String key, T value, Date expiresAt) {
        long millTime = expiresAt.getTime() - System.currentTimeMillis();
        redisValue.set(key, serializeToString(value), millTime, TimeUnit.MILLISECONDS);
        return true;
    }

    public <T> void add(String key, T value, long time, TimeUnit timeUnit) {
        redisValue.set(key, serializeToString(value), time, timeUnit);
    }

    @Override
    public boolean set(String key, String value) {
        redisValue.set(key, value);
        return true;
    }

    @Override
    public <T> boolean set(String key, T value) {
        redisValue.set(key, serializeToString(value));
        return true;
    }

    @Override
    public <T> Boolean replace(String key, T value) {
        return redisValue.setIfAbsent(key, serializeToString(value));
    }

    @Override
    public boolean add(String key, String value, Date expiresAt) {
        long expiresTime = expiresAt.getTime() - System.currentTimeMillis();
        redisValue.set(key, value, expiresTime, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public <T> boolean set(String key, T value, Date expiresAt) {
        return add(key, value, expiresAt);
    }

    @Override
    public boolean set(String key, String value, Date expiresAt) {
        return add(key, value, expiresAt);
    }

    @Override
    public <T> boolean replace(String key, T value, Date expiresAt) {
        replace(key, value);
        expireEntryAt(key, expiresAt);
        return true;
    }

    @Override
    public <T> boolean add(String key, T value, int expiresIn, TimeUnit timeUnit) {
        this.add(key, serializeToString(value), expiresIn, timeUnit);
        return false;
    }

    @Override
    public boolean add(String key, String value, int expiresIn, TimeUnit timeUnit) {
        redisValue.set(key, value, expiresIn, timeUnit);
        return true;
    }

    @Override
    public <T> boolean set(String key, T value, int expiresIn, TimeUnit timeUnit) {
        add(key, serializeToString(value), expiresIn, timeUnit);
        return true;
    }

    @Override
    public boolean set(String key, String value, int expiresIn, TimeUnit timeUnit) {
        this.add(key, value, expiresIn, timeUnit);
        return true;
    }

    @Override
    public <T> boolean replace(String key, T value, int expiresIn, TimeUnit timeUnit) {
        replace(key, value);
        expireEntryIn(key, expiresIn, timeUnit);
        return true;
    }

    @Override
    public void flushAll() {
        throw new NotImplementedException();
    }

    @Override
    public <T> Map<String, T> getAll(Collection<String> keys, Class<T> tClass) {
        List<String> strings = redisValue.multiGet(keys);

        List<T> list = new ArrayList<>();

        for (String string : strings) {
            list.add(deserializeFromString(string, tClass));
        }

        return zipToMap(keys, list);
    }

    @Override
    public <T> Map<String, T> getAll(Collection<String> keys, TypeReference<T> typeReference) {
        List<String> listKey = keys.stream().collect(Collectors.toList());
        List<String> listValue = redisValue.multiGet(keys);

        List<T> list = new ArrayList<>();

        for (String string : listValue) {
            if (string != null) {
                list.add(deserializeFromString(string, typeReference));
            } else {
                list.add(null);
            }
        }
//        return list;
        return zipToMap(keys, list);
    }

    @Override
    public Map<String, String> getAll(Collection<String> keys) {
        List<String> strings = redisValue.multiGet(keys);

        return zipToMap(keys, strings);
    }

    @Override
    public <T> void setAll(Map<String, T> values) {

//        List<String> list = new ArrayList<>();
//        for (T t : values.values()) {
//            list.add(serializeToString(t));
//        }
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, T> stringTEntry : values.entrySet()) {
            T value = stringTEntry.getValue();
            if (value != null && value.getClass().equals(String.class)) {
                map.put(stringTEntry.getKey(), (String) value);
            } else {
                map.put(stringTEntry.getKey(), serializeToString(value));
            }
        }

//        Map<String, String> map = zipToMap(values.keySet(), list);

        redisValue.multiSet(map);
    }

    @Override
    public long time() {
        return (Long) redisTemplateclient.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.time();
            }
        });
    }
}
