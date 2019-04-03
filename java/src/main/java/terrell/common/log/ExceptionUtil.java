package terrell.common.log;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:25 10/1/19
 */

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Description:
 */
public class ExceptionUtil {
    public static String getFullStackTrace(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        String stackTraceString = sw.getBuffer().toString();
        return stackTraceString;
    }
}
