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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MCItemListField extends JItemListField<MItemBlock> {

	private final MCItem.ListProvider supplier;

	public MCItemListField(MCreator mcreator, MCItem.ListProvider supplier) {
		super(mcreator);
		this.supplier = supplier;

		elementsList.setCellRenderer(new CustomListCellRenderer());
	}

	@Override public List<MItemBlock> getElementsToAdd() {
		return MCItemSelectorDialog.openMultiSelectorDialog(mcreator, supplier).stream()
				.map(e -> new MItemBlock(mcreator.getWorkspace(), e.getName())).collect(Collectors.toList());
	}

	class CustomListCellRenderer extends JLabel implements ListCellRenderer<MItemBlock> {

		@Override
		public Component getListCellRendererComponent(JList<? extends MItemBlock> list, MItemBlock value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);

			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") :
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 2, 0, 2, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")),
					BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);

			setToolTipText(
					value.getUnmappedValue().replace("CUSTOM:", "").replace("Blocks.", "").replace("Items.", ""));

			setIcon(new ImageIcon(ImageUtils.resize(
					MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), value.getUnmappedValue()).getImage(), 22)));

			return this;
		}
	}

}
