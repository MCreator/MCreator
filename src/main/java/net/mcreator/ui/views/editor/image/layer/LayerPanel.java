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
import net.mcreator.ui.component.TransparentToolBar;
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
	private final JButton add;
	private final JButton up;
	private final JButton down;
	private final JButton editMeta;
	private final JButton toggleVisibility;
	private final JButton delete;

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
		TransparentToolBar controls = new TransparentToolBar();

		JLabel closed = L10N.label("dialog.imageeditor.layer_panel_no_image");
		JLabel empty = L10N.label("dialog.imageeditor.layer_panel_no_layers");

		add = new JButton(UIRES.get("18px.add"));
		up = new JButton(UIRES.get("18px.up"));
		down = new JButton(UIRES.get("18px.down"));
		editMeta = new JButton(UIRES.get("18px.edit"));
		toggleVisibility = new JButton(UIRES.get("18px.visibility"));
		delete = new JButton(UIRES.get("18px.remove"));

		add.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_new_layer"));
		add.setMargin(new Insets(0, 0, 0, 0));
		add.setOpaque(false);
		add.setBorder(BorderFactory.createEmptyBorder());
		add.setEnabled(false);

		up.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_move_up"));
		up.setMargin(new Insets(0, 0, 0, 0));
		up.setOpaque(false);
		up.setBorder(BorderFactory.createEmptyBorder());
		up.setEnabled(false);

		down.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_move_down"));
		down.setMargin(new Insets(0, 0, 0, 0));
		down.setOpaque(false);
		down.setBorder(BorderFactory.createEmptyBorder());
		down.setEnabled(false);

		editMeta.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_rename_layer"));
		editMeta.setMargin(new Insets(0, 0, 0, 0));
		editMeta.setOpaque(false);
		editMeta.setBorder(BorderFactory.createEmptyBorder());

		toggleVisibility.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_toggle_visibility"));
		toggleVisibility.setMargin(new Insets(0, 0, 0, 0));
		toggleVisibility.setOpaque(false);
		toggleVisibility.setBorder(BorderFactory.createEmptyBorder());

		delete.setToolTipText(L10N.t("dialog.imageeditor.layer_panel_delete_layer"));
		delete.setMargin(new Insets(0, 0, 0, 0));
		delete.setOpaque(false);
		delete.setBorder(BorderFactory.createEmptyBorder());

		canEdit(false);

		add.addActionListener(e -> {
			NewLayerDialog dialog = new NewLayerDialog(f, canvas);
			dialog.setVisible(true);
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
			int confirmDialog = JOptionPane
					.showConfirmDialog(f, L10N.t("dialog.imageeditor.layer_panel_confirm_layer_deletion_message") + selected(),
							L10N.t("dialog.imageeditor.layer_panel_confirm_layer_deletion_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (confirmDialog == 0)
				canvas.remove(selectedID());
		});

		controls.add(add);
		controls.add(up);
		controls.add(down);
		controls.add(toggleVisibility);
		controls.add(editMeta);
		controls.add(delete);

		layerPanel.setOpaque(false);

		layerPanel.add(PanelUtils.totalCenterInPanel(closed), LayerListMode.CLOSED.toString());
		layerPanel.add(PanelUtils.totalCenterInPanel(empty), LayerListMode.EMPTY.toString());

		//setOpaque(false);

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
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && r != null && r
						.contains(e.getPoint()))
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
		String newName = (String) JOptionPane
				.showInputDialog(f, L10N.t("dialog.imageeditor.layer_panel_enter_new_name"), L10N.t("dialog.imageeditor.layer_panel_rename_layer"), JOptionPane.PLAIN_MESSAGE, null, null,
						current.toString());
		if (newName != null) {
			current.setName(newName);
			rename.setAfter(current);
			versionManager.addRevision(rename);
			repaintList();
		}
	}

	public void setListMode(LayerListMode mode) {
		if (!(layerList == null && mode == LayerListMode.NORMAL))
			this.mode = mode;
		CardLayout cl = (CardLayout) (layerPanel.getLayout());
		cl.show(layerPanel, mode.toString());
	}

	public void select(int selected) {
		layerList.setSelectedIndex(selected);
		updateSelection();
	}

	public int selectedID() {
		return layerList.getSelectedIndex();
	}

	public Layer selected() {
		int selectedIndex = layerList.getSelectedIndex();
		if (selectedIndex == -1)
			return null;
		return canvas.get(selectedIndex);
	}

	public void updateSelection() {
		updateControls();
		Layer selected = selected();
		if (selected != null)
			toolPanel.setLayer(selected);
	}

	private void updateControls() {
		if (canvas.size() > 0) {
			setListMode(LayerListMode.NORMAL);
			if (selectedID() != -1) {
				canEdit(true);
				down.setEnabled(selectedID() < canvas.size() - 1);
				up.setEnabled(selectedID() > 0);
			} else {
				up.setEnabled(false);
				down.setEnabled(false);
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
		delete.setEnabled(can);
	}
}

enum LayerListMode {
	CLOSED, EMPTY, NORMAL
}