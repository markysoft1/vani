package org.vani.core.locating.factory;

import java.lang.annotation.Annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vani.core.VaniContext;
import org.vani.core.util.AnnotationInterceptor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * This class wraps an annotation instance with another proxy instance, which
 * calls will be intercepted by @link {@link AnnotationInterceptor}.
 * 
 * @author Thomas
 *
 */
public class AnnotationProxyFactory {
	private final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private VaniContext vaniContext;

	public AnnotationProxyFactory() {
	}

	@SuppressWarnings("unchecked")
	public <R extends Annotation> R createProxy(R annotation) {
		R result;
		try {
			//@formatter:off
			result = (R) new ByteBuddy()
					.subclass(annotation.annotationType())
					.method(ElementMatchers.any())
					.intercept(MethodDelegation.to(new AnnotationInterceptor(annotation,vaniContext,this)))
					.make()
					.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded()
					.newInstance();
			//@formatter:on
		} catch (Exception ex) {
			logger.warn("cannot proxy locator annotation for resolving placeholders: " + ex.getMessage(), ex);
			result = annotation;
		}
		return result;
	}

}
