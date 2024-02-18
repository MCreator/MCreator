/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import freemarker.template.Template;
import net.mcreator.generator.template.base.DefaultFreemarkerConfiguration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class provides inline FTL templates and also caches them
 */
public class InlineTemplatesHandler {

	private static final DefaultFreemarkerConfiguration configuration = new DefaultFreemarkerConfiguration();

	private static final Map<String, Template> cache = new ConcurrentHashMap<>();

	public static Template getTemplate(String template) throws IOException {
		AtomicReference<IOException> exception = new AtomicReference<>();
		Template retval = cache.computeIfAbsent(template, s -> {
			try {
				return new Template("INLINE_TEMPLATE", template, configuration);
			} catch (IOException e) {
				exception.set(e);
				return null;
			}
		});

		if (exception.get() != null)
			throw exception.get();
		else
			return retval;
	}

	public static DefaultFreemarkerConfiguration getConfiguration() {
		return configuration;
	}

}
