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
import net.mcreator.ui.init.L10N;
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
					JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.bedrock.enable_addons"),
							L10N.t("dialog.bedrock.enable_addons.title"), JOptionPane.INFORMATION_MESSAGE);
				}

				mcreator.getGradleConsole().exec("build", taskResult -> {
					String exportFile = workspace.getGeneratorConfiguration().getGradleTaskFor("export_file");

					File addonFile = new File(workspace.getWorkspaceFolder(), exportFile);
					if (addonFile.isFile()) {
						try {
							// stop running MC if any
							if (WindowsProcessUtil.isProcessRunning(MC_PROCESS)) {
								String[] options = new String[] { L10N.t("dialog.bedrock.options.close_reload"),
										L10N.t("dialog.bedrock.options.close_reload_always"), L10N.t("dialog.bedrock.options.cancel") };
								int option = PreferencesManager.PREFERENCES.bedrock.silentReload ?
										0 :
										JOptionPane.showOptionDialog(mcreator,
												L10N.t("dialog.bedrock.already_opened"),
												L10N.t("dialog.bedrock.already_opened.title"), JOptionPane.YES_NO_OPTION,
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
							JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.bedrock.failed"),
									L10N.t("dialog.bedrock.failed.title"), JOptionPane.WARNING_MESSAGE);
							LOG.warn("Failed to open add-on", e);
						}
					}
				});
			} else {
				JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.bedrock.unsupported"),
						L10N.t("dialog.bedrock.unsupported.title"), JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.bedrock.unsupported.windows"),
					L10N.t("dialog.bedrock.unsupported"), JOptionPane.WARNING_MESSAGE);
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
