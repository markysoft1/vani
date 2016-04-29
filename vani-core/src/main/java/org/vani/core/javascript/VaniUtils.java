package org.vani.core.javascript;

import org.vani.core.annotation.DetectionScript;
import org.vani.core.annotation.JavaScript;
import org.vani.core.annotation.JavaScriptFunction;

@JavaScript(source = "classpath:org/vani/javascript/vani-utils.js")
public interface VaniUtils {
	@JavaScriptFunction(name = "window.vani.uuid4")
	String uuid4();

	@DetectionScript("window.vani !== undefined")
	public boolean isAvailable();

}
