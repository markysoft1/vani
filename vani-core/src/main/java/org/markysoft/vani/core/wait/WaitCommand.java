package org.markysoft.vani.core.wait;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public abstract class WaitCommand<T> {
	protected final String UNSUPPORTED_MSG = "'Method not supported by " + getClass().getSimpleName() + "'!";
	protected final Log logger = LogFactory.getLog(getClass());
	protected T target;
	protected boolean notFlag;
	protected Function<T, Boolean> conditionFunction;
	protected Predicate<T> conditionPredicate;
	protected Supplier<Boolean> conditionSupplier;
	protected String message;

	public WaitCommand(T target) {
		this.target = target;
	}

	public void setNotFlag(boolean notFlag) {
		this.notFlag = notFlag;
	}

	public void setConditionFunction(Function<T, Boolean> conditionFunction) {
		this.conditionFunction = conditionFunction;
	}

	public void setConditionPredicate(Predicate<T> conditionPredicate) {
		this.conditionPredicate = conditionPredicate;
	}

	public void setConditionSupplier(Supplier<Boolean> conditionSupplier) {
		this.conditionSupplier = conditionSupplier;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean eval() {
		boolean result = false;
		try {
			if (conditionFunction != null) {
				result = conditionFunction.apply(target);
			} else if (conditionPredicate != null) {
				result = conditionPredicate.test(target);
			} else if (conditionSupplier != null) {
				result = conditionSupplier.get();
			} else {
				throw new IllegalWaitCondition("Wait command is not fully configured! No condition found!");
			}
		} catch (Exception ex) {
			if (!StringUtils.isEmpty(message)) {
				logger.warn(message);
			}
			throw ex;
		}
		return result || notFlag;
	}

}
