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

import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;

import java.util.Arrays;

public enum TextureType {
	BLOCK("block"), ITEM("item"), ENTITY("entity"), EFFECT("effect"), PARTICLE("particle"), SCREEN("screen"), ARMOR(
			"armor"), OTHER("other");

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
	public static TextureType[] getSupportedTypes(Workspace workspace, boolean withArmor) {
		return withArmor ?
				TextureType.values() :
				Arrays.stream(TextureType.values()).filter(t -> t != TextureType.ARMOR && t.isSupported(workspace))
						.toArray(TextureType[]::new);
	}

	public boolean isSupported(Workspace workspace) {
		return workspace.getGeneratorStats().getTextureCoverageInfo().containsKey(this)
				&& workspace.getGeneratorStats().getTextureCoverageInfo().get(this)
				== GeneratorStats.CoverageStatus.FULL;
	}

	@Override public String toString() {
		return L10N.t("dialog.textures_import." + this.id);
	}
}
