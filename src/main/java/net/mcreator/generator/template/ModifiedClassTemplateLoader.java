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

package net.mcreator.generator.template;

import freemarker.cache.ClassTemplateLoader;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ModifyTemplateEvent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModifiedClassTemplateLoader extends ClassTemplateLoader {

	private static final Map<Object, String> templateNameCache = new ConcurrentHashMap<>();
	private final GeneratorConfiguration generatorConfiguration;

	public ModifiedClassTemplateLoader(ClassLoader classLoader, String basePackagePath,
			GeneratorConfiguration generatorConfiguration) {
		super(classLoader, basePackagePath);
		this.generatorConfiguration = generatorConfiguration;
	}

	@Override public Object findTemplateSource(String name) throws IOException {
		Object source = super.findTemplateSource(name);
		if (source != null) {
			templateNameCache.put(source, getBasePackagePath() + name);
		}
		return source;
	}

	@Override public void closeTemplateSource(Object templateSource) throws IOException {
		templateNameCache.remove(templateSource);
		super.closeTemplateSource(templateSource);
	}

	@Override public Reader getReader(Object templateSource, String encoding) throws IOException {
		String logicalName = templateNameCache.get(templateSource);
		if (logicalName == null) {
			return super.getReader(templateSource, encoding);
		}

		ModifyTemplateEvent event = new ModifyTemplateEvent(logicalName, () -> {
			try (Reader reader = super.getReader(templateSource, encoding)) {
				return IOUtils.toString(reader);
			}
		}, generatorConfiguration);
		MCREvent.event(event);

		// if no plugin edit and read, this will ensure only one I/O open.
		if (!event.isModified() && !event.hasContentRead()) {
			return super.getReader(templateSource, encoding);
		}
		return new StringReader(event.getTemplateContent());
	}
}