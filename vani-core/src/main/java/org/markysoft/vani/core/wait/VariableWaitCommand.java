package org.markysoft.vani.core.wait;

import org.markysoft.vani.core.javascript.VaniUtils;
import org.springframework.util.StringUtils;

public class VariableWaitCommand extends WaitCommand<Object> {
	private String variableName;
	private VaniUtils vaniUtils;

	public VariableWaitCommand(String variableName, VaniUtils vaniUtils) {
		super(null);
		this.variableName = variableName;
		this.vaniUtils = vaniUtils;
	}

	@Override
	public boolean eval() {
		try {
			target = vaniUtils.get(variableName, null);
		} catch (Exception ex) {
			if (!StringUtils.isEmpty(message)) {
				logger.warn(message);
			}
			throw ex;
		}

		boolean result = super.eval();
		return result;
	}
}
