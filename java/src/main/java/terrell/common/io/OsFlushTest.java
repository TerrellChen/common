package terrell.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Terrell Chen
 */
public class OsFlushTest {
    private static final String FILE_PATH = "/Users/chentairan/temp/flush";
    private static final double MB = 1024 * 1024 * 8;
    public static void main(String[] args) throws InterruptedException, IOException{
        testOsFlush(FILE_PATH);
    }

    public static void testOsFlush(String filePath) throws IOException {
        File file = new File(filePath);
        file.delete();
        file.createNewFile();
        startThreadToMonitorFileLength(filePath);
        startThreadToWriteFile(filePath, 10);
    }

    /**
     *
     * @param filePath
     * @param size MB
     */
    public static void startThreadToWriteFile(String filePath, int size) {
        int actualSize = size * (int) MB;
        byte[] testData = new byte[actualSize];
        for (int i=0;i<actualSize;i++){
            testData[i] = 1;
        }

        File file = new File(filePath);
        writeFile(file, testData);
    }

    private static void writeFile(File file, byte[] testData){
        new Thread(() -> {
            try(OutputStream outputStream = new FileOutputStream(file, true)) {
                while (true){
                    outputStream.write(testData);
//                    Thread.sleep(100);
                }
            } catch (Exception e){

            }
        }).start();
    }

    public static void startThreadToMonitorFileLength(String filePath) {
        AtomicLong fileLength = new AtomicLong(0);
        File file = new File(filePath);

        new Thread(() -> {
            while (true){
                if (file.length() != fileLength.get()){
                    System.out.println((double) file.length() / MB);
                }
                fileLength.set(file.length());
            }
        }).start();
    }
}
