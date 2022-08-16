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
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataListComboBox extends JComboBox<DataListEntry> {

	public DataListComboBox(MCreator mcreator, List<DataListEntry> list) {
		super(list.toArray(new DataListEntry[0]));
		init(mcreator);
	}

	public DataListComboBox(MCreator mcreator) {
		init(mcreator);
	}

	private void init(MCreator mcreator) {
		setRenderer(new CustomRenderer(mcreator));
	}

	public void setSelectedItem(String string) {
		this.setSelectedItem(new DataListEntry.Dummy(string));
	}

	public void setSelectedItem(MappableElement mappableElement) {
		if (mappableElement == null)
			setSelectedIndex(0);
		else
			this.setSelectedItem(new DataListEntry.Dummy(mappableElement.getUnmappedValue()));
	}

	public void setSelectedItem(DataListEntry dataListEntry) {
		super.setSelectedItem(dataListEntry);
	}

	@Override @Nonnull public DataListEntry getSelectedItem() {
		Object superretval = super.getSelectedItem();
		if (superretval == null)
			return new DataListEntry.Null();

		return (DataListEntry) super.getSelectedItem();
	}

	public static class CustomRenderer extends JLabel implements ListCellRenderer<DataListEntry> {

		private final MCreator mcreator;

		public CustomRenderer(MCreator mcreator) {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);

			this.mcreator = mcreator;
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

			HashMap<String,String> tra = new java.util.HashMap<>(
					Map.of("FARMER", "农夫", "FLETCHER", "制箭师", "CLERIC", "牧羊人", "ARMORER", "盔甲匠", "TOOLSMITH", "工具匠",
							"BUTCHER", "屠夫", "CARTOGRAPHER", "制图师", "LIBRARIAN", "图书管理员", "MASON", "石匠", "SHEPHERD",
							"牧羊人"));
			tra.put("LEATHERWORKER","皮匠");
			tra.put("FISHERMAN","渔夫");
			tra.put("WATER","流水");
			tra.put("LAVA","岩浆");
			tra.put("MISC","杂项");
			tra.put("BUILDING_BLOCKS","建筑方块");
			tra.put("DECORATIONS","装饰性方块");
			tra.put("REDSTONE","红石");
			tra.put("TRANSPORTATION","交通运输");
			tra.put("FOOD","食物");
			tra.put("TOOLS","工具");
			tra.put("COMBAT","战斗用品");
			tra.put("BREWING","酿造");
			tra.put("MATERIALS","材料");


			setText(tra.getOrDefault(value.getReadableName(),value.getReadableName()));

			if (value instanceof DataListEntry.Custom) {
				setIcon(MCItem.getBlockIconBasedOnName(((DataListEntry.Custom) value).getModElement().getWorkspace(),
						value.getName()));
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
				setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			}

			setHorizontalTextPosition(SwingConstants.RIGHT);
			setHorizontalAlignment(SwingConstants.LEFT);

			return this;
		}

	}

}
