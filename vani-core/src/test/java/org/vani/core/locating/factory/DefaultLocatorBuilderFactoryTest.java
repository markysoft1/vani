package org.vani.core.locating.factory;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.vani.core.VaniContext;
import org.vani.core.locating.LocatorBuilder;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLocatorBuilderFactoryTest {
	private DefaultLocatorBuilderFactory bean;

	@Mock
	private VaniContext vaniContext;
	@Mock
	private ConfigurableListableBeanFactory configurableBeanFactory;

	@Mock
	private ApplicationContext appContext;

	@Before
	public void setUp() {
		bean = new DefaultLocatorBuilderFactory(configurableBeanFactory);
		bean.vaniContext = vaniContext;

		when(vaniContext.getAppContext()).thenReturn(appContext);
	}

	/**
	 * tests {@link DefaultLocatorBuilderFactory#get(Class)} when there is
	 * already an instance registered for provided {@link LocatorBuilder} class.
	 * <p>
	 * As result, registered instance must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testGetExistingLocatorBuilder() {
		System.out.println("testGetExistingLocatorBuilder");

		TestLocatorBuilder expected = new TestLocatorBuilder();
		String builderName = "testLocatorBuilder";
		when(configurableBeanFactory.containsBean(builderName)).thenReturn(true);
		when(configurableBeanFactory.getBean(builderName)).thenReturn(expected);

		LocatorBuilder result = bean.get(TestLocatorBuilder.class);

		verify(configurableBeanFactory, times(1)).getBean(builderName);
		verify(vaniContext, times(0)).createBean(anyObject());
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultLocatorBuilderFactory#get(Class)} when there is no
	 * registered instance for provided {@link LocatorBuilder} class.
	 * <p>
	 * As result, new instance must be created by
	 * {@link VaniContext#createBean(Class)} and registered as spring bean.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testGetNewLocatorBuilder() {
		System.out.println("testGetNewLocatorBuilder");

		TestLocatorBuilder expected = new TestLocatorBuilder();
		String builderName = "testLocatorBuilder";
		when(vaniContext.createBean(TestLocatorBuilder.class)).thenReturn(expected);

		LocatorBuilder result = bean.get(TestLocatorBuilder.class);

		verify(configurableBeanFactory, times(1)).registerSingleton(builderName, expected);
		verify(configurableBeanFactory, times(0)).getBean(anyString());
		verify(vaniContext, times(1)).createBean(TestLocatorBuilder.class);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong result: ", expected, result);
	}

	class TestLocatorBuilder implements LocatorBuilder {

		@Override
		public By build(Annotation annotation) {
			return null;
		}
	}
}
