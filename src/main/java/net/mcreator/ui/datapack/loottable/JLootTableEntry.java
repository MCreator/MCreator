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

package net.mcreator.ui.datapack.loottable;

import net.mcreator.element.types.LootTable;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JLootTableEntry extends JPanel {

	private final MCItemHolder item;
	private final JSpinner weight = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));

	private final JSpinner mincount = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));
	private final JSpinner maxcount = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));

	private final JSpinner minEnchantmentsLevel = new JSpinner(new SpinnerNumberModel(0, 0, 64000, 1));
	private final JSpinner maxEnchantmentsLevel = new JSpinner(new SpinnerNumberModel(0, 0, 64000, 1));

	private final JCheckBox affectedByFortune = new JCheckBox("Affected by fortune");
	private final JCheckBox explosionDecay = new JCheckBox("Enable explosion decay");

	private final JComboBox<String> silkTouchMode = new JComboBox<>(
			new String[] { "Ignore silk touch", "Only with silk touch", "Only without silk touch" });

	public JLootTableEntry(MCreator mcreator, JPanel parent, List<JLootTableEntry> entryList) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		item = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line1.setOpaque(false);

		line1.add(new JLabel("Entry item: "));
		line1.add(item);
		line1.add(new JLabel("Entry weight: "));
		line1.add(weight);

		line1.add(new JEmptyBox(15, 5));

		line1.add(new JLabel("Min count: "));
		line1.add(mincount);
		line1.add(new JLabel("Max count: "));
		line1.add(maxcount);

		line1.add(new JEmptyBox(15, 5));

		line1.add(affectedByFortune);
		line1.add(explosionDecay);

		line1.add(new JEmptyBox(15, 5));

		line1.add(new JLabel("Silk touch mode: "));
		line1.add(silkTouchMode);

		affectedByFortune.setOpaque(false);
		explosionDecay.setOpaque(false);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText("Remove this entry");
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});

		JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line2.setOpaque(false);

		line2.add(new JLabel("Entry item enchantments level min: "));
		line2.add(minEnchantmentsLevel);
		line2.add(new JLabel("max: "));
		line2.add(maxEnchantmentsLevel);

		add(PanelUtils.centerAndEastElement(line1, PanelUtils.join(remove)));
		add(line2);

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
	}

	public LootTable.Pool.Entry getEntry() {
		if (!item.containsItem())
			return null;

		LootTable.Pool.Entry entry = new LootTable.Pool.Entry();
		entry.type = "item";
		entry.item = item.getBlock();

		entry.weight = (int) weight.getValue();

		entry.minCount = (int) mincount.getValue();
		entry.maxCount = (int) maxcount.getValue();

		entry.minEnchantmentLevel = (int) minEnchantmentsLevel.getValue();
		entry.maxEnchantmentLevel = (int) maxEnchantmentsLevel.getValue();

		entry.affectedByFortune = affectedByFortune.isSelected();
		entry.explosionDecay = explosionDecay.isSelected();

		entry.silkTouchMode = silkTouchMode.getSelectedIndex();

		return entry;
	}

	public void setEntry(LootTable.Pool.Entry e) {
		item.setBlock(e.item);
		weight.setValue(e.weight);

		mincount.setValue(e.minCount);
		maxcount.setValue(e.maxCount);

		minEnchantmentsLevel.setValue(e.minEnchantmentLevel);
		maxEnchantmentsLevel.setValue(e.maxEnchantmentLevel);

		affectedByFortune.setSelected(e.affectedByFortune);
		explosionDecay.setSelected(e.explosionDecay);

		silkTouchMode.setSelectedIndex(e.silkTouchMode);
	}
}
