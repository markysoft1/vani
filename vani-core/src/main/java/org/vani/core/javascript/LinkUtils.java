package org.vani.core.javascript;

import java.util.List;

import org.vani.core.annotation.DetectionScript;
import org.vani.core.annotation.JavaScript;
import org.vani.core.annotation.JavaScriptDependency;
import org.vani.core.annotation.JavaScriptFunction;

@JavaScript(source = "classpath:org/vani/javascript/link-utils.js")
@JavaScriptDependency({ JQuery.class })
public interface LinkUtils {

	@DetectionScript("window.vani !== undefined && window.vani.linkUtils !== undefined")
	public boolean isAvailable();

	@JavaScriptFunction(name = "window.vani.linkUtils.getApplicableUrls")
	public List<String> getApplicableUrls(String[] patterns);
}
