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

import javax.annotation.Nullable;
import java.awt.*;

public class VariableElementType {

	//Define each global variables. The instantiation is made in VariableElementTypeLoader.
	public static VariableElementType STRING;
	public static VariableElementType LOGIC;
	public static VariableElementType NUMBER;
	public static VariableElementType ITEMSTACK;

	private String name;
	private String color;
	//Use for compatibility with old workspaces
	@Nullable private String dependencyType;
	private String defaultValue;
	private String blocklyVariableType;
	private String javaClass;
	//Use for compatibility with old workspaces
	//The name used for procedure blocks
	@Nullable private String blockName;

	public Color getColor() {
		return Color.decode(color);
	}

	public String getName() {
		return name;
	}

	public String getDependencyType() {
		if(dependencyType != null)
			return dependencyType;
		else
			return name;
	}

	public String getBlocklyVariableType() {
		return blocklyVariableType;
	}

	public String getJavaClass() {
		return javaClass;
	}

	public String getBlockName() {
		if(blockName != null)
			return blockName;
		else
			return name;

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
