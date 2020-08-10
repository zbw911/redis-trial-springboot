package com.zhangbaowei.quorum;

import com.example.testcachelib.demo.config.redis.RedisClient;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbaowei
 * Create  on 2020/7/17 13:52.
 */
public class Clients {
    /**
     * 注册表
     */
    private static Set<String> allClientList = new HashSet<>();
    private static Logger logger = LoggerFactory.getLogger(Clients.class);

    private static String getClientName() {
        String localServerIp = getServerIp();
        return localServerIp;
    }

    private static String getServerIp() {
        String SERVER_IP = null;
        try {
            Enumeration var5 = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (var5.hasMoreElements() && SERVER_IP == null) {
                NetworkInterface ni = (NetworkInterface) var5.nextElement();
                Enumeration address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = (InetAddress) address.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        SERVER_IP = ip.getHostAddress();
                        break;
                    }
                }
            }
            return SERVER_IP;
        } catch (SocketException var51) {
            SERVER_IP = "127.127.127.127";
            return SERVER_IP;
        }
    }

    public Set<String> getAllClientList() {
        return allClientList;
    }

    /**
     * 我是谁？
     */
    public String whoAmI() {
        return getClientName();
    }

    private String getClusterKey(String clusterName) {
        String registertable = "clienttable:" + clusterName;
        return registertable;
    }

    /**
     * 我是主？
     *
     * @return
     */
    public boolean iAmMaster() {
        return whoAmI().equals(whoIsMaster());
    }

    /**
     * 谁是主
     *
     * @return
     */
    public String whoIsMaster() {
        if (CollectionUtils.isEmpty(allClientList)) {
            return null;
        }

        String minIp = allClientList.stream()
                .min((x, y) -> x.compareTo(y))
                .get();

        return minIp;
    }

    /**
     * 心跳，
     * 注册
     * 移除
     *
     * @param redisClient
     * @param clusterName
     */
    public void register(RedisClient redisClient, String clusterName) {
        String registertable = getClusterKey(clusterName);
        //超过10S的从注册表中移除
        int timeout = 10;
        //注册自己
        long currentTimeMillis = redisClient.time();
        redisClient.addItemToSortedSet(registertable, getClientName(), currentTimeMillis);
        //取过期列表
        Set<String> rangeFromSortedSetByHighestScore = redisClient.getRangeFromSortedSetByHighestScore(registertable, 0, currentTimeMillis - timeout * 1000);
        //清除
        for (String s : rangeFromSortedSetByHighestScore) {
            redisClient.removeItemFromSortedSet(registertable, s);
            logger.trace("移除Client:" + s + String.join(",", allClientList));
        }
        allClientList = redisClient.getAllItemsFromSortedSet(registertable);
    }

    /**
     * 移除注册
     *
     * @param redisClient
     * @param clusterName
     */
    public void unregister(RedisClient redisClient, String clusterName) {
        String registertable = getClusterKey(clusterName);
        redisClient.removeItemFromSortedSet(registertable, getClientName());
    }
}
