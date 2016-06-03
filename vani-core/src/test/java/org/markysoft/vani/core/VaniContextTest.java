package org.markysoft.vani.core;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptDependency;
import org.markysoft.vani.core.annotation.JsTypeHandler;
import org.markysoft.vani.core.javascript.JQueryTypeHandler;
import org.markysoft.vani.core.javascript.JavaScriptLoader;
import org.markysoft.vani.core.javascript.JavaScriptSource;
import org.markysoft.vani.core.javascript.TypeHandler;
import org.markysoft.vani.core.locating.JQueryElement;
import org.markysoft.vani.core.locating.factory.JavaScriptProxyFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@RunWith(MockitoJUnitRunner.class)
public class VaniContextTest {
	private VaniContext bean;

	@Mock
	private Reflections reflections;
	@Mock
	private Environment environment;
	@Mock
	private ApplicationContext appContext;
	@Mock
	private ConfigurableListableBeanFactory configurableBeanFactory;
	@Mock
	private JavaScriptLoader jsLoader;
	@Mock
	private JavaScriptProxyFactory jsProxyFactory;

	@Mock
	private Profile profileAnnotation;
	@Mock
	private JavaScriptSource jsSource;
	@Mock
	private JavaScriptSource jsSourceWithDependency;
	@Mock
	private JavaScriptSource jsSourceWithPlugin;
	@Mock
	private Object jsInterfaceProxy;
	@Mock
	private Object initialisedBean;

	@Captor
	private ArgumentCaptor<WebDriver> captorDriver;
	@Captor
	private ArgumentCaptor<Class<?>> captorJSInterface;
	@Captor
	private ArgumentCaptor<JavaScript> captorJavaScript;
	@Captor
	private ArgumentCaptor<JavaScriptSource> captorJSSource;
	@Captor
	private ArgumentCaptor<String> captorString;
	@Captor
	private ArgumentCaptor<Object> captorObject;

	@Before
	public void setUp() {
		bean = new VaniContext();
		bean.setAppContext(appContext);
		bean.setConfigurableBeanFactory(configurableBeanFactory);
		bean.setEnvironment(environment);
		bean.setJsLoader(jsLoader);
		bean.setJsProxyFactory(jsProxyFactory);
		bean.setReflections(reflections);
	}

	/**
	 * tests
	 * {@link VaniContext#isProfileEnabled(org.springframework.context.annotation.Profile)}
	 * with {@code NULL} as parameter.
	 * <p>
	 * As result, {@code false} should be returned, because {@code NULL} as
	 * parameter value will skip checking.
	 * </p>
	 */
	@Test
	public void testIsProfileEnabledWithNull() {
		System.out.println("testIsProfileEnabledWithNull");

		boolean result = bean.isProfileEnabled(null);

		Assert.assertFalse("false must be returned because of NULL as parameter value!", result);
		verify(environment, times(0)).getActiveProfiles();
	}

	/**
	 * tests
	 * {@link VaniContext#isProfileEnabled(org.springframework.context.annotation.Profile)}
	 * when no active profiles are available.
	 * <p>
	 * As result, {@code false} should be returned, because there are no active
	 * profiles.
	 * </p>
	 */
	@Test
	public void testIsProfileEnabledWithoutActiveProfiles() {
		System.out.println("testIsProfileEnabledWithoutActiveProfiles");

		boolean result = bean.isProfileEnabled(profileAnnotation);

		Assert.assertFalse("false must be returned, because there are no active profiles!", result);
		verify(environment, times(1)).getActiveProfiles();
		verify(profileAnnotation, times(0)).value();
	}

	/**
	 * tests
	 * {@link VaniContext#isProfileEnabled(org.springframework.context.annotation.Profile)}
	 * when an active profile is available, but given annotation specify no
	 * profiles.
	 * <p>
	 * As result, {@code false} should be returned, because no profiles are
	 * specified for checking.
	 * </p>
	 */
	@Test
	public void testIsProfileEnabledWithAnnotationWithoutProfiles() {
		System.out.println("testIsProfileEnabledWithAnnotationWithoutProfiles");

		when(environment.getActiveProfiles()).thenReturn(new String[] { "profile1" });
		when(profileAnnotation.value()).thenReturn(new String[] {});

		boolean result = bean.isProfileEnabled(profileAnnotation);

		Assert.assertFalse("false must be returned because of annotation has no profiles to check!", result);
		verify(environment, times(1)).getActiveProfiles();
		verify(profileAnnotation, times(1)).value();
	}

	/**
	 * tests
	 * {@link VaniContext#isProfileEnabled(org.springframework.context.annotation.Profile)}
	 * when active profiles are available, but given annotation specify inactive
	 * profile.
	 * <p>
	 * As result, {@code false} should be returned, because specified no profile
	 * is inactive.
	 * </p>
	 */
	@Test
	public void testIsProfileEnabledWithInactiveProfiles() {
		System.out.println("testIsProfileEnabledWithInactiveProfiles");

		when(environment.getActiveProfiles()).thenReturn(new String[] { "profile1", "profile2" });
		when(profileAnnotation.value()).thenReturn(new String[] { "profile164" });

		boolean result = bean.isProfileEnabled(profileAnnotation);

		Assert.assertFalse("false must be returned, because specified profile is inactive!", result);
		verify(environment, times(1)).getActiveProfiles();
		verify(profileAnnotation, times(1)).value();
	}

	/**
	 * tests
	 * {@link VaniContext#isProfileEnabled(org.springframework.context.annotation.Profile)}
	 * when active profiles are available, but given annotation specify inactive
	 * profile.
	 * <p>
	 * As result, {@code false} should be returned, because specified no profile
	 * is inactive.
	 * </p>
	 */
	@Test
	public void testIsProfileEnabledWithActiveProfiles() {
		System.out.println("testIsProfileEnabledWithActiveProfiles");

		when(environment.getActiveProfiles()).thenReturn(new String[] { "profile1", "profile2" });
		when(profileAnnotation.value()).thenReturn(new String[] { "profile165", "profile1" });

		boolean result = bean.isProfileEnabled(profileAnnotation);

		Assert.assertTrue("true must be returned, because one profile is active!", result);
		verify(environment, times(1)).getActiveProfiles();
		verify(profileAnnotation, times(1)).value();
	}

	/**
	 * tests {@link VaniContext#resolveExpression(String)} with empty string as
	 * parameter.
	 * <p>
	 * As result, empty literal should be returned.
	 * </p>
	 */
	@Test
	public void testResolveExpressionWithEmptyLiteral() {
		System.out.println("testResolveExpressionWithEmptyLiteral");

		String literal = "";
		when(configurableBeanFactory.resolveEmbeddedValue(literal)).thenReturn(literal);

		String result = bean.resolveExpression(literal);

		Assert.assertEquals("wrong result: ", literal, result);
		verify(configurableBeanFactory, times(1)).resolveEmbeddedValue(literal);
	}

	/**
	 * tests {@link VaniContext#resolveExpression(String)} with string without
	 * placeholder.
	 * <p>
	 * As result, same string as provided must be returned.
	 * </p>
	 */
	@Test
	public void testResolveExpressionWithoutExpresion() {
		System.out.println("testResolveExpressionWithoutExpresion");

		String literal = "This has no placeholder!";
		when(configurableBeanFactory.resolveEmbeddedValue(literal)).thenReturn(literal);

		String result = bean.resolveExpression(literal);

		Assert.assertEquals("wrong result: ", literal, result);
		verify(configurableBeanFactory, times(1)).resolveEmbeddedValue(literal);
	}

	/**
	 * tests {@link VaniContext#resolveExpression(String)} with string with
	 * placeholder.
	 * <p>
	 * As result, string with resolved placeholder must be returned.
	 * </p>
	 */
	@Test
	public void testResolveExpressionWithExpresion() {
		System.out.println("testResolveExpressionWithExpresion");

		String literal = "This has no ${label.hint}!";
		String expected = "This has no placeholder!";
		when(configurableBeanFactory.resolveEmbeddedValue(literal)).thenReturn(expected);

		String result = bean.resolveExpression(literal);

		Assert.assertEquals("wrong result: ", expected, result);
		verify(configurableBeanFactory, times(1)).resolveEmbeddedValue(literal);
	}

	/**
	 * tests {@link VaniContext#createDefaultDriver()} when no JS-interfaces
	 * exists.
	 * <p>
	 * As result, returned instance must be registered as singelton.
	 * </p>
	 */
	@Test
	public void testCreateDefaultDriver() {
		System.out.println("testCreateDefaultDriver");

		WebDriver result = bean.createDefaultDriver();

		verify(configurableBeanFactory, times(1)).registerSingleton(eq("firefoxDriver"), captorDriver.capture());
		Assert.assertNotNull("no instance found!", result);
		Assert.assertEquals("created webDriver was not registered: ", result, captorDriver.getValue());

		result.quit();
	}

	/**
	 * tests {@link VaniContext#registerJavaScripts()}} when no JS-interfaces
	 * exists.
	 * <p>
	 * As result, nothing should be done, because no JS-interface are available.
	 * </p>
	 */
	@Test
	public void testRegisterJavaScriptsWithoutJSInterfaces() {
		System.out.println("testRegisterJavaScriptsWithoutJSInterfaces");

		bean.registerJavaScripts();

		verify(reflections, times(1)).getTypesAnnotatedWith(JavaScript.class);
		verify(reflections, times(1)).getTypesAnnotatedWith(JsTypeHandler.class);
		verify(jsLoader, times(0)).load(anyObject(), anyObject(), anyObject());
	}

	/**
	 * tests {@link VaniContext#registerJavaScripts()}} with different
	 * js-interfaces (normal, with dependency, with plugin).
	 * <p>
	 * As result, js-interfaces should be loaded, proxied and registered as
	 * singleton.
	 * </p>
	 */
	@Test
	public void testRegisterJavaScripts() {
		System.out.println("testRegisterJavaScripts");

		Set<Class<?>> jsClasses = new HashSet<Class<?>>(Arrays.asList(JSInterface.class,
				JSInterfaceWithDependency.class, JSInterfaceWithSuper.class, JSInterfaceWithSuperJSInterface.class,
				JSInterfaceWithUnknownDependency.class, JSInterfaceWithUnknownJSInterface.class));
		List<JavaScript> jsAnnotations = new ArrayList<>(jsClasses.size());
		jsClasses.forEach(c -> jsAnnotations.add(c.getDeclaredAnnotation(JavaScript.class)));
		Set<String> expectedBeanNames = new HashSet<>(Arrays.asList("jSInterface", "jSInterfaceWithDependency",
				"jSInterfaceWithSuper", "jSInterfaceWithSuperJSInterface", "jSInterfaceWithUnknownJSInterface",
				"jSInterfaceWithUnknownDependency"));

		when(reflections.getTypesAnnotatedWith(JavaScript.class)).thenReturn(jsClasses);
		when(jsLoader.load(anyObject(), eq(JSInterface.class), eq(bean))).thenReturn(jsSource);
		when(jsLoader.load(anyObject(), eq(JSInterfaceWithSuper.class), eq(bean))).thenReturn(jsSource);
		when(jsLoader.load(anyObject(), eq(JSInterfaceWithDependency.class), eq(bean)))
				.thenReturn(jsSourceWithDependency);
		when(jsLoader.load(anyObject(), eq(JSInterfaceWithSuperJSInterface.class), eq(bean)))
				.thenReturn(jsSourceWithPlugin);
		when(jsLoader.load(anyObject(), eq(JSInterfaceWithUnknownDependency.class), eq(bean))).thenReturn(jsSource);
		when(jsLoader.load(anyObject(), eq(JSInterfaceWithUnknownJSInterface.class), eq(bean))).thenReturn(jsSource);
		Set<JavaScriptSource<?>> expectedSources = new HashSet<>(
				Arrays.asList(jsSource, jsSourceWithDependency, jsSourceWithPlugin));
		when(jsProxyFactory.createProxy(anyObject())).thenReturn(jsInterfaceProxy);

		bean.registerJavaScripts();

		verify(reflections, times(1)).getTypesAnnotatedWith(JavaScript.class);
		// -------verify jsLoader.load------------
		verify(jsLoader, times(6)).load(captorJavaScript.capture(), captorJSInterface.capture(), eq(bean));
		Assert.assertArrayEquals("wrong JS annotations found: ", jsAnnotations.toArray(),
				captorJavaScript.getAllValues().toArray());
		Assert.assertArrayEquals("wrong JS interfaces found: ", jsClasses.toArray(),
				captorJSInterface.getAllValues().toArray());
		// ------verify jsProxyFactory.createProxy------
		verify(jsProxyFactory, times(6)).createProxy(captorJSSource.capture());
		for (JavaScriptSource<?> actualSource : captorJSSource.getAllValues()) {
			Assert.assertTrue("unexpected js-Source found!", expectedSources.contains(actualSource));
		}
		verify(jsSource, times(0)).addDependency(anyObject());
		verify(jsSource, times(0)).addPlugin(anyObject());
		verify(jsSourceWithDependency, times(1)).addDependency(jsSource);
		verify(jsSourceWithDependency, times(0)).addPlugin(anyObject());
		verify(jsSourceWithPlugin, times(1)).addPlugin(jsSource);
		verify(jsSourceWithPlugin, times(0)).addDependency(anyObject());
		// -----verify configurableBeanFactory.registerSingleton-------
		verify(configurableBeanFactory, times(6)).registerSingleton(captorString.capture(), eq(jsInterfaceProxy));
		List<String> actualBeanNames = captorString.getAllValues();
		for (String actualBeanName : actualBeanNames) {
			Assert.assertTrue("wrong registered jsInterface beanName '" + actualBeanName + "': ",
					expectedBeanNames.remove(actualBeanName));
		}

		verify(reflections, times(1)).getTypesAnnotatedWith(JsTypeHandler.class);
	}

	@JavaScript(name = "normalJS")
	interface JSInterface {
	}

	@JavaScript(name = "withDependency")
	@JavaScriptDependency(JSInterface.class)
	interface JSInterfaceWithDependency {
	}

	@JavaScript(name = "super")
	interface JSInterfaceWithSuper extends Serializable {
	}

	@JavaScript(name = "superJS")
	interface JSInterfaceWithSuperJSInterface extends Serializable, JSInterface {
	}

	@JavaScript(name = "withUnknownDependency")
	@JavaScriptDependency(UnknownJSInterface.class)
	interface JSInterfaceWithUnknownDependency {
	}

	@JavaScript(name = "withUnknownPlugin")
	interface JSInterfaceWithUnknownJSInterface extends Serializable, UnknownJSInterface {
	}

	@JavaScript(name = "unknownJS")
	interface UnknownJSInterface {
	}

	/**
	 * tests {@link VaniContext#initJsTypeHandler()} when no JS-typeHandler
	 * exists.
	 * <p>
	 * As result, nothing should be done, because no JS-typeHandler are
	 * available.
	 * </p>
	 */
	@Test
	public void testInitJsTypeHandlerWithoutHandlers() {
		System.out.println("testInitJsTypeHandlerWithoutHandlers");

		bean.initJsTypeHandler();

		verify(reflections, times(1)).getTypesAnnotatedWith(JsTypeHandler.class);
		verify(configurableBeanFactory, times(0)).initializeBean(anyObject(), anyObject());
	}

	/**
	 * tests {@link VaniContext#initJsTypeHandler()} registering only
	 * {@link JQueryTypeHandler}.
	 * <p>
	 * As result, instance of type-handler must be created and initialised by
	 * spring. Finally {@link VaniContext#getTypeHandlerFor(Class)} must return
	 * created instance for {@link JQueryElement}.
	 * </p>
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testInitJsTypeHandler() {
		System.out.println("testInitJsTypeHandler");

		when(reflections.getTypesAnnotatedWith(JsTypeHandler.class))
				.thenReturn(new HashSet<>(Arrays.asList(JQueryTypeHandler.class)));
		when(configurableBeanFactory.initializeBean(anyObject(), eq("jQueryTypeHandler"))).thenReturn(initialisedBean);

		bean.initJsTypeHandler();

		verify(reflections, times(1)).getTypesAnnotatedWith(JsTypeHandler.class);
		verify(configurableBeanFactory, times(1)).initializeBean(captorObject.capture(), anyString());
		verify(configurableBeanFactory, times(1)).autowireBeanProperties(initialisedBean,
				AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);

		TypeHandler actualHandler = bean.getTypeHandlerFor(JQueryElement.class);
		Assert.assertEquals("wrong typeHandler for JQueryElement: ", captorObject.getValue(), actualHandler);
	}

	/**
	 * tests {@link VaniContext#getTypeHandlerFor(Class)} when no JS-typeHandler
	 * were registered.
	 * <p>
	 * As result, {@code NULL} should be returned, because no JS-typeHandler are
	 * available.
	 * </p>
	 */
	@Test
	public void testGetTypeHandlerForWhenNoHandlersAreAvailable() {
		System.out.println("testGetTypeHandlerForWhenNoHandlersAreAvailable");

		JQueryTypeHandler result = (JQueryTypeHandler) bean.getTypeHandlerFor(JQueryElement.class);

		Assert.assertNull("result must be NULL, because no handlers were registered!", result);
	}

	/**
	 * tests {@link VaniContext#getTypeHandlerFor(Class)} when no appropriate
	 * JS-typeHandler is registered.
	 * <p>
	 * As result, {@code NULL} should be returned, because no appropriate
	 * JS-typeHandler is available.
	 * </p>
	 */
	@Test
	public void testGetTypeHandlerForUnknownType() {
		System.out.println("testGetTypeHandlerForUnknownType");

		bean.registerTypeHandler(new TypeHandler<Integer, String>() {
			@Override
			public Class<Integer> getTargetType() {
				return Integer.class;
			}

			@Override
			public Integer get(String scriptResult, WebDriver webDriver) {
				return null;
			}
		});
		JQueryTypeHandler result = (JQueryTypeHandler) bean.getTypeHandlerFor(JQueryElement.class);

		Assert.assertNull("result must be NULL, because no appropriate handler is available!", result);
	}
}
