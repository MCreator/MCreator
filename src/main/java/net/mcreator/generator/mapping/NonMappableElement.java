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

import net.mcreator.minecraft.DataListEntry;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * In some cases, one may need to pass a string where a MappableElement is expected. This class is used for such cases.
 */
public class NonMappableElement extends MappableElement {

	@Nullable private Workspace workspace = null;

	public NonMappableElement(String value) {
		super(null, value);
	}

	@Override public String getMappedValue() {
		return getUnmappedValue();
	}

	@Override public boolean canProperlyMap() {
		return true;
	}

	@Override public Optional<DataListEntry> getDataListEntry() {
		return Optional.empty();
	}

	@Override public void setWorkspace(@Nullable Workspace workspace) {
		this.workspace = workspace;
	}

	@Nullable @Override public Workspace getWorkspace() {
		return this.workspace;
	}

}
