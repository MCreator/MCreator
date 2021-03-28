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

public class VariableElementType {

	public static final VariableElementType STRING = new VariableElementType("string", "0x609986", "\"\"", "String", "String");
	public static final VariableElementType LOGIC = new VariableElementType("logic", "boolean", "0x607c99", "false", "Boolean", "boolean");
	public static final VariableElementType NUMBER = new VariableElementType("number", "0x606999", "0", "Number", "double");
	public static final VariableElementType ITEMSTACK = new VariableElementType("itemstack", "0x996069", "ItemStack.EMPTY", "MCItem", "ItemStack");

	private final String type;
	private final String color;
	private final String dependencyType;
	private final String defaultValue;
	private final String blocklyVariableType;
	private final String javaClass;

	private VariableElementType(String type, String color, String defaultValue, String blocklyVariableType, String javaClass) {
		this(type, type, color, defaultValue, blocklyVariableType, javaClass);
	}

	private VariableElementType(String type, String dependencyType, String color, String defaultValue, String blocklyVariableType, String javaClass) {
		this.type = type;
		this.dependencyType = dependencyType;
		this.color = color;
		this.defaultValue = defaultValue;
		this.blocklyVariableType = blocklyVariableType;
		this.javaClass = javaClass;
		VariableElement.addVariable(this);
	}

	public Color getColor() {
		return Color.decode(color);
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

	public String getJavaClass() {
		return javaClass;
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
