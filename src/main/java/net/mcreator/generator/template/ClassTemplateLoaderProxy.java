/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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
import freemarker.cache.TemplateLoader;
import freemarker.cache.URLTemplateSource;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ModifyTemplateEvent;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.*;

public class ClassTemplateLoaderProxy implements TemplateLoader {

	private final ClassTemplateLoader templateLoader;

	public ClassTemplateLoaderProxy(ClassTemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	@Override public Object findTemplateSource(String name) throws IOException {
		Object source = templateLoader.findTemplateSource(name);
		if (source == null) {
			return null;
		}
		// if we do not append the base package root. Plugin developer will only get file name.
		return new URLTemplateSourceHolder((URLTemplateSource) source, templateLoader.getBasePackagePath() + name);
	}

	@Override public long getLastModified(Object templateSource) {
		if (templateSource instanceof URLTemplateSourceHolder holder) {
			return templateLoader.getLastModified(holder.urlTemplateSource());
		}
		return templateLoader.getLastModified(templateSource);
	}

	@Override public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof URLTemplateSourceHolder holder) {
			templateLoader.closeTemplateSource(holder.urlTemplateSource());
		} else {
			templateLoader.closeTemplateSource(templateSource);
		}
	}

	@Override public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof URLTemplateSourceHolder(URLTemplateSource urlSource, String logicalName)) {
			ModifyTemplateEvent event = new ModifyTemplateEvent(logicalName, () -> {
				try (Reader reader = templateLoader.getReader(urlSource, encoding)) {
					return IOUtils.toString(reader);
				}
			});
			MCREvent.event(event);
			if (!event.isModified()) {
				return templateLoader.getReader(urlSource, encoding);
			}
			return new StringReader(event.getTemplateContent());
		}
		return templateLoader.getReader(templateSource, encoding);
	}

	private record URLTemplateSourceHolder(@Nonnull URLTemplateSource urlTemplateSource, @Nonnull String name) {}
}