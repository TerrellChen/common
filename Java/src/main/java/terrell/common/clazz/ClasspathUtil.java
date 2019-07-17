package terrell.common.clazz;
/**
 * @author: TerrellChen
 * @version: Created in 上午10:44 2/4/19
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Description: 手动类加载
 */
public class ClasspathUtil {

    static Logger logger = LoggerFactory.getLogger(ClasspathUtil.class);

    /**
     * 加载并返回指定条件下的类
     * @param basePackage
     * @param from
     * @param annotation
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<Class<? extends T>> scan(String basePackage, Class<T> from, Class<? extends Annotation> annotation) throws Exception {
        List<Class<? extends T>> classes = new ArrayList<>();
        List<Class<? extends T>> list = scan(basePackage, from);
        for (Class<? extends T> c : list) {
            if (!c.isAnnotationPresent(annotation)) {
                continue;
            }
            classes.add(c);
        }
        return classes;
    }

    /**
     * 加载并返回指定条件下的类
     * @param basePackage
     * @param from
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<Class<? extends T>> scan(String basePackage, Class<T> from) throws Exception {
        List<Class<? extends T>> list = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String resourceName = basePackage.replaceAll("\\.", "/");
        URL url = loader.getResource(resourceName);
        if (url == null) {
            return list;
        }
        URI uri = url.toURI();
        if ("jar".equals(uri.getScheme())) {
            CodeSource source = ClasspathUtil.class.getProtectionDomain().getCodeSource();
            ZipInputStream zip = new ZipInputStream(source.getLocation().openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) {
                    break;
                }
                String name = e.getName();
                if (!name.startsWith(resourceName) || !name.endsWith(".clazz") || name.contains("$")) {
                    continue;
                }
                Class<?> clazz = loader.loadClass(name.replaceAll("/", ".").replace(".clazz", ""));
                if (from.isAssignableFrom(clazz)) {
                    list.add((Class<? extends T>) clazz);
                }
            }
        } else {
            File urlFile = new File(url.toURI());
            File[] files = urlFile.listFiles();
            if (files != null) {
                for (File f : files) {
                    scanFile(loader, basePackage, f, from, list);
                }
            }
        }
        return list;
    }

    /**
     * 从文件中加载指定类
     * @param loader
     * @param pkgName
     * @param file
     * @param from
     * @param list
     * @param <T>
     * @throws ClassNotFoundException
     */
    private static <T> void scanFile(ClassLoader loader, String pkgName, File file, Class<T> from, List<Class<? extends T>> list) throws ClassNotFoundException {
        if (file.isFile()) {
            String className = file.getName().replace(".clazz", "");
            Class clazz = loader.loadClass(pkgName + "." + className);
            if (!from.isAssignableFrom(clazz)) {
                logger.info("{} is not {}", clazz, from);
                return;
            }
            logger.warn("add clazz {}", className);
            list.add(clazz);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    scanFile(loader, pkgName + "." + file.getName(), f, from, list);
                }
            }

        }
    }

}
