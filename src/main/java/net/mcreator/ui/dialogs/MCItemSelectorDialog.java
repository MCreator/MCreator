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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.TagsNameValidator;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MCItemSelectorDialog extends MCreatorDialog {

	private final FilterModel model = new FilterModel();
	private final JList<MCItem> list = new JList<>(model);
	private final JTextField jtf = new JTextField(16);
	private final JTextField filterField = new JTextField(14);
	private final JPanel mainComponent;

	private final MCItem.ListProvider blocksConsumer;

	private final MCreator mcreator;

	private ActionListener itemSelectedListener;

	public MCItemSelectorDialog(MCreator mcreator, MCItem.ListProvider blocksConsumer, boolean supportTags) {
		super(mcreator);

		this.mcreator = mcreator;
		this.blocksConsumer = blocksConsumer;

		setTitle(L10N.t("dialog.item_selector.title"));
		setModal(true);
		setIconImage(UIRES.get("icon").getImage());
		list.setCellRenderer(new Render());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		filterField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0) {
				model.refilter();
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				model.refilter();
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				model.refilter();
			}
		});

		jtf.setEnabled(false);
		jtf.setBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					setVisible(false);
					if (itemSelectedListener != null)
						itemSelectedListener.actionPerformed(new ActionEvent(this, 0, ""));
				}
			}
		});

		JPanel buttons = new JPanel();
		JButton naprej2 = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		JButton naprej = L10N.button("dialog.item_selector.use_selected");
		naprej.addActionListener(e -> {
			setVisible(false);
			if (itemSelectedListener != null)
				itemSelectedListener.actionPerformed(new ActionEvent(this, 0, ""));
		});

		if (supportTags) {
			JButton useTags = L10N.button("dialog.item_selector.use_tag");
			buttons.add(useTags);

			VComboBox<String> tagName = new VComboBox<>();

			tagName.setValidator(new TagsNameValidator<>(tagName, true));

			tagName.addItem("");
			tagName.addItem("tag");
			tagName.addItem("category/tag");

			tagName.setEditable(true);
			tagName.setOpaque(false);
			tagName.setForeground(Color.white);
			ComponentUtils.deriveFont(tagName, 16);

			tagName.enableRealtimeValidation();

			useTags.addActionListener(e -> {
				int result = JOptionPane.showConfirmDialog(this,
						PanelUtils.northAndCenterElement(L10N.label("dialog.item_selector.enter_tag_name"), tagName),
						L10N.t("dialog.item_selector.use_tag"), JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					if (tagName.getValidationStatus().getValidationResultType()
							!= Validator.ValidationResultType.ERROR) {
						String selectedItem = (String) tagName.getSelectedItem();
						if (selectedItem != null) {
							MCItem mcItem = new MCItem.Tag(mcreator.getWorkspace(), selectedItem);
							model.addElement(mcItem);
							list.setSelectedValue(mcItem, true);

							setVisible(false);
							if (itemSelectedListener != null)
								itemSelectedListener.actionPerformed(new ActionEvent(this, 0, ""));
						}
					} else {
						JOptionPane.showMessageDialog(this,
								L10N.t("dialog.item_selector.error_invalid_tag_name_message"),
								L10N.t("dialog.item_selector.error_invalid_tag_name_title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}

		buttons.add(naprej);

		add("South", PanelUtils.westAndEastElement(PanelUtils.centerInPanel(naprej2), buttons));

		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(0);

		list.addListSelectionListener(event -> {
			MCItem bl = list.getSelectedValue();
			jtf.setText(bl.getReadableName());
		});

		naprej2.addActionListener(event -> setVisible(false));

		ComponentUtils.deriveFont(jtf, 15);
		ComponentUtils.deriveFont(filterField, 15);

		JButton all = L10N.button("dialog.item_selector.all");
		all.addActionListener(event -> filterField.setText(""));
		JButton blocks = L10N.button("dialog.item_selector.blocks");
		blocks.addActionListener(event -> filterField.setText("block"));
		JButton items = L10N.button("dialog.item_selector.items");
		items.addActionListener(event -> filterField.setText("item"));
		JButton mods = L10N.button("dialog.item_selector.custom_elements");
		mods.addActionListener(event -> filterField.setText("mcreator"));

		JComponent top = PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.item_selector.name"), jtf,
				L10N.label("dialog.item_selector.display_filter"), filterField, new JLabel(""),
				all, blocks, items, mods);

		top.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

		mainComponent = new JPanel(new BorderLayout());
		mainComponent.add("North", top);

		mainComponent.add("Center", new JScrollPane(list));

		add("Center", mainComponent);

		setSize(881, 425);

		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		setLocationRelativeTo(mcreator);
	}

	public void setItemSelectedListener(ActionListener itemSelectedListener) {
		this.itemSelectedListener = itemSelectedListener;
	}

	static class Render extends JLabel implements ListCellRenderer<MCItem> {

		@Override
		public Component getListCellRendererComponent(JList<? extends MCItem> list, MCItem value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setToolTipText("<html>" + value.getReadableName() + "<br><small>" + value.getDescription());
			if (value.icon.getIconWidth() != 32)
				setIcon(new ImageIcon(ImageUtils.resize(value.icon.getImage(), 32)));
			else
				setIcon(value.icon);

			if (isSelected) {
				setOpaque(true);
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			} else {
				setOpaque(false);
			}

			return this;
		}

	}

	private class FilterModel extends DefaultListModel<MCItem> {
		ArrayList<MCItem> items;
		ArrayList<MCItem> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public MCItem getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(MCItem o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof MCItem) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		private void refilter() {
			filterItems.clear();
			String term = filterField.getText();
			filterItems.addAll(items.stream().filter(item ->
					item.getName().toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH)) || item
							.getReadableName().toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH))
							|| item.getDescription().toLowerCase(Locale.ENGLISH)
							.contains(term.toLowerCase(Locale.ENGLISH)) || item.getType().toLowerCase(Locale.ENGLISH)
							.contains(term.toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));
			fireContentsChanged(this, 0, getSize());
		}
	}

	@Override public void setVisible(boolean visible) {
		if (visible) {
			reloadElements();
		}
		super.setVisible(visible);
	}

	private void reloadElements() {
		model.removeAllElements();
		blocksConsumer.provide(mcreator.getWorkspace()).forEach(model::addElement);
	}

	public MCItem getSelectedMCItem() {
		return list.getSelectedValue();
	}

	public static MCItem openSelectorDialog(MCreator parent, MCItem.ListProvider blocks) {
		MCItemSelectorDialog bsd = new MCItemSelectorDialog(parent, blocks, false);
		bsd.reloadElements();
		JComponent mainComponent = bsd.mainComponent;
		mainComponent.setPreferredSize(new Dimension(870, 380));

		JOptionPane pane = new JOptionPane(mainComponent);
		JDialog dialog = pane.createDialog(parent, "Block/item selector");
		bsd.setItemSelectedListener(e -> dialog.dispose());
		dialog.setVisible(true);

		return bsd.list.getSelectedValue();
	}

	public static List<MCItem> openMultiSelectorDialog(MCreator parent, MCItem.ListProvider blocks) {
		MCItemSelectorDialog bsd = new MCItemSelectorDialog(parent, blocks, false);
		bsd.reloadElements();
		JComponent mainComponent = bsd.mainComponent;
		mainComponent.setPreferredSize(new Dimension(870, 380));
		bsd.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JOptionPane pane = new JOptionPane(mainComponent);
		JDialog dialog = pane.createDialog(parent, L10N.t("dialog.item_selector.title"));
		bsd.setItemSelectedListener(e -> dialog.dispose());
		dialog.setVisible(true);

		return bsd.list.getSelectedValuesList();
	}

}
