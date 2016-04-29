package org.vani.core.wait;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.springframework.util.StringUtils;

public class ByWaitCommand extends WaitCommand<WebElement> {
	protected By by;
	protected SearchContext searchContext;

	public ByWaitCommand(By by, SearchContext searchContext) {
		super(null);
		this.by = by;
		this.searchContext = searchContext;
	}

	@Override
	public boolean eval() {
		try {
			target = by.findElement(searchContext);
		} catch (Exception ex) {
			if (!StringUtils.isEmpty(message)) {
				logger.warn(message);
			}
			throw ex;
		}

		boolean result = super.eval();
		return result;
	}
}
