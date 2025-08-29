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

import freemarker.cache.TemplateLoader;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.generator.ModifyTemplateEvent;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ModifiedTemplateLoader implements TemplateLoader {

	private final TemplateLoader originalTemplateLoader;
	private final ConcurrentMap<Object, String> templateSourceToName;

	public ModifiedTemplateLoader(TemplateLoader originalTemplateLoader) {
		this.originalTemplateLoader = originalTemplateLoader;
		templateSourceToName = new ConcurrentHashMap<>();
	}

	/**
	 * An api for others
	 *
	 * @return The original Template Loader
	 */
	public TemplateLoader getOriginalTemplateLoader() {
		return originalTemplateLoader;
	}

	@Override public Object findTemplateSource(String name) throws IOException {
		Object templateSource = originalTemplateLoader.findTemplateSource(name);
		templateSourceToName.put(templateSource, name);
		return templateSource;
	}

	@Override public long getLastModified(Object templateSource) {
		return originalTemplateLoader.getLastModified(templateSource);
	}

	@Override public Reader getReader(Object templateSource, String encoding) throws IOException {
		Reader originalTemplateReader = originalTemplateLoader.getReader(templateSource, encoding);
		ModifyTemplateEvent modifyTemplateEvent = new ModifyTemplateEvent(templateSourceToName.get(templateSource),
				originalTemplateReader);
		MCREvent.event(modifyTemplateEvent);
		return modifyTemplateEvent.getTemplateContentReader();
	}

	@Override public void closeTemplateSource(Object templateSource) throws IOException {
		originalTemplateLoader.closeTemplateSource(templateSource);
		templateSourceToName.remove(templateSource);
	}
}
