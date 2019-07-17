package terrell.common.net;
/**
 * @author: TerrellChen
 * @version: Created in 下午9:21 2/4/19
 */

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrell.common.log.ExceptionUtil;

import java.net.InetAddress;

/**
 * Description:
 */
public class PingUtil {
    private static Logger logger = LoggerFactory.getLogger(PingUtil.class);

    public static boolean ping(String ip) {
        if(Strings.isNullOrEmpty(ip)){
            return false;
        }

        int timeOut = 3000;//设置超时时间
        boolean status = false;

        try{
            status = InetAddress.getByName(ip).isReachable(timeOut);
        }catch(Exception e){
            logger.info(ExceptionUtil.getFullStackTrace(e));
        }

        return status;
    }
}
