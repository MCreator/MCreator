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

package net.mcreator.plugin.modapis;

import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.YamlUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModAPIManager {

	private static final Logger LOG = LogManager.getLogger("Mod API manager");

	private static final Map<String, ModAPI> modApiList = new HashMap<>();

	public static void initAPIs() {
		Set<String> fileNames = PluginLoader.INSTANCE.getResources("apis", Pattern.compile(".*\\.yaml"));

		Load yamlLoad = new Load(YamlUtil.getSimpleLoadSettings());

		for (String apidefinition : fileNames) {
			String config = FileIO.readResourceToString(PluginLoader.INSTANCE, apidefinition);

			// load generator configuration
			try {
				Map<?, ?> apiconfiguration = (Map<?, ?>) yamlLoad.loadFromString(config);

				ModAPI modAPI = new ModAPI(FilenameUtilsPatched.getBaseName(apidefinition),
						apiconfiguration.get("name").toString(), new HashMap<>());

				for (Object keyraw : apiconfiguration.keySet()) {
					String key = keyraw.toString();
					if (!key.equals("name")) {
						Map<?, ?> impldef = (Map<?, ?>) apiconfiguration.get(keyraw);
						String gradle = (String) impldef.get("gradle");
						List<?> updateFiles = (List<?>) impldef.get("update_files");
						boolean requiredWhenEnabled =
								impldef.get("required_when_enabled") != null && Boolean.parseBoolean(
										impldef.get("required_when_enabled").toString());

						if (updateFiles == null)
							updateFiles = Collections.emptyList();

						ModAPIImplementation implementation = new ModAPIImplementation(modAPI, gradle,
								updateFiles.stream().map(Object::toString).collect(Collectors.toList()),
								requiredWhenEnabled);
						modAPI.implementations().put(key, implementation);
					}
				}

				modApiList.put(FilenameUtilsPatched.getBaseName(apidefinition), modAPI);

				LOG.debug("Loaded mod API definition: " + FilenameUtilsPatched.getBaseName(apidefinition));
			} catch (YamlEngineException e) {
				LOG.error("Failed to load mod API definition for: " + apidefinition + ": " + e.getMessage());
			}
		}
	}

	public static List<ModAPIImplementation> getModAPIsForGenerator(String generatorName) {
		List<ModAPIImplementation> implementations = new ArrayList<>();

		for (ModAPI api : modApiList.values())
			if (api.implementations().containsKey(generatorName))
				implementations.add(api.implementations().get(generatorName));

		return implementations;
	}

	public static ModAPIImplementation getModAPIForNameAndGenerator(String name, String generatorName) {
		ModAPI modAPI = modApiList.get(name);
		if (modAPI != null) {
			return modAPI.implementations().get(generatorName);
		}

		return null;
	}

	public static void deleteAPIs(Workspace workspace, WorkspaceSettings workspaceSettings) {
		List<ModAPIImplementation> apis = workspaceSettings.getMCreatorDependencies().stream()
				.map(e -> ModAPIManager.getModAPIForNameAndGenerator(e, workspace.getGenerator().getGeneratorName()))
				.toList();
		for (ModAPIImplementation api : apis) {
			if (api.update_files() != null) {
				for (String fileRelative : api.update_files()) {
					File file = new File(workspace.getWorkspaceFolder(), fileRelative);
					if (workspace.getFolderManager().isFileInWorkspace(file) && file.isFile())
						file.delete();
				}
			}
		}
	}

}
