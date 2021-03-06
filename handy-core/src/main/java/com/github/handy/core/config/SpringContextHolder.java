package com.github.handy.core.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:29
 */
@Configuration
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    //取得存储在静态变量中的ApplicationContext.
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    //从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return ( T ) applicationContext.getBean(name);
    }

    /**
     * 根据类型和bean name获取指定的bean
     * @param beanName
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> requiredType) {
        checkApplicationContext();
        return applicationContext.getBean(beanName, requiredType);
    }

    /**
     * 根据类型获取对应的bean
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        checkApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    //发布事件
    public static void publishEvent(ApplicationEvent event) {
        checkApplicationContext();
        applicationContext.publishEvent(event);
    }

    //获取环境中property信息
    public static String getProperty(String key, String defaultValue) {
        String result = defaultValue;
        Environment environment = getBean(Environment.class);
        if (environment != null) {
            result = environment.getProperty(key, defaultValue);
        }
        return result;
    }


    private static void checkApplicationContext() {
        Assert.notNull(applicationContext,
                "applicationContext is null,please check spring  environment.......");
    }
}
