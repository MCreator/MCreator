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

public enum VariableElementType {

	@SerializedName("string") STRING(0x609986), @SerializedName("logic") LOGIC(
			0x607c99), @SerializedName("number") NUMBER(0x606999), @SerializedName("itemstack") ITEMSTACK(0x996069),
			@SerializedName("blockstate") BLOCKSTATE(0xA6A65C);

	private final int color;

	VariableElementType(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public String toDependencyType() {
		switch (this) {
		case NUMBER:
			return "number";
		case LOGIC:
			return "boolean";
		case STRING:
			return "string";
		case ITEMSTACK:
			return "itemstack";
		case BLOCKSTATE:
			return "blockstate";
		}

		return null;
	}

	@SuppressWarnings("unused") public String getJavaType(Workspace workspace) {
		if (toDependencyType() != null)
			return new NameMapper(workspace, "types").getMapping(toDependencyType());
		return null;
	}

	@SuppressWarnings("unused") public String getDefaultValue(Workspace workspace) {
		switch (this) {
		case NUMBER:
			return "0";
		case LOGIC:
			return "false";
		case STRING:
			return "\"\"";
		case ITEMSTACK:
			return "ItemStack.EMPTY";
		case BLOCKSTATE:
			return "Blocks.AIR.getDefaultState()";
		}

		return "";
	}

	public enum Scope {
		@SerializedName("map") GLOBAL_MAP, @SerializedName("world") GLOBAL_WORLD, @SerializedName("global") GLOBAL_SESSION, @SerializedName("player_lifetime") PLAYER_LIFETIME, @SerializedName("player_persistent") PLAYER_PERSISTENT
	}

}
