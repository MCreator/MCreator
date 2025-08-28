/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.plugin.events;

import net.mcreator.plugin.MCREvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class ModifyTemplateResultEvent extends MCREvent {

	private final String templateName;
	private final Map<String, Object> dataModel;
	private final String templateOutputOriginal;

	private String templateOutput;

	public ModifyTemplateResultEvent(@Nullable String templateName, @Nonnull String templateContent,
			@Nonnull Map<String, Object> dataModel) {
		this.templateName = templateName;
		this.templateOutput = templateContent;
		this.templateOutputOriginal = templateContent;
		this.dataModel = dataModel;
	}

	/**
	 * @return Template file name
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @return Original template output content before any modifications from plugins
	 */
	public String getTemplateOutputOriginal() {
		return templateOutputOriginal;
	}

	/**
	 * @return Data model used to generate the template contents
	 */
	public Map<String, Object> getDataModel() {
		return dataModel;
	}

	/**
	 * @return Current template output content. At this point, plugins with higher priority may have already modified it
	 */
	public String getTemplateOutput() {
		return templateOutput;
	}

	public void setTemplateOutput(String templateContent) {
		this.templateOutput = templateContent;
	}

}
