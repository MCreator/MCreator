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

package net.mcreator.element.types.interfaces;

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.mapping.MappableElement;

import java.util.Collection;
import java.util.Collections;

/**
 * These methods are used by {@link net.mcreator.workspace.ReferencesFinder ReferencesFinder} to acquire
 * all {@link MappableElement MappableElements} declared by mod element type storage class implementing this interface,
 * as well as all procedures from that class.
 */
public interface IOtherModElementsDependent {

	/**
	 * @return List of names of all elements (mappable or MEs) used by mod element instance and not already provided as names of entries
	 * in the list returned by {@link #getUsedElementMappings()}
	 * <p>
	 * Format of string in returned collection: with {@code CUSTOM:} prefix for entries created from other mod elements or unmapped name for datalist entries
	 */
	default Collection<String> getUsedElementNames() {
		return Collections.emptyList();
	}

	/**
	 * @return List of all mapping entries found on a mod element instance, excluding those that can not be custom
	 * (can not be imported from other mod elements).
	 */
	default Collection<? extends MappableElement> getUsedElementMappings() {
		return Collections.emptyList();
	}

	/**
	 * @return List of all procedures found on a mod element instance.
	 */
	default Collection<? extends Procedure> getUsedProcedures() {
		return Collections.emptyList();
	}
}
