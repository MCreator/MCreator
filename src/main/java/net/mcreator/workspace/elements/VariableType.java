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

import java.awt.*;
import java.util.Map;

@SuppressWarnings("unused") public class VariableType {

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

	public Color getBlocklyColor() {
		try {
			if (!color.startsWith("#"))
				return BlocklyBlockUtil.getBlockColorFromHUE(Integer.parseInt(color));
			else
				return Color.decode(color);
		} catch (Exception e) {
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
