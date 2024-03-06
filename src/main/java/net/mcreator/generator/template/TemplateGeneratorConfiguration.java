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

		List<TemplateLoader> templateLoaderList = new ArrayList<>();

		// Load templates from the generator subfolder
		for (String path : generatorConfiguration.getGeneratorPaths(generatorSubfolder)) {
			templateLoaderList.add(new ClassTemplateLoader(PluginLoader.INSTANCE, "/" + path));
		}

		// Load templates from the utils subfolder
		for (String path : generatorConfiguration.getGeneratorPaths("utils")) {
			templateLoaderList.add(new ClassTemplateLoader(PluginLoader.INSTANCE, "/" + path));
		}

		configuration.setTemplateLoader(new MultiTemplateLoader(templateLoaderList.toArray(new TemplateLoader[0])));
	}

	public DefaultFreemarkerConfiguration getConfiguration() {
		return configuration;
	}

}
