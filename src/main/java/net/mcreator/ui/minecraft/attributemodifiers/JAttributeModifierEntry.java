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

package net.mcreator.ui.minecraft.attributemodifiers;

import net.mcreator.element.parts.AttributeEntry;
import net.mcreator.element.parts.AttributeModifierEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.util.List;

public class JAttributeModifierEntry extends JSimpleListEntry<AttributeModifierEntry> {

	private final Workspace workspace;

	private final DataListComboBox equipmentSlot;
	private final DataListComboBox attribute;
	private final JSpinner amount = new JSpinner(new SpinnerNumberModel(0, -1024, 1024, 0.001));
	private final JComboBox<String> operation = new JComboBox<>(
			new String[] { "ADD_VALUE", "ADD_MULTIPLIED_BASE", "ADD_MULTIPLIED_TOTAL" });

	public JAttributeModifierEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JAttributeModifierEntry> entryList, boolean isPotionEffectEntry) {
		super(parent, entryList);
		this.workspace = mcreator.getWorkspace();

		equipmentSlot = new DataListComboBox(mcreator, ElementUtil.loadAllEquipmentSlots());
		equipmentSlot.setRenderer(new JComboBox<>().getRenderer());

		attribute = new DataListComboBox(mcreator, ElementUtil.loadAllAttributes(workspace));
		attribute.setRenderer(new JComboBox<>().getRenderer());

		if (!isPotionEffectEntry) {
			line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("attribute_modifiers/equipment_slot"),
					L10N.label("elementgui.common.attribute_modifier.equipment_slot")));
			line.add(equipmentSlot);
		}

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("attribute_modifiers/attribute"),
				L10N.label("elementgui.common.attribute_modifier.attribute")));
		line.add(attribute);

		if (isPotionEffectEntry) {
			line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("attribute_modifiers/amount_per_level"),
					L10N.label("elementgui.common.attribute_modifier.amount_per_level")));
		} else {
			line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("attribute_modifiers/amount"),
					L10N.label("elementgui.common.attribute_modifier.amount")));
		}
		line.add(amount);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("attribute_modifiers/operation"),
				L10N.label("elementgui.common.attribute_modifier.operation")));
		line.add(operation);

	}

	@Override public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(equipmentSlot, ElementUtil.loadAllEquipmentSlots());
		ComboBoxUtil.updateComboBoxContents(attribute, ElementUtil.loadAllAttributes(workspace));
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		equipmentSlot.setEnabled(enabled);
		attribute.setEnabled(enabled);
		amount.setEnabled(enabled);
		operation.setEnabled(enabled);
	}

	@Override public AttributeModifierEntry getEntry() {
		AttributeModifierEntry entry = new AttributeModifierEntry();
		entry.equipmentSlot = equipmentSlot.getSelectedItem().toString();
		entry.attribute = new AttributeEntry(workspace, attribute.getSelectedItem());
		entry.amount = (double) amount.getValue();
		entry.operation = (String) operation.getSelectedItem();
		return entry;
	}

	@Override public void setEntry(AttributeModifierEntry e) {
		equipmentSlot.setSelectedItem(e.equipmentSlot);
		attribute.setSelectedItem(e.attribute);
		amount.setValue(e.amount);
		operation.setSelectedItem(e.operation);
	}

}