/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.NumberProviderEntry;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;

@SuppressWarnings("unused") public class ItemExtension extends GeneratableElement {

	public MItemBlock item;

	public boolean enableFuel;
	@Nullable public NumberProviderEntry fuelNumberProvider;
	public NumberProcedure fuelPower;
	public Procedure fuelSuccessCondition;

	public boolean hasDispenseBehavior;
	public Procedure dispenseSuccessCondition;
	public Procedure dispenseResultItemstack;

	@Nullable public NumberProviderEntry compostableNumberProvider;
	@Numeric(init = 0, min = 0, max = 1, step = 0.01) public double compostLayerChance;

	private ItemExtension() {
		this(null);
	}

	public ItemExtension(ModElement element) {
		super(element);
	}

	public boolean hasFuelProcedure() {
		if (!enableFuel || (fuelNumberProvider != null))
			return false;
		Workspace workspace = getModElement().getWorkspace();
		return Procedure.hasProcedure(fuelPower, workspace) || Procedure.hasProcedure(fuelSuccessCondition, workspace);
	}

	public boolean generateFuelProvider() {
		return enableFuel && fuelNumberProvider == null;
	}

	public boolean isCompostable() {
		return compostableNumberProvider != null || compostLayerChance > 0;
	}

	public boolean generateCompostableProvider() {
		return compostableNumberProvider == null && compostLayerChance > 0;
	}
}
