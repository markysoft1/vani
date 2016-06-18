package org.markysoft.vani.core.locating;

import org.markysoft.vani.core.condition.Is;
import org.markysoft.vani.core.javascript.VaniUtils;
import org.markysoft.vani.core.wait.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class PageMarkerHandler {
	protected final static String VANI_PAGE_MARKER_NAME = "vaniPageMarker";
	private String name;
	@Value("${vani.pageMarker.name:pageIsReady}")
	private String defaultName;
	@Autowired
	private VaniUtils vaniUtils;
	@Autowired
	private WaitUtil waitUtil;
	private long timeoutInMillis = 30 * 1000;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTimeoutInMillis(long timeoutInMillis) {
		this.timeoutInMillis = timeoutInMillis;
	}

	public void setVaniMarker(WebDriver webDriver) {
		vaniUtils.set(VANI_PAGE_MARKER_NAME, System.currentTimeMillis(), webDriver);
	}

	public void waitUntilMarkerIsPresent(WebDriver webDriver) {
		this.waitUntilMarkerIsPresent(name, timeoutInMillis, webDriver);
	}

	public void waitUntilMarkerIsPresent(String markerName, long timeoutInMillis, WebDriver webDriver) {
		waitUtil.variable(VANI_PAGE_MARKER_NAME).is(Is::present).not().until(timeoutInMillis, 500, webDriver);

		if (StringUtils.isEmpty(markerName)) {
			markerName = getPageMarkerName();
		}
		waitUtil.variable(markerName).is(Is::present).until(timeoutInMillis, 500, webDriver);
	}

	protected String getPageMarkerName() {
		String result = name;
		if (StringUtils.isEmpty(result)) {
			result = defaultName;
		}

		return result;
	}

	public void setVaniUtils(VaniUtils vaniUtils) {
		this.vaniUtils = vaniUtils;
	}

	public void setWaitUtil(WaitUtil waitUtil) {
		this.waitUtil = waitUtil;
	}

	protected void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}
}
