package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Declares the annotated page object as start page. This means that the url of
 * the page object will be loaded by webdriver before your test code starts to
 * run.
 * </p>
 * <p>
 * Usage:
 * 
 * <pre>
 * ...
 * public class ExampleTest{
 *  &#064;Page
 * 	&#064;Startpage
 * 	private HomePage home;
 * 
 * 	&#064;Test
 * 	public void test1(){
 * 		home.to();
 * 	}
 * }
 * </pre>
 * 
 * In this example the url of HomePage will be loaded before the code in 'test1'
 * will be executed.
 * </p>
 * 
 * @author Thomas
 *
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Startpage {

}
