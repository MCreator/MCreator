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

package net.mcreator.element.parts;

import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;

/**
 * Mod element parameters represented by instances of classes implementing this interface gather some used data
 * from the configured workspace. In other words, these objects need workspace reference defined
 * before they can be used by workspace managers or code generators.
 */
public interface IWorkspaceDependent {

	void setWorkspace(@Nullable Workspace workspace);

}
