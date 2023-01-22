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
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.IOtherModElementsDependent;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.workspace.elements.ModElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ItemExtension extends GeneratableElement implements IOtherModElementsDependent {

	public MItemBlock item;

	public boolean enableFuel;
	public NumberProcedure fuelPower;
	public Procedure fuelSuccessCondition;

	public boolean hasDispenseBehavior;
	public Procedure dispenseSuccessCondition;
	public Procedure dispenseResultItemstack;

	public double compostLayerChance;

	public ItemExtension(ModElement element) {
		super(element);
	}

	@Override public Collection<? extends MappableElement> getUsedModElements() {
		return Collections.singletonList(item);
	}

	@Override public Collection<? extends Procedure> getUsedProcedures() {
		return Arrays.asList(fuelPower, fuelSuccessCondition, dispenseSuccessCondition, dispenseResultItemstack);
	}
}
