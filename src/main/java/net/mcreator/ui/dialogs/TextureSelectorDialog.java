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

import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class TextureSelectorDialog extends MCreatorDialog {

	public JButton naprej = new JButton(UIManager.getString("OptionPane.okButtonText"));

	private final FilterModel model = new FilterModel();

	public JList<ResourcePointer> list = new JList<>(model);

	private final JTextField filterField = new JTextField(20);

	public TextureSelectorDialog(Iterable<ResourcePointer> block, JFrame f) {
		super(f, L10N.t("dialog.textures_selector.title_window"), true);
		setIconImage(UIRES.getBuiltIn("icon").getImage());
		list.setCellRenderer(new Render());

		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		block.forEach(model::addElement);

		JPanel buttons = new JPanel();
		JButton naprej2 = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		buttons.add(naprej2);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.addListSelectionListener(event -> naprej.doClick());
		naprej2.addActionListener(event -> setVisible(false));

		ComponentUtils.deriveFont(filterField, 12);
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

		JPanel pno2 = new JPanel();

		pno2.add(L10N.label("dialog.textures_selector.search"));
		pno2.add(filterField);

		JLabel jtf = L10N.label("dialog.textures_selector.select_texture");
		ComponentUtils.deriveFont(jtf, 17);
		jtf.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		add("South", buttons);
		add("Center", new JScrollPane(list));
		add("North", PanelUtils.westAndEastElement(jtf, PanelUtils.totalCenterInPanel(pno2)));

		setSize(740, 370);
		setLocationRelativeTo(f);
	}

	static class Render extends JLabel implements ListCellRenderer<ResourcePointer> {
		@Override
		public Component getListCellRendererComponent(JList<? extends ResourcePointer> list, ResourcePointer ma,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(Color.blue);
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			ImageIcon icon = ImageMakerTexturesCache.CACHE.get(ma);
			if (icon != null)
				setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
			else if (!ma.isInClasspath())
				setIcon(new ImageIcon(
						ImageUtils.resize(new ImageIcon(((File) ma.identifier).getAbsolutePath()).getImage(), 32)));

			setToolTipText(ma.toString());

			setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

			return this;
		}
	}

	private class FilterModel extends DefaultListModel<ResourcePointer> {
		ArrayList<ResourcePointer> items;
		ArrayList<ResourcePointer> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public ResourcePointer getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(ResourcePointer o) {
			items.add(o);
			refilter();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof ResourcePointer) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		private void refilter() {
			filterItems.clear();
			String term = filterField.getText();
			filterItems.addAll(items.stream().filter(item -> item.toString().toLowerCase(Locale.ENGLISH)
					.contains(term.toLowerCase(Locale.ENGLISH))).toList());
			fireContentsChanged(this, 0, getSize());
		}
	}
}
