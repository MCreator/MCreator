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
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.util.Locale;

public class DefaultFreemarkerConfiguration extends Configuration {

	private final BeansWrapper beansWrapper;

	public DefaultFreemarkerConfiguration() {
		super(Configuration.VERSION_2_3_31);
		setAutoEscapingPolicy(Configuration.DISABLE_AUTO_ESCAPING_POLICY);
		setDefaultEncoding("UTF-8");
		setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		setNumberFormat("computer");
		setBooleanFormat("c");
		setTemplateUpdateDelayMilliseconds(Integer.MAX_VALUE);
		setCacheStorage(new SoftCacheStorage());
		setLocale(Locale.ENGLISH);
		setLogTemplateExceptions(false);

		BeansWrapperBuilder wrapperBuilder = new BeansWrapperBuilder(Configuration.VERSION_2_3_31);
		wrapperBuilder.setExposeFields(true);
		this.beansWrapper = wrapperBuilder.build();
	}

	public BeansWrapper getBeansWrapper() {
		return beansWrapper;
	}

}
