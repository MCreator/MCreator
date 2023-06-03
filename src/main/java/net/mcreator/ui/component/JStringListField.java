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

package net.mcreator.ui.component;

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.ListEditorDialog;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This class represents a component that stores list of strings and allows to edit them using {@link ListEditorDialog}.
 */
public class JStringListField extends JPanel {

	private final DefaultListModel<String> entriesModel = new DefaultListModel<>();

	private final TechnicalButton edit = new TechnicalButton(UIRES.get("18px.edit"));
	private final TechnicalButton clear = new TechnicalButton(UIRES.get("18px.removeall"));

	private boolean uniqueEntries = false;

	/**
	 * Sole constructor.
	 *
	 * @param parent    The parent window that the editor dialog would be shown over.
	 * @param validator Function that returns a validator for each list entry when editing them in the dialog,
	 *                  {@code null} means no validation.
	 */
	public JStringListField(Window parent, @Nullable Function<VTextField, Validator> validator) {
		super(new BorderLayout());

		JList<String> entriesList = new JList<>(entriesModel);
		entriesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		entriesList.setVisibleRowCount(1);
		entriesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		entriesList.setCellRenderer(new CustomListCellRenderer());

		JScrollPane pane = new JScrollPane(PanelUtils.totalCenterInPanel(entriesList));
		pane.setPreferredSize(getPreferredSize());
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane.setWheelScrollingEnabled(false);
		pane.addMouseWheelListener(new MouseAdapter() {
			@Override public void mouseWheelMoved(MouseWheelEvent evt) {
				int value = pane.getHorizontalScrollBar().getValue();
				if (evt.getWheelRotation() == 1) {
					value += pane.getHorizontalScrollBar().getBlockIncrement() * evt.getScrollAmount();
					if (value > pane.getHorizontalScrollBar().getMaximum())
						value = pane.getHorizontalScrollBar().getMaximum();
				} else if (evt.getWheelRotation() == -1) {
					value -= pane.getHorizontalScrollBar().getBlockIncrement() * evt.getScrollAmount();
					if (value < 0)
						value = 0;
				}
				pane.getHorizontalScrollBar().setValue(value);
			}
		});

		edit.setOpaque(false);
		edit.setMargin(new Insets(0, 0, 0, 0));
		edit.setBorder(BorderFactory.createEmptyBorder());
		edit.setContentAreaFilled(false);
		edit.addActionListener(e -> {
			List<String> newTextList = ListEditorDialog.open(parent, entriesModel.elements(), validator, uniqueEntries);
			if (newTextList != null)
				setTextList(newTextList);
		});

		clear.setOpaque(false);
		clear.setMargin(new Insets(0, 0, 0, 0));
		clear.setBorder(BorderFactory.createEmptyBorder());
		clear.setContentAreaFilled(false);
		clear.addActionListener(e -> entriesModel.clear());

		JPanel controls = PanelUtils.totalCenterInPanel(PanelUtils.join(edit, clear));
		controls.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		controls.setOpaque(true);
		controls.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		add("Center", pane);
		add("East", controls);
	}

	@Override public void setEnabled(boolean b) {
		super.setEnabled(b);
		edit.setEnabled(b);
		clear.setEnabled(b);
	}

	/**
	 * @param uniqueEntries Whether duplicate string entries in the list are forbidden.
	 * @return This field instance.
	 */
	public JStringListField setUniqueEntries(boolean uniqueEntries) {
		this.uniqueEntries = uniqueEntries;
		return this;
	}

	/**
	 * @return List of string entries stored in this component.
	 */
	public List<String> getTextList() {
		return Collections.list(entriesModel.elements());
	}

	/**
	 * @param newTextList List of string entries to be stored in this component.
	 */
	public void setTextList(Collection<String> newTextList) {
		entriesModel.clear();
		entriesModel.addAll(newTextList);
	}

	private static class CustomListCellRenderer extends JLabel implements ListCellRenderer<String> {

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(true);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") :
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 5, 0, 5, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")),
					BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			setText(value);
			return this;
		}
	}
}
