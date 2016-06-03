package org.markysoft.vani.core.wait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.locator.ByJQuery;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * This class provides convenience methods for declaring wait conditions.
 * 
 * @author Thomas
 *
 */
public class WaitBuilder implements WaitOperatorBuilder, WaitConditionTargetBuilder {
	protected List<WaitCommand> commands = new ArrayList<>(1);
	protected ConjunctionType conjunctionType;
	protected VaniContext vaniContext;

	public WaitBuilder(WaitCommand command, VaniContext vaniContext) {
		commands.add(command);
		this.vaniContext = vaniContext;
	}

	public WaitBuilder(VaniContext vaniContext) {
		this.vaniContext = vaniContext;
	}

	protected WaitCommand cmd() {
		return commands.isEmpty() ? null : commands.get(commands.size() - 1);
	}

	@Override
	public WaitOperatorBuilder is(Predicate<?> predicate) {
		cmd().setConditionPredicate(predicate);
		return this;
	}

	@Override
	public WaitOperatorBuilder is(Supplier<Boolean> supplier) {
		cmd().setConditionSupplier(supplier);
		return this;
	}

	@Override
	public WaitOperatorBuilder is(Function<?, Boolean> function) {
		cmd().setConditionFunction(function);
		return this;
	}

	@Override
	public WaitOperatorBuilder not() {
		cmd().setNotFlag(true);
		return this;
	}

	@Override
	public WaitConditionTargetBuilder and() {
		this.conjunctionType = ConjunctionType.AND;
		return this;
	}

	@Override
	public WaitConditionTargetBuilder or() {
		this.conjunctionType = ConjunctionType.OR;
		return this;
	}

	@Override
	public boolean until(long timeout, long period, WebDriver webDriver) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(webDriver).withTimeout(timeout, TimeUnit.MILLISECONDS)
				.pollingEvery(period, TimeUnit.MILLISECONDS);
		wait.ignoring(NoSuchElementException.class);
		return wait.until(evalFunction);
	}

	@Override
	public boolean until(long timeout, long period) {
		return until(timeout, period, null);
	}

	@Override
	public WaitOperatorBuilder element(WebElement element) {
		commands.add(new WebElementWaitCommand(element));
		return this;
	}

	@Override
	public WaitOperatorBuilder element(String selector) {
		commands.add(new ByWaitCommand(new ByJQuery(selector, vaniContext), null));
		return this;
	}

	@Override
	public WaitOperatorBuilder element(String selector, SearchContext rootElement) {
		commands.add(new ByWaitCommand(new ByJQuery(selector, vaniContext), rootElement));
		return this;
	}

	@Override
	public WaitOperatorBuilder webDriver(WebDriver webDriver) {
		commands.add(new WebDriverWaitCommand(webDriver));
		return this;
	}

	public WaitOperatorBuilder ajax(String url, long startInMillis, WebDriver webDriver) {
		commands.add(new AjaxWaitCommand(vaniContext, url, startInMillis, webDriver));
		return this;
	}

	protected com.google.common.base.Function<WebDriver, Boolean> evalFunction = new com.google.common.base.Function<WebDriver, Boolean>() {

		@Override
		public Boolean apply(WebDriver webDriver) {
			boolean result = true;
			for (WaitCommand cmd : commands) {
				result &= cmd.eval();
			}
			return result;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public <T, R> WaitOperatorBuilder has(Function<T, R> function, R expected) {
		cmd().setConditionPredicate(new Predicate<T>() {
			@Override
			public boolean test(T target) {
				R actual = function.apply(target);
				boolean result = expected.equals(actual);
				return result;
			}
		});

		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WaitOperatorBuilder spel(String condition) {
		cmd().setConditionPredicate(new Predicate<WebElement>() {
			ExpressionParser parser = new SpelExpressionParser();
			Expression exp = parser.parseExpression(condition);

			@Override
			public boolean test(WebElement element) {
				boolean result = exp.getValue(element, Boolean.class);
				return result;
			}
		});
		return this;
	}

}
