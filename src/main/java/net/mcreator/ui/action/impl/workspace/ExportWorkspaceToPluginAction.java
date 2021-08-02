/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.action.impl.workspace;

import com.esotericsoftware.yamlbeans.YamlWriter;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportWorkspaceToPluginAction extends BasicAction {
	private static final Logger LOG = LogManager.getLogger("Workspace to Plugin");

	public ExportWorkspaceToPluginAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.export_workspace_plugin"),
				e -> exportWorkspaceToPlugin(actionRegistry.getMCreator(),
						actionRegistry.getMCreator().getWorkspace()));
	}

	private static void exportWorkspaceToPlugin(MCreator mcreator, Workspace workspace) {
		LOG.debug("Exporting " + workspace.getWorkspaceSettings().getModName() + " to a MCreator plugin");
		String modid = workspace.getWorkspaceSettings().getModID();
		//File exportFile = FileDialogs.getSaveDialog(mcreator, new String[] { ".zip" });

		ProgressDialog dial = new ProgressDialog(mcreator, L10N.t("dialog.workspace.export_workspace_plugin.title"));

		Thread thread = new Thread(() -> {
			List<String> supportedMETs = new ArrayList<>() {{
				add(ModElementType.ADVANCEMENT.getRegistryName());
			}};

			String path =
					workspace.getWorkspaceFolder().getPath() + File.separator + ".mcreator" + File.separator + modid;
			new File(path).mkdirs();

			LOG.debug("Generating files...");
			for (String type : supportedMETs) {
				ProgressDialog.ProgressUnit p = new ProgressDialog.ProgressUnit(
						L10N.t("dialog.workspace.export_workspace_plugin.progress." + type));
				dial.addProgress(p);
				String fileName = null;

				if (ModElementType.ADVANCEMENT.getRegistryName().equals(type)) {
					fileName = "achievements";
				}

				File dataListFile = new File(path + File.separator + "datalists", fileName + ".yaml");

				try {
					//Finish creating missing folders and files
					if (!dataListFile.getParentFile().exists())
						dataListFile.getParentFile().mkdirs();
					if (!dataListFile.exists()) {
						dataListFile.createNewFile();
					}
					// We create a folder for each Forge generator loaded in the cache
					for (GeneratorConfiguration genConfig : Generator.GENERATOR_CACHE.values()) {
						if (genConfig.getGeneratorFlavor() == GeneratorFlavor.FORGE && checkMCVersion(
								genConfig.getGeneratorMinecraftVersion())) {
							File mappingFile = new File(
									path + File.separator + genConfig.getGeneratorName() + File.separator + "mappings",
									fileName + ".yaml");
							if (!mappingFile.getParentFile().exists())
								mappingFile.getParentFile().mkdirs();

						}
					}

					// Data list file
					List<Object> dlValues = new ArrayList<>(); // This list will contain the data list file's values.
					Map<Object, Object> mappingValues = new HashMap<>(); // This list will contain the mapping files' values.
					for (ModElement me : workspace.getModElements()) {
						if (me.getType().equals(ModElementType.ADVANCEMENT)) {
							dlValues.add(modid + "/" + me.getRegistryName());
							mappingValues.put(modid + "/" + me.getRegistryName(), modid + ":" + me.getRegistryName());
						}
					}

					YamlWriter writerDL = new YamlWriter(new FileWriter(dataListFile));
					writerDL.write(dlValues);
					writerDL.close();

					for (GeneratorConfiguration genConfig : Generator.GENERATOR_CACHE.values()) {
						if (genConfig.getGeneratorFlavor() == GeneratorFlavor.FORGE && checkMCVersion(
								genConfig.getGeneratorMinecraftVersion())) {
							File mappingFile = new File(
									path + File.separator + genConfig.getGeneratorName() + File.separator + "mappings",
									fileName + ".yaml");
							YamlWriter writerGenerator = new YamlWriter(new FileWriter(mappingFile));
							writerGenerator.write(mappingValues);
							writerGenerator.close();
						}
					}
				} catch (IOException e) {
					LOG.error("Could not create " + fileName, e);
					p.err();
					dial.refreshDisplay();
				}
				p.ok();
				dial.refreshDisplay();
			}

			dial.hideAll();
		});
		thread.start();
		dial.setVisible(true);
	}

	private static boolean checkMCVersion(String mcVersion) {
		List<String> versionsToSKip = new ArrayList<>() {{
			add("1.12.2");
			add("1.14.4");
		}};  // We skip these versions as we don't know if they can support every mod element
		return !versionsToSKip.contains(mcVersion);
	}
}
