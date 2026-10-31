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

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.plugin.MCREvent;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

public class ModifyTemplateEvent extends MCREvent {

	private final String templateName;
	private final Supplier<String> contentLoader;
	private boolean modified;
	private final GeneratorConfiguration generatorConfiguration;

	private volatile String templateContentOrigin;
	private volatile String templateContent;

	public ModifyTemplateEvent(@Nonnull String templateName, @Nonnull ThrowingSupplier<String> contentLoader,
			GeneratorConfiguration generatorConfiguration) {
		this.templateName = templateName;
		this.contentLoader = () -> {
			try {
				return contentLoader.get();
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to load template: " + templateName, e);
			}
		};
		this.generatorConfiguration = generatorConfiguration;
	}

	public String getTemplateName() {
		return templateName;
	}

	/**
	 * Returns the current template content.
	 * <p>
	 * If a plugin has modified the template (i.e. {@link #isModified()} returns {@code true}),
	 * the content that was set via {@link #setTemplateContent(String)} is returned immediately.
	 * <p>
	 * Otherwise, if the original content has not yet been loaded, it is loaded once from the
	 * underlying content loader and then cached in both {@code templateContentOrigin} and
	 * {@code templateContent}. Subsequent calls will reuse the cached value and will not trigger
	 * another load.
	 *
	 * @return the effective template content, which is either the plugin‑modified content or the
	 * original template content
	 * @see #setTemplateContent(String)
	 * @see #getTemplateContentOrigin()
	 * @see #isModified()
	 *
	 **/
	public String getTemplateContent() {
		if (modified) {
			return templateContent;
		}

		if (templateContentOrigin == null) {
			templateContentOrigin = contentLoader.get();
			templateContent = templateContentOrigin;
		}
		return templateContent;
	}

	/**
	 * Returns the original template content as it was before any modifications were applied by plugins.
	 * <p>
	 * The content is loaded lazily on the first call and then cached in {@code templateContentOrigin}.
	 * If no plugin has modified the template (i.e. {@link #isModified()} returns {@code false}), this method
	 * also sets the effective template content to this original content, so that {@link #getTemplateContent()}
	 * returns the unmodified version.
	 *
	 * @return the original, unmodified template content
	 * @see #getTemplateContent()
	 * @see #isModified()
	 */
	public String getTemplateContentOrigin() {
		if (templateContentOrigin == null) {
			templateContentOrigin = contentLoader.get();
			if (!modified) {
				templateContent = templateContentOrigin;
			}
		}
		return templateContentOrigin;
	}

	/**
	 * Sets the new template content that will be used as the result of this event.
	 * <p>
	 * If the given {@code templateContent} is {@code null}, the method returns immediately and
	 * leaves the current content and modification state unchanged. Otherwise, the new content is
	 * stored and the event is marked as modified, so that {@link #isModified()} will return
	 * {@code true} and {@link #getTemplateContent()} will return the newly set content.
	 * <p>
	 * Calling this method multiple times will overwrite the previously set content, but the
	 * event will remain marked as modified.
	 *
	 * @param templateContent the new template content to set; if {@code null}, the call is ignored
	 * @see #getTemplateContent()
	 * @see #isModified()
	 */
	public void setTemplateContent(String templateContent) {
		if (templateContent == null) {
			return;
		}
		this.templateContent = templateContent;
		this.modified = true;
	}

	/**
	 * @return true if a plugin called setTemplateOutput().
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @return true if event has read the template content.
	 */
	public boolean hasContentRead() {
		return templateContentOrigin != null;
	}

	public GeneratorConfiguration getGeneratorConfiguration() {
		return generatorConfiguration;
	}

	@FunctionalInterface public interface ThrowingSupplier<T> {
		T get() throws IOException;
	}
}