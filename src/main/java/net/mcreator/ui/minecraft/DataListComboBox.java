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

package net.mcreator.ui.minecraft;

import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.Objects;

public class DataListComboBox extends SearchableComboBox<DataListEntry> {

	public DataListComboBox(MCreator mcreator, Collection<DataListEntry> list) {
		this(mcreator, list, false);
	}

	public DataListComboBox(MCreator mcreator, Collection<DataListEntry> list, boolean rawNames) {
		super(list.toArray(new DataListEntry[0]));
		init(mcreator, rawNames);
	}

	public DataListComboBox(MCreator mcreator) {
		init(mcreator, false);
	}

	// Set when the entry to select was not found, so the fallback selection can be marked until a different entry is selected
	@Nullable private String brokenReference = null;
	@Nullable private DataListEntry brokenReferenceFallback = null;

	private void init(MCreator mcreator, boolean rawNames) {
		setRenderer(new CustomRenderer(mcreator, rawNames));
		addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED && brokenReference != null && !Objects.equals(e.getItem(),
					brokenReferenceFallback))
				clearBrokenReference();
		});
	}

	public void setSelectedItem(String string) {
		this.setSelectedItem(new DataListEntry.Dummy(string));
	}

	public void setSelectedItem(MappableElement mappableElement) {
		if (mappableElement == null)
			setSelectedIndex(getItemCount() > 0 ? 0 : -1);
		else
			this.setSelectedItem(new DataListEntry.Dummy(mappableElement.getUnmappedValue()));
	}

	public void setSelectedItem(DataListEntry dataListEntry) {
		super.setSelectedItem(dataListEntry);

		DataListEntry selected = super.getSelectedItem();
		if (dataListEntry != null && !dataListEntry.getName().isEmpty() && !dataListEntry.equals(selected)) {
			brokenReference = dataListEntry.getName();
			brokenReferenceFallback = selected;
			setToolTipText(L10N.t("datalist_combobox.broken_reference",
					brokenReference.replace(NameMapper.MCREATOR_PREFIX, ""),
					selected != null ? selected.getReadableName() : ""));
			repaint();
		} else {
			clearBrokenReference();
		}
	}

	private void clearBrokenReference() {
		if (brokenReference != null) {
			brokenReference = null;
			brokenReferenceFallback = null;
			setToolTipText(null);
			repaint();
		}
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		if (brokenReference != null && Objects.equals(super.getSelectedItem(), brokenReferenceFallback)) {
			g.setColor(ValidationResult.Type.ERROR.getColor());
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			ValidationResult.Type.ERROR.icon(11).paintIcon(this, g, getWidth() - 11, getHeight() - 11);
		}
	}

	@Override @Nonnull public DataListEntry getSelectedItem() {
		Object superretval = super.getSelectedItem();
		if (superretval == null)
			return new DataListEntry.Null();

		return super.getSelectedItem();
	}

	public static class CustomRenderer extends JLabel implements ListCellRenderer<DataListEntry> {

		private final MCreator mcreator;
		private final boolean rawNames;

		public CustomRenderer(MCreator mcreator, boolean rawNames) {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);

			this.mcreator = mcreator;
			this.rawNames = rawNames;
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends DataListEntry> list, DataListEntry value,
				int index, boolean isSelected, boolean cellHasFocus) {

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setText(rawNames ? value.getName() : value.getReadableName());

			if (value instanceof DataListEntry.Custom custom) {
				setIcon(MCItem.getBlockIconBasedOnName(custom.getModElement().getWorkspace(), value.getName()));
			} else if (value.getTexture() == null) {
				setIcon(new EmptyIcon(32, 32));
			} else {
				setIcon(BlockItemIcons.getIconForItem(value.getTexture()));
			}

			if (!value.isSupportedInWorkspace(mcreator.getWorkspace())) {
				Icon imageIcon = getIcon();
				if (imageIcon instanceof ImageIcon)
					setIcon(ImageUtils.changeSaturation((ImageIcon) imageIcon, 0.1f));
				setText(L10N.t("datalist_combobox.not_supported", getText()));
				setForeground(Theme.current().getAltForegroundColor());
			}

			setHorizontalTextPosition(SwingConstants.RIGHT);
			setHorizontalAlignment(SwingConstants.LEFT);

			return this;
		}

	}

}
