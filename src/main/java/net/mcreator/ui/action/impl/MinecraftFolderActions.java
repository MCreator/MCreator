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

package net.mcreator.ui.action.impl;

import net.mcreator.io.OS;
import net.mcreator.minecraft.MinecraftFolderUtils;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MinecraftFolderActions {

	public static class OpenJavaEditionFolder extends BasicAction {
		public OpenJavaEditionFolder(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.open_java_edition_dir"), actionEvent -> {
				File file = MinecraftFolderUtils.getJavaEditionFolder();
				if (file != null) {
					DesktopUtils.openSafe(file);
				} else {
					JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
							L10N.t("dialog.open_java_edition_dir.fail.message"),
							L10N.t("dialog.open_java_edition_dir.fail.title"), JOptionPane.WARNING_MESSAGE);
				}
			});
		}

	}

	public static class OpenBedrockEditionFolder extends BasicAction {
		public OpenBedrockEditionFolder(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.open_bedrock_edition_dir"), actionEvent -> {
				File file = MinecraftFolderUtils.getBedrockEditionFolder();
				if (file != null) {
					DesktopUtils.openSafe(file);
				} else {
					JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
							L10N.t("dialog.open_bedrock_edition_dir.fail.message"),
							L10N.t("dialog.open_bedrock_edition_dir.fail.title"), JOptionPane.WARNING_MESSAGE);
				}
			});
		}

		@Override public boolean isEnabled() {
			return OS.getOS() == OS.WINDOWS;
		}
	}

}
