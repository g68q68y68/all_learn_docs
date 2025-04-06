package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(clazz);
            } catch (Exception ex) {
                log.error("Get bean exception ", ex);
            }

        }
        return null;
    }

    public static <T> T getBean(String name) {
        if (applicationContext != null) {
            try {
                return (T) applicationContext.getBean(name);
            } catch (Exception ex) {
                log.error("Get bean exception ", ex);
            }
        }
        return null;
    }

}
