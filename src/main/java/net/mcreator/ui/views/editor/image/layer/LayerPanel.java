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

package net.mcreator.ui.views.editor.image.layer;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.imageeditor.NewLayerDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.tool.ToolPanel;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Direction;
import net.mcreator.ui.views.editor.image.versioning.change.ListRelocation;
import net.mcreator.ui.views.editor.image.versioning.change.Rename;
import net.mcreator.ui.views.editor.image.versioning.change.VisibilityChange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LayerPanel extends JPanel {
	private JList<Layer> layerList;
	private final JPanel layerPanel = new JPanel(new CardLayout());
	private final JButton add, up, down, editMeta, toggleVisibility, delete, duplicate, mergeDown;

	private Canvas canvas;
	private LayerListMode mode;

	private final MCreator f;
	private final ToolPanel toolPanel;

	private final VersionManager versionManager;

	public LayerPanel(MCreator f, ToolPanel toolPanel, VersionManager versionManager) {
		super(new BorderLayout());
		this.f = f;
		this.toolPanel = toolPanel;
		this.versionManager = versionManager;
		JToolBar controls = new JToolBar();

		JLabel closed = L10N.label("dialog.imageeditor.layer_panel_no_image");
		JLabel empty = L10N.label("dialog.imageeditor.layer_panel_no_layers");

		add = new JButton(UIRES.get("18px.add"));
		up = new JButton(UIRES.get("18px.up"));
		down = new JButton(UIRES.get("18px.down"));
		editMeta = new JButton(UIRES.get("18px.edit"));
		toggleVisibility = new JButton(UIRES.get("18px.visibility"));
		delete = new JButton(UIRES.get("18px.remove"));
		duplicate = new JButton(UIRES.get("18px.duplicate"));
		mergeDown = new JButton(UIRES.get("18px.merge"));

		add.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_new_layer"));
		add.setMargin(new Insets(1, 1, 1, 1));
		add.setEnabled(false);

		up.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_move_up"));
		up.setMargin(new Insets(1, 1, 1, 1));
		up.setEnabled(false);

		down.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_move_down"));
		down.setMargin(new Insets(1, 1, 1, 1));
		down.setEnabled(false);

		editMeta.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_rename_layer"));
		editMeta.setMargin(new Insets(1, 1, 1, 1));

		toggleVisibility.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_toggle_visibility"));
		toggleVisibility.setMargin(new Insets(1, 1, 1, 1));

		delete.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_delete_layer"));
		delete.setMargin(new Insets(1, 1, 1, 1));

		duplicate.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_duplicate_layer"));
		duplicate.setMargin(new Insets(1, 1, 1, 1));

		mergeDown.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_merge_layers_down"));
		mergeDown.setMargin(new Insets(1, 1, 1, 1));

		canEdit(false);

		add.addActionListener(e -> {
			if (isFloating()) {
				canvas.consolidateFloating();
				updateControls();
			} else {
				NewLayerDialog dialog = new NewLayerDialog(f, canvas);
				dialog.setVisible(true);
			}
		});

		up.addActionListener(e -> {
			canvas.moveUp(selectedID());
			versionManager.addRevision(new ListRelocation(canvas, selected(), Direction.UP));
		});

		down.addActionListener(e -> {
			canvas.moveDown(selectedID());
			versionManager.addRevision(new ListRelocation(canvas, selected(), Direction.DOWN));
		});

		editMeta.addActionListener(e -> promptRename());

		toggleVisibility.addActionListener(e -> {
			Layer current = selected();
			boolean newVis = !current.isVisible();
			current.setVisible(newVis);
			versionManager.addRevision(new VisibilityChange(canvas, current, newVis));
		});

		delete.addActionListener(e -> {
			int confirmDialog = JOptionPane.showConfirmDialog(f,
					L10N.t("dialog.imageeditor.layer_panel_confirm_layer_deletion_message") + selected(),
					L10N.t("dialog.imageeditor.layer_panel_confirm_layer_deletion_title"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirmDialog == 0) {
				canvas.remove(selectedID());
				updateControls();
			}
		});

		duplicate.addActionListener(e -> {
			if (selected() != null)
				canvas.add(selected().copy());
		});

		mergeDown.addActionListener(e -> {
			if (selected() != null)
				canvas.mergeDown(selectedID());
		});

		controls.add(add);
		controls.add(duplicate);
		controls.add(mergeDown);
		controls.add(up);
		controls.add(down);
		controls.add(toggleVisibility);
		controls.add(editMeta);
		controls.add(delete);

		layerPanel.setOpaque(false);

		layerPanel.add(PanelUtils.totalCenterInPanel(closed), LayerListMode.CLOSED.toString());
		layerPanel.add(PanelUtils.totalCenterInPanel(empty), LayerListMode.EMPTY.toString());

		add(layerPanel, BorderLayout.CENTER);
		add(controls, BorderLayout.NORTH);
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;

		layerList = new JList<>(canvas);

		layerList.setOpaque(false);
		layerList.setCellRenderer(new LayerListCellRenderer());
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerList.addListSelectionListener(e -> updateSelection());

		layerList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				Rectangle r = layerList.getCellBounds(0, layerList.getLastVisibleIndex());
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && r != null && r.contains(
						e.getPoint()))
					promptRename();
			}
		});

		layerPanel.add(layerList, LayerListMode.NORMAL.toString());
		add.setEnabled(true);

		repaintAll();
	}

	private void promptRename() {
		Layer current = selected();
		Rename rename = new Rename(canvas, current);
		String newName = (String) JOptionPane.showInputDialog(f,
				L10N.t("dialog.imageeditor.layer_panel_enter_new_name"),
				L10N.t("dialog.imageeditor.layer_panel_rename_layer"), JOptionPane.PLAIN_MESSAGE, null, null,
				current.toString());
		if (newName != null) {
			current.setName(newName);
			rename.setAfter(current);
			versionManager.addRevision(rename);
			repaintList();
		}
	}

	private void setListMode(LayerListMode mode) {
		if (!(layerList == null && mode == LayerListMode.NORMAL))
			this.mode = mode;
		CardLayout cl = (CardLayout) (layerPanel.getLayout());
		cl.show(layerPanel, mode.toString());
	}

	public void updateFloatingLayer() {
		updateControls();
	}

	public void select(int selected) {
		if (selected < 0)
			layerList.setSelectedIndex(0);
		else if (selected >= canvas.size())
			layerList.setSelectedIndex(canvas.size() - 1);
		else
			layerList.setSelectedIndex(selected);
		updateSelection();
	}

	public int selectedID() {
		return layerList.getSelectedIndex();
	}

	public boolean isFloating() {
		return canvas.getFloatingLayer() != null && canvas.getFloatingLayer().isPasted();
	}

	public Layer selected() {
		if (layerList == null)
			return null;

		int selectedIndex = layerList.getSelectedIndex();
		if (selectedIndex == -1)
			return null;
		return canvas.get(selectedIndex);
	}

	public void updateSelection() {
		Layer selected = selected();
		if (selected != null)
			toolPanel.setLayer(selected);
		updateControls();
	}

	public void updateControls() {
		if (!canvas.isEmpty()) {
			setListMode(LayerListMode.NORMAL);
			if (selectedID() != -1) {
				boolean floating = isFloating();
				canEdit(!floating);
				down.setEnabled(selectedID() < canvas.size() - 1 && !floating);
				up.setEnabled(selectedID() > 0 && !floating);
				mergeDown.setEnabled(selectedID() < canvas.size() - 1);
				layerList.setEnabled(!floating);
				if (floating) {
					add.setIcon(UIRES.get("18px.add_new"));
					add.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_new_layer.floating"));
					delete.setEnabled(true);
				} else {
					add.setIcon(UIRES.get("18px.add"));
					add.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_new_layer"));
				}
			} else {
				up.setEnabled(false);
				down.setEnabled(false);
				mergeDown.setEnabled(false);
				canEdit(false);
			}
		} else
			setListMode(LayerListMode.EMPTY);
		repaintAll();
	}

	public void repaintAll() {
		layerList.repaint();
		if (canvas != null && canvas.getCanvasRenderer() != null)
			canvas.getCanvasRenderer().repaint();
	}

	public void repaintList() {
		layerList.repaint();
	}

	private void canEdit(boolean can) {
		editMeta.setEnabled(can);
		toggleVisibility.setEnabled(can);
		delete.setEnabled(can && canvas.size() > 1); // Disable the button if only one layer is present
		duplicate.setEnabled(can);
	}
}

enum LayerListMode {
	CLOSED, EMPTY, NORMAL
}