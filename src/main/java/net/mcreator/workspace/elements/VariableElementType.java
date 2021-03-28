/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.workspace.elements;

import com.google.gson.annotations.SerializedName;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.workspace.Workspace;

import java.awt.*;
import java.util.*;

public class VariableElementType {
	private static final Map<VariableElementType, String> variables = new HashMap<>();

	public static final VariableElementType STRING = new VariableElementType("string", 0x609986, "\"\"", "String");
	public static final VariableElementType LOGIC = new VariableElementType("logic", "boolean", 0x607c99, "false", "Boolean");
	public static final VariableElementType NUMBER = new VariableElementType("number", 0x606999, "0", "Number");
	public static final VariableElementType ITEMSTACK = new VariableElementType("itemstack", 0x996069, "ItemStack.EMPTY", "MCItem");

	private final String type;
	private final int color;
	private final String dependencyType;
	private final String defaultValue;
	private final String blocklyVariableType;

	private VariableElementType(String type, int color, String defaultValue, String blocklyVariableType) {
		this(type, type, color, defaultValue, blocklyVariableType);
	}

	private VariableElementType(String type, String dependencyType, int color, String defaultValue, String blocklyVariableType) {
		this.type = type;
		this.dependencyType = dependencyType;
		this.color = color;
		this.defaultValue = defaultValue;
		this.blocklyVariableType = blocklyVariableType;
		variables.put(this, type.toUpperCase());
	}

	public static VariableElementType getVariableFromType(String type) {
		for (VariableElementType var : variables.keySet()) {
			if(var.getBlocklyVariableType().equals(type) || var.getType().equals(type.toLowerCase())) {
				return var;
			}
		}
		return null;
	}

	public static Set<VariableElementType> getVariables() {
		return variables.keySet();
	}

	public static Collection<String> getAllTypes() {
		return variables.values();
	}

	public Color getColor() {
		return new Color(color);
	}

	public String getType() {
		return type;
	}

	public String getDependencyType() {
		return dependencyType;
	}

	public String getBlocklyVariableType() {
		return blocklyVariableType;
	}

	@SuppressWarnings("unused") public String getJavaType(Workspace workspace) {
		return new NameMapper(workspace, "types").getMapping(getDependencyType());
	}

	@SuppressWarnings("unused") public String getDefaultValue() {
		return defaultValue;
	}

	public enum Scope {
		@SerializedName("map") GLOBAL_MAP, @SerializedName("world") GLOBAL_WORLD, @SerializedName("global") GLOBAL_SESSION, @SerializedName("player_lifetime") PLAYER_LIFETIME, @SerializedName("player_persistent") PLAYER_PERSISTENT
	}

}
