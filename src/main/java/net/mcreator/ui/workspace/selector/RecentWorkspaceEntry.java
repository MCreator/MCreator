/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.workspace.selector;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class RecentWorkspaceEntry {

	@NotNull private String name;
	private GeneratorFlavor type;

	@NotNull private final String path;

	public RecentWorkspaceEntry(Workspace workspace, File path) {
		this.name = workspace.getWorkspaceSettings().getModName();
		this.path = path.toString();
		this.type = workspace.getGeneratorConfiguration().getGeneratorFlavor();
	}

	public void update(RecentWorkspaceEntry source) {
		this.name = source.getName();
		this.type = source.getType();
	}

	@NotNull public File getPath() {
		return new File(path);
	}

	@NotNull public String getName() {
		return name;
	}

	@NotNull public GeneratorFlavor getType() {
		if (type == null)
			return GeneratorFlavor.UNKNOWN;

		return type;
	}

	@Override public int hashCode() {
		return path.hashCode();
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof RecentWorkspaceEntry) {
			RecentWorkspaceEntry cmpObj = (RecentWorkspaceEntry) obj;
			return cmpObj.path.equals(path);
		}
		return false;
	}
}
