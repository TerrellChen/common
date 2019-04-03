package terrell.common.spring;
/**
 * @author: TerrellChen
 * @version: Created in 下午2:42 20/5/18
 */

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Description: 获取加载的bean相关
 */
public class ApplicationContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext=null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (ApplicationContextUtil.applicationContext == null) {
            ApplicationContextUtil.applicationContext = applicationContext;
        }
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> beanClass){
        return applicationContext.getBean(name, beanClass);
    }

}
