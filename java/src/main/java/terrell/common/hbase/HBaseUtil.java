package terrell.common.hbase;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:13 10/1/19
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrell.common.log.ExceptionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 */
public class HBaseUtil {
    private static Logger logger = LoggerFactory.getLogger(HBaseUtil.class);

    private static User user;
    private static String hbaseZookeeperQuorum = "";
    private static Configuration configuration;
    private static Connection connection = null;
    private static final Object lock = new Object();

    private static ExecutorService pool = Executors.newFixedThreadPool(10);


    public static synchronized Connection getConnection(){
        if (connection == null || connection.isClosed()){
            synchronized (lock){
                if (connection == null || connection.isClosed()){
                    try {
                        config();
                        connection = ConnectionFactory.createConnection(configuration, pool, user);
                    } catch (IOException e){
                        logger.error(ExceptionUtil.getFullStackTrace(e));
                    }
                }
            }
            return connection;
        }
        return connection;
    }

    private static void config() {
        try {
            if (configuration == null){
                configuration = HBaseConfiguration.create();
                configuration.set("hbase.zookeeper.property.clientPort", "2181");
                configuration.set("hbase.zookeeper.quorum",hbaseZookeeperQuorum);
                configuration.set("hbase.zookeeper.znode.parent", "/hbase");
                configuration.set("hbase.region_client.inflight_limit", "1024");
                configuration.set("hbase.region_client.pending_limit", "1024");
                configuration.set("hbase.rpcs.buffered_flush_interval", "1000");
                configuration.set("hbase.rpcs.batch.size", "1024");
                configuration.setInt("hbase.rpc.timeout",20000);
                configuration.setInt("hbase.client.operation.timeout",30000);
                String[] groups = new String[1];
                groups[0] = "www";
                user = User.createUserForTesting(configuration, "www", groups);
            }
        } catch (Exception e){
            logger.error(ExceptionUtil.getFullStackTrace(e));
        }
    }


    public static List<String> testHBase() {
        Connection connection = getConnection();
        try {

            Admin admin = connection.getAdmin();
            try {
                HTableDescriptor[] hTableDescriptors = admin.listTables();
                List<String> res = new ArrayList<String>();
                if (hTableDescriptors == null)  {
                    return null;
                }
                for (HTableDescriptor hTableDescriptor:hTableDescriptors) {
                    res.add(hTableDescriptor.getNameAsString());
                }
                return res;
            } catch (Exception e){
                logger.error(ExceptionUtil.getFullStackTrace(e));
            } finally {
                admin.close();
            }
        } catch (IOException e){
            logger.error(ExceptionUtil.getFullStackTrace(e));
        }
        return null;
    }

    public static boolean put(String hBaseTableName, String rowKey, String family, String qualifier, byte[] value){
        Connection connection = getConnection();
        TableName tableName = TableName.valueOf(hBaseTableName);
        try {
            Table table = connection.getTable(tableName);
            try {
                Put put = new Put(rowKey.getBytes());
                put.addColumn(family.getBytes(), qualifier.getBytes(), value);
                table.put(put);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(ExceptionUtil.getFullStackTrace(e));
            } catch (Throwable t){
                t.printStackTrace();
                logger.error(ExceptionUtil.getFullStackTrace(t));
            } finally {
                table.close();
            }
        } catch (IOException e){
            logger.error(ExceptionUtil.getFullStackTrace(e));
        }
        return false;
    }

    public static String get(String hBaseTableName, String rowKey, String family, String qualifier){
        Connection connection = getConnection();
        TableName tableName = TableName.valueOf(hBaseTableName);
        try {

            Table table = connection.getTable(tableName);
            try{
                Get get = new Get(Bytes.toBytes(rowKey));
                Result rs = table.get(get);
                String result = new String(rs.getValue(Bytes.toBytes(family),Bytes.toBytes(qualifier)));
                return result;
            } catch (Exception e){
                e.printStackTrace();
                logger.error(ExceptionUtil.getFullStackTrace(e));
            } finally {
                table.close();
            }
        } catch (IOException e){
            logger.error(ExceptionUtil.getFullStackTrace(e));
        }
        return "";
    }

}
