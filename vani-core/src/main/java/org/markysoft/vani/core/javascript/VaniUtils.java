package org.markysoft.vani.core.javascript;

import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;

@JavaScript(source = "classpath:org/markysoft/vani/javascript/vani-utils.js")
public interface VaniUtils {
	@JavaScriptFunction(name = "window.vani.uuid4")
	String uuid4();

	@DetectionScript("window.vani !== undefined")
	public boolean isAvailable();

}
