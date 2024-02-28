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

package net.mcreator.generator;

import net.mcreator.generator.template.TemplateExpressionParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GeneratorTemplate {

	private final File file;

	private final Map<?, ?> templateDefinition;

	private final String templateIdentifier;

	private final Map<String, Object> dataModel = new HashMap<>();

	/**
	 * @param file               File where this GeneratorTemplate should be generated into
	 * @param templateIdentifier String that is equal for all GeneratorTemplate generated for the same template entry in element definition
	 * @param templateDefinition Map defining the template properties that generator uses
	 */
	GeneratorTemplate(File file, String templateIdentifier, Map<?, ?> templateDefinition) {
		this.file = file;
		this.templateIdentifier = templateIdentifier;
		this.templateDefinition = templateDefinition;
	}

	/**
	 * @return File where this GeneratorTemplate should be generated into
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Used to determine what template belongs to what file. Same GeneratorTemplate file of different MEs
	 * can belong to the same template entry in element definition that was used to generate said file.
	 * <p>
	 * Currently used to determine which files to copy to which files when duplicating ME with locked code.
	 *
	 * @return String that is equal for all GeneratorTemplate generated for the same template entry in element definition
	 */
	public String getTemplateIdentifier() {
		return templateIdentifier;
	}

	/**
	 * @return true if this template should be visually hidden in the UI
	 */
	public boolean isHidden() {
		return templateDefinition.get("hidden") != null && templateDefinition.get("hidden").equals("true");
	}

	/**
	 * @return usercode comment format for this template
	 */
	public String getUsercodeComment() {
		return (String) templateDefinition.get("usercodeComment");
	}

	/**
	 * @return Data model that is used to generate this GeneratorTemplate (data accessible in FTL templates)
	 */
	public Map<String, Object> getDataModel() {
		return dataModel;
	}

	public void addDataModelEntry(String key, Object value) {
		dataModel.put(key, value);
	}

	// Helper functions below

	public GeneratorFile toGeneratorFile(String code) {
		return new GeneratorFile(this, GeneratorFile.Writer.fromString((String) templateDefinition.get("writer")),
				code);
	}

	public boolean shouldBeSkippedBasedOnCondition(Generator generator, Object conditionData) {
		return TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(generator, templateDefinition,
				conditionData);
	}

	/**
	 * @return Map defining the template properties that generator uses
	 */
	public Map<?, ?> getTemplateDefinition() {
		return templateDefinition;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return file.equals(((GeneratorTemplate) o).file);
	}

	@Override public int hashCode() {
		return file.hashCode();
	}

}
