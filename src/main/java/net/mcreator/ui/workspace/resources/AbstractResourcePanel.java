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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractResourcePanel<T> extends JPanel implements IReloadableFilterable {

	protected final WorkspacePanel workspacePanel;

	private final ResourceFilterModel<T> filterModel;
	private final Consumer<T> deleteSelectedElement;

	public AbstractResourcePanel(WorkspacePanel workspacePanel, ResourceFilterModel<T> filterModel,
			ListCellRenderer<T> render, Consumer<T> deleteSelectedElement) {
		super(new BorderLayout());
		setOpaque(false);

		this.filterModel = filterModel;
		this.workspacePanel = workspacePanel;
		this.deleteSelectedElement = deleteSelectedElement;

		JSelectableList<T> elementList = new JSelectableList<>(filterModel);
		elementList.setOpaque(false);
		elementList.setCellRenderer(render);
		elementList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane sp = new JScrollPane(elementList);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		add("Center", sp);

		elementList.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteCurrentlySelected(elementList);
				}
			}
		});

		add("North", createToolBar());
	}

	abstract TransparentToolBar createToolBar();

	private void deleteCurrentlySelected(JSelectableList<T> structureElementList) {
		List<T> files = structureElementList.getSelectedValuesList();
		if (!files.isEmpty()) {
			int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.structure.confirm_deletion_message"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == 0) {
				files.forEach(deleteSelectedElement);
				reloadElements();
			}
		}
	}

	@Override public abstract void reloadElements();

	@Override public void refilterElements() {
		filterModel.refilter();
	}
}
