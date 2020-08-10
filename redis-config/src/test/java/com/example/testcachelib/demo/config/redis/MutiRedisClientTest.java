package com.example.testcachelib.demo.config.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author zhangbaowei
 * Create  on 2020/7/8 10:39.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MutiRedisClientTest {

    @Autowired
    RedisClient redisClient;
    @Autowired
    @Qualifier("first")
    RedisClient redisClient_frist;

    @Autowired
    @Qualifier("second")
    RedisClient redisClient_second;

    @Test
    public void allset() {
        redisClient.set("zhangbaowei", "hao");
        redisClient_frist.set("zhangbaowei", "hao");
        redisClient_second.set("zhangbaowei", "hao");
    }

    @Test
    public void all() {
        Arrays.asList(redisClient, redisClient_frist, redisClient_second)
                .forEach(x -> {
                    testAll_(x);
                    testAll(x);
                    testsetAll(x);
                    testExpire(x);
                    testGetSetStr(x);
                    getAllKeys(x);
                });
    }

    public void testAll_(RedisClient redisClient) {
        RedisClientImplTest.Point p = new RedisClientImplTest.Point();
        p.setX(1);
        p.setY(2);

        redisClient.set("all:1", p);

        p = new RedisClientImplTest.Point();
        p.setX(3);
        p.setY(4);

        redisClient.set("all:2", p);

        Map<String, RedisClientImplTest.Point> all = redisClient.getAll(Arrays.asList("all:1", "kongkong", "all:2", "all:notexist"), new TypeReference<RedisClientImplTest.Point>() {
        });

        for (Map.Entry<String, RedisClientImplTest.Point> stringPointEntry : all.entrySet()) {
            System.out.println(stringPointEntry.getKey() + "=> " + stringPointEntry.getValue());
        }
    }

    public void testAll(RedisClient redisClient) {
        redisClient.set("x", 1);
        redisClient.set("y", 1);
        Map<String, String> all = redisClient.getAll(Arrays.asList("x", "y"));

        for (String s : all.keySet()) {
            System.out.println(s + " " + all.get(s));
        }
    }

    public void testsetAll(RedisClient redisClient) {

        Map<String, String> m = new HashMap<>();
        m.put("1", "a");
        m.put("2", "b");

        redisClient.setAll(m);

        Map<String, String> all = redisClient.getAll(m.keySet());
        for (String s : all.keySet()) {
            System.out.println(s + " " + all.get(s));
        }

        redisClient.expireEntryIn(m.keySet(), 1, TimeUnit.HOURS);
    }

    public void testExpire(RedisClient redisClient) {
        redisClient.set("key", "v");
        redisClient.expireEntryIn("key", 10, TimeUnit.SECONDS);
        System.out.println(redisClient.getExpire("key"));
        System.out.println(redisClient.getExpire("key", TimeUnit.MILLISECONDS));
    }

    public void testGetSetStr(RedisClient redisClient) {
        String source = "v1\"2";
        redisClient.set("key", source);
        System.out.println(redisClient.get("key"));
    }

    public void getAllKeys(RedisClient redisClient) {
        Set<String> allKeys = redisClient.getAllKeys();

        allKeys.forEach(x -> System.out.println(x));

        Assert.assertTrue("", allKeys.size() > 0);
    }

    @Test
    public void setAll() {
        String[] s = {"1", "3", "2"};
        String[] s2 = {"3", "9", "6"};

        redisClient.setAll(Arrays.asList(s), Arrays.asList(s2));

        assertEquals("3", redisClient.get("1"));
        assertEquals("9", redisClient.get("3"));
        assertEquals("6", redisClient.get("2"));

        redisClient.removeAll(Arrays.asList(s));
    }

    @Test
    public void setValues() {

        redisClient.setValue("setValues", "setValues");

        String setValues = redisClient.getValue("setValues");

        assertEquals("setValues", setValues);

        redisClient.remove("setValues");
    }

    @Test
    public void setValue() {
    }

    @Test
    public void setValue1() {
    }

    @Test
    public void setRedisKeyTest() {
        String key = "aaa";
        redisClient.set(key, "bbbb", new Date(System.currentTimeMillis() + 30 * 60 * 1000L));
        Long expire = redisClient.getExpire(key);
        Assert.assertTrue("expire=>" + expire, expire > 1799 && expire <= 30 * 60);
        key = "bbb";
        redisClient.set(key, "bbbb", 100, TimeUnit.SECONDS);

        String s = redisClient.get(key);
        Assert.assertEquals("不一致", "bbbb", s);

        Long expire1 = redisClient.getExpire(key);
        Assert.assertTrue(expire1 == 100);
    }

    @Test
    public void setValueIfNotExists() {
        String key = "setValueIfNotExists";

        redisClient.set(key, "hehe");
        boolean setValueIfNotExists = redisClient.setValueIfNotExists(key, "setValueIfNotExists");
        assertFalse(setValueIfNotExists);

        redisClient.remove("setValueIfNotExists");
        setValueIfNotExists = redisClient.setValueIfNotExists(key, "setValueIfNotExists");
        assertTrue(setValueIfNotExists);

        redisClient.remove("setValueIfNotExists");
    }

    @Test
    public void getValues() {
        redisClient.set("user1", new User() {
            {
                setId(1);
            }
        });
        redisClient.set("user2", new User() {
            {
                setId(2);
            }
        });

        String[] a = {"user1", "user2"};

        List<User> values = redisClient.getValues(Arrays.asList(a), User.class);

        values.forEach(x -> System.out.println(x.toString()));

        assertEquals(2, values.size());
    }

    @Test
    public void getValuesMap() {
        redisClient.set("a1", "2");
        redisClient.set("a2", "4");

        Map<String, String> valuesMap = redisClient.getValuesMap(Arrays.asList("a1", "a3", "a2"));

        valuesMap.forEach((x, y) -> System.out.println(x + "=>" + y));
    }

    @Test
    public void getValuesMap1() {
        redisClient.set("user1", new User() {
            {
                setId(1);
            }
        });
        redisClient.set("user2", new User() {
            {
                setId(2);
            }
        });

        String[] a = {"user1", "user2"};

        Map<String, User> valuesMap = redisClient.getValuesMap(Arrays.asList(a), User.class);

        valuesMap.forEach((x, y) -> System.out.println(x + "=>" + y));
    }

    @Test
    public void incrementValue() {
        String key = "decrementValueKey";
        redisClient.set(key, 0);
        redisClient.incrementValue(key);

        Integer integer = redisClient.get(key, Integer.class);
        System.out.println(integer);

        assertEquals(1, (long) integer);
    }

    @Test
    public void incrementValueBy() {
        String key = "decrementValueKey";
        redisClient.set(key, 0);
        redisClient.incrementValueBy(key, 2);

        Integer integer = redisClient.get(key, Integer.class);
        System.out.println(integer);

        assertEquals(2, (long) integer);
    }

    @Test
    public void decrementValue() {
        String key = "decrementValueKey";
        redisClient.set(key, 0);
        redisClient.decrementValue(key);

        Integer integer = redisClient.get(key, Integer.class);
        System.out.println(integer);

        assertEquals(-1, (long) integer);
    }

    @Test
    public void decrementValueBy() {
        String key = "decrementValueKey";
        redisClient.set(key, 0);
        redisClient.decrementValueBy(key, 2);

        Integer integer = redisClient.get(key, Integer.class);
        System.out.println(integer);

        assertEquals(-2, (long) integer);
    }

    @Test
    public void searchKeys() {
        Set<String> strings = redisClient.searchKeys("*");

        strings.forEach(x -> System.out.println(x));

        assertTrue(strings.size() > 0);
    }

    @Test
    public void type() {
        redisClient.set("valulekey", "");
        redisClient.addItemToList("listkey", "");
        redisClient.addItemToSet("setKey", "");
        redisClient.addItemToSortedSet("sortsetkey", "");
        redisClient.setEntryInHash("hashkey", "", "");

        System.out.println(redisClient.type("valulekey"));
        System.out.println(redisClient.type("listkey"));
        System.out.println(redisClient.type("setKey"));
        System.out.println(redisClient.type("sortsetkey"));
        System.out.println(redisClient.type("hashkey"));

        System.out.println(redisClient.type("NoneKey"));

        redisClient.remove("valulekey");
        redisClient.remove("listkey");
        redisClient.remove("setKey");
        redisClient.remove("sortsetkey");
        redisClient.remove("hashkey");
        redisClient.remove("NoneKey");
    }

    @Test
    public void scanAllSortedSetItems() {

        String sortkey = "sortKey";

        redisClient.remove(sortkey);

        for (int i = 0; i < 10; i++) {
            redisClient.addItemToSortedSet(sortkey, Integer.toString(i));
        }
    }

    @Test
    public void getSetString() {

        String key = "aaaa";
        redisClient.set(key, key);
        if (redisClient.containsKey(key)) {
            String value = redisClient.get(key);
            redisClient.expireEntryIn(key, 100, TimeUnit.SECONDS);

            if (!key.equals(value)) {
                throw new RuntimeException("Key != value " + key + "-" + value);
            }
        }
    }

    @Test
    public void setContainsItem() {
    }

    @Test
    public void getIntersectFromSets() {
    }

    @Test
    public void storeIntersect_union_differences_FromSets() {

        redisClient.remove("set1");
        redisClient.remove("set2");
        redisClient.remove("set3");
        redisClient.remove("set4");

        redisClient.addRangeToSet("set1", Arrays.asList("1", "2", "3"));
        redisClient.getAllItemsFromSet("set1").forEach(x -> System.out.println(x));
        System.out.println("****************************************");

        redisClient.addRangeToSet("set2", Arrays.asList("1", "2", "4"));
        redisClient.addRangeToSet("set4", Arrays.asList("1", "2", "3"));
        redisClient.getAllItemsFromSet("set2").forEach(x -> System.out.println(x));
        System.out.println("********************** storeIntersectFromSets ******************");

        redisClient.storeIntersectFromSets("set3", "set1", "set2");

        Set<String> set3 = redisClient.getAllItemsFromSet("set3");

        set3.forEach(x -> System.out.println(x));
        System.out.println("********************** getIntersectFromSets ******************");
        redisClient.getIntersectFromSets("set1", "set2").forEach(x -> System.out.println(x));
        redisClient.getIntersectFromSets(Arrays.asList("set1", "set2")).forEach(x -> System.out.println(x));

        System.out.println("******************* getUnionFromSets *********************");
        redisClient.getUnionFromSets("set1", "set2").forEach(x -> System.out.println(x));

        System.out.println("******************* getDifferencesFromSet *********************");
        redisClient.getDifferencesFromSet("set1", "set2", "set4").forEach(x -> System.out.println(x));

        redisClient.remove("set1");
        redisClient.remove("set2");
        redisClient.remove("set3");
    }

    @Test
    public void getUnionFromSets() {
    }

    @Test
    public void storeUnionFromSets() {
        redisClient.addItemToSet("s1", "s1-1");
        redisClient.addItemToSet("s1", "s1-2");
        redisClient.addItemToSet("s2", "s2-1");
        redisClient.addItemToSet("s2", "s2-2");

        redisClient.storeUnionFromSets("s3", "s1", "s2");
        Set<String> s3 = redisClient.getAllItemsFromSet("s3");

        for (String s : s3) {
            System.out.println(s);
        }

        redisClient.removeAll(Arrays.asList("s1", "s2", "s3"));
    }

    @Test
    public void getDifferencesFromSet() {
    }

    @Test
    public void storeDifferencesFromSet() {
    }

    @Test
    public void getRandomItemFromSet() {
    }

    @Test
    public void getAllItemsFromList() {
    }

    @Test
    public void getRangeFromList() {
    }

    @Test
    public void getRangeFromSortedList() {
    }

    @Test
    public void getSortedItemsFromList() {
    }

    @Test
    public void addItemToList() {
    }

    @Test
    public void addRangeToList() {
        String listid1 = "listid";
        redisClient.addRangeToList(listid1, Arrays.asList("1", "2", "3"));

        List<String> listid = redisClient.getAllItemsFromList(listid1);

        listid.forEach(x -> System.out.println(x));

        redisClient.remove(listid1);
    }

    @Test
    public void prependItemToList() {
    }

    @Test
    public void prependRangeToList() {
    }

    @Test
    public void removeAllFromList() {
    }

    @Test
    public void removeStartFromList() {
    }

    @Test
    public void blockingRemoveStartFromList() {
    }

    @Test
    public void blockingRemoveStartFromLists() {
    }

    @Test
    public void removeEndFromList() {
    }

    @Test
    public void trimList() {
    }

    @Test
    public void removeItemFromList() {
    }

    @Test
    public void removeItemFromList1() {
    }

    @Test
    public void getListCount() {
    }

    @Test
    public void getItemFromList() {
    }

    @Test
    public void setItemInList() {
    }

    @Test
    public void enqueueItemOnList() {
    }

    @Test
    public void dequeueItemFromList() {
    }

    @Test
    public void blockingDequeueItemFromList() {
    }

    @Test
    public void blockingDequeueItemFromLists() {
    }

    @Test
    public void pushItemToList() {
    }

    @Test
    public void popItemFromList() {
    }

    @Test
    public void blockingPopItemFromList() {
    }

    @Test
    public void blockingPopItemFromLists() {
    }

    @Test
    public void popAndPushItemBetweenLists() {
    }

    @Test
    public void blockingPopAndPushItemBetweenLists() {
    }

    @Test
    public void addItemToSortedSet() {
    }

    @Test
    public void addItemToSortedSet1() {
    }

    @Test
    public void addRangeToSortedSet() {
    }

    @Test
    public void addRangeToSortedSet1() {
    }

    @Test
    public void removeItemFromSortedSet() {
    }

    @Test
    public void removeItemsFromSortedSet() {
    }

    @Test
    public void popItemWithLowestScoreFromSortedSet() {

        String sortSetKey = "sortSetKey123";
        redisClient.remove(sortSetKey);
        for (int i = 0; i < 10; i++) {
            redisClient.addItemToSortedSet(sortSetKey, Integer.toString(i), i);
        }
        Set<String> allItemsFromSortedSet = redisClient.getAllItemsFromSortedSet(sortSetKey);

        allItemsFromSortedSet.forEach(x -> System.out.println(x));

        String s = redisClient.popItemWithLowestScoreFromSortedSet(sortSetKey);

        System.out.println("**************************************");
        System.out.println(s);
        System.out.println("**************************************");
        allItemsFromSortedSet = redisClient.getAllItemsFromSortedSet(sortSetKey);

        allItemsFromSortedSet.forEach(x -> System.out.println(x));
    }

    @Test
    public void popItemWithHighestScoreFromSortedSet() {
        String sortSetKey = "sortSetKey123";
        redisClient.remove(sortSetKey);
        for (int i = 0; i < 10; i++) {
            redisClient.addItemToSortedSet(sortSetKey, Integer.toString(i), i);
        }
        Set<String> allItemsFromSortedSet = redisClient.getAllItemsFromSortedSetDesc(sortSetKey);

        allItemsFromSortedSet.forEach(x -> System.out.println(x));

        String s = redisClient.popItemWithHighestScoreFromSortedSet(sortSetKey);

        System.out.println("**************************************");
        System.out.println(s);
        System.out.println("**************************************");
        allItemsFromSortedSet = redisClient.getAllItemsFromSortedSetDesc(sortSetKey);

        allItemsFromSortedSet.forEach(x -> System.out.println(x));
    }

    @Test
    public void sortedSetContainsItem() {
        redisClient.sortedSetContainsItem("aaq", "bbbbb");
    }

    @Test
    public void incrementItemInSortedSet() {
        redisClient.addItemToSortedSet("aaaabbb", "c");
        System.out.println(redisClient.removeItemFromSortedSet("aaaabbb", "c"));
        System.out.println(redisClient.removeItemFromSortedSet("aaaabbb", "c"));
    }

    @Test
    public void incrementItemInSortedSet1() {
    }

    @Test
    public void getItemIndexInSortedSet() {
    }

    @Test
    public void getItemIndexInSortedSetDesc() {
    }

    @Test
    public void getAllItemsFromSortedSet() {
    }

    @Test
    public void getAllItemsFromSortedSetDesc() {
    }

    @Test
    public void getRangeFromSortedSet() {
    }

    @Test
    public void getRangeFromSortedSetDesc() {
    }

    @Test
    public void getAllWithScoresFromSortedSet() {
    }

    @Test
    public void getRangeWithScoresFromSortedSet() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetDesc() {
        String id = "iiid";

        redisClient.ping();
        redisClient.addItemToSortedSet(id, "1", 1);
        redisClient.addItemToSortedSet(id, "2", 2);
        redisClient.addItemToSortedSet(id, "3", 3);

        Map<String, Double> rangeWithScoresFromSortedSetDesc = redisClient.getRangeWithScoresFromSortedSetDesc(id, 0, 100);
    }

    @Test
    public void getRangeFromSortedSetByLowestScore() {
    }

    @Test
    public void getRangeFromSortedSetByLowestScore1() {
    }

    @Test
    public void getRangeFromSortedSetByLowestScore2() {
    }

    @Test
    public void getRangeFromSortedSetByLowestScore3() {
    }

    @Test
    public void getRangeFromSortedSetByLowestScore4() {
    }

    @Test
    public void getRangeFromSortedSetByLowestScore5() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByLowestScore() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByLowestScore1() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByLowestScore2() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByLowestScore3() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByLowestScore4() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByLowestScore5() {
    }

    @Test
    public void getRangeFromSortedSetByHighestScore() {
    }

    @Test
    public void getRangeFromSortedSetByHighestScore1() {
    }

    @Test
    public void getRangeFromSortedSetByHighestScore2() {
    }

    @Test
    public void getRangeFromSortedSetByHighestScore3() {
    }

    @Test
    public void getRangeFromSortedSetByHighestScore4() {
    }

    @Test
    public void getRangeFromSortedSetByHighestScore5() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByHighestScore() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByHighestScore1() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByHighestScore2() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByHighestScore3() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByHighestScore4() {
    }

    @Test
    public void getRangeWithScoresFromSortedSetByHighestScore5() {
    }

    @Test
    public void removeRangeFromSortedSet() {
    }

    @Test
    public void removeRangeFromSortedSetByScore() {
    }

    @Test
    public void removeRangeFromSortedSetByScore1() {
    }

    @Test
    public void getSortedSetCount() {

        String addItemToSortedSet = "addItemToSortedSet";

        for (int i = 0; i < 10; i++) {
            redisClient.addItemToSortedSet(addItemToSortedSet, Integer.toString(i), i);
        }

        System.out.println(redisClient.getSortedSetCount(addItemToSortedSet));
        System.out.println(redisClient.getSortedSetCount(addItemToSortedSet, 1, 2));
        System.out.println(redisClient.getSortedSetCount(addItemToSortedSet, 0, -1));
        Long itemIndexInSortedSet = redisClient.getItemIndexInSortedSet(addItemToSortedSet, "2");
        assertEquals((long) 2L, (long) itemIndexInSortedSet);
        assertEquals(0L, (long) redisClient.getItemIndexInSortedSet(addItemToSortedSet, "0"));
        assertEquals(0L, (long) redisClient.getItemIndexInSortedSet(addItemToSortedSet, "0"));

        Long itemIndexInSortedSet1 = redisClient.getItemIndexInSortedSet(addItemToSortedSet, "-1");
        Long itemIndexInSortedSet2 = redisClient.getItemIndexInSortedSetDesc(addItemToSortedSet, "-1");

        redisClient.remove(addItemToSortedSet);
    }

    @Test
    public void getSortedSetCount1() {
    }

    @Test
    public void getSortedSetCount2() {
    }

    @Test
    public void getSortedSetCount3() {
    }

    @Test
    public void getItemScoreInSortedSet() {
    }

    @Test
    public void storeIntersectFromSortedSets() {
    }

    @Test
    public void storeIntersectFromSortedSets1() {
    }

    @Test
    public void storeUnionFromSortedSets() {
        redisClient.addRangeToSortedSet("set1", Arrays.asList("1", "2", "3"), 1);
        redisClient.getAllItemsFromSortedSet("set1").forEach(x -> System.out.println(x));
        System.out.println("****************************************");

        redisClient.addRangeToSortedSet("set2", Arrays.asList("1", "2", "4"), 1);
        // redisClient.addRangeToSet("set4", Arrays.asList("1", "2", "3"));
        redisClient.getAllItemsFromSortedSet("set2").forEach(x -> System.out.println(x));
        System.out.println("********************** storeIntersectFromSets ******************");

//        redisClient.storeIntersectFromSortedSets("set3", "set1", "set2");
//
//        Set<String> set3 = redisClient.getAllItemsFromSortedSet("set3");
////
//        set3.forEach(x -> System.out.println(x));
//        System.out.println("********************** getIntersectFromSets ******************");
//
//        System.out.println("******************* getUnionFromSets *********************");
//        redisClient.storeUnionFromSortedSets("set5", "set1", "set2");
//        redisClient.getAllItemsFromSortedSet("set5").forEach(x -> System.out.println(x));
//
//        System.out.println("******************* getDifferencesFromSet *********************");
//        redisClient.getDifferencesFromSet("set1", "set2","set4").forEach(x -> System.out.println(x));
//
        redisClient.remove("set1");
        redisClient.remove("set2");
        redisClient.remove("set3");
        redisClient.remove("set4");
        redisClient.remove("set5");
    }

    @Test
    public void storeUnionFromSortedSets1() {
    }

    @Test
    public void searchSortedSet() {
    }

    @Test
    public void searchSortedSetCount() {
    }

    @Test
    public void removeRangeFromSortedSetBySearch() {
    }

    @Test
    public void hashContainsEntry() {
    }

    @Test
    public void setEntryInHash() {
    }

    @Test
    public void setEntryInHashIfNotExists() {
    }

    @Test
    public void setRangeInHash() {
    }

    @Test
    public void incrementValueInHash() {
    }

    @Test
    public void incrementValueInHash1() {
    }

    @Test
    public void getValueFromHash() {
    }

    @Test
    public void getValuesFromHash() {
    }

    @Test
    public void removeEntryFromHash() {
    }

    @Test
    public void getHashCount() {
    }

    @Test
    public void getHashKeys() {
    }

    @Test
    public void getHashValues() {
    }

    @Test
    public void getAllEntriesFromHash() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void removeAll() {
    }

    @Test
    public void get() {
    }

    @Test
    public void get1() {
    }

    @Test
    public void get2() {
    }

    @Test
    public void increment() {
    }

    @Test
    public void decrement() {
    }

    @Test
    public void add() {
    }

    @Test
    public void set() {
    }

    @Test
    public void replace() {
    }

    @Test
    public void add1() {
    }

    @Test
    public void set1() {
    }

    @Test
    public void replace1() {
    }

    @Test
    public void add2() {
    }

    @Test
    public void set2() {
    }

    @Test
    public void replace2() {
    }

    @Test
    public void flushAll() {
    }

    @Test
    public void getAll() {
    }

    @Test
    public void setAll1() {
    }

    @Test
    public void TestSortList() {
        String key = "heheTestTestSortList" + System.currentTimeMillis();

        redisClient.addItemToSortedSet(key, "vvv", System.currentTimeMillis());

        System.out.println(redisClient.getSortedSetCount(key));

        Map<String, Double> rangeWithScoresFromSortedSet = redisClient.getRangeWithScoresFromSortedSet(key, 0, 100);
        System.out.println(rangeWithScoresFromSortedSet.size());
        Map<String, Double> rangeWithScoresFromSortedSetDesc = redisClient.getRangeWithScoresFromSortedSetDesc(key, 0, 100);
        System.out.println(rangeWithScoresFromSortedSetDesc.size());
    }

    @Test
    public void loopRead() throws InterruptedException {
        for (int i = 0; i < 100000; i++) {
            redisClient.set(String.valueOf(i), i);
            redisClient.get(String.valueOf(i));
            Thread.sleep(100);
        }
    }

    public static class Point {
        int x;
        int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}