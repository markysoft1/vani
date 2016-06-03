package org.markysoft.vani.core.javascript;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JsCallFunction;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.openqa.selenium.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * This class is responsible for reading source code of declared js-interfaces.
 * <p>
 * The resource path can be a filesystem path or classpath. Both are supported
 * and could be mixed.
 * </p>
 * <p>
 * For details of the source declaration see {@link JavaScript}.
 * </p>
 * 
 * @author Thomas
 *
 * @see JavaScript
 * @see DetectionScript
 * @see JsCallFunction
 * @see JavaScriptSource
 */
public class JavaScriptLoader {
	private Log logger = LogFactory.getLog(JavaScriptLoader.class);
	@Autowired
	private VaniReflectionUtil reflectionUtil;

	/**
	 * This method is responsible for loading the javascript code from
	 * filesystem or classpath. Additionally, the detection script and custom
	 * call function will be resolved when declared.
	 * 
	 * @param javaScript
	 *            annotation with source declaration
	 * @param jsInterface
	 * @param vaniContext
	 * @return returns a {@link JavaScriptSource} object, which contains the
	 *         source code, custom call function and detection script. If
	 *         resource could not be found, object will have an empty source.
	 * @throws JavaScriptReadException
	 *             will be thrown if an IOException occurs during reading js
	 *             source
	 */
	public <T> JavaScriptSource<T> load(JavaScript javaScript, Class<T> jsInterface, VaniContext vaniContext)
			throws JavaScriptReadException {
		String[] sourcePaths = javaScript.sources();
		if ((sourcePaths == null || sourcePaths.length == 0) && !StringUtils.isEmpty(javaScript.source())) {
			sourcePaths = new String[] { javaScript.source() };
		}

		StringBuilder source = new StringBuilder();
		if (sourcePaths != null) {
			for (String sourcePath : sourcePaths) {
				if (StringUtils.isEmpty(sourcePath)) {
					continue;
				}
				if (sourcePath.startsWith("classpath")) {
					String classpath = sourcePath.replace("classpath:", "");
					source.append(fromClasspath(classpath, vaniContext));
				} else {
					source.append(fromFilesystem(sourcePath, vaniContext));
				}
				source.append("\n");
			}
		}

		JavaScriptSource<T> result = new JavaScriptSource<>(jsInterface, source.toString());
		setDetectedMethod(result);
		setCallFunction(result, vaniContext);
		return result;
	}

	/**
	 * This method will look for method annotated with ({@link DetectionScript}
	 * in taret interface of provided {@link JavaScriptSource}. If a method is
	 * found, the javascript code of the annotation will be set to specified
	 * {@code jsSource} as detection script.
	 * 
	 * @param jsSource
	 */
	protected <T> void setDetectedMethod(JavaScriptSource<T> jsSource) {
		Method detectionMethod = reflectionUtil.getAnnotatedMethodWith(jsSource.getJsInterface(), DetectionScript.class,
				null);
		if (detectionMethod != null) {
			DetectionScript detectionAnnotation = detectionMethod.getDeclaredAnnotation(DetectionScript.class);
			jsSource.setDetectionScriptAnnotation(detectionAnnotation);
		}
	}

	/**
	 * This method will look for method annotated with {@link JsCallFunction} in
	 * the target interface of provided {@link JavaScriptSource}. If a method is
	 * found, the corresponding source will be loaded and set to the provided
	 * {@link JavaScriptSource} as custom javascript call function.
	 * <p>
	 * It supports filesystem and classpath declaration.
	 * </p>
	 * 
	 * @param jsSource
	 * @param vaniContext
	 */
	protected <T> void setCallFunction(JavaScriptSource<T> jsSource, VaniContext vaniContext) {
		Method callMethod = reflectionUtil.getAnnotatedMethodWith(jsSource.getJsInterface(), JsCallFunction.class,
				null);
		if (callMethod != null) {
			JsCallFunction callFuncAnnotation = callMethod.getDeclaredAnnotation(JsCallFunction.class);
			if (StringUtils.isEmpty(callFuncAnnotation.value())) {
				logger.warn("js-" + jsSource.getJsInterface() + " has callFunction without path!");
				return;
			}

			String source = "";
			String sourcePath = callFuncAnnotation.value();
			if (sourcePath.startsWith("classpath")) {
				String classpath = sourcePath.replace("classpath:", "");
				source = fromClasspath(classpath, vaniContext).toString();
			} else {
				source = fromFilesystem(sourcePath, vaniContext).toString();
			}

			if (!StringUtils.isEmpty(source)) {
				JavaScriptCallFunction callFunction = new JavaScriptCallFunction(callFuncAnnotation, source,
						callMethod);
				jsSource.setJsCallFunction(callFunction);
			}
		}
	}

	/**
	 * method to read javsScript file specified by path from classpath. If
	 * classpath contains a wildcard or there are resources with same name but
	 * paths are different, multiple entries will be returned.
	 * <p>
	 * If you provide resource and path, only matching resources with provided
	 * path will be used.
	 * </p>
	 * 
	 * @param sourcePath
	 * @param vaniContext
	 * @return returns {@link StringBuffer} containing only source code of
	 *         specified js-script by {@code sourcePath} or empty
	 *         {@link StringBuilder} if file does not exist
	 * @throws JavaScriptReadException
	 *             will be thrown if an IOException occurs during reading js
	 *             source
	 */
	protected StringBuilder fromClasspath(String classpath, VaniContext vaniContext) throws JavaScriptReadException {
		Set<String> paths = getResourcesPathFromClasspath(classpath, vaniContext);
		if (paths.isEmpty()) {
			logger.warn("no matching js-script files for classpath '" + classpath + "' found!");
		}
		StringBuilder source = new StringBuilder();
		for (String path : paths) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			InputStream is = ClassLoader.class.getResourceAsStream(path);
			if (is != null) {
				try {
					source.append(IOUtils.readFully(is));
					source.append("\n");
				} catch (Exception ex) {
					throw new JavaScriptReadException(
							"cannot read classpath js-file with path '" + path + "': " + ex.getMessage(), ex);
				}
			} else {
				logger.warn("js-script with classpath '" + path + "' not found!");
			}
		}
		return source;
	}

	/**
	 * method to read javsScript file specified by path from file system.
	 * 
	 * @param sourcePath
	 * @param vaniContext
	 * @return returns {@link StringBuffer} containing only source code of
	 *         specified js-script by {@code sourcePath} or empty
	 *         {@link StringBuilder} if file does not exist
	 * @throws JavaScriptReadException
	 *             will be thrown if an error occurs during collecting paths to
	 *             specified resources or reading source file
	 */
	protected StringBuilder fromFilesystem(String sourcePath, VaniContext vaniContext) throws JavaScriptReadException {
		StringBuilder result = new StringBuilder();
		List<Path> paths = new ArrayList<>();
		try {
			paths = getResourcesPathFromFilesystem(sourcePath);
		} catch (Throwable ex) {
			throw new JavaScriptReadException(
					"cannot collect paths for property source '" + sourcePath + "': " + ex.getMessage(), ex);
		}

		if (paths.isEmpty()) {
			logger.warn("requested resource '" + sourcePath + "' not found!");
			return result;
		}
		for (Path path : paths) {
			try {
				InputStream is = Files.newInputStream(path);
				if (is != null) {
					try {
						result.append(IOUtils.readFully(is));
					} finally {
						is.close();
					}
					result.append("\n");
				} else {
					logger.warn("resource path '" + path + "' not found!");
				}
			} catch (Exception ex) {
				throw new JavaScriptReadException(
						"cannot read java script source with path '" + path + "' from filesystem: " + ex.getMessage(),
						ex);
			}
		}

		return result;
	}

	/**
	 * method to get paths of resources on filesystem. Regex is <b>NOT</b>
	 * supported.
	 * <p>
	 * If you don't provide an path, it will look in current working path for
	 * it.
	 * </p>
	 * 
	 * @param sourceData
	 * @return returns paths for all matching filesystem resources
	 * @throws Throwable
	 *             will be thrown if an error occurs during search for given
	 *             resources on filesystem
	 */
	protected List<Path> getResourcesPathFromFilesystem(String resourcePath) throws Throwable {
		List<Path> result = new ArrayList<>();
		int idx = resourcePath.lastIndexOf("/");
		String path = "";
		if (idx > -1) {
			path = resourcePath.substring(0, idx);
			resourcePath = resourcePath.substring(idx + 1);
		}

		DirectoryStream<Path> stream = null;
		if (StringUtils.isEmpty(path)) {
			path = ".";
		}
		Path dir = Paths.get(path);
		stream = Files.newDirectoryStream(dir, resourcePath);
		stream.forEach(p -> result.add(p));

		return result;
	}

	/**
	 * method to get paths of classpath resources. If classpath contains a
	 * wildcard or there are resources with same name but paths are different,
	 * multiple entries will be returned.
	 * <p>
	 * If you provide resource and path, only matching resources with provided
	 * path will be returned.
	 * </p>
	 * 
	 * @param classpath
	 *            name of resource (also path and regex are supported)
	 * @param context
	 * @return returns paths for all matching classpath resources
	 */
	protected Set<String> getResourcesPathFromClasspath(String classpath, VaniContext context) {
		Set<String> result = null;
		boolean isPathUsed = classpath.contains("/");
		String path = "";
		if (isPathUsed) {
			int idx = classpath.lastIndexOf("/");
			path = classpath.substring(0, idx);
			classpath = classpath.substring(idx + 1);
		}
		result = context.getReflections().getResources(Pattern.compile(classpath));

		if (isPathUsed) {
			Set<String> matches = new HashSet<>();
			Pattern fullPattern = Pattern.compile(path + "/" + classpath);
			for (String entry : result) {
				if (fullPattern.matcher(entry).find()) {
					matches.add(entry);
				}
			}
			result = matches;
		}
		return result;
	}

	public void setReflectionUtil(VaniReflectionUtil reflectionUtil) {
		this.reflectionUtil = reflectionUtil;
	}
}
