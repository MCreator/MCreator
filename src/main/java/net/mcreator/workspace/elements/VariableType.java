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
import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Map;

@SuppressWarnings("unused") public class VariableType {
	private static final Logger LOG = LogManager.getLogger("Variable type");

	private String name;
	private String color;
	private String blocklyVariableType;
	private boolean returnTypeOnly;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	/**
	 * Returns the color of the blocks associated with this variable type. If the field is a valid hex color code, it's
	 * returned as-is. If it's a valid integer, it's treated as a hue to get the color with the correct saturation and
	 * value.
	 *
	 * @return The color of the associated blocks, or black if it's badly formatted.
	 */
	public Color getBlocklyColor() {
		try {
			if (!color.startsWith("#"))
				return BlocklyBlockUtil.getBlockColorFromHUE(Integer.parseInt(color));
			else
				return Color.decode(color);
		} catch (Exception e) {
			LOG.warn("The color for variable type " + name + " isn't formatted correctly. Using color black for it");
			return Color.BLACK;
		}
	}

	public boolean isReturnTypeOnly() {
		return returnTypeOnly;
	}

	public String getBlocklyVariableType() {
		return blocklyVariableType;
	}

	public String getJavaType(Workspace workspace) {
		return new NameMapper(workspace, "types").getMapping(getName());
	}

	public String getDefaultValue(Workspace Workspace) {
		return Workspace.getGeneratorConfiguration().getVariableTypes().getDefaultValue(this);
	}

	public Map<?, ?> getScopeDefinition(Workspace workspace, String scope) {
		return workspace.getGeneratorConfiguration().getVariableTypes().getScopeDefinition(this, scope);
	}

	public boolean canBeGlobal(GeneratorConfiguration generatorConfiguration) {
		return generatorConfiguration.getVariableTypes().canBeGlobal(this);
	}

	public boolean canBeLocal(GeneratorConfiguration generatorConfiguration) {
		return generatorConfiguration.getVariableTypes().canBeLocal(this);
	}

	public Scope[] getSupportedScopesWithoutLocal(GeneratorConfiguration generatorConfiguration) {
		return generatorConfiguration.getVariableTypes().getSupportedScopesWithoutLocal(this);
	}

	@Override public String toString() {
		return getName();
	}

	public enum Scope {
		@SerializedName("map") GLOBAL_MAP, @SerializedName("world") GLOBAL_WORLD, @SerializedName("global") GLOBAL_SESSION, @SerializedName("player_lifetime") PLAYER_LIFETIME, @SerializedName("player_persistent") PLAYER_PERSISTENT, @SerializedName("local") LOCAL
	}

}
