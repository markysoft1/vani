package org.markysoft.vani.core.wait;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.javascript.XhrTracking;
import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

public class AjaxWaitCommand extends WaitCommand<Object> {
	protected VaniContext vaniContext;
	protected long startMillis;
	protected String url;
	protected XhrTracking xhrTracking;
	protected WebDriver webDriver;

	public AjaxWaitCommand(VaniContext vaniContext, String url, long startMillis, WebDriver webDriver) {
		super(null);
		this.vaniContext = vaniContext;
		this.url = url;
		this.startMillis = startMillis;
		this.xhrTracking = vaniContext.getAppContext().getBean(XhrTracking.class);
		this.webDriver = webDriver;
	}

	@Override
	public boolean eval() {
		boolean result = false;
		try {
			result = xhrTracking.hasRequestFor(url, startMillis, webDriver);
		} catch (Exception ex) {
			if (!StringUtils.isEmpty(message)) {
				logger.warn(message);
			}
			throw ex;
		}
		return result;
	}
}
