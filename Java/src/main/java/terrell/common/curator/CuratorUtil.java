package terrell.common.curator;
/**
 * @author: TerrellChen
 * @version: Created in 下午9:08 2/4/19
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 */
@Component
public class CuratorUtil {
    static Logger logger = LoggerFactory.getLogger(CuratorUtil.class);

    private static CuratorFramework client;

    private static LeaderLatch leaderLatch;

    private static InterProcessMutex lock;

    private static final String localhost = "";

    private static String zookeeperAddress = "";
    private static String zookeeperPath = "";
    private static final String zookeeperElectionNode = "/master";
    private static int zookeeperRetryTime = 10;
    private static int zookeeperRetryBetween = 5000;
    private static AtomicBoolean shutdown = new AtomicBoolean(false);

    @PostConstruct
    public static void init(){
        clientReconnect();
    }

    @PreDestroy
    public static void shutdown(){
        shutdown.set(true);
        close();
    }

    public static CuratorFramework getAliveClient() {
        CuratorFramework aliveClient = client;
        if (aliveClient != null && aliveClient.getState().equals(CuratorFrameworkState.STARTED)) {
            return aliveClient;
        }
        clientReconnect();
        return client;
    }

    public static synchronized void clientReconnect() {
        close();
        try {
            connect();
        } catch (IOException e) {

        }
    }

    public static synchronized void close() {
        if (client != null) {
            try {
                relinquishLeadership();
                client.close();
            } catch (Exception e) {
            }
            client = null;
        }
    }

    public static synchronized void connect() throws IOException {
        logger.info("start connect zookeeper");
        if (client == null) {
            if (shutdown.get() == true){
                return;
            }
            client = CuratorFrameworkFactory.newClient(
                    zookeeperAddress,
                    new RetryNTimes(zookeeperRetryTime, zookeeperRetryBetween)
            );
            logger.info(client.getState().toString());
        }
        if (client.getState().equals(CuratorFrameworkState.LATENT)) {
            client.start();
            logger.info(client.getState().toString());
        }

    }

    private static void resetLock() {
        CuratorFramework aliveClient = getAliveClient();
        if (lock == null) {
            lock = new InterProcessMutex(aliveClient, zookeeperPath);
            try {
                lock.release();
            } catch (IllegalMonitorStateException e) {
                logger.info(Thread.currentThread().getName() + ": 没有拿到锁，跳过执行");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getLock() {
        if (lock == null) {
            resetLock();
        }
        try {
            if (lock.acquire(0,TimeUnit.SECONDS)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void releaseLock() {
        if (lock == null) {
            resetLock();
            return;
        }
        try {
            lock.release();
        } catch (IllegalMonitorStateException e) {
            logger.info(Thread.currentThread().getName() + ": 没有拿到锁，跳过执行");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getChildren(){
        try {
            List<String> children = getAliveClient().getChildren().forPath(zookeeperPath);
            System.out.println(children);
            Stat stat = new Stat();
            System.out.println(new String(getAliveClient().getData().storingStatIn(stat).forPath(zookeeperPath)));
        } catch (Exception e){

        }
    }

    private static void beCandidate() throws Exception{
        logger.info("start be candidate");
        if (leaderLatch == null) {
            String masterPath = zookeeperPath + zookeeperElectionNode;
            leaderLatch = new LeaderLatch(getAliveClient(),
                    masterPath,
                    localhost);
            logger.info(leaderLatch.getState().toString());
        }
        if (leaderLatch.getState().equals(LeaderLatch.State.LATENT)) {
            leaderLatch.start();
            logger.info(leaderLatch.getState().toString());
        }
    }

    public static void relinquishLeadership(){
        if (leaderLatch != null) {
            try {
                leaderLatch.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            leaderLatch = null;
        }
    }

    private static void reBeCandidate(){
        relinquishLeadership();
        try {
            beCandidate();
        } catch (Exception e) {

        }
    }

    public static boolean hasLeadership(){
        if (shutdown.get()){
            return false;
        }
        if (leaderLatch == null || leaderLatch.getState().equals(LeaderLatch.State.CLOSED)) {
            reBeCandidate();
        }
        if (leaderLatch != null){
            return leaderLatch.hasLeadership();
        } else {
            return false;
        }
    }

    public static JSONObject getStatus() {
        JSONObject result = new JSONObject();
        result.put("hasLeadership", hasLeadership());
        result.put("state", leaderLatch.getState());
        result.put("id", leaderLatch.getId());
        try{
            result.put("leader",leaderLatch.getLeader());
            result.put("participants",leaderLatch.getParticipants());
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void online(){
        shutdown.set(false);
        clientReconnect();
    }

    public static void offline(){
        shutdown();
    }
}
