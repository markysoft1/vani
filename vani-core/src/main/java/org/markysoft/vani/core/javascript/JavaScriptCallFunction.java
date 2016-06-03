package org.markysoft.vani.core.javascript;

import java.lang.reflect.Method;

import org.markysoft.vani.core.annotation.JsCallFunction;

public class JavaScriptCallFunction {
	private JsCallFunction jsCcallFunctionAnnotation;
	private String callFunctionSource;
	private Method callMethod;

	public JavaScriptCallFunction(JsCallFunction callFunctionAnnotation, String callFunctionSource, Method callMethod) {
		super();
		this.jsCcallFunctionAnnotation = callFunctionAnnotation;
		this.callFunctionSource = callFunctionSource;
		this.callMethod = callMethod;
	}

	public JsCallFunction getJsCallFunctionAnnotation() {
		return jsCcallFunctionAnnotation;
	}

	public String getCallFunctionSource() {
		return callFunctionSource;
	}

	public Method getCallMethod() {
		return callMethod;
	}

}
