package org.markysoft.vani.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class PageAnnotationProcessor extends VaniAnnotationProcessor {

	@Autowired
	public PageAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
		super(beanFactory);
	}

	@Override
	protected void configureFieldInjection(Object bean) {
		Class<?> managedBeanClass = bean.getClass();
		FieldCallback fieldCallback = new PageFieldCallback(bean, vaniContext);
		ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);
	}

}
