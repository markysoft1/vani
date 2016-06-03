package org.markysoft.vani.core.javascript;

import org.markysoft.vani.core.annotation.GlobalReference;
import org.markysoft.vani.core.annotation.JsCallFunction;

/**
 * This interface declares a holder for a global reference. It is necessary for
 * implementing custom page caching mechanism. For more information, see
 * {@link GlobalReference}.
 * 
 * @author Thomas
 * @see GlobalReference
 * @see JsCallFunction
 */
public interface GlobalReferenceHolder {

	/**
	 * @return returns corresponding global reference value
	 */
	String getReference();

}
