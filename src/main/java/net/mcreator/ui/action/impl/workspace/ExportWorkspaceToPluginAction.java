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
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExportWorkspaceToPluginAction extends BasicAction {
	private static final Logger LOG = LogManager.getLogger("Workspace to Plugin");

	public ExportWorkspaceToPluginAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.export_workspace_plugin"),
				e -> exportWorkspaceToPlugin(actionRegistry.getMCreator(),
						actionRegistry.getMCreator().getWorkspace()));
	}

	private static void exportWorkspaceToPlugin(MCreator mcreator, Workspace workspace) {
		LOG.debug("Exporting " + workspace.getWorkspaceSettings().getModName() + " to a MCreator plugin");

		ProgressDialog dial = new ProgressDialog(mcreator, L10N.t("dialog.workspace.export_workspace_plugin.title"));

		Thread thread = new Thread(() -> {
			Map<String[], String> supportedMETs = new HashMap<>() {{
				put(new String[] { ModElementType.ADVANCEMENT.getRegistryName() }, "achievements");
				put(new String[] { ModElementType.BLOCK.getRegistryName(), ModElementType.FLUID.getRegistryName(),
								ModElementType.PLANT.getRegistryName(), ModElementType.ARMOR.getRegistryName(),
								ModElementType.ITEM.getRegistryName(), ModElementType.FOOD.getRegistryName(),
								ModElementType.RANGEDITEM.getRegistryName(), ModElementType.TOOL.getRegistryName() },
						"blocksitems");
				put(new String[] { ModElementType.ENCHANTMENT.getRegistryName() }, "enchantments");
				put(new String[] { ModElementType.PARTICLE.getRegistryName() }, "particles");
				put(new String[] { ModElementType.TAB.getRegistryName() }, "tabs");
			}};

			String modid = workspace.getWorkspaceSettings().getModID();
			String modName = workspace.getWorkspaceSettings().getModName().replace(" ", "");
			String modPackage = workspace.getWorkspaceSettings().getModElementsPackage() + ".";
			String path =
					workspace.getWorkspaceFolder().getPath() + File.separator + ".mcreator" + File.separator + modid;
			new File(path).mkdirs();

			LOG.debug("Generating files...");
			for (String[] type : supportedMETs.keySet()) {
				ProgressDialog.ProgressUnit p = new ProgressDialog.ProgressUnit(
						L10N.t("dialog.workspace.export_workspace_plugin.progress." + supportedMETs.get(type)));
				dial.addProgress(p);

				File dataListFile = new File(path + File.separator + "datalists", supportedMETs.get(type) + ".yaml");

				try {

					// Data list and mapping files
					List<Object> dlValues = new ArrayList<>(); // This list will contain the data list file's values.
					Map<String, Object> mappingValues = new HashMap<>(); // This list will contain the mapping files' values.
					// If false, it means the workspace doesn't contain this MET, so we don't need to create files.
					for (ModElement me : workspace.getModElements()) {
						if (Arrays.stream(type).collect(Collectors.toList()).contains(me.getType().getRegistryName())) {
							// Create the data list and mapping values
							if (me.getType().equals(ModElementType.ADVANCEMENT)) {
								dlValues.add(modid + "/" + me.getRegistryName());
								mappingValues.put(modid + "/" + me.getRegistryName(),
										modid + ":" + me.getRegistryName());

							} else if (me.getType().equals(ModElementType.BLOCK) || me.getType()
									.equals(ModElementType.PLANT)) {
								/*
								Map<String, String> map = new HashMap<>();
								map.put()

								 */
							} else if (me.getType().equals(ModElementType.ENCHANTMENT) || me.getType()
									.equals(ModElementType.PARTICLE)) {
								dlValues.add(modName + "." + me.getName());
								mappingValues.put(modName + "." + me.getName(),
										modPackage + me.getType().getRegistryName() + "." + me.getName()
												+ StringUtils.capitalize(me.getType().getRegistryName()) + "."
												+ me.getType().getRegistryName());

							} else if (me.getType().equals(ModElementType.TAB)) {
								// Data list
								Map<String, String> map = new HashMap<>();
								map.put(modName + "." + me.getName(), null);
								map.put("texture", me.getName());
								dlValues.add(map);

								// Copy tab's icon
								FileIO.copyFile(new File(workspace.getWorkspaceFolder(),
										".mcreator" + File.separator + "modElementThumbnails" + File.separator
												+ me.getName() + ".png"), new File(path,
										"datalists" + File.separator + "icons" + File.separator + me.getName()
												+ ".png"));

								// Mappings
								mappingValues.put(modName + "." + me.getName(),
										modPackage + "itemgroup." + me.getName() + "ItemGroup.tab");

							}
						}
					}

					// Data list file creation
					YamlWriter writerDL = new YamlWriter(new FileWriter(dataListFile));
					writerDL.write(dlValues);
					writerDL.close();

					// Mapping files creation
					List<String> supportedForgeVersions = new ArrayList<>();
					for (GeneratorConfiguration genConfig : Generator.GENERATOR_CACHE.values()) {
						if (genConfig.getGeneratorFlavor() == GeneratorFlavor.FORGE && checkMCVersion(
								genConfig.getGeneratorMinecraftVersion())) {
							File mappingFile = new File(
									path + File.separator + genConfig.getGeneratorName() + File.separator + "mappings",
									supportedMETs.get(type) + ".yaml");
							mappingFile.getParentFile().mkdirs();
							YamlWriter writerGenerator = new YamlWriter(new FileWriter(mappingFile));
							writerGenerator.write(mappingValues);
							writerGenerator.close();
						}
					}
				} catch (IOException e) {
					LOG.error("Could not create " + supportedMETs.get(type), e);
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
		}};  // We skip these versions as they are no longer supported, so they have missing mod elements.
		return !versionsToSKip.contains(mcVersion);
	}
}
