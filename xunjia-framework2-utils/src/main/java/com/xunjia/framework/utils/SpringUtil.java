package com.xunjia.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext currentApplicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		currentApplicationContext = applicationContext;
	}
	
	public static <T> T getObject(Class<T> clazz) {
		return currentApplicationContext.getBean(clazz);
	}
}
