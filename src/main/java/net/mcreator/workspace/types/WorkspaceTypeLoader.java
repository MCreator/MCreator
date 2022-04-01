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

package net.mcreator.workspace.types;

import com.google.gson.Gson;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.util.FilenameUtilsPatched;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class WorkspaceTypeLoader {

	private static final Logger LOG = LogManager.getLogger("Workspace Type Loader");
	public static WorkspaceTypeLoader INSTANCE;
	public List<WorkspaceType> REGISTRY = new ArrayList<>();

	public WorkspaceTypeLoader() {
		LOG.debug("Loading workspace types");

		final Gson gson = new Gson();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("workspacetypes", Pattern.compile("^[^$].*\\.json"));
		for (String file : fileNames) {
			WorkspaceType workspaceType = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file),
					WorkspaceType.class);
			workspaceType.setID(FilenameUtilsPatched.getBaseName(file));

			REGISTRY.add(workspaceType);
		}

		// We manually add the mod workspace type as this is a special type
		REGISTRY.add(new WorkspaceType("mod", ModElementTypeLoader.getModElementsInModWT()));
	}

	public static void loadGeneratorFlavors() {
		INSTANCE = new WorkspaceTypeLoader();
	}

	public WorkspaceType fromID(String id) throws IllegalArgumentException {
		for (WorkspaceType flavor : REGISTRY) {
			if (flavor.getID().equals(id))
				return flavor;
		}

		throw new IllegalArgumentException("Workspace type " + id + " is not a registered type");
	}
}
