package org.vani.core.locating.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;
import org.vani.core.VaniContext;
import org.vani.core.locating.LocatorBuilder;
import org.vani.core.locating.UnableToLocateException;

/**
 * This implementation of {@link LocatorBuilderFactory} will only create a new
 * instance, if there is no spring bean available.
 * <p>
 * A created instance will be registered as singelton. The instantiation will be
 * executed before first access on given implementation.
 * </p>
 * 
 * @author Thomas
 *
 */
public class DefaultLocatorBuilderFactory implements LocatorBuilderFactory {
	protected final Log logger = LogFactory.getLog(getClass());
	protected ConfigurableListableBeanFactory configurableBeanFactory;
	@Autowired
	protected VaniContext vaniContext;

	public DefaultLocatorBuilderFactory(ConfigurableListableBeanFactory bf) {
		configurableBeanFactory = bf;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public LocatorBuilder get(Class<? extends LocatorBuilder> builderClass) {
		LocatorBuilder result = null;
		String builderName = StringUtils.uncapitalize(builderClass.getSimpleName());
		if (!configurableBeanFactory.containsBean(builderName)) {
			try {
				result = vaniContext.createBean(builderClass);
				configurableBeanFactory.registerSingleton(builderName, result);
			} catch (Exception ex) {
				throw new UnableToLocateException(
						"Cannot create new instance of LocatorBuilder '" + builderClass + "': " + ex, ex);
			}
		} else {
			result = (LocatorBuilder) configurableBeanFactory.getBean(builderName);
			logger.info("Bean named '" + builderName + "' already exists used as current bean reference.");
		}
		return result;
	}

}
