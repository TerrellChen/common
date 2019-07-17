package terrell.common.net;
/**
 * @author: TerrellChen
 * @version: Created in 下午9:19 2/4/19
 */

import java.net.InetAddress;
import java.util.regex.Pattern;

/**
 * Description:
 */
public class IpUtil {
    static Pattern pattern = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
    private static String IP = "";

    public static boolean validate(String ip) {
        return pattern.matcher(ip).matches();
    }

    public static String getLocalIp() {
        if ("".equals(IP)) {
            try {
                IP = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
            }
        }
        return IP;
    }
}
