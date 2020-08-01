package terrell.common.reflect;
/**
 * @author: TerrellChen
 * @version: Created in 01:10 2020-08-01
 */


import org.reflections.Reflections;
import terrell.common.entity.Bar;
import terrell.common.entity.Foo;
import terrell.common.entity.FooInterface;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

/**
 * Description:
 */
public class TestMethodHandle {

    public static void main(String[] args) throws Throwable {
        Class fooClazz = Class.forName("terrell.common.entity.Foo");

        Class barClazz = Thread.currentThread().getContextClassLoader().loadClass("terrell.common.entity.Bar");
        Object fooInstance = fooClazz.newInstance();

        // get public methodHandle: findVirtual
        MethodHandle publicFooBarMethodHandle = MethodHandles.lookup().findVirtual(fooClazz, "bar", MethodType.methodType(Object.class, int.class, char.class, String.class));
        System.out.println(publicFooBarMethodHandle.invoke(fooInstance, 0, 'a', "a"));
        System.out.println(publicFooBarMethodHandle.invokeExact((Foo)fooInstance, 0, 'a', "a"));
        System.out.println(publicFooBarMethodHandle.invokeWithArguments(fooInstance, 0, 'a', "a"));

        // get protected methodHandle: unreflect
        Method protectedFooBarMethod = fooClazz.getDeclaredMethod("bar", int.class, char.class);
        protectedFooBarMethod.setAccessible(true);
        MethodHandle protectedFooBarMethodHandle = MethodHandles.lookup().unreflect(protectedFooBarMethod);
        System.out.println(protectedFooBarMethodHandle.invoke(fooInstance, 0, 'a'));
//         something wrong: java.lang.invoke.WrongMethodTypeException: expected (Foo,int,char)int but found (Foo,int,char)Object
//        System.out.println(protectedFooBarMethodHandle.invokeExact((Foo)fooInstance, 0, 'a'));
        System.out.println(protectedFooBarMethodHandle.invokeWithArguments(fooInstance, 0, 'a'));

        // get private methodhandle: unreflect
        Method privateFooBarMethod = fooClazz.getDeclaredMethod("bar", int.class);
        privateFooBarMethod.setAccessible(true);
        MethodHandle privateFooBarMethodHandle = MethodHandles.lookup().unreflect(privateFooBarMethod);
        System.out.println(privateFooBarMethodHandle.invoke(fooInstance, 0));
//         something wrong: java.lang.invoke.WrongMethodTypeException: expected (Foo,int,char)int but found (Foo,int,char)Object
//        System.out.println(privateFooBarMethodHandle.invokeExact((Foo)fooInstance, 0));
        System.out.println(privateFooBarMethodHandle.invokeWithArguments(fooInstance, 0));

        // get public in anonymous class
        Class fooInterfaceClazz = Class.forName("terrell.common.entity.FooInterface");
        Reflections reflections = new Reflections("terrell.common.entity");
        Set<Class<? extends FooInterface>> classesSubTypesOfFooInterface = reflections.getSubTypesOf(FooInterface.class);
        Iterator<Class<? extends FooInterface>> iterator  = classesSubTypesOfFooInterface.iterator();
        Class fooInterfaceAnonymousImplementation = null;
        while (iterator.hasNext()) {
            fooInterfaceAnonymousImplementation = iterator.next();
            if (fooInterfaceAnonymousImplementation.getName().contains("Bar")) {
                System.out.println(fooInterfaceAnonymousImplementation.getName());
                break;
            }

        }
        fooInterfaceClazz.getClasses();
        Method  anonymousPrivateBarFooMethod = fooInterfaceAnonymousImplementation.getMethod("bar", int.class, char.class, String.class);
        anonymousPrivateBarFooMethod.setAccessible(true);
        MethodHandle anonymousPrivateBarFooMethodHandle = MethodHandles.lookup().unreflect(anonymousPrivateBarFooMethod);

        Field field = Bar.class.getDeclaredField("fooInterface");
        field.setAccessible(true);
        Object anonymousInstance = field.get(Bar.class);
//        System.out.println(anonymousPrivateBarFooMethodHandle.invoke(f, 0, 'a', ""));
        System.out.println(anonymousPrivateBarFooMethodHandle.invoke(anonymousInstance, 0, 'a', ""));
        System.out.println(anonymousPrivateBarFooMethodHandle.invokeExact(anonymousInstance, 0, 'a', ""));
        System.out.println(anonymousPrivateBarFooMethodHandle.invokeWithArguments(anonymousInstance, 0, 'a', ""));

        System.exit(0);
    }
}
