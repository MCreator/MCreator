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

package net.mcreator.ui.views.editor.image.color;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.ArrayListListModel;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class ListEditPanel<T> extends JPanel {

	protected final MCreator mcreator;

	private final JPanel contentPanel = new JPanel(new CardLayout());
	JScrollPane listScrollPane = new JScrollPane();
	private ArrayListListModel<T> listModel;
	protected JList<T> list;
	private final JButton add, duplicate, edit, up, down, delete;
	private ListMode mode = ListMode.EMPTY;

	private MouseListener listListener;

	private final ListSelectionListener listSelectionListener = this::valueChanged;
	private final ListDataListener listDataListener;

	private boolean requestAtLeastOneElement = false;

	private boolean promptOnDelete = true;

	public ListEditPanel(MCreator mcreator) {
		super(new BorderLayout());
		this.mcreator = mcreator;

		JToolBar controls = new JToolBar();
		controls.setOpaque(false);
		controls.setFloatable(false);

		JLabel closed = L10N.label("dialog.image_maker.list_edit_panel.nothing_opened");
		JLabel empty = L10N.label("dialog.image_maker.list_edit_panel.nothing_to_show");

		add = new JButton(UIRES.get("18px.add"));
		duplicate = new JButton(UIRES.get("18px.duplicate"));
		up = new JButton(UIRES.get("18px.up"));
		down = new JButton(UIRES.get("18px.down"));
		edit = new JButton(UIRES.get("18px.edit"));
		delete = new JButton(UIRES.get("18px.remove"));

		add.setToolTipText(L10N.t("dialog.image_maker.list_edit_panel.add"));
		add.setMargin(new Insets(1, 1, 1, 1));
		add.setEnabled(false);

		duplicate.setToolTipText(L10N.t("dialog.image_maker.list_edit_panel.duplicate"));
		duplicate.setMargin(new Insets(1, 1, 1, 1));

		up.setToolTipText(L10N.t("dialog.image_maker.list_edit_panel.move_up"));
		up.setMargin(new Insets(1, 1, 1, 1));
		up.setEnabled(false);

		down.setToolTipText(L10N.t("dialog.image_maker.list_edit_panel.move_down"));
		down.setMargin(new Insets(1, 1, 1, 1));
		down.setEnabled(false);

		edit.setToolTipText(L10N.t("dialog.image_maker.list_edit_panel.edit"));
		edit.setMargin(new Insets(1, 1, 1, 1));

		delete.setToolTipText(L10N.t("dialog.image_maker.list_edit_panel.delete"));
		delete.setMargin(new Insets(1, 1, 1, 1));

		canEdit(false);

		add.addActionListener(e -> {
			T newItem = createNew(selected());
			if (newItem != null) {
				listModel.add(selectedIndex() + 1, newItem);
				select(selectedIndex() + 1);
			}
		});

		duplicate.addActionListener(e -> {
			if (selected() != null)
				listModel.add(selectedIndex(), selected());
			updateControls();
		});

		up.addActionListener(e -> {
			listModel.moveUp(selectedIndex());
			select(selectedIndex() - 1);
		});

		down.addActionListener(e -> {
			listModel.moveDown(selectedIndex());
			select(selectedIndex() + 1);
		});

		edit.addActionListener(e -> promptEdit(selected()));

		delete.addActionListener(e -> {
			int selected = selectedIndex();

			if (promptOnDelete) {
				int confirmDialog = JOptionPane.showConfirmDialog(mcreator,
						L10N.t("dialog.image_maker.list_edit_panel.dialog.confirm_deletion.message",
								getItemName(selected())),
						L10N.t("dialog.image_maker.list_edit_panel.dialog.confirm_deletion.title"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (confirmDialog == 0) {
					listModel.remove(selectedIndex());
					updateControls();
				}
			} else {
				listModel.remove(selectedIndex());
				updateControls();
			}

			list.setSelectedIndex(selected);
		});
		listDataListener = new ListDataListener() {
			@Override public void intervalAdded(ListDataEvent e) {
				updateControls();
			}

			@Override public void intervalRemoved(ListDataEvent e) {
				updateControls();
			}

			@Override public void contentsChanged(ListDataEvent e) {}
		};

		controls.add(add);
		controls.add(duplicate);
		controls.add(up);
		controls.add(down);
		controls.add(edit);
		controls.add(delete);

		contentPanel.setOpaque(false);

		contentPanel.add(listScrollPane, ListMode.NORMAL.toString());
		contentPanel.add(PanelUtils.totalCenterInPanel(closed), ListMode.CLOSED.toString());
		contentPanel.add(PanelUtils.totalCenterInPanel(empty), ListMode.EMPTY.toString());

		add(contentPanel, BorderLayout.CENTER);
		add(controls, BorderLayout.NORTH);
	}

	protected abstract void itemSelected(T selected);

	@Nullable public abstract T createNew(T selected);

	protected abstract void promptEdit(T selected);

	protected abstract String getItemName(T selected);

	private void setListMode(ListMode mode) {
		this.mode = mode;
		CardLayout cl = (CardLayout) (contentPanel.getLayout());
		cl.show(contentPanel, mode.toString());
	}

	public void select(int selected) {
		if (selected < 0)
			list.setSelectedIndex(0);
		else if (selected >= listModel.size())
			list.setSelectedIndex(listModel.size() - 1);
		else
			list.setSelectedIndex(selected);
		updateControls();
	}

	public int selectedIndex() {
		if (mode == ListMode.NORMAL)
			return list.getSelectedIndex();
		return -1;
	}

	public T selected() {
		if (mode == ListMode.NORMAL)
			return list.getSelectedValue();
		return null;
	}

	public void updateControls() {
		if (!listModel.isEmpty()) {
			setListMode(ListMode.NORMAL);
			if (selectedIndex() != -1) {
				down.setEnabled(selectedIndex() < listModel.size() - 1);
				up.setEnabled(selectedIndex() > 0);
				canEdit(true);
			} else {
				up.setEnabled(false);
				down.setEnabled(false);
				canEdit(false);
			}
		} else
			setListMode(ListMode.EMPTY);
		repaintAll();
	}

	public void repaintAll() {
		list.repaint();
	}

	public void setRequestAtLeastOneElement(boolean requestAtLeastOneElement) {
		this.requestAtLeastOneElement = requestAtLeastOneElement;
	}

	public void setPromptOnDelete(boolean promptOnDelete) {
		this.promptOnDelete = promptOnDelete;
	}

	private void canEdit(boolean can) {
		edit.setEnabled(can);
		duplicate.setEnabled(can);
		delete.setEnabled(can && (!requestAtLeastOneElement
				|| list.getModel().getSize() > 1)); // Disable the button if only one element is present
	}

	public void setList(ArrayListListModel<T> contents, ListCellRenderer<T> renderer) {
		if (listModel != null)
			listModel.removeListDataListener(listDataListener);
		if (list != null) {
			list.removeListSelectionListener(listSelectionListener);
			list.removeMouseListener(listListener);
			contentPanel.remove(list);
		}

		listModel = contents;

		list = new JList<>(listModel);
		listScrollPane.setViewportView(list);

		list.setOpaque(false);

		list.setCellRenderer(renderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listListener = new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && r != null && r.contains(
						e.getPoint()))
					itemDoubleClicked(selected());
			}
		};

		list.addListSelectionListener(listSelectionListener);
		list.addMouseListener(listListener);

		listModel.addListDataListener(listDataListener);
		add.setEnabled(true);

		updateControls();
	}

	protected void itemDoubleClicked(T selected) {
		promptEdit(selected());
	}

	private void valueChanged(ListSelectionEvent e) {
		itemSelected(selected());
		updateControls();
	}

	enum ListMode {
		CLOSED, EMPTY, NORMAL
	}
}