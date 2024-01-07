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

package net.mcreator.ui.dialogs;

import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class MCItemSelectorDialog extends SearchableSelectorDialog<MCItem> {

	private final JList<MCItem> list = new JList<>(model);
	private final JTextField jtf = new JTextField(16);

	private ActionListener itemSelectedListener;

	public MCItemSelectorDialog(MCreator mcreator, MCItem.ListProvider supplier, boolean supportTags) {
		this(mcreator, supplier, supportTags, false);
	}

	public MCItemSelectorDialog(MCreator mcreator, MCItem.ListProvider supplier, boolean supportTags,
			boolean hasPotions) {
		super(mcreator, supplier::provide);

		setTitle(L10N.t("dialog.item_selector.title"));
		list.setCellRenderer(new Render());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jtf.setEnabled(false);
		jtf.setBorder(BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor()));

		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					setVisible(false);
					dispose();
					if (itemSelectedListener != null)
						itemSelectedListener.actionPerformed(new ActionEvent(this, 0, ""));
				}
			}
		});

		JPanel buttons = new JPanel();
		JButton cancelButton = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		JButton useSelectedButton = L10N.button("dialog.item_selector.use_selected");
		useSelectedButton.addActionListener(e -> {
			setVisible(false);
			dispose();
			if (itemSelectedListener != null)
				itemSelectedListener.actionPerformed(new ActionEvent(this, 0, ""));
		});

		if (supportTags) {
			JButton useTags = L10N.button("dialog.item_selector.use_tag");
			buttons.add(useTags);
			useTags.addActionListener(e -> {
				TagType tagType = TagType.BLOCKS;
				List<MCItem> items = supplier.provide(mcreator.getWorkspace());
				for (MCItem item : items) {
					if (item.getType().equals("item")) {
						tagType = TagType.ITEMS;
						break;
					}
				}

				String tag = AddTagDialog.openAddTagDialog(this, mcreator, tagType, "tag", "category/tag");
				if (tag != null) {
					MCItem mcItem = new MCItem.Tag(mcreator.getWorkspace(), tag);
					model.addElement(mcItem);
					list.setSelectedValue(mcItem, true);
					setVisible(false);
					dispose();
					if (itemSelectedListener != null)
						itemSelectedListener.actionPerformed(new ActionEvent(this, 0, ""));
				}
			});
		}

		buttons.add(useSelectedButton);

		add("South", PanelUtils.westAndEastElement(PanelUtils.centerInPanel(cancelButton), buttons));

		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(0);

		list.addListSelectionListener(event -> {
			MCItem bl = list.getSelectedValue();
			if (bl != null)
				jtf.setText(bl.getReadableName());
		});

		cancelButton.addActionListener(event -> {
			list.clearSelection();
			setVisible(false);
		});

		ComponentUtils.deriveFont(jtf, 15);

		JComponent top;
		JButton all = L10N.button("dialog.item_selector.all");
		all.addActionListener(event -> filterField.setText(""));
		JButton blocks = L10N.button("dialog.item_selector.blocks");
		blocks.addActionListener(event -> filterField.setText("block"));
		JButton items = L10N.button("dialog.item_selector.items");
		items.addActionListener(event -> filterField.setText("item"));
		JButton mods = L10N.button("dialog.item_selector.custom_elements");
		mods.addActionListener(event -> filterField.setText("custom"));

		if (hasPotions) {
			JButton potions = L10N.button("dialog.item_selector.potions");
			potions.addActionListener(event -> filterField.setText("potion:"));
			top = PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.item_selector.name"), jtf,
					L10N.label("dialog.item_selector.display_filter"), filterField, new JLabel(""), all, blocks, items,
					potions, mods);
		} else {
			top = PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.item_selector.name"), jtf,
					L10N.label("dialog.item_selector.display_filter"), filterField, new JLabel(""), all, blocks, items,
					mods);
		}

		top.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

		JPanel mainComponent = new JPanel(new BorderLayout());
		mainComponent.add("North", top);

		mainComponent.add("Center", new JScrollPane(list));

		add("Center", mainComponent);

		setSize(hasPotions ? 970 : 900, 425);

		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		setLocationRelativeTo(mcreator);

		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				list.clearSelection();
				dispose();
			}
		});
	}

	public void setItemSelectedListener(ActionListener itemSelectedListener) {
		this.itemSelectedListener = itemSelectedListener;
	}

	static class Render extends JLabel implements ListCellRenderer<MCItem> {

		@Override
		public Component getListCellRendererComponent(JList<? extends MCItem> list, MCItem value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setToolTipText("<html>" + value.getReadableName() + (value.getDescription().isEmpty() ?
					"" :
					("<br><small>" + value.getDescription())));
			if (value.icon.getIconWidth() != 32)
				setIcon(new ImageIcon(ImageUtils.resize(value.icon.getImage(), 32)));
			else
				setIcon(value.icon);

			if (isSelected) {
				setOpaque(true);
				setBackground(Theme.current().getForegroundColor());
			} else {
				setOpaque(false);
			}

			return this;
		}

	}

	@Override Predicate<MCItem> getFilter(String term) {
		String lowercaseTerm = term.toLowerCase(Locale.ENGLISH);
		return item -> item.getName().toLowerCase(Locale.ENGLISH).contains(lowercaseTerm) || item.getReadableName()
				.toLowerCase(Locale.ENGLISH).contains(lowercaseTerm) || item.getType().toLowerCase(Locale.ENGLISH)
				.contains(lowercaseTerm);
	}

	public MCItem getSelectedMCItem() {
		return list.getSelectedValue();
	}

	public static MCItem openSelectorDialog(MCreator parent, MCItem.ListProvider blocks) {
		MCItemSelectorDialog bsd = new MCItemSelectorDialog(parent, blocks, false);
		bsd.setVisible(true);
		return bsd.list.getSelectedValue();
	}

	public static List<MCItem> openMultiSelectorDialog(MCreator parent, MCItem.ListProvider blocks) {
		return openMultiSelectorDialog(parent, blocks, false);
	}

	public static List<MCItem> openMultiSelectorDialog(MCreator parent, MCItem.ListProvider blocks,
			boolean supportTags) {
		MCItemSelectorDialog bsd = new MCItemSelectorDialog(parent, blocks, supportTags);
		bsd.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		bsd.setVisible(true);
		return bsd.list.getSelectedValuesList();
	}

}
