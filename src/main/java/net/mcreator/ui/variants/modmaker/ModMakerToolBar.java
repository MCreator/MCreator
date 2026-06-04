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

import static net.mcreator.ui.action.ActionUtils.hideableButton;

class ModMakerToolBar extends MainToolBar {

	public ModMakerToolBar(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected void assembleLeftSection(MCreator mcreator) {
		hideableButton(add(mcreator.getActionRegistry().createTexture));
		hideableButton(add(mcreator.getActionRegistry().createAnimatedTexture));

		addSeparator(new Dimension(10, 4));

		hideableButton(add(mcreator.getActionRegistry().importBlockTexture));
		hideableButton(add(mcreator.getActionRegistry().importItemTexture));
		hideableButton(add(mcreator.getActionRegistry().importSound));
		hideableButton(add(mcreator.getActionRegistry().importStructure));

		addSeparator(new Dimension(10, 4));

		hideableButton(add(mcreator.getActionRegistry().importJavaModel));
		hideableButton(add(mcreator.getActionRegistry().importJavaModelAnimation));
		hideableButton(add(mcreator.getActionRegistry().importJSONModel));
		hideableButton(add(mcreator.getActionRegistry().importOBJModel));
		hideableButton(add(mcreator.getActionRegistry().importBedrockModel));

		addSeparator(new Dimension(10, 4));
		hideableButton(add(mcreator.getActionRegistry().setCreativeTabItemOrder));

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
