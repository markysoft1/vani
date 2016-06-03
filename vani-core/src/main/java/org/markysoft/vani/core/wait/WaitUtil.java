package org.markysoft.vani.core.wait;

import java.util.concurrent.TimeUnit;

import org.markysoft.vani.core.VaniContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

public class WaitUtil {
	@Autowired
	protected VaniContext vaniContext;

	public WaitOperatorBuilder element(WebElement element) {
		return new WaitBuilder(vaniContext).element(element);
	}

	public WaitOperatorBuilder element(String selector) {
		return new WaitBuilder(vaniContext).element(selector);
	}

	public WaitOperatorBuilder element(String selector, SearchContext rootElement) {
		return new WaitBuilder(vaniContext).element(selector, rootElement);
	}

	public WaitOperatorBuilder webDriver(WebDriver value) {
		return new WaitBuilder(vaniContext).webDriver(value);
	}

	public void wait(long millis, WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(millis, TimeUnit.MILLISECONDS);
	}

	public void waitTime(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
		}
	}

	/**
	 * will wait until all jquery ajaxCalls are finished or given timeout is
	 * reached
	 * 
	 * @param timeoutInMillis
	 */
	public void ajaxJQuery(long timeoutInMillis, WebDriver webDriver) {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis() - start) < timeoutInMillis) {
			Long activeAjaxCalls = (Long) ((JavascriptExecutor) webDriver).executeScript("return $.active;");
			if (activeAjaxCalls > 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			} else {
				break;
			}
		}
	}

	/**
	 * will wait until all jquery ajaxCalls are finished or given timeout is
	 * reached
	 * 
	 * @param timeoutInMillis
	 * @param delayInMillis
	 *            after this delay the check begins to work
	 */
	public void ajaxJQuery(long timeoutInMillis, long delayInMillis, WebDriver webDriver) {
		waitTime(delayInMillis);
		ajaxJQuery(timeoutInMillis, webDriver);
	}

	/**
	 * will wait until a jquery ajaxCall for given URL (regex is also supported)
	 * is sent after provided ({@code startInMillis} mark or given
	 * {@code timeoutInMillis} is reached
	 * 
	 * @param url
	 *            requested url
	 * @param startInMillis
	 *            start time for filtering
	 * @param timeoutInMillis
	 * @param webDriver
	 */
	public void ajaxJQuery(String url, long startInMillis, long timeoutInMillis, WebDriver webDriver) {
		new WaitBuilder(vaniContext).ajax(url, startInMillis, webDriver).until(timeoutInMillis, 500, webDriver);
	}
}
