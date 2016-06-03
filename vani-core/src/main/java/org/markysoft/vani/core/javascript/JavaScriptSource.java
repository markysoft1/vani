package org.markysoft.vani.core.javascript;

import java.util.ArrayList;
import java.util.List;

import org.markysoft.vani.core.annotation.DetectionScript;

public class JavaScriptSource<T> {
	private String source;
	private String name;
	private Class<T> jsInterface;
	private List<JavaScriptSource<?>> dependencies;
	private List<JavaScriptSource<?>> plugins;
	private DetectionScript detectionScriptAnnotation;
	private JavaScriptCallFunction jsCallFunction;

	public JavaScriptSource() {
	}

	public JavaScriptSource(Class<T> jsInterface, String source) {
		this.source = source;
		this.jsInterface = jsInterface;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JavaScriptSource<?>> getDependencies() {
		if (dependencies == null) {
			dependencies = new ArrayList<>();
		}
		return dependencies;
	}

	public void setDependencies(List<JavaScriptSource<?>> dependencies) {
		this.dependencies = dependencies;
	}

	public void addDependency(JavaScriptSource dependency) {
		if (dependencies == null) {
			dependencies = new ArrayList<>(1);
		}

		dependencies.add(dependency);
	}

	public Class<T> getJsInterface() {
		return jsInterface;
	}

	public void setJsInterface(Class<T> jsInterface) {
		this.jsInterface = jsInterface;
	}

	public DetectionScript getDetectionScriptAnnotation() {
		return detectionScriptAnnotation;
	}

	public void setDetectionScriptAnnotation(DetectionScript detectionScriptAnnotation) {
		this.detectionScriptAnnotation = detectionScriptAnnotation;
	}

	public void setJsCallFunction(JavaScriptCallFunction jsCallFunction) {
		this.jsCallFunction = jsCallFunction;
	}

	public JavaScriptCallFunction getJsCallFunction() {
		return jsCallFunction;
	}

	public void addPlugin(JavaScriptSource<?> plugin) {
		if (this.plugins == null) {
			this.plugins = new ArrayList<>(1);
		}
		this.plugins.add(plugin);
	}

	public List<JavaScriptSource<?>> getPlugins() {
		if (this.plugins == null) {
			this.plugins = new ArrayList<>();
		}
		return plugins;
	}
}
