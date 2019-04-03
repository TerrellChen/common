package terrell.common.security;
/**
 * @author: TerrellChen
 * @version: Created in 下午9:14 2/4/19
 */

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Description:
 */
public class AESUtil {
    static Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String password = "";

    /**
     *
     * @param content
     * @return 返回加密后的内容
     */
    public static String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            logger.error("数据加密失败: " + e);
        }
        return null;
    }

    /**
     * 返回解密后的内容
     * @param content
     * @return
     */
    public static String decrypt(String content){
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "utf-8");
        } catch (Exception e) {
            logger.error("数据解密失败: ", e);
        }
        return null;
    }

    /**
     * 返回生成指定算法密钥生成的KeyGenerator对象
     * @return
     */
    private static SecretKeySpec getSecretKey(){
        KeyGenerator keyGenerator = null;
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGenerator.init(128, random);
            SecretKey secretKey = keyGenerator.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (Exception e) {
            logger.error("KeyGenerator生成失败: " + e);
        }

        return null;
    }
}
