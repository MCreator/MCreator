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
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TemplateGenerator {

	private static final Logger LOG = LogManager.getLogger("Template Generator");

	private final TemplateGeneratorConfiguration templateGeneratorConfiguration;
	private final Generator generator;
	private final BaseDataModelProvider baseDataModelProvider;

	private final Map<String, Template> inline_template_cache = new ConcurrentHashMap<>();

	public TemplateGenerator(TemplateGeneratorConfiguration templateGeneratorConfiguration, Generator generator) {
		this.generator = generator;
		this.templateGeneratorConfiguration = templateGeneratorConfiguration;
		this.baseDataModelProvider = generator.getBaseDataModelProvider();
	}

	public String generateBaseFromTemplate(String templateName, Map<String, Object> dataModel, String fixedVariables)
			throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());

		extractVariables(fixedVariables, dataModel);

		dataModel.put("variables", generator.getWorkspace().getVariableElements());
		dataModel.put("sounds", generator.getWorkspace().getSoundElements());
		dataModel.put("javamodels",
				Model.getModels(generator.getWorkspace()).stream().filter(model -> model.getType() == Model.Type.JAVA)
						.collect(Collectors.toList()));

		return generateTemplate(templateName, dataModel);
	}

	public String generateElementFromTemplate(GeneratableElement element, String templateName,
			Map<String, Object> dataModel, String fixedVariables, @Nullable IAdditionalTemplateDataProvider provider)
			throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());

		extractVariables(fixedVariables, dataModel);

		dataModel.put("data", element);
		dataModel.put("registryname", element.getModElement().getRegistryName());
		dataModel.put("name", element.getModElement().getName());

		if (provider != null)
			provider.provideAdditionalData(dataModel);

		return generateTemplate(templateName, dataModel);
	}

	public String generateListItemFromTemplate(Object item, int itemIndex, GeneratableElement element,
			String templateName, Map<String, Object> dataModel, String fixedVariables,
			@Nullable IAdditionalTemplateDataProvider provider) throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());

		extractVariables(fixedVariables, dataModel);

		dataModel.put("data", item);
		dataModel.put("itemindex", itemIndex);
		dataModel.put("parent", element);
		dataModel.put("registryname", element.getModElement().getRegistryName());
		dataModel.put("name", element.getModElement().getName());

		if (provider != null)
			provider.provideAdditionalData(dataModel);

		return generateTemplate(templateName, dataModel);
	}

	public String generateFromTemplate(String templateName, Map<String, Object> dataModel)
			throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());
		return generateTemplate(templateName, dataModel);
	}

	public String generateFromString(String template, Map<String, Object> dataModel) throws TemplateGeneratorException {
		dataModel.putAll(baseDataModelProvider.provide());
		return generateTemplateFromString(template, dataModel);
	}

	public boolean hasTemplate(String templateName) {
		try {
			return templateGeneratorConfiguration.getConfiguration().getTemplate(templateName) != null;
		} catch (IOException e) {
			return false;
		}
	}

	private String generateTemplate(String templateName, Map<String, Object> dataModel)
			throws TemplateGeneratorException {
		try {
			Template freemarkerTemplate = templateGeneratorConfiguration.getConfiguration().getTemplate(templateName);
			StringWriter stringWriter = new StringWriter();
			freemarkerTemplate.process(dataModel, stringWriter,
					templateGeneratorConfiguration.getConfiguration().getObjectWrapper());
			return stringWriter.getBuffer().toString();
		} catch (IOException | TemplateException e) {
			LOG.error("Failed to generate template: " + templateName, e);
			throw new TemplateGeneratorException();
		}
	}

	private String generateTemplateFromString(String template, Map<String, Object> dataModel)
			throws TemplateGeneratorException {
		try {
			Template freemarkerTemplate = inline_template_cache.get(template);
			if (freemarkerTemplate == null) {
				freemarkerTemplate = new Template("INLINE_TEMPLATE", template,
						templateGeneratorConfiguration.getConfiguration());
				inline_template_cache.put(template, freemarkerTemplate);
			}

			StringWriter stringWriter = new StringWriter();
			freemarkerTemplate.process(dataModel, stringWriter,
					templateGeneratorConfiguration.getConfiguration().getObjectWrapper());
			return stringWriter.getBuffer().toString();
		} catch (IOException | TemplateException e) {
			LOG.error("Failed to generate template from string", e);
			throw new TemplateGeneratorException();
		}
	}

	/**
	 * Load any hardcoded variables from template definition into dataModel
	 *
	 * @param variables Variables string from YAML definition to load into the model
	 * @param dataModel Data model to place variables into
	 */
	private void extractVariables(@Nullable String variables, Map<String, Object> dataModel) {
		if (variables != null) {
			try {
				String[] vars = variables.split(";");
				for (String var : vars) {
					String[] data = var.split("=");
					dataModel.put("var_" + data[0].trim(), data[1].trim());
				}
			} catch (Exception e) {
				LOG.warn("Failed to parse hardcoded variables", e);
			}
		}
	}

}
