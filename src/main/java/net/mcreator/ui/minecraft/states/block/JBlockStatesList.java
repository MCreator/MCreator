/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.block;

import net.mcreator.element.types.Block;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.util.diff.ListDiff;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.*;
import java.util.function.Supplier;

public class JBlockStatesList extends JSimpleEntriesList<JBlockStatesListEntry, Block.StateEntry>
		implements IValidable {

	private final Supplier<List<PropertyData<?>>> currentPropertiesSupplier;
	private final Supplier<Boolean> isBlockNotTinted;

	private final List<PropertyData<?>> lastPropertyState = new ArrayList<>();

	public JBlockStatesList(MCreator mcreator, IHelpContext gui,
			Supplier<List<PropertyData<?>>> currentPropertiesSupplier, Supplier<Boolean> isBlockNotTinted) {
		super(mcreator, gui);
		this.currentPropertiesSupplier = currentPropertiesSupplier;
		this.isBlockNotTinted = isBlockNotTinted;

		entries.setLayout(new BoxLayout(entries, BoxLayout.Y_AXIS));

		add.setText(L10N.t("elementgui.block.custom_states.add"));

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	// called when a property is removed
	public void propertiesChanged() {
		List<PropertyData<?>> currentPropertyState = currentPropertiesSupplier.get();

		// Iterate all properties that have been removed (old list, new list)
		for (PropertyData<?> removed : ListDiff.getListDiff(lastPropertyState, currentPropertyState).removed()) {
			propertyRemoved(removed);
		}

		lastPropertyState.clear();
		lastPropertyState.addAll(currentPropertyState);
	}

	// Called when a state property is removed, to make sure we correct state definitions
	private void propertyRemoved(PropertyData<?> data) {
		Set<StateMap> duplicateFilter = new HashSet<>();
		Iterator<JBlockStatesListEntry> iterator = entryList.iterator();
		while (iterator.hasNext()) {
			JBlockStatesListEntry s = iterator.next();
			StateMap stateMap = s.getStateLabel().getStateMap();
			stateMap.remove(data);
			if (stateMap.isEmpty() || !duplicateFilter.add(stateMap)) { // if the state map is empty or a duplicate is found
				iterator.remove(); // remove the JItemStatesListEntry
				entries.remove(s.getContainerPanel());
			} else {
				s.getStateLabel().setStateMap(stateMap);
			}
		}

		entries.revalidate();
		entries.repaint();
	}

	boolean isBlockNotTinted() {
		return isBlockNotTinted.get();
	}

	@Nullable @Override
	protected JBlockStatesListEntry newEntry(JPanel parent, List<JBlockStatesListEntry> entryList, boolean userAction) {
		JStateLabel stateLabel = new JStateLabel(mcreator, currentPropertiesSupplier,
				() -> entryList.stream().map(JBlockStatesListEntry::getStateLabel));
		if (userAction && !stateLabel.editState())
			return null;

		return new JBlockStatesListEntry(mcreator, gui, parent, entryList, stateLabel, this, userAction);
	}

	@Override public ValidationResult getValidationStatus() {
		// validate state definitions - if a certain property is used in one state, it needs to be present in all states
		List<Block.StateEntry> entries = getEntries();

		// collect all used properties
		Set<PropertyData<?>> usedProperties = new HashSet<>();
		for (Block.StateEntry entry : entries) {
			usedProperties.addAll(entry.stateMap.keySet());
		}

		// make sure all states contain all used properties
		for (Block.StateEntry entry : entries) {
			if (!entry.stateMap.keySet().containsAll(usedProperties)) {
				return new ValidationResult(ValidationResult.Type.ERROR,
						L10N.t("elementgui.block.custom_states.error_missing_properties",
								usedProperties.stream().map(BlockStatePropertyUtils::propertyRegistryName).toList()));
			}
		}

		return ValidationResult.PASSED;
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return null;
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		entryList.forEach(validationResult::addValidationElement);
		validationResult.addValidationElement(this);
		return validationResult;
	}

}