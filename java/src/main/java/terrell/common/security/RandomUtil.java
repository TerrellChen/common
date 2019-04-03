package terrell.common.security;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:55 20/5/18
 */

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Description: 随机数相关
 */
public class RandomUtil {
    private static ThreadLocal<SecureRandom> secureRandom;

    static {
        secureRandom = ThreadLocal.withInitial(() -> {
            try {
                return SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        });
    }

    /**
     * 简单的double随机数
     * @return
     */
    public static double simpleDoubleRandom(){
        return Math.random();
    }

    /**
     * 线程独立的安全随机数: 默认int范围
     * @return
     */
    public static int secureIntRandom(){
        return secureRandom.get().nextInt(100);
    }

    /**
     * 线程独立的安全随机数: 带范围
     * @return
     */
    public static int secureIntRandom(int bound){
        return secureRandom.get().nextInt(bound);
    }
}
