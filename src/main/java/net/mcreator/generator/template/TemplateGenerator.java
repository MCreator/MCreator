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

import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.Generator;
import net.mcreator.generator.template.base.BaseDataModelProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class TemplateGenerator {

	private static final Logger LOG = LogManager.getLogger("Template Generator");

	private final TemplateGeneratorConfiguration templateGeneratorConfiguration;
	private final Generator generator;
	private final BaseDataModelProvider baseDataModelProvider;

	public TemplateGenerator(TemplateGeneratorConfiguration templateGeneratorConfiguration, Generator generator) {
		this.generator = generator;
		this.templateGeneratorConfiguration = templateGeneratorConfiguration;
		this.baseDataModelProvider = new BaseDataModelProvider(generator);
	}

	public String generateElementFromTemplate(GeneratableElement element, String templateName,
			Map<String, Object> dataModel, @Nullable IAdditionalTemplateDataProvider provider)
			throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());

		dataModel.put("data", element);
		dataModel.put("registryname", element.getModElement().getRegistryName());
		dataModel.put("name", element.getModElement().getName());

		if (provider != null)
			provider.provideAdditionalData(dataModel);

		return generateTemplate(templateName, dataModel);
	}

	public String generateBaseFromTemplate(String templateName, Map<String, Object> dataModel)
			throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());

		dataModel.put("variables", generator.getWorkspace().getVariableElements());
		dataModel.put("sounds", generator.getWorkspace().getSoundElements());

		return generateTemplate(templateName, dataModel);
	}

	public String generateFromTemplate(String templateName, Map<String, Object> dataModel)
			throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());
		return generateTemplate(templateName, dataModel);
	}

	private String generateTemplate(String templateName, Map<String, Object> dataModel)
			throws TemplateGeneratorException {
		try {
			Template freemarkerTemplate = templateGeneratorConfiguration.getConfiguration().getTemplate(templateName);
			StringWriter stringWriter = new StringWriter();
			freemarkerTemplate.process(dataModel, stringWriter, templateGeneratorConfiguration.getBeansWrapper());
			return stringWriter.getBuffer().toString();
		} catch (IOException | TemplateException e) {
			LOG.error("Failed to generate template: " + templateName, e);
			throw new TemplateGeneratorException();
		}
	}

}
