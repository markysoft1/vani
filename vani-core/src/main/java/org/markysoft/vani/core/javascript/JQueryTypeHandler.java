package org.markysoft.vani.core.javascript;

import org.markysoft.vani.core.annotation.JsTypeHandler;
import org.markysoft.vani.core.locating.JQueryElement;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This {@link TypeHandler} is responsible for converting the cache reference to
 * {@link JQueryElement}.
 * <p>
 * The js-interface {@link JQuery} returns {@link JQueryElement}, but the call
 * function for all jquery functions will cache the matching jquery object and
 * returns its cache reference. So this string must be wrapped by a new
 * {@link JQueryElement}.
 * </p>
 * 
 * @author Thomas
 *
 */
@JsTypeHandler
public class JQueryTypeHandler implements TypeHandler<JQueryElement, String> {
	@Autowired
	private JQuery jQuery;

	@Override
	public JQueryElement get(String reference, WebDriver webDriver) {
		JQueryElement result = new JQueryElement(jQuery, webDriver, reference);
		return result;
	}

	@Override
	public Class<JQueryElement> getTargetType() {
		return JQueryElement.class;
	}

	public void setjQuery(JQuery jQuery) {
		this.jQuery = jQuery;
	}
}
