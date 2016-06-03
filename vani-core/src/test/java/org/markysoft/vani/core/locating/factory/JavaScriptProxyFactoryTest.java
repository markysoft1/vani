package org.markysoft.vani.core.locating.factory;

import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.ManualJavaScriptInterface;
import org.markysoft.vani.core.locating.factory.JavaScriptProxyFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.Reflections;

@RunWith(MockitoJUnitRunner.class)
public class JavaScriptProxyFactoryTest {
	private JavaScriptProxyFactory bean;

	@Mock
	private VaniContext vaniContext;

	@Mock
	private Reflections reflections;

	@Before
	public void setUp() {
		bean = new JavaScriptProxyFactory();
		bean.vaniContext = vaniContext;

		when(vaniContext.getReflections()).thenReturn(reflections);
	}

	/**
	 * tests
	 * {@link JavaScriptProxyFactory#loadManualJSInterfaceImplementation(Class)}
	 * when provided {@code jsInterfaceClass} doesn't have super interfaces.
	 * <p>
	 * As result, empty list must be returned,because provided class has no
	 * super interfaces.
	 * </p>
	 */
	@Test
	public void testLoadManualJSInterfaceImplementationWithoutSuperInterfaces() {
		System.out.println("testLoadManualJSInterfaceImplementationWithoutSuperInterfaces");

		Class<?> jsInterface = JsInterfaceWithoutSuperInterfaces.class;

		List<Object> result = bean.loadManualJSInterfaceImplementation(jsInterface);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertTrue("result must be empty, because jsInterface has no super interfaces!", result.isEmpty());
	}

	/**
	 * tests
	 * {@link JavaScriptProxyFactory#loadManualJSInterfaceImplementation(Class)}
	 * when provided {@code jsInterfaceClass} has super interfaces (non-manual
	 * and unimplemented manual).
	 * <p>
	 * As result, empty list must be returned,because provided class has no
	 * manual interfaces with implementations.
	 * </p>
	 */
	@Test
	public void testLoadManualJSInterfaceImplementationWithNonManualAndUnimplementedManualInterfaces() {
		System.out.println("testLoadManualJSInterfaceImplementationWithNonManualAndUnimplementedManualInterfaces");

		Class<?> jsInterface = JsInterfaceWithoutManualImplementations.class;
		when(reflections.getSubTypesOf(UnimplementedManualInterface.class)).thenReturn(new HashSet());

		List<Object> result = bean.loadManualJSInterfaceImplementation(jsInterface);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertTrue("result must be empty, because jsInterface has no manual interfaces with implementations!",
				result.isEmpty());
	}

	/**
	 * tests
	 * {@link JavaScriptProxyFactory#loadManualJSInterfaceImplementation(Class)}
	 * when provided {@code jsInterfaceClass} has super interfaces (non-manual
	 * and implemented manual).
	 * <p>
	 * As result, an instance of implemented manual must be contained in
	 * returned list.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testLoadManualJSInterfaceImplementationWithNonManualAndImplementedManualInterfaces() {
		System.out.println("testLoadManualJSInterfaceImplementationWithNonManualAndImplementedManualInterfaces");

		Class<?> jsInterface = JsInterfaceWithManualImplementation.class;
		when(reflections.getSubTypesOf(ManualInterface.class))
				.thenReturn(new HashSet(Arrays.asList(ManualInterfaceImpl.class)));
		ManualInterfaceImpl expected = new ManualInterfaceImpl();
		when(vaniContext.createBean(ManualInterfaceImpl.class)).thenReturn(expected);

		List<Object> result = bean.loadManualJSInterfaceImplementation(jsInterface);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong count: ", 1, result.size());
		Assert.assertEquals("wrong result on idx '0'", expected, result.get(0));
	}

	interface JsInterfaceWithoutSuperInterfaces {
	}

	interface JsInterfaceWithoutManualImplementations extends Serializable, UnimplementedManualInterface {
	}

	interface JsInterfaceWithManualImplementation extends Serializable, ManualInterface {
	}

	@ManualJavaScriptInterface
	interface ManualInterface {
	}

	@ManualJavaScriptInterface
	interface UnimplementedManualInterface {
	}

	class ManualInterfaceImpl implements ManualInterface {
	}
}
