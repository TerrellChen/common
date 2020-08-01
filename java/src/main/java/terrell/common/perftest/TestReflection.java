package terrell.common.perftest;
/**
 * @author: TerrellChen
 * @version: Created in 18:21 2020-08-01
 */

import com.esotericsoftware.reflectasm.MethodAccess;
import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Description:
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(batchSize = 1, iterations = 1, time = 5)
@Fork(value = 1, warmups = 1)
@Measurement(iterations = 2, time = 5)
public class TestReflection {
    private static MethodHandle methodHandle;
    private static Method method;
    private static MethodAccess methodAccess;

    static {
        try {
            method = TestReflection.class.getMethod("doSomething", int.class, int.class);
            methodHandle = MethodHandles.lookup().unreflect(method);
            methodAccess = MethodAccess.get(TestReflection.class);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static int COUNT = 100000;

    public int doSomething(int a, int b) {
        if (a > b) {
            return a * 100 - b << 2;
        } else {
            return a + b * 15;
        }
    }

//    @Benchmark
//    public int testInline() {
//        int start = 0;
//        for (int i = 0; i < COUNT; i++) {
//            start += i;
//        }
//        return start;
//    }

    @Benchmark
    public int testDirectInvoke() {
        int start = 0;
        for (int i = 0; i < COUNT; i++) {
            start = (int) doSomething(start, i);
        }
        return start;
    }

    @Benchmark
    public int testCachedMethod() throws Exception {
        int start = 0;
        for (int i = 0; i < COUNT; i++) {
            start = (int) method.invoke(this, start, i);
        }
        return start;
    }

    @Benchmark
    public int testCachedMethodHandleAndInvokeWithArguments() throws Throwable {
        int start = 0;
        for (int i = 0; i < COUNT; i++) {
            start = (int) methodHandle.invokeWithArguments(this, start, i);
        }
        return start;
    }

    @Benchmark
    public int testCachedMethodHandleAndInvoke() throws Throwable {
        int start = 0;
        for (int i = 0; i < COUNT; i++) {
            start = (int) methodHandle.invoke(this, start, i);
        }
        return start;
    }

    @Benchmark
    public int testCachedMethodHandleAndInvokeExact() throws Throwable {
        int start = 0;
        for (int i = 0; i < COUNT; i++) {
            start = (int) methodHandle.invokeExact(this, start, i);
        }
        return start;
    }

    @Benchmark
    public int testCachedReflectAsm() {
        int start = 0;
        for (int i = 0; i < COUNT; i++) {
            start = (int) methodAccess.invoke(this, "doSomething", start, i);
        }
        return start;
    }

}
