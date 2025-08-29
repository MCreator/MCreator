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

package net.mcreator.plugin.events.generator;

import net.mcreator.plugin.MCREvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Reader;

public class ModifyTemplateEvent extends MCREvent {
	private final String templateName;
	private final Reader originalTemplateContentReader;

	private Reader templateContentReader;

	public ModifyTemplateEvent(@Nullable String templateName, @Nonnull Reader templateContentReader) {
		this.templateName = templateName;
		this.templateContentReader = templateContentReader;
		this.originalTemplateContentReader = templateContentReader;
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
	public Reader getOriginalTemplateContentReader() {
		return originalTemplateContentReader;
	}

	/**
	 * @return Current template output content. At this point, plugins with higher priority may have already modified it
	 */
	public Reader getTemplateContentReader() {
		return templateContentReader;
	}

	public void setTemplateContentReader(Reader templateContentReader) {
		this.templateContentReader = templateContentReader;
	}
}
