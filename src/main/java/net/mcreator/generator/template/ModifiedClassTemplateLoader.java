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
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ModifyTemplateEvent;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.*;

public class ModifiedClassTemplateLoader extends ClassTemplateLoader {

	public ModifiedClassTemplateLoader(ClassLoader classLoader, String basePackagePath) {
		super(classLoader,basePackagePath);
	}

	@Override public Object findTemplateSource(String name) throws IOException {
		Object source = super.findTemplateSource(name);
		if (source == null) {
			return null;
		}
		// if we do not append the base package root. Plugin developer will only receive file name.
		// eg. mixin.ftl.json -> neoforge-1.x/templates/modbase/mixin.ftl.json
		return new TemplateSourceHolder(source, getBasePackagePath() + name);
	}

	@Override public long getLastModified(Object templateSource) {
		if (templateSource instanceof TemplateSourceHolder holder) {
			return super.getLastModified(holder.templateSource());
		}
		return super.getLastModified(templateSource);
	}

	@Override public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof TemplateSourceHolder holder) {
			super.closeTemplateSource(holder.templateSource());
		} else {
			super.closeTemplateSource(templateSource);
		}
	}

	@Override public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof TemplateSourceHolder(Object templateSource1, String logicalName)) {
			ModifyTemplateEvent event = new ModifyTemplateEvent(logicalName, () -> {
				try (Reader reader = getReader(templateSource1, encoding)) {
					return IOUtils.toString(reader);
				}
			});
			MCREvent.event(event);
			// if no plugin edit and read, this will ensure only one I/O open.
			if (!event.isModified() && !event.hasContentRead()) {
				return super.getReader(templateSource1, encoding);
			}
			return new StringReader(event.getTemplateContent());
		}
		return super.getReader(templateSource, encoding);
	}

	private record TemplateSourceHolder(@Nonnull Object templateSource, @Nonnull String name) {}
}