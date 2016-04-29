package org.vani.core.wait;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;

public interface WaitOperatorBuilder {

	WaitOperatorBuilder is(Predicate<?> predicate);

	WaitOperatorBuilder is(Supplier<Boolean> supplier);

	WaitOperatorBuilder is(Function<?, Boolean> function);

	public <T, R> WaitOperatorBuilder has(Function<T, R> function, R expected);

	WaitOperatorBuilder not();

	public WaitConditionTargetBuilder and();

	public WaitConditionTargetBuilder or();

	/**
	 * this method executes the declared wait condition on specified
	 * {@code webDriver}.
	 * 
	 * @param timeout
	 *            maximum time to wait (in millis)
	 * @param period
	 *            after that time the check will be executed (in millis)
	 * @param webDriver
	 * @return returns true, if condition is fulfilled, else false
	 */
	public boolean until(long timeout, long period, WebDriver webDriver);

	/**
	 * this method executes the declared wait condition.
	 * 
	 * @param timeout
	 *            maximum time to wait (in millis)
	 * @param period
	 *            after that time the check will be executed (in millis)
	 * @return returns true, if condition is fulfilled, else false
	 */
	public boolean until(long timeout, long period);

	/**
	 * evaluate expresssion defined by spring expression language
	 * 
	 * @param condition
	 *            spel expression
	 */
	WaitOperatorBuilder spel(String condition);
}
