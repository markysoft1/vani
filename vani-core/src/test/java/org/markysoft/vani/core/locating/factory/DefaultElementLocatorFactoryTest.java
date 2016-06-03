package org.markysoft.vani.core.locating.factory;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.locating.RegionElementLocator;
import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.VaniElementLocator;
import org.markysoft.vani.core.locating.WebElementLocator;
import org.markysoft.vani.core.locating.factory.DefaultElementLocatorFactory;
import org.markysoft.vani.core.locating.factory.RegionFactory;
import org.markysoft.vani.core.util.FieldTypeInfo;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class DefaultElementLocatorFactoryTest {
	private DefaultElementLocatorFactory bean;

	@Mock
	private VaniContext vaniContext;
	@Mock
	private RegionFactory regionFactory;

	@Mock
	private FieldTypeInfo fieldTypeInfo;
	@Mock
	private SearchContext searchContext;
	@Mock
	private By by;
	@Mock
	private ApplicationContext appContext;

	@Before
	public void setUp() {
		bean = new DefaultElementLocatorFactory();
		bean.vaniContext = vaniContext;
		bean.regionFactory = regionFactory;

		when(vaniContext.getAppContext()).thenReturn(appContext);
	}

	/**
	 * tests
	 * {@link DefaultElementLocatorFactory#createLocator(FieldTypeInfo, SearchContext, By, boolean)}
	 * when target type of underlying field is not a {@link WebElement} or
	 * {@link RegionObject}.
	 * <p>
	 * As result, {@code NULL} must be returned, because there is no
	 * {@link ElementLocator} for given {@code targetType}.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCreateLocatorWithUnsupportedType() {
		System.out.println("testCreateLocatorWithUnsupportedType");

		when(fieldTypeInfo.getTargetType()).thenReturn((Class) String.class);

		VaniElementLocator result = bean.createLocator(fieldTypeInfo, searchContext, by, false);

		Assert.assertNull("result must be NULL, because provided type is unsupported!", result);
	}

	/**
	 * tests
	 * {@link DefaultElementLocatorFactory#createLocator(FieldTypeInfo, SearchContext, By, boolean)}
	 * when target type of underlying field is {@link WebElement}.
	 * <p>
	 * As result, {@code WebElementLocator} must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCreateLocatorForWebElement() {
		System.out.println("testCreateLocatorForWebElement");

		when(fieldTypeInfo.getTargetType()).thenReturn((Class) WebElement.class);

		VaniElementLocator result = bean.createLocator(fieldTypeInfo, searchContext, by, false);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong type of locator: ", WebElementLocator.class, result.getClass());
	}

	/**
	 * tests
	 * {@link DefaultElementLocatorFactory#createLocator(FieldTypeInfo, SearchContext, By, boolean)}
	 * when target type of underlying field is {@link RegionObject}.
	 * <p>
	 * As result, {@code RegionElementLocator} must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCreateLocatorForRegionElement() {
		System.out.println("testCreateLocatorForRegionElement");

		when(fieldTypeInfo.getTargetType()).thenReturn((Class) PageObject.class);

		VaniElementLocator result = bean.createLocator(fieldTypeInfo, searchContext, by, false);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong type of locator: ", RegionElementLocator.class, result.getClass());
	}

}
