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

package net.mcreator.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mcreator.io.FileIO;
import net.mcreator.io.OS;
import net.mcreator.io.WindowsProcessUtil;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;

public class BedrockUtils {

	private static final Logger LOG = LogManager.getLogger("Bedrock Utils");

	private static final String MC_PROCESS = "Minecraft.Windows.exe";

	public static void reinstallAddon(MCreator mcreator, Workspace workspace) {
		if (OS.getOS() == OS.WINDOWS) {
			File bedrockDir = MinecraftFolderUtils.getBedrockEditionFolder();

			File bpdev = new File(bedrockDir, "development_behavior_packs");
			File rpdev = new File(bedrockDir, "development_resource_packs");

			if (bedrockDir != null) {
				// first we try to detect if this pack already exists and remove it
				detectAndDeletePack(new File(bedrockDir, "behavior_packs"), workspace.getWorkspaceInfo().getUUID());
				detectAndDeletePack(new File(bedrockDir, "behavior_packs"),
						workspace.getWorkspaceInfo().getUUID("resourcepack"));

				boolean detected_bp = detectAndDeletePack(bpdev, workspace.getWorkspaceInfo().getUUID());
				boolean detected_rp = detectAndDeletePack(rpdev, workspace.getWorkspaceInfo().getUUID("resourcepack"));

				if (!detected_bp || !detected_rp) {
					JOptionPane.showMessageDialog(mcreator,
							"<html>Your Add-On was not added to your Minecraft Bedrock Edition before.<br><br>"
									+ "Make sure you <b>enable your addon</b> and <b>enable it for selected world</b>.<br>"
									+ "In order for the addon to work, make sure your test world has both"
									+ "<br><b>Cheats and experimental gameplay enabled</b> for the Add-On to work properly.",
							"Enabling Add-On", JOptionPane.INFORMATION_MESSAGE);
				}

				mcreator.getGradleConsole().exec("build", taskResult -> {
					String exportFile = workspace.getGeneratorConfiguration().getGradleTaskFor("export_file");

					File addonFile = new File(workspace.getWorkspaceFolder(), exportFile);
					if (addonFile.isFile()) {
						try {
							// stop running MC if any
							if (WindowsProcessUtil.isProcessRunning(MC_PROCESS)) {
								String[] options = new String[] { "Close and reload",
										"Close and reload (don't ask again)", "Cancel test run" };
								int option = PreferencesManager.PREFERENCES.bedrock.silentReload ?
										0 :
										JOptionPane.showOptionDialog(mcreator,
												"<html>Minecraft Bedrock Edition was detected to be already running. You can: <ol>"
														+ "<li>Close it (<b>any open world will not be saved</b>), reload addon and start it again</li>"
														+ "<li>Do as the first option and disable this message for the future runs</li>"
														+ "<li>Cancel this run so you can close Minecraft BE manually and then manually press the play button again",
												"Minecraft Bedrock Edition already open", JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
								if (option != 2) {
									if (option == 1) {
										PreferencesManager.PREFERENCES.bedrock.silentReload = true;
										PreferencesManager.storePreferences(PreferencesManager.PREFERENCES);
									}

									WindowsProcessUtil.killProcess(MC_PROCESS);
									loadPackAndRun(bpdev, rpdev, workspace);
								}
							} else {
								loadPackAndRun(bpdev, rpdev, workspace);
							}
						} catch (Exception e) {
							JOptionPane.showMessageDialog(mcreator,
									"<html>Failed to open Minecraft Bedrock edition.<br>Make sure you have it installed from the store.",
									"Run client failed", JOptionPane.WARNING_MESSAGE);
							LOG.warn("Failed to open add-on", e);
						}
					}
				});
			} else {
				JOptionPane
						.showMessageDialog(mcreator, "It seems you do not have Bedrock Edition installed from store.",
								"Run client not supported", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(mcreator, "Bedrock Edition is only supported on Windows 10 at the moment",
					"Run client not supported", JOptionPane.WARNING_MESSAGE);
		}
	}

	private static void loadPackAndRun(File bpdev, File rpdev, Workspace workspace) throws Exception {
		FileIO.copyDirectory(workspace.getGenerator().getSourceRoot(),
				new File(bpdev, workspace.getWorkspaceSettings().getModID()));
		FileIO.copyDirectory(workspace.getGenerator().getResourceRoot(),
				new File(rpdev, workspace.getWorkspaceSettings().getModID()));

		WindowsProcessUtil.startProcessAsync(
				"cmd.exe /c start \"\" \"shell:AppsFolder\\Microsoft.MinecraftUWP_8wekyb3d8bbwe!App\"");
	}

	private static boolean detectAndDeletePack(File bpacksdir, String uuid) {
		try {
			File[] packs = bpacksdir.listFiles();
			for (File pack : packs != null ? packs : new File[0]) {
				File manifest = new File(pack, "manifest.json");
				if (manifest.isFile()) {
					JsonElement jelement = JsonParser.parseString(FileIO.readFileToString(manifest));
					String packUUID = jelement.getAsJsonObject().get("header").getAsJsonObject().get("uuid")
							.getAsString();
					if (packUUID.equalsIgnoreCase(uuid)) {
						FileIO.deleteDir(pack);
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOG.warn("Failed to properly delete existing pack", e);
		}

		return false;
	}

}
