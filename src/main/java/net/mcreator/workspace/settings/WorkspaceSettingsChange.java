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

package net.mcreator.workspace.settings;

import net.mcreator.util.GSONCompare;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorkspaceSettingsChange {

	@Nonnull public WorkspaceSettings workspaceSettings;
	@Nullable public WorkspaceSettings oldSettings;

	public boolean modidchanged;
	public boolean packagechanged;
	public boolean generatorchanged;

	public boolean mcreatorDepsChanged;

	public WorkspaceSettingsChange(@Nonnull WorkspaceSettings workspaceSettings,
			@Nullable WorkspaceSettings oldSettings) {
		this.workspaceSettings = workspaceSettings;
		this.oldSettings = oldSettings;
		if (oldSettings != null) {
			this.modidchanged =
					oldSettings.getModID() != null && !workspaceSettings.getModID().equals(oldSettings.getModID());
			this.packagechanged =
					oldSettings.getModElementsPackage() != null && !workspaceSettings.getModElementsPackage()
							.equals(oldSettings.getModElementsPackage());
			this.generatorchanged =
					oldSettings.getCurrentGenerator() != null && !workspaceSettings.getCurrentGenerator()
							.equals(oldSettings.getCurrentGenerator());

			this.mcreatorDepsChanged = !GSONCompare.deepEquals(workspaceSettings.getMCreatorDependenciesRaw(),
					oldSettings.getMCreatorDependenciesRaw());
		}
	}

	public boolean gradleCachesRebuildNeeded() {
		return mcreatorDepsChanged; // generatorchanged not needed as caches will be rebuilt during workspace switch
	}

	public boolean refactorNeeded() {
		return gradleCachesRebuildNeeded() || generatorchanged || modidchanged || packagechanged;
	}
}
