/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.variants.modmaker;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MainToolBar;

import java.awt.*;

class ModMakerToolBar extends MainToolBar {

	public ModMakerToolBar(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected void assembleLeftSection(MCreator mcreator) {
		add(mcreator.getActionRegistry().createMCItemTexture);
		add(mcreator.getActionRegistry().createAnimatedTexture);

		addSeparator(new Dimension(10, 4));

		add(mcreator.getActionRegistry().importBlockTexture);
		add(mcreator.getActionRegistry().importItemTexture);
		add(mcreator.getActionRegistry().importSound);
		add(mcreator.getActionRegistry().importStructure);

		addSeparator(new Dimension(10, 4));

		add(mcreator.getActionRegistry().importJavaModel);
		add(mcreator.getActionRegistry().importJavaModelAnimation);
		add(mcreator.getActionRegistry().importJSONModel);
		add(mcreator.getActionRegistry().importOBJModel);

		addSeparator(new Dimension(10, 4));
		add(mcreator.getActionRegistry().setCreativeTabItemOrder);

		addSeparator(new Dimension(10, 4));
	}

	@Override protected void assembleRightSection(MCreator mcreator) {
		add(mcreator.getActionRegistry().workspaceSettings);

		addSeparator(new Dimension(10, 4));

		add(mcreator.getActionRegistry().regenerateCode);
		add(mcreator.getActionRegistry().buildWorkspace);

		addSeparator(new Dimension(10, 4));

		add(mcreator.getActionRegistry().runClient);
		add(mcreator.getActionRegistry().debugClient);
		add(mcreator.getActionRegistry().runServer);
		add(mcreator.getActionRegistry().cancelGradleTaskAction);

		addSeparator(new Dimension(10, 4));

		add(mcreator.getActionRegistry().exportToJAR);
	}

}
