package org.markysoft.vani.core.javascript;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.javascript.JQuery;
import org.markysoft.vani.core.javascript.JQueryTypeHandler;
import org.markysoft.vani.core.javascript.JavaScriptCallFunction;
import org.markysoft.vani.core.locating.JQueryElement;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class JQueryTypeHandlerTest {
	private JQueryTypeHandler bean;

	@Mock
	private JQuery jquery;

	@Mock
	private WebDriver webDriver;

	@Captor
	private ArgumentCaptor<Pattern> captorPattern;
	@Captor
	private ArgumentCaptor<JavaScriptCallFunction> captorJsCallFunction;

	@Before
	public void setUp() {
		bean = new JQueryTypeHandler();
		bean.setjQuery(jquery);
	}

	/**
	 * tests {@link JQueryTypeHandler#get(String, WebDriver)} with reference and
	 * webDriver.
	 * <p>
	 * As result, new {@link JQueryElement} instance will be returned, which
	 * have the provided reference and webDriver.
	 * </p>
	 */
	@Test
	public void testGet() {
		System.out.println("testGet");

		String reference = "something";
		when(jquery.objectAttribute(anyObject(), eq("length"), eq(webDriver))).thenReturn(5L);

		JQueryElement result = bean.get(reference, webDriver);

		verify(jquery, times(1)).objectAttribute(result, "selector", webDriver);
		verify(jquery, times(1)).objectAttribute(result, "length", webDriver);
		Assert.assertEquals("wrong reference: ", reference, result.getReference());
	}

	/**
	 * tests {@link JQueryTypeHandler#getTargetType()}.
	 * <p>
	 * As result, new {@link JQueryElement} class must be returned.
	 * </p>
	 */
	@Test
	public void testGetTargetType() {
		System.out.println("testGetTargetType");

		Class<JQueryElement> result = bean.getTargetType();

		Assert.assertEquals("wrong target type: ", JQueryElement.class, result);
	}
}
