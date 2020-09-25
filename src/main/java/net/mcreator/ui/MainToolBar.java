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

package net.mcreator.ui;

import net.mcreator.ui.component.JEmptyBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainToolBar extends JToolBar {

	MainToolBar(MCreator mcreator) {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));
		setFloatable(false);

		add(new JEmptyBox(4, 4));

		add(mcreator.actionRegistry.createMCItemTexture);
		add(mcreator.actionRegistry.createAnimatedTexture);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.importBlockTexture);
		add(mcreator.actionRegistry.importItemTexture);
		add(mcreator.actionRegistry.importOtherTexture);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.importSound);
		add(mcreator.actionRegistry.importStructure);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.importJavaModel);
		add(mcreator.actionRegistry.importJSONModel);
		add(mcreator.actionRegistry.importOBJModel);

		addSeparator(new Dimension(10, 5));

		add(mcreator.actionRegistry.openMaterialPackMaker);
		add(mcreator.actionRegistry.openOrePackMaker);
		add(mcreator.actionRegistry.openToolPackMaker);
		add(mcreator.actionRegistry.openArmorPackMaker);
		add(mcreator.actionRegistry.openWoodPackMaker);

		addSeparator(new Dimension(10, 4));
		add(mcreator.actionRegistry.setCreativeTabItemOrder);

		add(Box.createHorizontalGlue());

		add(mcreator.actionRegistry.setupVCSOrSettings);
		add(mcreator.actionRegistry.syncFromRemote);
		add(mcreator.actionRegistry.syncToRemote);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.workspaceSettings);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.regenerateCode);
		add(mcreator.actionRegistry.buildWorkspace);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.runClient);
		add(mcreator.actionRegistry.runServer);
		add(mcreator.actionRegistry.cancelGradleTaskAction);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.exportToJAR);

		add(new JEmptyBox(4, 4));
	}

	@Override public JButton add(Action action) {
		JButton button = super.add(action);
		button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent mouseEvent) {
				super.mouseEntered(mouseEvent);
				button.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			}

			@Override public void mouseExited(MouseEvent mouseEvent) {
				super.mouseExited(mouseEvent);
				button.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			}
		});
		return button;
	}

}
