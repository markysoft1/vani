package org.vani.spring;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.vani.core.VaniContext;
import org.vani.core.javascript.JavaScriptLoader;
import org.vani.core.locating.factory.AnnotationProxyFactory;
import org.vani.core.locating.factory.DefaultElementLocatorFactory;
import org.vani.core.locating.factory.DefaultLocatorBuilderFactory;
import org.vani.core.locating.factory.ElementLocatorFactory;
import org.vani.core.locating.factory.JavaScriptProxyFactory;
import org.vani.core.locating.factory.LocatorBuilderFactory;
import org.vani.core.locating.factory.RegionFactory;
import org.vani.core.locating.page.DefaultPageCrawler;
import org.vani.core.locating.page.DefaultPageHandlerFactory;
import org.vani.core.locating.page.PageCrawler;
import org.vani.core.locating.page.PageHandlerFactory;
import org.vani.core.util.VaniReflectionUtil;
import org.vani.core.wait.WaitUtil;

@Configuration
@Profile("!vani-custom")
public class VaniDefaultConfiguration implements ApplicationContextAware {
	@Autowired
	private Environment environment;
	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	private ApplicationContext applicationContext;
	private Reflections reflections;

	@Bean
	public JavaScriptLoader jsLoader() {
		return new JavaScriptLoader();
	}

	@Bean
	public LocatorBuilderFactory locatorBuilderFactory() {
		return new DefaultLocatorBuilderFactory(beanFactory);
	}

	@Bean
	public JavaScriptProxyFactory jsProxyFactory() {
		return new JavaScriptProxyFactory();
	}

	@Bean
	public VaniContext vaniContext() {
		VaniContext bean = new VaniContext();
		bean.setAppContext(applicationContext);
		bean.setEnvironment(environment);
		bean.setReflections(reflections());
		bean.setConfigurableBeanFactory(beanFactory);

		return bean;
	}

	@Bean
	public PageHandlerFactory pageHandlerFactory() {
		return new DefaultPageHandlerFactory();
	}

	@Bean
	public PageCrawler pageCrawler() {
		return new DefaultPageCrawler();
	}

	@Bean
	public WaitUtil waitUtil() {
		return new WaitUtil();
	}

	@Bean
	public PageAnnotationProcessor pageAnnotationProcessor() {
		PageAnnotationProcessor bean = new PageAnnotationProcessor(beanFactory);
		return bean;
	}

	@Bean
	public LocatorAnnotationProcessor locatorAnnotationProcessor() {
		LocatorAnnotationProcessor bean = new LocatorAnnotationProcessor(beanFactory);
		return bean;
	}

	@Bean
	public RegionFactory regionFactory() {
		return new SpringRegionFactory(beanFactory);
	}

	@Bean
	public VaniReflectionUtil reflectionUtil() {
		return new VaniReflectionUtil();
	}

	@Bean
	public AnnotationProxyFactory annotationProxyFactory() {
		return new AnnotationProxyFactory();
	}

	@Bean
	public ElementLocatorFactory elementLocatorFactory() {
		return new DefaultElementLocatorFactory();
	}

	protected Reflections reflections() {
		if (this.reflections == null) {
			//@formatter:off
			reflections= new Reflections(new ConfigurationBuilder()
					.setUrls(ClasspathHelper.forClassLoader())
					.setScanners(
							new SubTypesScanner(), 
							new TypeAnnotationsScanner(),
							new FieldAnnotationsScanner(),
							new MethodAnnotationsScanner(),
							new ResourcesScanner().filterResultsBy(
									new FilterBuilder().include(".xml").include(".properties").include(".txt").exclude(".pom").exclude(".MF").exclude(".SF").exclude(".RSA").exclude(".html"))
					));
			//@formatter:on
		}
		return reflections;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
