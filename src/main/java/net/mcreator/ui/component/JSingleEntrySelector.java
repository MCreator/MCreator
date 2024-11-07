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

package net.mcreator.ui.component;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.IconUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JSingleEntrySelector<T> extends JPanel {

	private final JLabel readableText = new JLabel();
	private final TechnicalButton edit = new TechnicalButton(UIRES.get("18px.edit"));
	private final TechnicalButton remove = new TechnicalButton(UIRES.get("18px.remove"));
	private final List<ActionListener> listeners = new ArrayList<>();

	protected final MCreator mcreator;
	protected T currentEntry;

	public JSingleEntrySelector(MCreator mcreator) {
		this.mcreator = mcreator;

		setLayout(new BorderLayout(0, 0));
		setBackground(Theme.current().getBackgroundColor());

		edit.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
		remove.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);

		edit.addActionListener(event -> {
			T newEntry = openEntrySelector();
			if (newEntry != null) {
				setEntry(newEntry);
				updateReadableText();
			}
		});
		remove.addActionListener(e -> setEntry(null));

		readableText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		readableText.setHorizontalAlignment(JTextField.CENTER);
		readableText.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					edit.doClick();
			}
		});
		ComponentUtils.deriveFont(readableText, 14);
		readableText.setPreferredSize(getPreferredSize());

		add("Center", readableText);

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		buttonsPanel.setOpaque(true);
		buttonsPanel.add(edit);
		buttonsPanel.add(remove);
		buttonsPanel.setBackground(Theme.current().getBackgroundColor());
		buttonsPanel.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, getBackground()));

		JComponent buttons = PanelUtils.totalCenterInPanel(buttonsPanel);
		buttons.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.current().getAltBackgroundColor()));
		buttons.setOpaque(true);

		add(readableText, BorderLayout.CENTER);
		add(buttons, BorderLayout.EAST);
	}

	@Override public void setEnabled(boolean enabled) {
		readableText.setEnabled(enabled);
		edit.setEnabled(enabled);
		remove.setEnabled(enabled);
	}

	@Override public String getToolTipText() {
		return readableText.getText();
	}

	public boolean isEmpty() {
		return currentEntry == null;
	}

	public void updateReadableText() {
		readableText.setIcon(null);
		if (currentEntry == null) {
			readableText.setText("");
		} else if (currentEntry instanceof MappableElement mappableElement) {
			Optional<DataListEntry> dataListEntryOpt = mappableElement.getDataListEntry();
			if (dataListEntryOpt.isPresent()) {
				DataListEntry dataListEntry = dataListEntryOpt.get();
				readableText.setText(dataListEntry.getReadableName());
				if (dataListEntry.getTexture() != null) {
					readableText.setIcon(
							IconUtils.resize(BlockItemIcons.getIconForItem(dataListEntry.getTexture()), 18));
				}
			} else {
				String unmappedValue = mappableElement.getUnmappedValue();
				readableText.setText(unmappedValue.replace("CUSTOM:", "").replace("Blocks.", "").replace("Items.", "")
						.replace("#", ""));

				if (unmappedValue.startsWith("CUSTOM:"))
					readableText.setIcon(
							IconUtils.resize(MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), unmappedValue),
									18));
				else if (unmappedValue.startsWith("#"))
					readableText.setIcon(IconUtils.resize(MCItem.TAG_ICON, 18));
			}
		} else {
			readableText.setText(StringUtils.machineToReadableName(currentEntry.toString().replace("CUSTOM:", "")));

			if (currentEntry.toString().contains("CUSTOM:"))
				readableText.setIcon(IconUtils.resize(MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), currentEntry.toString()),
						18));
		}
	}

	protected abstract T openEntrySelector();

	public T getEntry() {
		return currentEntry;
	}

	public void setEntry(T newEntry) {
		currentEntry = newEntry;
		updateReadableText();
		listeners.forEach(l -> l.actionPerformed(new ActionEvent("", 0, "")));
	}

	public void addEntrySelectedListener(ActionListener a) {
		listeners.add(a);
	}
}
