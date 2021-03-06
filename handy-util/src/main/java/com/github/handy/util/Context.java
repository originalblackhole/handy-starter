package com.github.handy.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>上下文工具类</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 17:43
 */
public class Context extends ConcurrentHashMap<String, Object> {

    protected static Class<? extends Context> contextClass = Context.class;

    protected static final ThreadLocal<Context> threadLocal = new InheritableThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            try {
                return contextClass.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    };

    public Context() {
        super();
    }

    public static void setContextClass(Class<? extends Context> clazz) {
        contextClass = clazz;
    }

    public static Context getCurrentContext() {
        Context context = threadLocal.get();
        return context;
    }

    //清除线程上下文
    public void unset() {
        threadLocal.remove();
    }


    /**
     * puts the key, value into the map. a null value will remove the key from the map
     */
    public void set(String key, Object value) {
        if (value != null) {
            put(key, value);
        } else {
            remove(key);
        }
    }
}
