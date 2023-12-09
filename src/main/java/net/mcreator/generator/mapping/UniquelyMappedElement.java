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

package net.mcreator.generator.mapping;

/**
 * Used to make sure that the element is unique after mapping by overriding {@link #hashCode()} and {@link #equals(Object)} methods
 * that compare the mapped values instead of the unmapped ones as done by the default implementation of MappableElement.
 */
public class UniquelyMappedElement extends MappableElement {

	public UniquelyMappedElement(MappableElement original) {
		super(original.mapper, original.getUnmappedValue());
	}

	@Override public int hashCode() {
		return getMappedValue().hashCode();
	}

	@Override public boolean equals(Object element) {
		return element instanceof MappableElement && getMappedValue().equals(
				((MappableElement) element).getMappedValue());
	}

}
