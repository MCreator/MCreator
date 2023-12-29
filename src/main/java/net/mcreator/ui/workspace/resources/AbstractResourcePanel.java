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

package net.mcreator.ui.workspace.resources;

import net.mcreator.ui.component.JSelectableList;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

/**
 * <p>An abstract class used to standardize code, methods and features across the different resource tabs.
 * This class is optional and only expected to be used when the behavior of a tab is similar to the others (e.g. sounds, structures, etc.)
 * It should not be used in a case where it is different in some way (e.g. textures having multiple lists).</p>
 *
 * @param <T> Type of resources that can be managed in this tab.
 */
public abstract class AbstractResourcePanel<T> extends JPanel implements IReloadableFilterable {

	protected final WorkspacePanel workspacePanel;

	private final TransparentToolBar bar = new TransparentToolBar();

	protected final ResourceFilterModel<T> filterModel;
	protected JSelectableList<T> elementList;

	public AbstractResourcePanel(WorkspacePanel workspacePanel, ResourceFilterModel<T> filterModel,
			ListCellRenderer<T> render) {
		this(workspacePanel, filterModel, render, JList.VERTICAL);
	}

	public AbstractResourcePanel(WorkspacePanel workspacePanel, ResourceFilterModel<T> filterModel,
			ListCellRenderer<T> render, int layoutOrientation) {
		super(new BorderLayout());
		setOpaque(false);

		this.filterModel = filterModel;
		this.workspacePanel = workspacePanel;

		elementList = new JSelectableList<>(filterModel);
		elementList.setOpaque(false);
		elementList.setCellRenderer(render);
		elementList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		elementList.setLayoutOrientation(layoutOrientation);
		if (layoutOrientation == JList.HORIZONTAL_WRAP)
			elementList.setVisibleRowCount(-1);

		JScrollPane sp = new JScrollPane(elementList);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI(Theme.current().getBackgroundColor(),
				Theme.current().getAltBackgroundColor(), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		add("Center", sp);

		elementList.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteCurrentlySelected();
				}
			}
		});

		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		add("North", bar);
	}

	abstract void deleteCurrentlySelected();

	@Override public void refilterElements() {
		filterModel.refilter();
	}

	protected void addToolBarButton(String translationKey, ImageIcon icon, ActionListener actionListener) {
		bar.add(AbstractWorkspacePanel.createToolBarButton(translationKey, icon, actionListener));
	}

	protected void addToolBarButton(String translationKey, ImageIcon icon, MouseListener mouseListener) {
		bar.add(AbstractWorkspacePanel.createToolBarButton(translationKey, icon, mouseListener));
	}

}
