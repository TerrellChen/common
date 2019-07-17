package terrell.common.io;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:51 8/1/19
 */

import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description:
 */
public class FileUtil {
    private final static String[] strHex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String getMD5(byte[] contents){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(contents);
            for (int i = 0; i < bytes.length; i++){
                int d = bytes[i];
                if (d < 0){
                    d += 256;
                }
                int d1 = d/16;
                int d2 = d%16;
                stringBuilder.append(strHex[d1] + strHex[d2]);
            }
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    public static String getMD5(File file) throws Exception{
        //防止多进程共用MessageDigest造成md5值计算错误，此处采用局部变量
        MessageDigest MD5 = null;
        FileInputStream fileInputStream = null;
        try {
            MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        }finally {
            if (fileInputStream != null){
                fileInputStream.close();
            }
        }
    }

    public static Object readObjectFromFile(File file) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return objectInputStream.readObject();
            }
        }
    }

    public static void saveObjectToFile(File file, Object object) throws IOException{
        if (!file.exists()){
            file.createNewFile();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            }
        }
    }
}
