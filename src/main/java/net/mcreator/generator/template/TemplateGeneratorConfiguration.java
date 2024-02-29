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
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.template.base.DefaultFreemarkerConfiguration;
import net.mcreator.plugin.PluginLoader;

import java.util.ArrayList;
import java.util.List;

public class TemplateGeneratorConfiguration {

	private final DefaultFreemarkerConfiguration configuration;

	public TemplateGeneratorConfiguration(GeneratorConfiguration generatorConfiguration, String generatorSubfolder) {
		configuration = new DefaultFreemarkerConfiguration();

		List<String> templateLoaderPaths = new ArrayList<>();
		templateLoaderPaths.add(generatorConfiguration.getGeneratorName());
		templateLoaderPaths.addAll(generatorConfiguration.getImports());

		List<TemplateLoader> templateLoaderList = new ArrayList<>();
		for (String templateLoaderPath : templateLoaderPaths) {
			ClassTemplateLoader baseLoader = new ClassTemplateLoader(PluginLoader.INSTANCE,
					"/" + templateLoaderPath + "/" + generatorSubfolder + "/");
			ClassTemplateLoader utilLoader = new ClassTemplateLoader(PluginLoader.INSTANCE,
					"/" + templateLoaderPath + "/utils/");

			templateLoaderList.add(baseLoader);
			templateLoaderList.add(utilLoader);
		}

		configuration.setTemplateLoader(new MultiTemplateLoader(templateLoaderList.toArray(new TemplateLoader[0])));
	}

	public DefaultFreemarkerConfiguration getConfiguration() {
		return configuration;
	}

}
