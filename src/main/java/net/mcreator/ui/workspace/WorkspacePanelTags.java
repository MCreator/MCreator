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

import net.mcreator.minecraft.TagType;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.NewTagDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.workspace.elements.TagElement;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WorkspacePanelTags extends AbstractWorkspacePanel {

	private final TableRowSorter<TableModel> sorter;
	private final JTable elements;

	public WorkspacePanelTags(WorkspacePanel workspacePanel) {
		super(workspacePanel);
		setLayout(new BorderLayout(0, 5));

		elements = new JTable(new DefaultTableModel(
				new Object[] { L10N.t("workspace.tags.tag_type"), L10N.t("workspace.tags.tag_namespace"),
						L10N.t("workspace.tags.tag_name"), L10N.t("workspace.tags.tag_elements") }, 0) {
			@Override public boolean isCellEditable(int row, int column) {
				return column == 3;
			}
		}) {
			@Override public TableCellEditor getCellEditor(int row, int column) {
				// https://stackoverflow.com/questions/11858286/how-to-use-custom-jtable-cell-editor-and-cell-renderer
				// TODO: implement
				return super.getCellEditor(row, column);
			}

			@Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				// https://stackoverflow.com/questions/11858286/how-to-use-custom-jtable-cell-editor-and-cell-renderer
				// TODO: implement
				return super.prepareRenderer(renderer, row, column);
			}
		};

		sorter = new TableRowSorter<>(elements.getModel());
		elements.setRowSorter(sorter);

		elements.setBackground(Theme.current().getBackgroundColor());
		elements.setSelectionBackground(Theme.current().getAltBackgroundColor());
		elements.setForeground(Color.white);
		elements.setSelectionForeground(Theme.current().getBackgroundColor());
		elements.setBorder(BorderFactory.createEmptyBorder());
		elements.setGridColor(Theme.current().getAltBackgroundColor());
		elements.setRowHeight(28);
		elements.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		ComponentUtils.deriveFont(elements, 13);

		JTableHeader header = elements.getTableHeader();
		header.setBackground(Theme.current().getInterfaceAccentColor());
		header.setForeground(Theme.current().getBackgroundColor());

		TableColumn lastColumn = header.getColumnModel().getColumn(3);
		header.setResizingColumn(lastColumn);

		header.getColumnModel().getColumn(0).setWidth(220);
		header.getColumnModel().getColumn(1).setWidth(220);
		header.getColumnModel().getColumn(2).setWidth(220);

		JScrollPane sp = new JScrollPane(elements);
		sp.setBackground(Theme.current().getBackgroundColor());
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI(Theme.current().getBackgroundColor(),
				Theme.current().getAltBackgroundColor(), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		sp.setColumnHeaderView(null);

		JPanel holder = new JPanel(new BorderLayout());
		holder.setOpaque(false);
		holder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		holder.add(sp);

		add("Center", holder);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		bar.add(createToolBarButton("workspace.tags.add_new", UIRES.get("16px.add.gif"), e -> {
			TagElement tag = NewTagDialog.showNewTagDialog(workspacePanel.getMCreator());
			if (tag != null) {
				workspacePanel.getMCreator().getWorkspace().addTagElement(tag);
				reloadElements();
			}
		}));

		bar.add(createToolBarButton("common.delete_selected", UIRES.get("16px.delete.gif"),
				e -> deleteCurrentlySelected()));

		add("North", bar);

		elements.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteCurrentlySelected();
				}
			}
		});
	}

	// TODO: implement
	/*@Override public boolean isSupportedInWorkspace() {
		return workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("tags")
				!= GeneratorStats.CoverageStatus.NONE;
	}*/

	private void deleteCurrentlySelected() {
		if (elements.getSelectedRow() == -1)
			return;

		// TODO: are you sure dialog

		Arrays.stream(elements.getSelectedRows()).mapToObj(el -> new TagElement((TagType) elements.getValueAt(el, 0),
						elements.getValueAt(el, 1).toString() + ":" + elements.getValueAt(el, 2).toString()))
				.forEach(tagElement -> {
					System.err.println(tagElement);
					// TODO: verify tag element has no managed entries
					workspacePanel.getMCreator().getWorkspace().removeTagElement(tagElement);
				});
		reloadElements();
	}

	@Override public void reloadElements() {
		int row = elements.getSelectedRow();

		DefaultTableModel model = (DefaultTableModel) elements.getModel();
		model.setRowCount(0);

		for (Map.Entry<TagElement, List<String>> tag : workspacePanel.getMCreator().getWorkspace().getTagElements()
				.entrySet()) {
			model.addRow(new Object[] { tag.getKey().type(), tag.getKey().getNamespace(), tag.getKey().getName(),
					tag.getValue() });
		}
		refilterElements();

		try {
			elements.setRowSelectionInterval(row, row);
		} catch (Exception ignored) {
		}
	}

	@Override public void refilterElements() {
		try {
			sorter.setRowFilter(RowFilter.regexFilter(workspacePanel.search.getText()));
		} catch (Exception ignored) {
		}
	}

}
