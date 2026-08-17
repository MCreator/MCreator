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

import freemarker.cache.URLTemplateLoader;
import freemarker.cache.URLTemplateSource;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ModifyTemplateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;

public class URLTemplateLoaderProxy extends URLTemplateLoader {

	private static final Logger LOGGER = LogManager.getLogger(URLTemplateLoaderProxy.class);

	private final URLTemplateLoader templateLoader;

	public URLTemplateLoaderProxy(URLTemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	@Override public Object findTemplateSource(String name) throws IOException {
		return templateLoader.findTemplateSource(name);
	}

	@Override public long getLastModified(Object templateSource) {
		return templateLoader.getLastModified(templateSource);
	}

	@Override public void closeTemplateSource(Object templateSource) throws IOException {
		templateLoader.closeTemplateSource(templateSource);
	}

	@Override public Boolean getURLConnectionUsesCaches() {
		return templateLoader.getURLConnectionUsesCaches();
	}

	@Override public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
		templateLoader.setURLConnectionUsesCaches(urlConnectionUsesCaches);
	}

	@Override protected URL getURL(String name) {
		return null;
	}

	@Override public Reader getReader(Object templateSource, String encoding) throws IOException {
		var reader = templateLoader.getReader(templateSource, encoding);
		if (templateSource instanceof URLTemplateSource urlTemplateSource) {
			var event = new ModifyTemplateEvent(urlTemplateSource.toString(), () -> {
				var wr = new StringWriter();
				try {
					reader.transferTo(wr);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return wr.toString();
			});
			MCREvent.event(event);
			if (event.isModified()) {
				return new StringReader(event.getTemplateOutput());
			}
		}
		return reader;
	}
}
