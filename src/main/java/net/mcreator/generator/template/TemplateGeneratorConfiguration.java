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

package net.mcreator.generator.template;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import net.mcreator.generator.template.base.DefaultFreemarkerConfiguration;
import net.mcreator.plugin.PluginLoader;

public class TemplateGeneratorConfiguration {

	private final DefaultFreemarkerConfiguration configuration;

	public TemplateGeneratorConfiguration(String generatorName, String generatorSubfolder) {
		configuration = new DefaultFreemarkerConfiguration();

		ClassTemplateLoader baseLoader = new ClassTemplateLoader(PluginLoader.INSTANCE,
				"/" + generatorName + "/" + generatorSubfolder + "/");
		ClassTemplateLoader utilLoader = new ClassTemplateLoader(PluginLoader.INSTANCE,
				"/" + generatorName + "/utils/");
		MultiTemplateLoader loader = new MultiTemplateLoader(new TemplateLoader[] { baseLoader, utilLoader });

		configuration.setTemplateLoader(loader);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public BeansWrapper getBeansWrapper() {
		return configuration.getBeansWrapper();
	}
}
