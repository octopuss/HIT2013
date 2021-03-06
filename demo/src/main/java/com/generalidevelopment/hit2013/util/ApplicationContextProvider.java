package com.generalidevelopment.hit2013.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		// Wiring the ApplicationContext into a static method
		AppContext.setApplicationContext(ctx);

	}

}
