package org.markysoft.vani.core.wait;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public interface WaitConditionTargetBuilder {

	WaitOperatorBuilder element(WebElement element);

	WaitOperatorBuilder element(String selector);

	public WaitOperatorBuilder element(String selector, SearchContext rootElement);

	WaitOperatorBuilder webDriver(WebDriver webDriver);
}
