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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

public class ModifyTemplateEvent extends MCREvent {

	private final String templateName;
	private final Supplier<String> contentLoader;

	// Volatile because listeners run on the plugin event queue thread
	private volatile String loadedContent;       // cached after first read
	private volatile String modifiedContent;     // set by plugin via setTemplateOutput

	public ModifyTemplateEvent(@Nullable String templateName,
			@Nonnull ThrowingSupplier<String, IOException> contentLoader) {
		this.templateName = templateName;
		this.contentLoader = () -> {
			try {
				return contentLoader.get();
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to load template: " + templateName, e);
			}
		};
	}

	@Nullable
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * Returns the current template content. If no plugin has called setTemplateOutput(),
	 * this loads the original content once and caches it.
	 */
	@Nonnull
	public String getTemplateOutput() {
		// If modified content exists, return it (plugin override)
		if (modifiedContent != null) {
			return modifiedContent;
		}
		// Otherwise load and cache original content
		if (loadedContent == null) {
			loadedContent = contentLoader.get();
		}
		return loadedContent;
	}

	/**
	 * Sets a new template content. This marks the event as modified.
	 */
	public void setTemplateOutput(@Nonnull String templateContent) {
		this.modifiedContent = templateContent;
	}

	/**
	 * @return true if a plugin called setTemplateOutput()
	 */
	public boolean isModified() {
		return modifiedContent != null;
	}

	/**
	 * @return true if any plugin called getTemplateOutput() (which loads the content)
	 */
	public boolean wasContentRead() {
		return loadedContent != null || modifiedContent != null;
	}

	@FunctionalInterface
	public interface ThrowingSupplier<T, E extends Exception> {
		T get() throws E;
	}
}