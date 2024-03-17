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

import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.DamageTypeEntry;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.AddCommonTagsDialog;
import net.mcreator.ui.dialogs.NewTagDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.*;
import net.mcreator.workspace.elements.TagElement;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkspacePanelTags extends AbstractWorkspacePanel {

	private final TableRowSorter<TableModel> sorter;
	private final JTable elements;

	// Cache of list fields (so cell renderer just sets the value instead of making a new object)
	private final MCItemListField listFieldBlocksItems = new MCItemListField(workspacePanel.getMCreator(),
			ElementUtil::loadBlocksAndItems, false, true);
	private final SpawnableEntityListField listFieldEntities = new SpawnableEntityListField(
			workspacePanel.getMCreator(), true);
	private final BiomeListField listFieldBiomes = new BiomeListField(workspacePanel.getMCreator(), true);
	private final ModElementListField listFieldFunctions = new ModElementListField(workspacePanel.getMCreator(),
			ModElementType.FUNCTION);
	private final DamageTypeListField listFieldDamageTypes = new DamageTypeListField(workspacePanel.getMCreator(),
			true);

	private final JEmptyBox DUMMY_FIELD = new JEmptyBox();

	// Cache of list editor
	private ItemListFieldCellEditor lastEditor = null;

	public WorkspacePanelTags(WorkspacePanel workspacePanel) {
		super(workspacePanel);
		setLayout(new BorderLayout(0, 5));

		listFieldBlocksItems.disableItemCentering();
		listFieldEntities.disableItemCentering();
		listFieldBiomes.disableItemCentering();
		listFieldFunctions.disableItemCentering();
		listFieldDamageTypes.disableItemCentering();

		listFieldBlocksItems.hideButtons();
		listFieldEntities.hideButtons();
		listFieldBiomes.hideButtons();
		listFieldFunctions.hideButtons();
		listFieldDamageTypes.hideButtons();

		listFieldBlocksItems.setEnabled(false);
		listFieldEntities.setEnabled(false);
		listFieldBiomes.setEnabled(false);
		listFieldFunctions.setEnabled(false);
		listFieldDamageTypes.setEnabled(false);

		listFieldBlocksItems.setOpaque(false);
		listFieldEntities.setOpaque(false);
		listFieldBiomes.setOpaque(false);
		listFieldFunctions.setOpaque(false);
		listFieldDamageTypes.setOpaque(false);

		elements = new JTable(new DefaultTableModel(
				new Object[] { L10N.t("workspace.tags.tag_type"), L10N.t("workspace.tags.tag_namespace"),
						L10N.t("workspace.tags.tag_name"), L10N.t("workspace.tags.tag_elements") }, 0) {
			@Override public boolean isCellEditable(int row, int column) {
				return column == 3;
			}
		}) {
			@Override public TableCellEditor getCellEditor(int row, int column) {
				if (column == 3) {
					TagElement tagElement = tagElementForRow(row);
					if (lastEditor != null && lastEditor.getTagElement().equals(tagElement)) {
						return lastEditor;
					} else {
						if (lastEditor != null)
							lastEditor.cancelTimer();
						return lastEditor = new ItemListFieldCellEditor(tagElement);
					}
				}
				return super.getCellEditor(row, column);
			}

			@Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				JComponent retval = (JComponent) super.prepareRenderer(renderer, row, column);

				if (column == 3) {
					// Calculate how many elements can fit in the cell (assuming 31px per element which is true for ITEMS and BLOCKS)
					int visibleCount = (int) Math.ceil(elements.getColumnModel().getColumn(3).getWidth() / 31.0);
					TagElement tagElement = tagElementForRow(row);
					Stream<String> entries = workspacePanel.getMCreator().getWorkspace().getTagElements()
							.get(tagElement).stream().limit(visibleCount);
					JItemListField<?> listField = switch (tagElement.type()) {
						case ITEMS, BLOCKS -> {
							listFieldBlocksItems.setListElements(entries.map(
											e -> (MItemBlock) TagElement.entryToMappableElement(
													workspacePanel.getMCreator().getWorkspace(), tagElement.type(), e))
									.toList());
							yield listFieldBlocksItems;
						}
						case ENTITIES -> {
							listFieldEntities.setListElements(entries.map(
											e -> (EntityEntry) TagElement.entryToMappableElement(
													workspacePanel.getMCreator().getWorkspace(), tagElement.type(), e))
									.toList());
							yield listFieldEntities;
						}
						case BIOMES -> {
							listFieldBiomes.setListElements(entries.map(
											e -> (BiomeEntry) TagElement.entryToMappableElement(
													workspacePanel.getMCreator().getWorkspace(), tagElement.type(), e))
									.toList());
							yield listFieldBiomes;
						}
						case FUNCTIONS -> {
							listFieldFunctions.setListElements(entries.map(
											e -> (NonMappableElement) TagElement.entryToMappableElement(
													workspacePanel.getMCreator().getWorkspace(), tagElement.type(), e))
									.toList());
							yield listFieldFunctions;
						}
						case DAMAGE_TYPES -> {
							listFieldDamageTypes.setListElements(entries.map(
											e -> (DamageTypeEntry) TagElement.entryToMappableElement(
													workspacePanel.getMCreator().getWorkspace(), tagElement.type(), e))
									.toList());
							yield listFieldDamageTypes;
						}
					};
					listField.setBorder(retval.getBorder());
					return listField;
				}

				try {
					if (column == 0) {
						TagType tagType = (TagType) elements.getValueAt(row, 0);
						retval.setForeground(tagType.getColor().brighter());
					} else {
						retval.setForeground(Theme.current().getForegroundColor());
					}
					return retval;
				} catch (Exception ignored) {
					return DUMMY_FIELD;
				}
			}
		};

		sorter = new TableRowSorter<>(elements.getModel());
		sorter.toggleSortOrder(2);
		sorter.addRowSorterListener(e -> clearEditor());
		elements.setRowSorter(sorter);

		elements.setBackground(Theme.current().getBackgroundColor());
		elements.setSelectionBackground(Theme.current().getAltBackgroundColor());
		elements.setForeground(Theme.current().getForegroundColor());
		elements.setSelectionForeground(Theme.current().getForegroundColor());
		elements.setBorder(BorderFactory.createEmptyBorder());
		elements.setGridColor(Theme.current().getAltBackgroundColor());
		elements.setRowHeight(32);
		elements.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		elements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ComponentUtils.deriveFont(elements, 13);

		JTableHeader header = elements.getTableHeader();
		header.setBackground(Theme.current().getInterfaceAccentColor());
		header.setForeground(Theme.current().getBackgroundColor());

		header.getColumnModel().getColumn(0).setWidth(240);
		header.getColumnModel().getColumn(1).setWidth(240);
		header.getColumnModel().getColumn(2).setWidth(380);
		header.setResizingColumn(header.getColumnModel().getColumn(3));

		JScrollPane sp = new JScrollPane(elements);
		sp.setBackground(Theme.current().getBackgroundColor());
		sp.getViewport().setOpaque(false);

		sp.setColumnHeaderView(null);

		JPanel holder = new JPanel(new BorderLayout());
		holder.setOpaque(false);
		holder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		holder.add(sp);

		add("Center", holder);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		bar.add(createToolBarButton("workspace.tags.add_new", UIRES.get("16px.add"), e -> {
			TagElement tag = NewTagDialog.showNewTagDialog(workspacePanel.getMCreator());
			if (tag != null) {
				workspacePanel.getMCreator().getWorkspace().addTagElement(tag);
				reloadElements();
			}
		}));

		bar.add(createToolBarButton("workspace.tags.add_common", UIRES.get("16px.injecttags"), e -> {
			AddCommonTagsDialog.open(workspacePanel.getMCreator());
			reloadElements();
		}));

		bar.add(createToolBarButton("common.delete_selected", UIRES.get("16px.delete"),
				e -> deleteCurrentlySelected()));

		add("North", bar);

		elements.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteCurrentlySelected();
				}
			}
		});

		elements.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = elements.getSelectedRow();
				int selectedColumn = elements.getSelectedColumn();
				if (selectedRow >= 0 && selectedColumn == 3) {
					elements.editCellAt(selectedRow, 3);
				}
			}
		});
	}

	private TagElement tagElementForRow(int row) {
		return new TagElement((TagType) elements.getValueAt(row, 0),
				elements.getValueAt(row, 1).toString() + ":" + elements.getValueAt(row, 2).toString());
	}

	@Override public boolean isSupportedInWorkspace() {
		return workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("tags")
				!= GeneratorStats.CoverageStatus.NONE;
	}

	private void deleteCurrentlySelected() {
		if (elements.getSelectedRow() == -1)
			return;

		TagElement tagElement = tagElementForRow(elements.getSelectedRow());

		if (workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream()
				.anyMatch(TagElement::isEntryManaged)) {
			JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.tags.remove_tags_managed_error"), L10N.t("common.warning"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
				L10N.t("workspace.tags.remove_tags_confirmation"), L10N.t("common.confirmation"),
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (n == JOptionPane.YES_OPTION) {
			workspacePanel.getMCreator().getWorkspace().removeTagElement(tagElement);
			reloadElements();
		}
	}

	private void clearEditor() {
		if (elements.isEditing())
			elements.getCellEditor().stopCellEditing();
		lastEditor = null;
	}

	@Override public void reloadElements() {
		int row = elements.getSelectedRow();

		// Clear lastEditor when reloading elements, so we don't keep potentially outdated entries list in it
		clearEditor();

		DefaultTableModel model = (DefaultTableModel) elements.getModel();
		model.setRowCount(0);

		for (Map.Entry<TagElement, ArrayList<String>> tag : workspacePanel.getMCreator().getWorkspace().getTagElements()
				.entrySet()) {
			model.addRow(
					new Object[] { tag.getKey().type(), tag.getKey().getMCreatorNamespace(), tag.getKey().getName(),
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

	private class ItemListFieldCellEditor extends AbstractCellEditor implements TableCellEditor {

		private final JItemListField<? extends MappableElement> listField;

		private final TagElement tagElement;

		private Timer timer;

		public ItemListFieldCellEditor(TagElement tagElement) {
			this.tagElement = tagElement;

			this.listField = itemListFieldForRow(workspacePanel.getMCreator());
			if (this.listField != null) {
				this.listField.disableItemCentering();
				this.listField.setWarnOnRemoveAll(true);
				this.listField.setEnabled(false);
				this.listField.setOpaque(false);
				this.listField.setBorder(UIManager.getBorder("Table.focusSelectedCellHighlightBorder"));
				// Slight delay before enabling so initial click on the row doesn't trigger button actions
				timer = new Timer(250, e -> listField.setEnabled(true));
				timer.start();
			}
		}

		public void cancelTimer() {
			if (timer != null)
				timer.stop();
		}

		public TagElement getTagElement() {
			return tagElement;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return listField;
		}

		@Override public Object getCellEditorValue() {
			return listField.getListElements().stream().map(TagElement::entryFromMappableElement)
					.collect(Collectors.toCollection(ArrayList::new));
		}

		@SuppressWarnings("unchecked") @Override public boolean stopCellEditing() {
			ArrayList<String> newValue = (ArrayList<String>) getCellEditorValue();
			if (newValue != null) {
				workspacePanel.getMCreator().getWorkspace().getTagElements().put(tagElement, newValue);
				workspacePanel.getMCreator().getWorkspace().markDirty();
			}

			return super.stopCellEditing();
		}

		private JItemListField<? extends MappableElement> itemListFieldForRow(MCreator mcreator) {
			switch (tagElement.type()) {
			case ITEMS, BLOCKS -> {
				MCItemListField retval = new MCItemListField(mcreator,
						tagElement.type() == TagType.ITEMS ? ElementUtil::loadBlocksAndItems : ElementUtil::loadBlocks,
						false, true);
				retval.setListElements(mcreator.getWorkspace().getTagElements().get(tagElement).stream()
						.map(e -> (MItemBlock) TagElement.entryToMappableElement(mcreator.getWorkspace(),
								tagElement.type(), e)).toList());
				return retval;
			}
			case ENTITIES -> {
				SpawnableEntityListField retval = new SpawnableEntityListField(mcreator, true);
				retval.setListElements(mcreator.getWorkspace().getTagElements().get(tagElement).stream()
						.map(e -> (EntityEntry) TagElement.entryToMappableElement(mcreator.getWorkspace(),
								tagElement.type(), e)).toList());
				return retval;
			}
			case BIOMES -> {
				BiomeListField retval = new BiomeListField(mcreator, true);
				retval.setListElements(mcreator.getWorkspace().getTagElements().get(tagElement).stream()
						.map(e -> (BiomeEntry) TagElement.entryToMappableElement(mcreator.getWorkspace(),
								tagElement.type(), e)).toList());
				return retval;
			}
			case FUNCTIONS -> {
				ModElementListField retval = new ModElementListField(mcreator, ModElementType.FUNCTION);
				retval.setListElements(mcreator.getWorkspace().getTagElements().get(tagElement).stream()
						.map(e -> (NonMappableElement) TagElement.entryToMappableElement(mcreator.getWorkspace(),
								tagElement.type(), e)).toList());
				return retval;
			}
			case DAMAGE_TYPES -> {
				DamageTypeListField retval = new DamageTypeListField(mcreator, true);
				retval.setListElements(mcreator.getWorkspace().getTagElements().get(tagElement).stream()
						.map(e -> (DamageTypeEntry) TagElement.entryToMappableElement(mcreator.getWorkspace(),
								tagElement.type(), e)).toList());
				return retval;
			}
			}

			return null;
		}

	}

}
