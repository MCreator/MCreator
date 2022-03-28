/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.workspace.resources;

import java.util.Arrays;

public enum TextureType {
	BLOCK("block"), ITEM("item"), ARMOR("armor"), OTHER("other");

	private final String id;

	TextureType(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	/**
	 * <p>Get all {@link TextureType}s of the enum as a list</p>
	 *
	 * @param withArmor <p>Set to true to include the armor texture type into the possible types.</p>
	 * @return <p>A list of {@link TextureType}s</p>
	 */
	public static TextureType[] getTypes(boolean withArmor) {
		if (withArmor)
			return (TextureType[]) Arrays.stream(TextureType.values()).toArray();
		else
			return (TextureType[]) Arrays.stream(TextureType.values()).filter(t -> t != TextureType.ARMOR).toArray();
	}

	/**
	 * <p>Get the {@link TextureType} in the enum depending on {@param position} and {@param withArmor}.</p>
	 *
	 * @param position <p>The position of the texture type in the enum.</p>
	 * @param withArmor <p>Set to true to include the armor texture type into the possible types.</p>
	 * @return <p>The corresponding {@link TextureType}</p>
	 */
	public static TextureType getTextureType(int position, boolean withArmor) {
		return getTypes(withArmor)[position];
	}
}
