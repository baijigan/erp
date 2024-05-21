package com.cingsoft.cloud.helper.conctroller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    // 通过name获取Bean.
    public static <T> T getBeanByName(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBeanByClass(Class<T> className) {
        return (T) getApplicationContext().getBean(className);
    }
}