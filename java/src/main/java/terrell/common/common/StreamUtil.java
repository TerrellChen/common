package terrell.common.common;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:49 20/5/18
 */

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: JDK8 stream 相关
 */
@Component
public class StreamUtil {

    public static Stream getStream(Collection collection){
        return collection.stream();
    }

    public static Stream getStream(Object... objects){
        return Stream.of(objects);
    }

    public static List getList(Stream stream, Function function){
        return (List)stream.map(function).collect(Collectors.toList());
    }

    public static void main(String[] args) {
    }

}
