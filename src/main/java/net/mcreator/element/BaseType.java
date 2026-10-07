/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.element;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BaseType {

	private static final Map<String, BaseType> REGISTRY = new LinkedHashMap<>();

	// @formatter:off
	public static final BaseType BLOCK = of("block");
	public static final BaseType BLOCKENTITY = of("blockentity");
	public static final BaseType ITEM = of("item");
	public static final BaseType ENTITY = of("entity");
	public static final BaseType FEATURE = of("feature");
	public static final BaseType CONFIGUREDFEATURE = of("configuredfeature");
	public static final BaseType TRANSFORMABLE = of("transformable");
	// @formatter:on

	private final String name;

	private BaseType(String name) {
		this.name = name;
	}

	/**
	 * Returns the base type with the given name, registering it if it does not exist yet.
	 * Instances are interned, so base types can be compared by identity.
	 */
	public static synchronized BaseType of(String name) {
		return REGISTRY.computeIfAbsent(name.toLowerCase(Locale.ENGLISH), BaseType::new);
	}

	public static synchronized Collection<BaseType> values() {
		return List.copyOf(REGISTRY.values());
	}

	public String getName() {
		return name;
	}

	public String getPluralName() {
		if (name.endsWith("y"))
			return name.substring(0, name.length() - 1) + "ies";

		return name + "s";
	}

	@Override public String toString() {
		return name;
	}

}