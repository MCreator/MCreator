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

package net.mcreator.ui.action.impl.workspace.resources;

import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.workspace.resources.TextureType;

import javax.annotation.Nullable;
import java.awt.event.ActionListener;

public class TextureAction extends BasicAction {

	@Nullable private final TextureType textureType;

	public TextureAction(ActionRegistry actionRegistry, String name, ActionListener listener) {
		this(actionRegistry, name, listener, null);
	}

	public TextureAction(ActionRegistry actionRegistry, String name, ActionListener listener,
			@Nullable TextureType textureType) {
		super(actionRegistry, name, listener);
		this.textureType = textureType;
	}

	@Override public boolean isEnabled() {
		if (textureType != null && !textureType.isSupported(actionRegistry.getMCreator().getWorkspace()))
			return false;

		return actionRegistry.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("textures")
				!= GeneratorStats.CoverageStatus.NONE;
	}

}
