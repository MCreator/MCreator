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

package net.mcreator.ui.minecraft.modellayers;

import net.mcreator.element.types.LivingEntity;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.swing.*;
import java.util.List;
import java.util.function.Supplier;

public class JModelLayerList extends JSimpleEntriesList<JModelLayerListEntry, LivingEntity.ModelLayerEntry> {

	private final Supplier<List<PropertyDataWithValue<?>>> entityDataListProvider;

	public JModelLayerList(MCreator mcreator, IHelpContext gui,
			Supplier<List<PropertyDataWithValue<?>>> entityDataListProvider) {
		super(mcreator, gui);
		this.entityDataListProvider = entityDataListProvider;

		add.setText(L10N.t("elementgui.living_entity.add_model_layer"));

		ComponentUtils.borderWrap(this);
	}

	@Override
	protected JModelLayerListEntry newEntry(JPanel parent, List<JModelLayerListEntry> entryList, boolean userAction) {
		return new JModelLayerListEntry(mcreator, gui, parent, entryList, entityDataListProvider);
	}

	public void entityDataListChanged() {
		entryList.forEach(JModelLayerListEntry::entityDataListChanged);
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		entryList.forEach(validationResult::addValidationElement);
		return validationResult;
	}

}
