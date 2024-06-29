/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.generator.template.base;

import freemarker.cache.SoftCacheStorage;
import freemarker.core.JSONCFormat;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import net.mcreator.element.parts.TextureHolder;

import java.util.Locale;

public class DefaultFreemarkerConfiguration extends Configuration {

	private static final Version FTL_CONFIGURATION_VERSION = Configuration.VERSION_2_3_33;

	public DefaultFreemarkerConfiguration() {
		super(FTL_CONFIGURATION_VERSION);
		setAutoEscapingPolicy(Configuration.DISABLE_AUTO_ESCAPING_POLICY);
		setDefaultEncoding("UTF-8");
		setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		setNumberFormat("0.##########");
		setBooleanFormat("c");
		setCFormat(JSONCFormat.INSTANCE);
		setTemplateUpdateDelayMilliseconds(Integer.MAX_VALUE);
		setCacheStorage(new SoftCacheStorage());
		setLocale(Locale.ENGLISH);
		setLogTemplateExceptions(false);

		BeansWrapperBuilder wrapperBuilder = new BeansWrapperBuilder(FTL_CONFIGURATION_VERSION);
		wrapperBuilder.setExposeFields(true);
		setObjectWrapper(new CustomBeansWrapper(wrapperBuilder));
	}

	private static class CustomBeansWrapper extends BeansWrapper {

		public CustomBeansWrapper(BeansWrapperBuilder beansWrapperBuilder) {
			super(beansWrapperBuilder, true);
		}

		@Override public TemplateModel wrap(Object object) throws TemplateModelException {
			if (object instanceof TextureHolder textureHolder) {
				return textureHolder.toTemplateModel(this);
			}

			return super.wrap(object);
		}

	}

}
