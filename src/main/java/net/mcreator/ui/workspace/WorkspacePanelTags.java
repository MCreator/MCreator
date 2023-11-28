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
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.NewTagDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.*;
import net.mcreator.workspace.elements.TagElement;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
				if (column == 3) {
					ItemListFieldCellEditor retval = cellEditorForRow(row);
					if (retval != null) {
						return retval;
					}
				}
				return super.getCellEditor(row, column);
			}

			@Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				if (column == 3) {
					JItemListField<?> retval = itemListFieldForRow(row);
					if (retval != null) {
						retval.setEnabled(false);
						return retval;
					}
				}

				Component retval = super.prepareRenderer(renderer, row, column);
				if (column == 0) {
					TagType tagType = (TagType) elements.getValueAt(row, 0);
					retval.setForeground(tagType.getColor().brighter());
				} else {
					retval.setForeground(Theme.current().getForegroundColor());
				}
				return retval;
			}
		};

		sorter = new TableRowSorter<>(elements.getModel());
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

		elements.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = elements.getSelectedRow();
				if (selectedRow >= 0) {
					elements.editCellAt(selectedRow, 3);
				}
			}
		});

		elements.getModel().addTableModelListener(e -> {
			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() != TableModelEvent.ALL_COLUMNS) {
				int row = e.getFirstRow();
				//noinspection unchecked
				List<String> newValue = (List<String>) elements.getModel().getValueAt(row, e.getColumn());
				if (newValue != null) {
					workspacePanel.getMCreator().getWorkspace().getTagElements().put(tagElementForRow(row), newValue);
					workspacePanel.getMCreator().getWorkspace().markDirty();
				}
			}
		});
	}

	private TagElement tagElementForRow(int row) {
		return new TagElement((TagType) elements.getValueAt(row, 0),
				elements.getValueAt(row, 1).toString() + ":" + elements.getValueAt(row, 2).toString());
	}

	private JItemListField<?> itemListFieldForRow(int row) {
		TagElement tagElement = tagElementForRow(row);

		if (tagElement.type() == TagType.ITEMS) {
			MCItemListField retval = new MCItemListField(workspacePanel.getMCreator(), ElementUtil::loadBlocksAndItems,
					false, true);
			retval.disableItemCentering();
			retval.setListElements(
					workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream().map(e -> {
						MItemBlock itemBlock = new MItemBlock(workspacePanel.getMCreator().getWorkspace(),
								TagElement.getEntryName(e));
						itemBlock.setManaged(TagElement.isEntryManaged(e));
						return itemBlock;
					}).toList());
			return retval;
		} else if (tagElement.type() == TagType.BLOCKS) {
			MCItemListField retval = new MCItemListField(workspacePanel.getMCreator(), ElementUtil::loadBlocks, false,
					true);
			retval.disableItemCentering();
			retval.setListElements(
					workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream().map(e -> {
						MItemBlock itemBlock = new MItemBlock(workspacePanel.getMCreator().getWorkspace(),
								TagElement.getEntryName(e));
						itemBlock.setManaged(TagElement.isEntryManaged(e));
						return itemBlock;
					}).toList());
			return retval;
		} else if (tagElement.type() == TagType.ENTITIES) {
			SpawnableEntityListField retval = new SpawnableEntityListField(workspacePanel.getMCreator(), true);
			retval.disableItemCentering();
			retval.setListElements(
					workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream().map(e -> {
						EntityEntry entityEntry = new EntityEntry(workspacePanel.getMCreator().getWorkspace(), e);
						entityEntry.setManaged(TagElement.isEntryManaged(e));
						return entityEntry;
					}).toList());
			return retval;
		} else if (tagElement.type() == TagType.BIOMES) {
			BiomeListField retval = new BiomeListField(workspacePanel.getMCreator(), true);
			retval.disableItemCentering();
			retval.setListElements(
					workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream().map(e -> {
						BiomeEntry biomeEntry = new BiomeEntry(workspacePanel.getMCreator().getWorkspace(), e);
						biomeEntry.setManaged(TagElement.isEntryManaged(e));
						return biomeEntry;
					}).toList());
			return retval;
		} else if (tagElement.type() == TagType.FUNCTIONS) {
			ModElementListField retval = new ModElementListField(workspacePanel.getMCreator(), ModElementType.FUNCTION);
			retval.disableItemCentering();
			retval.setListElements(
					workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream().map(e -> {
						NonMappableElement nonMappableElement = new NonMappableElement(e);
						nonMappableElement.setManaged(TagElement.isEntryManaged(e));
						return nonMappableElement;
					}).toList());
			return retval;
		} else if (tagElement.type() == TagType.DAMAGE_TYPES) {
			DamageTypeListField retval = new DamageTypeListField(workspacePanel.getMCreator(), true);
			retval.disableItemCentering();
			retval.setListElements(
					workspacePanel.getMCreator().getWorkspace().getTagElements().get(tagElement).stream().map(e -> {
						DamageTypeEntry damageTypeEntry = new DamageTypeEntry(
								workspacePanel.getMCreator().getWorkspace(), e);
						damageTypeEntry.setManaged(TagElement.isEntryManaged(e));
						return damageTypeEntry;
					}).toList());
			return retval;
		}

		return null;
	}

	private ItemListFieldCellEditor cellEditorForRow(int row) {
		JItemListField<?> itemList = itemListFieldForRow(row);
		TagType tagType = (TagType) elements.getValueAt(row, 0);
		if (itemList != null) {
			return new ItemListFieldCellEditor(itemList) {
				@Override public Object getCellEditorValue() {
					//@formatter:off
					if (tagType == TagType.ITEMS) {
						return ((MCItemListField) itemList).getListElements().stream().map(TagElement::entryFromMappableElement).toList();
					} else if (tagType == TagType.BLOCKS) {
						return ((MCItemListField) itemList).getListElements().stream().map(TagElement::entryFromMappableElement).toList();
					} else if (tagType == TagType.ENTITIES) {
						return ((SpawnableEntityListField) itemList).getListElements().stream().map(TagElement::entryFromMappableElement).toList();
					} else if (tagType == TagType.BIOMES) {
						return ((BiomeListField) itemList).getListElements().stream().map(TagElement::entryFromMappableElement).toList();
					} else if (tagType == TagType.FUNCTIONS) {
						return ((ModElementListField) itemList).getListElements().stream().map(TagElement::entryFromMappableElement).toList();
					} else if (tagType == TagType.DAMAGE_TYPES) {
						return ((DamageTypeListField) itemList).getListElements().stream().map(TagElement::entryFromMappableElement).toList();
					}
					//@formatter:on
					return null;
				}
			};
		}
		return null;
	}

	// TODO: implement
	/*@Override public boolean isSupportedInWorkspace() {
		return workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("tags")
				!= GeneratorStats.CoverageStatus.NONE;
	}*/

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

	private static abstract class ItemListFieldCellEditor extends AbstractCellEditor implements TableCellEditor {

		private final JItemListField<?> listField;

		public ItemListFieldCellEditor(JItemListField<?> listField) {
			this.listField = listField;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return listField;
		}

	}

}
