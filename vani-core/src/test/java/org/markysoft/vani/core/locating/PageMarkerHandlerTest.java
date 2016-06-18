package org.markysoft.vani.core.locating;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.javascript.VaniUtils;
import org.markysoft.vani.core.wait.WaitOperatorBuilder;
import org.markysoft.vani.core.wait.WaitUtil;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class PageMarkerHandlerTest {
	private static final String DEFAULT_MARKER_NAME = "pageMarker";
	private PageMarkerHandler handler;

	@Mock
	private VaniUtils vaniUtils;
	@Mock
	private WaitUtil waitUtil;

	@Mock
	private WebDriver webDriver;
	@Mock
	private WaitOperatorBuilder waitOperatorBuilder;

	@Before
	public void setUp() throws Exception {
		handler = new PageMarkerHandler();
		handler.setWaitUtil(waitUtil);
		handler.setVaniUtils(vaniUtils);
		handler.setDefaultName(DEFAULT_MARKER_NAME);

		when(waitUtil.variable(DEFAULT_MARKER_NAME)).thenReturn(waitOperatorBuilder);
		when(waitUtil.variable(PageMarkerHandler.VANI_PAGE_MARKER_NAME)).thenReturn(waitOperatorBuilder);
		when(waitOperatorBuilder.is((Predicate) anyObject())).thenReturn(waitOperatorBuilder);
		when(waitOperatorBuilder.not()).thenReturn(waitOperatorBuilder);
	}

	/**
	 * tests {@link PageMarkerHandler#setVaniMarker(WebDriver)}.
	 * <p>
	 * As result, js-variable for vani's page marker must be set.
	 * </p>
	 */
	@Test
	public void testSetVaniMarker() {
		System.out.println("setVaniMarker");

		handler.setVaniMarker(webDriver);

		verify(vaniUtils, times(1)).set(eq(PageMarkerHandler.VANI_PAGE_MARKER_NAME), anyLong(), eq(webDriver));
	}

	/**
	 * tests {@link PageMarkerHandler#waitUntilMarkerIsPresent} when no page
	 * marker name is provided and no name is set to handler.
	 * <p>
	 * As result, default markerName must be used.
	 * </p>
	 */
	@Test
	public void testWaitUntilMarkerIsPresentWithoutMarkerName() {
		System.out.println("testWaitUntilMarkerIsPresentWithoutMarkerName");

		handler.waitUntilMarkerIsPresent(null, 345, webDriver);

		verify(waitUtil, times(1)).variable(PageMarkerHandler.VANI_PAGE_MARKER_NAME);
		verify(waitUtil, times(1)).variable(DEFAULT_MARKER_NAME);
	}

	/**
	 * tests {@link PageMarkerHandler#waitUntilMarkerIsPresent} when no page
	 * marker name is provided.
	 * <p>
	 * As result, markerName of handler (not default name) must be used.
	 * </p>
	 */
	@Test
	public void testWaitUntilMarkerIsPresentWithoutMarkerNameParameter() {
		System.out.println("testWaitUntilMarkerIsPresentWithoutMarkerNameParameter");

		handler.setName("myPageMarker");
		when(waitUtil.variable("myPageMarker")).thenReturn(waitOperatorBuilder);

		handler.waitUntilMarkerIsPresent(null, 345, webDriver);

		verify(waitUtil, times(1)).variable(PageMarkerHandler.VANI_PAGE_MARKER_NAME);
		verify(waitUtil, times(1)).variable("myPageMarker");
	}

	/**
	 * tests {@link PageMarkerHandler#waitUntilMarkerIsPresent} when page marker
	 * name is provided.
	 * <p>
	 * As result, markerName of handler (not default name) must be used.
	 * </p>
	 */
	@Test
	public void testWaitUntilMarkerIsPresentWithMarkerNameParameter() {
		System.out.println("testWaitUntilMarkerIsPresentWithMarkerNameParameter");

		handler.setName("myPageMarker");
		when(waitUtil.variable("test")).thenReturn(waitOperatorBuilder);

		handler.waitUntilMarkerIsPresent("test", 345, webDriver);

		verify(waitUtil, times(1)).variable(PageMarkerHandler.VANI_PAGE_MARKER_NAME);
		verify(waitUtil, times(1)).variable("test");
	}

}
