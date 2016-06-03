package org.markysoft.vani.core.javascript;

/**
 * This class is a simple implementation of {@link GlobalReferenceHolder}, which
 * holds the reference as global variable.
 * 
 * @author Thomas
 * @see GlobalReferenceHolder
 */
public class DefaultGlobalReferenceHolder implements GlobalReferenceHolder {
	private String reference;

	public DefaultGlobalReferenceHolder(String reference) {
		this.reference = reference;
	}

	@Override
	public String getReference() {
		return reference;
	};
}
