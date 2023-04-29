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
	private final JToolBar pluginTools = new JToolBar();

	MainToolBar(MCreator mcreator) {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));
		setFloatable(false);
		pluginTools.setBorder(BorderFactory.createEmptyBorder());
		pluginTools.setFloatable(false);

		add(new JEmptyBox(4, 4));

		addBuiltin(mcreator.actionRegistry.createMCItemTexture);
		addBuiltin(mcreator.actionRegistry.createAnimatedTexture);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.importBlockTexture);
		addBuiltin(mcreator.actionRegistry.importItemTexture);
		addBuiltin(mcreator.actionRegistry.importEntityTexture);
		addBuiltin(mcreator.actionRegistry.importScreenTexture);
		addBuiltin(mcreator.actionRegistry.importParticleTexture);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.importSound);
		addBuiltin(mcreator.actionRegistry.importStructure);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.importJavaModel);
		addBuiltin(mcreator.actionRegistry.importJSONModel);
		addBuiltin(mcreator.actionRegistry.importOBJModel);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.openMaterialPackMaker);
		addBuiltin(mcreator.actionRegistry.openOrePackMaker);
		addBuiltin(mcreator.actionRegistry.openToolPackMaker);
		addBuiltin(mcreator.actionRegistry.openArmorPackMaker);
		addBuiltin(mcreator.actionRegistry.openWoodPackMaker);

		addSeparator(new Dimension(10, 4));
		addBuiltin(mcreator.actionRegistry.setCreativeTabItemOrder);
		addBuiltin(mcreator.actionRegistry.injectDefaultTags);

		addSeparator(new Dimension(10, 4));
		add(pluginTools);
		add(Box.createHorizontalGlue());

		add(mcreator.actionRegistry.setupVCSOrSettings);
		add(mcreator.actionRegistry.syncFromRemote);
		add(mcreator.actionRegistry.syncToRemote);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.workspaceSettings);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.regenerateCode);
		addBuiltin(mcreator.actionRegistry.buildWorkspace);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.runClient);
		addBuiltin(mcreator.actionRegistry.runServer);
		addBuiltin(mcreator.actionRegistry.cancelGradleTaskAction);

		addSeparator(new Dimension(10, 4));

		addBuiltin(mcreator.actionRegistry.exportToJAR);

		add(new JEmptyBox(4, 4));
	}

	@Override public JButton add(Action action) {
		return addImpl(action, true);
	}

	private void addBuiltin(Action action) {
		addImpl(action, false);
	}

	private JButton addImpl(Action action, boolean custom) {
		JButton button = custom ? pluginTools.add(action) : super.add(action);
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
