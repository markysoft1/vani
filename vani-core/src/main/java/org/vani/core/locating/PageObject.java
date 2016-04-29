package org.vani.core.locating;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vani.core.annotation.Page;
import org.vani.core.annotation.PageUrl;
import org.vani.core.util.VaniReflectionUtil;

public abstract class PageObject extends RegionObject {
	@Autowired
	protected VaniReflectionUtil reflectionUtil;
	protected String pageUrl;

	/**
	 * method will navigate to corresponding url of this page. It will use the
	 * resolved url from {@link Page} annotation or {@link PageUrl} annotation.
	 */
	public void to() {
		if (StringUtils.isEmpty(pageUrl)) {
			PageUrl aPageUrl = reflectionUtil.getTypeAnnotation(PageUrl.class, getClass());
			if (aPageUrl != null) {
				this.pageUrl = aPageUrl.value();
			}

			if (StringUtils.isEmpty(this.pageUrl)) {
				Method urlGetter = reflectionUtil.getAnnotatedMethodWith(getClass(), PageUrl.class, String.class);
				if (urlGetter != null) {
					try {
						this.pageUrl = (String) urlGetter.invoke(this);
					} catch (Exception ex) {
						throw new PageNavigationException(
								"cannot get page url from annotated method '" + urlGetter + "': " + ex, ex);
					}
				}
			}
		}

		if (StringUtils.isEmpty(pageUrl)) {
			throw new PageNavigationException("Page object '" + getClass() + "' has no definition for page url! ");
		} else {
			pageUrl = vaniContext.resolveExpression(pageUrl);
			webDriver.get(pageUrl);
		}
	}

}
