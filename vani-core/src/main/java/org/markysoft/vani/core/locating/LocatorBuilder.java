package org.markysoft.vani.core.locating;

import java.lang.annotation.Annotation;

import org.markysoft.vani.core.annotation.LocatorBuilderClass;
import org.openqa.selenium.By;

/**
 * The implementations of this interface are responsible for converting custom
 * locator annotation into custom {@link By} implementations.
 * 
 * <p>
 * If you want to implement your own locator annotations, you only have to
 * create the annotation and marks it with {@link LocatorBuilderClass}. After
 * that you add a new class implements this interface and convert your
 * annotation to your {@link By} implementation.
 * </p>
 * <p>
 * <b>{@code LocatorBuilder} won't be registered as spring bean at startup</b>,
 * but you can use the spring dependency injection features. If you want to get
 * an instance, you only have to use following code:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;Autowired
 * private LocatorBuilderFactory locatorBuilderFactory;
 * ...
 * 
 * 
 * locatorBuilderFactory.get(JQueryLocatorBuilder.class);
 * </code>
 * </pre>
 * 
 * @author Thomas
 *
 * @param <A>
 *            source annotation
 * @param <B>
 *            target {@link By} locator
 */
public interface LocatorBuilder<A extends Annotation, B extends By> {
	/**
	 * method to build the locator implementation.
	 * 
	 * @param annotation
	 * @return returns to implementation of specified locator annotation
	 */
	public B build(A annotation);
}
