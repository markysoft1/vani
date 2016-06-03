package org.markysoft.vani.core.locating.factory;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.JQueryElement;
import org.markysoft.vani.core.locating.JQueryElementLocator;
import org.markysoft.vani.core.locating.RegionElementLocator;
import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.VaniElementLocator;
import org.markysoft.vani.core.locating.WebElementLocator;
import org.markysoft.vani.core.util.FieldTypeInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation is able to handle following field types:
 * <ul>
 * <li>{@link WebElement} => {@link WebElementLocator}</li>
 * <li>{@link RegionObject} => {@link RegionElementLocator}</li>
 * </ul>
 * For {@link RegionElementLocator} instance, the {@code shouldCache}-flag will
 * be set to {@code true}.
 * 
 * @author Thomas
 *
 */
public class DefaultElementLocatorFactory implements ElementLocatorFactory {
	@Autowired
	protected VaniContext vaniContext;
	@Autowired
	protected RegionFactory regionFactory;

	public DefaultElementLocatorFactory() {
	}

	@Override
	public VaniElementLocator<?> createLocator(FieldTypeInfo fieldTypeInfo, SearchContext searchContext, By by,
			boolean shouldCache) {
		Class<?> type = fieldTypeInfo.getTargetType();

		VaniElementLocator<?> result = null;
		if (JQueryElement.class.isAssignableFrom(type)) {
			result = new JQueryElementLocator(searchContext, by, shouldCache, fieldTypeInfo, vaniContext);
		} else if (WebElement.class.isAssignableFrom(type)) {
			result = new WebElementLocator(searchContext, by, shouldCache, fieldTypeInfo, vaniContext);
		} else if (RegionObject.class.isAssignableFrom(type)) {
			result = new RegionElementLocator(searchContext, by, true, fieldTypeInfo, regionFactory, vaniContext);
		}
		return result;
	}

}
