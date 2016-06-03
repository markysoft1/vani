package org.markysoft.vani.core.wait;

import org.openqa.selenium.WebElement;

public class WebElementWaitCommand extends WaitCommand<WebElement> {
	public WebElementWaitCommand(WebElement target) {
		super(target);
	}

}
