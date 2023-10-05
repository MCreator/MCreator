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

package net.mcreator.util;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.constructor.ConstructYamlNull;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.FailsafeScalarResolver;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;
import org.snakeyaml.engine.v2.schema.Schema;

import java.util.HashMap;
import java.util.Map;

public class YamlUtil {

	private static LoadSettings cache = null;

	/**
	 * Returns simple SnakeYAML load settings that mimic YAMLBeans behavior and also
	 * make parsing more lightweight and faster.
	 *
	 * @return Simple SnakeYAML load settings
	 */
	public static LoadSettings getSimpleLoadSettings() {
		if (cache != null)
			return cache;

		final Map<Tag, ConstructNode> tagConstructors = new HashMap<>();
		tagConstructors.put(Tag.NULL, new ConstructYamlNull());

		final ScalarResolver scalarResolver = new FailsafeScalarResolver();

		final Schema schema = new Schema() {
			@Override public ScalarResolver getScalarResolver() {
				return scalarResolver;
			}

			@Override public Map<Tag, ConstructNode> getSchemaTagConstructors() {
				return tagConstructors;
			}
		};

		return cache = LoadSettings.builder().setSchema(schema).build();
	}

}
