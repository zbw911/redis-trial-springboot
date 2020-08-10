package com.example.testcachelib.demo.config.redis;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Create by  zhangbaowei on 2018/8/21 10:51.
 */
public interface RedisClient extends ICacheClient {
//    Long Db { get; set; }
//
//    Long DbSize { get; }
//
//    Map<String, String> Info { get; }
//
//    Date GetServerTime();
//
//    Date LastSave { get; }
//
//    String Host { get; }
//
//    int Port { get; }
//
//    int ConnectTimeout { get; set; }
//
//    int retryTimeout { get; set; }
//
//    int retryCount { get; set; }
//
//    int SendTimeout { get; set; }
//
//    String Password { get; set; }
//
//    boolean HadExceptions { get; }

    boolean ping();

    Long getExpire(String key, final TimeUnit timeUnit);

    Long getExpire(String key);
//
//    String Echo(String text);
//
//    redisText Custom(params object[] cmdWithArgs);
//
//    void Save();
//
//    void SaveAsync();
//
//    void Shutdown();
//
//    void ShutdownNoSave();
//
//    void rewriteAppendOnlyFileAsync();
//
//    void FlushDb();
//
//    redisServerRole GetServerRole();
//
//    redisText GetServerRoleInfo();
//
//    String GetConfig(String item);
//
//    void setConfig(String item, String value);
//
//    void SaveConfig();
//
//    void resetInfoStats();
//
//    String GetClient();
//
//    void setClient(String name);
//
//    void KillClient(String address);
//
//    Long KillClients(String fromAddress = null, String withId = null, redisClientType? ofType = null, boolean? skipMe = null);

//    List<Map<String, String>> GetClientsInfo();

//    void PauseAllClients(TimeSpan duration);

//    String this[String key] { get; set; }

    Set<String> getAllKeys();

//    String UrnKey<T>(T value);
//
//    String UrnKey<T>(object id);
//
//    String UrnKey(Type type, object id);

    void setAll(Collection<String> keys, Collection<String> values);

//    void setAll(Map<String, String> map);

    void setValues(Map<String, String> map);

    void setValue(String key, String value);

    void setValue(String key, String value, int expireIn, TimeUnit timeUnit);

    Boolean setValueIfNotExists(String key, String value);

//    boolean setValueIfExists(String key, String value);

    String getValue(String key);

    String getAndSetValue(String key, String value);

    List<String> getValues(List<String> keys);

    <T> List<T> getValues(List<String> keys, Class<T> tClass);

    Map<String, String> getValuesMap(List<String> keys);

    <T> Map<String, T> getValuesMap(List<String> keys, Class<T> tClass);

    Integer appendToValue(String key, String value);

    void renameKey(String fromName, String toName);

//    <T> T getFromHash(Object id, Class<T> tClass);

    //    <T> void storeAsHash(T entity);
//
//    Object storeObject(Object entity);
//
    Boolean containsKey(String key);

    default boolean removeEntry(String... args) {
        return removeEntry(Arrays.asList(args));
    }

    boolean removeEntry(Collection<String> args);

    Long incrementValue(String key);

    Long incrementValueBy(String key, int count);

    Long incrementValueBy(String key, long count);

    double incrementValueBy(String key, double count);

    Long decrementValue(String key);

    Long decrementValueBy(String key, int count);

    Set<String> searchKeys(String pattern);

    String type(String key);

    RedisKeyType getEntryType(String key);

//    Long getStringCount(String key);

    String getRandomKey();

    Boolean expireEntryIn(String key, long expireIn, TimeUnit timeUnit);

    Boolean expireEntryIn(Collection<String> keys, long expireIn, TimeUnit timeUnit);

    Boolean expireEntryAt(String key, Date expireAt);

    Boolean expireEntryAt(Collection<String> keys, Date expireAt);
//    List<String> getSortedEntryValues(String key, int startingFrom, int endingAt);

    //    void WriteAll<TEntity>(Iterable<TEntity> entities);
//
//    Iterable<String> scanAllKeys(String pattern = null, int pageSize = 1000);
//
//    Iterable<String> scanAllSetItems(String setId, String pattern = null, int pageSize = 1000);
    default Iterable<AbstractMap.SimpleEntry<String, Double>> scanAllSortedSetItems(String setId) {
        return scanAllSortedSetItems(setId, null, 1000);
    }

    default Iterable<AbstractMap.SimpleEntry<String, Double>> scanAllSortedSetItems(String setId, String pattern) {
        return scanAllSortedSetItems(setId, pattern, 1000);
    }

    Iterable<AbstractMap.SimpleEntry<String, Double>> scanAllSortedSetItems(String setId, String pattern, int pageSize);

    default Iterable<AbstractMap.SimpleEntry<String, String>> scanAllHashEntries(String hashId) {
        return scanAllHashEntries(hashId, null, 1000);
    }

    default Iterable<AbstractMap.SimpleEntry<String, String>> scanAllHashEntries(String hashId, String pattern) {
        return scanAllHashEntries(hashId, pattern, 1000);
    }

    Iterable<AbstractMap.SimpleEntry<String, String>> scanAllHashEntries(String hashId, String pattern, int pageSize);

//    boolean addToHyperLog(String key, String... elements);
//
//    Long CountHyperLog(String key);
//
//    void MergeHyperLogs(String toKey, String... fromKeys);

    //    Long addGeoMember(String key, double longitude, double latitude, String member);
//
//    Long addGeoMembers(String key, params redisGeo[] geoPoints);
//    default double CalculateDistanceBetweenGeoMembers(String key, String fromMember, String toMember) {
//        return CalculateDistanceBetweenGeoMembers(key, fromMember, toMember, null);
//    }
//
//    double CalculateDistanceBetweenGeoMembers(String key, String fromMember, String toMember, String unit);

//    String[] getGeohashes(String key, String... members);
//
//    List<RedisGeo> getGeoCoordinates(String key, String... members);

//    String[] FindGeoMembersInRadius(String key, double longitude, double latitude, double radius, String unit);
//
//    List<RedisGeoResult> FindGeoResultsInRadius(String key, double longitude, double latitude, double radius, String unit, int count = null, boolean? sortByNearest = null);
//
//    String[] FindGeoMembersInRadius(String key, String member, double radius, String unit);
//
//    List<RedisGeoResult> FindGeoResultsInRadius(String key, String member, double radius, String unit, int count = null, boolean? sortByNearest = null);

//    /// <summary>Returns a high-level typed client API</summary>
//    /// <typeparam name="T"></typeparam>
//    IRedisTypedClient<T> As<T>();
//
//    IHasNamed<IRedisList> Lists { get; set; }
//
//    IHasNamed<IRedisSet> sets { get; set; }
//
//    IHasNamed<IRedisSortedSet> SortedSets { get; set; }
//
//    IHasNamed<IRedisHash> Hashes { get; set; }

//    IRedisTransaction CreateTransaction();
//
//    IRedisPipeline CreatePipeline();
//
//    IDisposable AcquireLock(String key);
//
//    IDisposable AcquireLock(String key, TimeSpan timeOut);
//
//    void Watch(String... keys);
//
//    void UnWatch();
//
//    IRedisSubscription CreateSubscription();

//    Long publishMessage(String toChannel, String message);

    Set<String> getAllItemsFromSet(String setId);

    void addItemToSet(String setId, String item);

    void addRangeToSet(String setId, List<String> items);

    void removeItemFromSet(String setId, String item);

    String popItemFromSet(String setId);

    List<String> popItemsFromSet(String setId, int count);

    void moveBetweenSets(String fromSetId, String toSetId, String item);

    Long getSetCount(String setId);

    Boolean isSetContainsItem(String setId, String item);

    default Set<String> getIntersectFromSets(String... setIds) {
        return getIntersectFromSets(Arrays.asList(setIds));
    }

    Set<String> getIntersectFromSets(Collection<String> setIds);

    void storeIntersectFromSets(String intoSetId, Collection<String> setIds);

    default void storeIntersectFromSets(String intoSetId, String... setIds) {
        storeIntersectFromSets(intoSetId, Arrays.asList(setIds));
    }

    default Set<String> getUnionFromSets(String... setIds) {
        return getUnionFromSets(Arrays.asList(setIds));
    }

    Set<String> getUnionFromSets(Collection<String> setIds);

    default void storeUnionFromSets(String intoSetId, String... setIds) {
        storeUnionFromSets(intoSetId, Arrays.asList(setIds));
    }

    void storeUnionFromSets(String intoSetId, Collection<String> setIds);

    default Set<String> getDifferencesFromSet(String fromSetId, String... withSetIds) {
        return getDifferencesFromSet(fromSetId, Arrays.asList(withSetIds));
    }

    Set<String> getDifferencesFromSet(String fromSetId, Collection<String> withSetIds);

    default void storeDifferencesFromSet(String intoSetId, String fromSetId, String... withSetIds) {
        storeDifferencesFromSet(intoSetId, fromSetId, Arrays.asList(withSetIds));
    }

    void storeDifferencesFromSet(String intoSetId, String fromSetId, Collection<String> withSetIds);

    String getRandomItemFromSet(String setId);

    List<String> getAllItemsFromList(String listId);

    List<String> getRangeFromList(String listId, int startingFrom, int endingAt);

    Set<String> getRangeFromSortedList(String listId, int startingFrom, int endingAt);

//    List<String> getSortedItemsFromList(String listId, SortOptions sortOptions);

    void addItemToList(String listId, String value);

    void addRangeToList(String listId, List<String> values);

    void prependItemToList(String listId, String value);

    void prependRangeToList(String listId, List<String> values);

    void removeAllFromList(String listId);

    String removeStartFromList(String listId);

    String blockingRemoveStartFromList(String listId, int timeOut, TimeUnit timeUnit);

//    ItemRef blockingRemoveStartFromLists(String[] listIds, int timeOut, TimeUnit timeUnit);

    String removeEndFromList(String listId);

    void trimList(String listId, int keepStartingFrom, int keepEndingAt);

    Long removeItemFromList(String listId, String value);

    Long removeItemFromList(String listId, String value, int noOfMatches);

    Long getListCount(String listId);

    String getItemFromList(String listId, int listIndex);

    void setItemInList(String listId, int listIndex, String value);

    void enqueueItemOnList(String listId, String value);

    String dequeueItemFromList(String listId);

    String blockingDequeueItemFromList(String listId, int timeOut, TimeUnit timeUnit);

//    ItemRef blockingDequeueItemFromLists(String[] listIds, int timeOut, TimeUnit timeUnit);

    void pushItemToList(String listId, String value);

    String popItemFromList(String listId);

    String blockingPopItemFromList(String listId, int timeOut, TimeUnit timeUnit);

//    ItemRef blockingPopItemFromLists(String[] listIds, int timeOut, TimeUnit timeUnit);

    String popAndPushItemBetweenLists(String fromListId, String toListId);

    String blockingPopAndPushItemBetweenLists(String fromListId, String toListId, int timeOut, TimeUnit timeUnit);

    Boolean addItemToSortedSet(String setId, String value);

    Boolean addItemToSortedSet(String setId, String value, double score);

    Boolean addRangeToSortedSet(String setId, List<String> values, double score);

    Boolean addRangeToSortedSet(String setId, List<String> values, long score);

    Boolean removeItemFromSortedSet(String setId, String value);

    Long removeItemsFromSortedSet(String setId, List<String> values);

    String popItemWithLowestScoreFromSortedSet(String setId);

    String popItemWithHighestScoreFromSortedSet(String setId);

    boolean sortedSetContainsItem(String setId, String value);

    Double incrementItemInSortedSet(String setId, String value, double incrementBy);

    Double incrementItemInSortedSet(String setId, String value, long incrementBy);

    Long getItemIndexInSortedSet(String setId, String value);

    Long getItemIndexInSortedSetDesc(String setId, String value);

    Set<String> getAllItemsFromSortedSet(String setId);

    Set<String> getAllItemsFromSortedSetDesc(String setId);

    Set<String> getRangeFromSortedSet(String setId, int fromRank, int toRank);

    Set<String> getRangeFromSortedSetDesc(String setId, int fromRank, int toRank);

    default Map<String, Double> getAllWithScoresFromSortedSet(String setId) {
        return getRangeWithScoresFromSortedSet(setId, 0, -1);
    }

    Map<String, Double> getRangeWithScoresFromSortedSet(String setId, int fromRank, int toRank);

    Map<String, Double> getRangeWithScoresFromSortedSetDesc(String setId, int fromRank, int toRank);

    Set<String> getRangeFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore);

    Set<String> getRangeFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore, int skip, int take);

    Set<String> getRangeFromSortedSetByLowestScore(String setId, double fromScore, double toScore);

    Set<String> getRangeFromSortedSetByLowestScore(String setId, long fromScore, long toScore);

    Set<String> getRangeFromSortedSetByLowestScore(String setId, double fromScore, double toScore, int skip, int take);

    Set<String> getRangeFromSortedSetByLowestScore(String setId, long fromScore, long toScore, int skip, int take);

    Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore);

    Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, String fromStringScore, String toStringScore, int skip, int take);

    Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, double fromScore, double toScore);

    Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, long fromScore, long toScore);

    Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, double fromScore, double toScore, int skip, int take);

    Map<String, Double> getRangeWithScoresFromSortedSetByLowestScore(String setId, long fromScore, long toScore, int skip, int take);

    Set<String> getRangeFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore);

    Set<String> getRangeFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore, int skip, int take);

    Set<String> getRangeFromSortedSetByHighestScore(String setId, double fromScore, double toScore);

    Set<String> getRangeFromSortedSetByHighestScore(String setId, long fromScore, long toScore);

    Set<String> getRangeFromSortedSetByHighestScore(String setId, double fromScore, double toScore, int skip, int take);

    Set<String> getRangeFromSortedSetByHighestScore(String setId, long fromScore, long toScore, int skip, int take);

    Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore);

    Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, String fromStringScore, String toStringScore, int skip, int take);

    Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, double fromScore, double toScore);

    Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, long fromScore, long toScore);

    Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, double fromScore, double toScore, int skip, int take);

    Map<String, Double> getRangeWithScoresFromSortedSetByHighestScore(String setId, long fromScore, long toScore, int skip, int take);

    Long removeRangeFromSortedSet(String setId, int minRank, int maxRank);

    Long removeRangeFromSortedSetByScore(String setId, double fromScore, double toScore);

    Long removeRangeFromSortedSetByScore(String setId, long fromScore, long toScore);

    Long getSortedSetCount(String setId);

    Long getSortedSetCount(String setId, String fromStringScore, String toStringScore);

    Long getSortedSetCount(String setId, long fromScore, long toScore);

    Long getSortedSetCount(String setId, double fromScore, double toScore);

    Double getItemScoreInSortedSet(String setId, String value);

    default long storeIntersectFromSortedSets(String intoSetId, String... setIds) {
        return storeIntersectFromSortedSets(intoSetId, Arrays.asList(setIds));
    }

    Long storeIntersectFromSortedSets(String intoSetId, Collection<String> setIds);

//    Long storeIntersectFromSortedSets(String intoSetId, String[] setIds, String[] args);

    default long storeUnionFromSortedSets(String intoSetId, String... setIds) {
        return storeUnionFromSortedSets(intoSetId, Arrays.asList(setIds));
    }

    Long storeUnionFromSortedSets(String intoSetId, Collection<String> setIds);

//    Long storeUnionFromSortedSets(String intoSetId, String[] setIds, String[] args);

    default Set<String> searchSortedSet(String setId, String start) {
        return searchSortedSet(setId, start, null, 0, 0);
    }

    default Set<String> searchSortedSet(String setId, String start, String end) {
        return searchSortedSet(setId, start, end, 0, 0);
    }

    default Set<String> searchSortedSet(String setId, String start, String end, int skip) {
        return searchSortedSet(setId, start, end, skip, 0);
    }

    Set<String> searchSortedSet(String setId, String start, String end, int skip, int take);

//    default long searchSortedSetCount(String setId, String start) {
//        return searchSortedSetCount(setId, start, null);
//    }

//    Long searchSortedSetCount(String setId, String start, String end);

//    default long removeRangeFromSortedSetBySearch(String setId, String start) {
//        return removeRangeFromSortedSetBySearch(setId, start, null);
//    }

//    Long removeRangeFromSortedSetBySearch(String setId, String start, String end);

    Boolean hashContainsEntry(String hashId, String key);

    void setEntryInHash(String hashId, String key, String value);

    Boolean setEntryInHashIfNotExists(String hashId, String key, String value);

    void setRangeInHash(String hashId, Map<String, String> keyValuePairs);

//    <T> void setRangeInHash(String hashId, Map<String, T> keyValuePairs);

    Long incrementValueInHash(String hashId, String key, int incrementBy);

    Double incrementValueInHash(String hashId, String key, double incrementBy);

    String getValueFromHash(String hashId, String key);

    default List<String> getValuesFromHash(String hashId, String... keys) {
        return getValuesFromHash(hashId, Arrays.asList(keys));
    }

    List<String> getValuesFromHash(String hashId, Collection<String> keys);

    boolean removeEntryFromHash(String hashId, String key);

    boolean removeEntryFromHash(String hashId, List<String> keys);

    Long getHashCount(String hashId);

    Set<String> getHashKeys(String hashId);

    List<String> getHashValues(String hashId);

    Map<String, String> getHashEntries(String hashId);
//    Map<String, String> getAllEntriesFromHash(String hashId);

//    T ExecCachedLua<T>(String scriptBody, Func<String, T> scriptSha1);
//
//    redisText ExecLua(String body, String... args);
//
//    redisText ExecLua(String luaBody, String[] keys, String[] args);
//
//    redisText ExecLuaSha(String sha1, String... args);
//
//    redisText ExecLuaSha(String sha1, String[] keys, String[] args);

//    String ExecLuaAsString(String luaBody, String... args);
//
//    String ExecLuaAsString(String luaBody, String[] keys, String[] args);
//
//    String ExecLuaShaAsString(String sha1, String... args);
//
//    String ExecLuaShaAsString(String sha1, String[] keys, String[] args);
//
//    Long ExecLuaAsInt(String luaBody, String... args);
//
//    Long ExecLuaAsInt(String luaBody, String[] keys, String[] args);
//
//    Long ExecLuaShaAsInt(String sha1, String... args);
//
//    Long ExecLuaShaAsInt(String sha1, String[] keys, String[] args);
//
//    List<String> ExecLuaAsList(String luaBody, String... args);
//
//    List<String> ExecLuaAsList(String luaBody, String[] keys, String[] args);
//
//    List<String> ExecLuaShaAsList(String sha1, String... args);
//
//    List<String> ExecLuaShaAsList(String sha1, String[] keys, String[] args);

//    String CalculateSha1(String luaBody);

//    boolean HasLuaScript(String sha1Ref);

//    Map<String, boolean> WhichLuaScriptsExists(String... sha1Refs);

//    void removeAllLuaScripts();

//    void KillRunningLuaScript();

//    String LoadLuaScript(String body);
}
