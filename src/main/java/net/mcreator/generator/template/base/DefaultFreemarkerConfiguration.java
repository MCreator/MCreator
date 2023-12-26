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
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.util.Locale;

public class DefaultFreemarkerConfiguration extends Configuration {

	private static final Version FTL_CONFIGURATION_VERSION = Configuration.VERSION_2_3_32;

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
		setObjectWrapper(wrapperBuilder.build());
	}

}
