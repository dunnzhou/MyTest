package com.test;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by dunn on 2017/11/27.
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(SpringContextUtils.class);
    private static ApplicationContext context; // Spring应用上下文环境

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /*
     * 实现了ApplicationContextAware 接口，必须实现该方法；
     *通过传递applicationContext参数初始化成员变量applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) context.getBean(name);
    }

    /**
     * 根据annotation获取Bean实例
     *
     * @param annotationType
     * @return List<Object>
     */
    public List<Object> getBeanForAnnotation(Class<? extends Annotation> annotationType) {
        String[] beansName = context.getBeanNamesForAnnotation(annotationType);
        if (Objects.nonNull(beansName) && beansName.length > 0) {
            List<Object> result = Lists.newArrayList();
            try {
                for (String name : beansName) {
                    Object bean = context != null ? context.getBean(name) : null;
                    result.add(bean);
                }
            } catch (BeansException e) {
                logger.warn("Not found bean by annotation[{}] ", annotationType, e);
            }
            return result;
        }
        return Collections.emptyList();
    }

    /**
     * 根据类型获取Bean实例
     *
     * @param type
     * @return List<Object>
     */
    public <T> List<T> getBeanForType(Class<T> type) {
        String[] beansName = context.getBeanNamesForType(type);
        if (Objects.nonNull(beansName) && beansName.length > 0) {
            List<T> result = Lists.newArrayList();
            try {
                for (String name : beansName) {
                    T bean = context != null ? (T) context.getBean(name) : null;
                    result.add(bean);
                }
            } catch (BeansException e) {
                logger.warn("Not found bean by class[{}] ", type, e);
            }
            return result;
        }
        return Collections.emptyList();
    }
}
