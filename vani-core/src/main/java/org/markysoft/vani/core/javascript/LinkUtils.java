package org.markysoft.vani.core.javascript;

import java.util.List;

import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptDependency;
import org.markysoft.vani.core.annotation.JavaScriptFunction;

@JavaScript(source = "classpath:org/markysoft/vani/javascript/link-utils.js")
@JavaScriptDependency({ JQuery.class })
public interface LinkUtils {

	@DetectionScript("window.vani !== undefined && window.vani.linkUtils !== undefined")
	public boolean isAvailable();

	@JavaScriptFunction(name = "window.vani.linkUtils.getApplicableUrls")
	public List<String> getApplicableUrls(String[] patterns);
}
