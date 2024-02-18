/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.workspace;

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

/**
 * This class represents a section of a workspace where certain workspace elements/files can be managed.
 */
public abstract class AbstractWorkspacePanel extends JPanel implements IReloadableFilterable {

	protected final WorkspacePanel workspacePanel;

	/**
	 * Sole constructor.
	 *
	 * @param workspacePanel The main workspace section.
	 */
	public AbstractWorkspacePanel(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		this.workspacePanel = workspacePanel;
		setOpaque(false);
	}

	/**
	 * Determines whether the section will be registered but not shown in the workspace when registered.
	 *
	 * @return Whether the section will be registered but not shown in the workspace when registered.
	 */
	public boolean isSupportedInWorkspace() {
		return true;
	}

	/**
	 * Determines whether clicking the tab button will actually switch the section.
	 *
	 * @return Whether clicking the tab button will actually switch the section.
	 */
	public boolean canSwitchToSection() {
		return true;
	}

	public static JButton createToolBarButton(String translationKey, ImageIcon icon) {
		return createToolBarButton(translationKey, icon, null, null);
	}

	public static JButton createToolBarButton(String translationKey, ImageIcon icon, ActionListener actionListener) {
		return createToolBarButton(translationKey, icon, actionListener, null);
	}

	public static JButton createToolBarButton(String translationKey, ImageIcon icon, MouseListener mouseListener) {
		return createToolBarButton(translationKey, icon, null, mouseListener);
	}

	public static JButton createToolBarButton(String translationKey, ImageIcon icon,
			@Nullable ActionListener actionListener, @Nullable MouseListener mouseListener) {
		JButton button = L10N.button(translationKey);
		button.setIcon(icon);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		ComponentUtils.deriveFont(button, 12);
		button.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		if (actionListener != null)
			button.addActionListener(actionListener);
		if (mouseListener != null)
			button.addMouseListener(mouseListener);
		return button;
	}

}
