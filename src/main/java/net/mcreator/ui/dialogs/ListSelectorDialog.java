/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Function;

/**
 * This abstract class provides a default look for searchable list selectors, similar to that of JOptionPane, but
 * featuring a search bar.
 *
 * @param <T> The type of elements contained in the list
 */
public abstract class ListSelectorDialog<T> extends SearchableSelectorDialog<T> {
	final JList<T> list = new JList<>(model);
	final JLabel message = new JLabel("");

	public ListSelectorDialog(MCreator mcreator, Function<Workspace, List<T>> entryProvider) {
		super(mcreator, entryProvider);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					setVisible(false);
					dispose();
				}
			}
		});

		JButton selectButton = L10N.button("dialog.item_selector.use_selected");
		selectButton.addActionListener(e -> {
			setVisible(false);
			dispose();
		});

		message.setBorder(BorderFactory.createEmptyBorder(7, 2, 2, 0));

		var top = PanelUtils.northAndCenterElement(message,
				PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.list_selector.filter"), filterField), 5, 5);

		top.setBorder(BorderFactory.createEmptyBorder(4, 6, 7, 0));

		JPanel mainComponent = new JPanel(new BorderLayout());
		mainComponent.add("North", top);
		var scrollPane = new JScrollPane(list);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
		mainComponent.add("Center", scrollPane);

		add("Center", mainComponent);
		add("South", PanelUtils.centerInPanel(selectButton));

		setSize(360, 360);

		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		setLocationRelativeTo(mcreator);

		// Don't add any selected value if the "Close window" button is pressed
		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				list.clearSelection();
				dispose();
			}
		});
	}

	public void setMessage(String message) {
		this.message.setText(message);
	}
}