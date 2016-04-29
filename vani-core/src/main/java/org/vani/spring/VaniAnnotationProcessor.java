package org.vani.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.vani.core.VaniContext;

public abstract class VaniAnnotationProcessor implements BeanPostProcessor {
	protected ConfigurableListableBeanFactory configurableBeanFactory;
	@Autowired
	protected VaniContext vaniContext;

	@Autowired
	public VaniAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		this.configurableBeanFactory = beanFactory;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		this.configureFieldInjection(bean);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	protected abstract void configureFieldInjection(Object bean);

	public void setVaniContext(VaniContext vaniContext) {
		this.vaniContext = vaniContext;
	}
}
