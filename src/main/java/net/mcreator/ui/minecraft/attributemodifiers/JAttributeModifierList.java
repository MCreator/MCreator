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
import net.mcreator.element.types.PotionEffect;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JAttributeModifierList extends JSimpleEntriesList<JAttributeModifierEntry, PotionEffect.AttributeModifierEntry> {

	public JAttributeModifierList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		add.setText(L10N.t("elementgui.potioneffect.add_modifier_entry"));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.potioneffect.modifiers"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
	}

	@Override protected JAttributeModifierEntry newEntry(JPanel parent, List<JAttributeModifierEntry> entryList, boolean userAction) {
		return new JAttributeModifierEntry(mcreator, gui, parent, entryList);
	}

	public AggregatedValidationResult getValidationResult() {
		Set<AttributeEntry> usedAttributes = new HashSet<>();
		for (var entry : entryList) {
			if (usedAttributes.contains(entry.getEntry().attribute)) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.potioneffect.error_attributes_must_be_unique"));
			}
			usedAttributes.add(entry.getEntry().attribute);
		}
		return new AggregatedValidationResult.PASS();
	}

}