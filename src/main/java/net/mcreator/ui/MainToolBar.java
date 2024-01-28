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
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainToolBar extends JToolBar {

	private final JToolBar pluginToolbarLeft = new JToolBar();
	private final JToolBar pluginToolbarRight = new JToolBar();

	MainToolBar(MCreator mcreator) {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getSecondAltBackgroundColor()));
		setFloatable(false);

		pluginToolbarLeft.setBorder(BorderFactory.createEmptyBorder());
		pluginToolbarLeft.setMargin(new Insets(0, 0, 0, 0));
		pluginToolbarLeft.setFloatable(false);

		pluginToolbarRight.setBorder(BorderFactory.createEmptyBorder());
		pluginToolbarRight.setMargin(new Insets(0, 0, 0, 0));
		pluginToolbarRight.setFloatable(false);

		add(new JEmptyBox(4, 4));

		add(mcreator.actionRegistry.createMCItemTexture);
		add(mcreator.actionRegistry.createAnimatedTexture);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.importBlockTexture);
		add(mcreator.actionRegistry.importItemTexture);
		add(mcreator.actionRegistry.importEntityTexture);
		add(mcreator.actionRegistry.importScreenTexture);
		add(mcreator.actionRegistry.importParticleTexture);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.importSound);
		add(mcreator.actionRegistry.importStructure);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.importJavaModel);
		add(mcreator.actionRegistry.importJSONModel);
		add(mcreator.actionRegistry.importOBJModel);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.openMaterialPackMaker);
		add(mcreator.actionRegistry.openOrePackMaker);
		add(mcreator.actionRegistry.openToolPackMaker);
		add(mcreator.actionRegistry.openArmorPackMaker);
		add(mcreator.actionRegistry.openWoodPackMaker);

		addSeparator(new Dimension(10, 4));
		add(mcreator.actionRegistry.setCreativeTabItemOrder);

		addSeparator(new Dimension(10, 4));

		add(pluginToolbarLeft);

		add(Box.createHorizontalGlue()); // split left and right toolbar sections

		add(pluginToolbarRight);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.workspaceSettings);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.regenerateCode);
		add(mcreator.actionRegistry.buildWorkspace);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.runClient);
		add(mcreator.actionRegistry.debugClient);
		add(mcreator.actionRegistry.runServer);
		add(mcreator.actionRegistry.cancelGradleTaskAction);

		addSeparator(new Dimension(10, 4));

		add(mcreator.actionRegistry.exportToJAR);

		add(new JEmptyBox(4, 4));
	}

	/**
	 * @implNote Plugins should use {@link #addToLeftToolbar(Action)} and {@link #addToRightToolbar(Action)}
	 * instead of this overridden method.
	 */
	@Override public JButton add(Action action) {
		return decorateToolbarButton(super.add(action));
	}

	public JButton addToLeftToolbar(Action action) {
		return decorateToolbarButton(pluginToolbarLeft.add(action));
	}

	public JButton addToRightToolbar(Action action) {
		return decorateToolbarButton(pluginToolbarRight.add(action));
	}

	private static JButton decorateToolbarButton(JButton button) {
		button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent mouseEvent) {
				super.mouseEntered(mouseEvent);
				button.setBackground(Theme.current().getAltBackgroundColor());
			}

			@Override public void mouseExited(MouseEvent mouseEvent) {
				super.mouseExited(mouseEvent);
				button.setBackground(Theme.current().getBackgroundColor());
			}
		});
		return button;
	}

}
