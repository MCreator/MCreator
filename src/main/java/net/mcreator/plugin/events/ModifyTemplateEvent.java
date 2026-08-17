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
import java.util.function.Supplier;

public class ModifyTemplateEvent extends MCREvent {

	private final String templateURL;
	private String templateOutputOriginal;
	private Supplier<String> templateContentProvider;
	private String templateOutput;
	private boolean modified;

	public ModifyTemplateEvent(@Nullable String templateURL, @Nonnull Supplier<String> templateContentProvider) {
		this.templateURL = templateURL;
		this.templateContentProvider = templateContentProvider;
	}

	/**
	 * @return Template file url
	 */
	public String getTemplateURL() {
		return templateURL;
	}

	/**
	 * @return Original template output content before any modifications from plugins
	 */
	public String getTemplateOutputOriginal() {
		if (templateOutputOriginal == null) {
			tryToGetTemplateContent();
		}
		return templateOutputOriginal;
	}

	/**
	 * @return Current template output content. At this point, plugins with higher priority may have already modified it
	 */
	public String getTemplateOutput() {
		if (templateOutput == null) {
			tryToGetTemplateContent();
		}
		return templateOutput;
	}

	private void tryToGetTemplateContent() {
		var template = templateContentProvider.get();
		templateOutput = template;
		templateOutputOriginal = template;
	}

	public void setTemplateOutput(String templateContent) {
		modified = true;
		this.templateOutput = templateContent;
	}

	public boolean isModified() {
		return modified;
	}
}
