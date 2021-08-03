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

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlWriter;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Plant;
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
								Map<String, String> map = new HashMap<>();
								map.put(modid + "/" + me.getRegistryName(), null);
								map.put("texture", me.getName());
								dlValues.add(map);

								mappingValues.put(modid + "/" + me.getRegistryName(),
										modid + ":" + me.getRegistryName());

								copyModElementIcon(workspace, me.getName(), path);
							} else if (me.getType().equals(ModElementType.BLOCK) || me.getType()
									.equals(ModElementType.PLANT) || me.getType().equals(ModElementType.ITEM)
									|| me.getType().equals(ModElementType.FOOD) || me.getType()
									.equals(ModElementType.RANGEDITEM) || me.getType().equals(ModElementType.TOOL)) {
								Map<String, String> map = new HashMap<>();
								map.put(modName + "." + me.getName(), null);

								String readableName = me.getName();
								if (me.getGeneratableElement() != null) {
									if (me.getType().equals(ModElementType.BLOCK))
										readableName = ((Block) me.getGeneratableElement()).name;
									else if (me.getType().equals(ModElementType.PLANT))
										readableName = ((Plant) me.getGeneratableElement()).name;
								}
								map.put("readable_name", readableName);

								if (me.getType().equals(ModElementType.BLOCK) || me.getType()
										.equals(ModElementType.PLANT))
									map.put("type", "block");
								else
									map.put("type", "item");
								map.put("texture", me.getName());
								dlValues.add(map);

								// Mappings
								List<String> values = new ArrayList<>();
								if (me.getType().equals(ModElementType.BLOCK) || me.getType()
										.equals(ModElementType.PLANT))
									values.add(modPackage + "block." + me.getName() + StringUtils.capitalize(
											me.getType().getRegistryName()) + ".block");
								else
									values.add(modPackage + "item." + me.getName() + StringUtils.capitalize(
											me.getType().getRegistryName()) + ".block");
								values.add(modid + ":" + me.getRegistryName());
								mappingValues.put(modName + "." + me.getName(), values);

								copyModElementIcon(workspace, me.getName(), path);
							} else if (me.getType().equals(ModElementType.ENCHANTMENT) || me.getType()
									.equals(ModElementType.PARTICLE) || me.getType().equals(ModElementType.TAB)) {
								// Custom enchantments don't have a custom icon, so no need to add a texture
								if (me.getType().equals(ModElementType.ENCHANTMENT)) {
									dlValues.add(modName + "." + me.getName());
								} else {
									Map<String, String> map = new HashMap<>();
									map.put(modName + "." + me.getName(), null);
									map.put("texture", me.getName());
									dlValues.add(map);
								}

								// Mappings
								if (me.getType().equals(ModElementType.TAB)) {
									mappingValues.put(modName + "." + me.getName(),
											modPackage + "itemgroup." + me.getName() + "ItemGroup.tab");
								} else {
									mappingValues.put(modName + "." + me.getName(),
											modPackage + me.getType().getRegistryName() + "." + me.getName()
													+ StringUtils.capitalize(me.getType().getRegistryName()) + "."
													+ me.getType().getRegistryName());
								}

								if (!me.getType().equals(ModElementType.ENCHANTMENT))
									copyModElementIcon(workspace, me.getName(), path);
							}
						}
					}

					// Data list file creation
					YamlWriter writerDL = new YamlWriter(new FileWriter(dataListFile));
					writerDL.write(dlValues);
					writerDL.close();

					// Mapping files creation
					for (GeneratorConfiguration genConfig : Generator.GENERATOR_CACHE.values()) {
						if (genConfig.getGeneratorFlavor() == GeneratorFlavor.FORGE && checkMCVersion(
								genConfig.getGeneratorMinecraftVersion())) {
							File mappingFile = new File(
									path + File.separator + genConfig.getGeneratorName() + File.separator + "mappings",
									supportedMETs.get(type) + ".yaml");
							mappingFile.getParentFile().mkdirs();
							YamlConfig config = new YamlConfig();
							YamlWriter writerGenerator = new YamlWriter(new FileWriter(mappingFile), config);
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

	private static void copyModElementIcon(Workspace workspace, String modElementName, String path) {
		FileIO.copyFile(new File(workspace.getWorkspaceFolder(),
						".mcreator" + File.separator + "modElementThumbnails" + File.separator + modElementName + ".png"),
				new File(path, "datalists" + File.separator + "icons" + File.separator + modElementName + ".png"));
	}
}
