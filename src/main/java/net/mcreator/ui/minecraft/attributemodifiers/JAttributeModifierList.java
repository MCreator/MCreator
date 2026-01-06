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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JAttributeModifierList
		extends JSimpleEntriesList<JAttributeModifierEntry, AttributeModifierEntry> {

	private final boolean isPotionEffectList;

	public JAttributeModifierList(MCreator mcreator, IHelpContext gui, boolean isPotionEffectList) {
		super(mcreator, gui);
		this.isPotionEffectList = isPotionEffectList;

		add.setText(L10N.t("elementgui.common.attribute_modifier.add_modifier_entry"));

		ComponentUtils.makeSection(this, L10N.t("elementgui.common.attribute_modifier.modifiers"));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	@Override
	protected JAttributeModifierEntry newEntry(JPanel parent, List<JAttributeModifierEntry> entryList,
			boolean userAction) {
		return new JAttributeModifierEntry(mcreator, gui, parent, entryList, isPotionEffectList);
	}

	public AggregatedValidationResult getValidationResult() {
		// Prevent duplicate attribute types only for potion effects
		if (isPotionEffectList) {
			Set<AttributeEntry> usedAttributes = new HashSet<>();
			for (var entry : entryList) {
				if (usedAttributes.contains(entry.getEntry().attribute)) {
					return new AggregatedValidationResult.FAIL(
							L10N.t("elementgui.common.attribute_modifier.error_attributes_must_be_unique"));
				}
				usedAttributes.add(entry.getEntry().attribute);
			}
		}
		return new AggregatedValidationResult.PASS();
	}

}