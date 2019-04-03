package terrell.common.common;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:51 19/2/19
 */
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Description:
 */
public class StringUtil {
    /**
     * 根据分隔符，去重
     * @param row
     * @param separator
     * @return
     */
    public static String dereplication(String row, String separator){
        String[] tempArray = row.split(separator);
        Set<String> treeSet = new TreeSet<String>();
        treeSet.addAll(Arrays.asList(tempArray));
        String result = StringUtils.join(treeSet, separator);
        return result;
    }

    /**
     * 将最后一个字符作为分隔符，合并前面的所有字符串
     * @param s
     * @return
     */
    public static String combine(String... s){
        String separator = s[s.length-1];
        String[] strings = Arrays.copyOf(s,s.length-1);
        String result = StringUtils.join(strings,separator);
        return result;
    }
}
