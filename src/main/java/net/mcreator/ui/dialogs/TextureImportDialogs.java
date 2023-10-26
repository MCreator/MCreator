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

package net.mcreator.ui.dialogs;

import javafx.stage.FileChooser;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.file.FileChooserType;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class TextureImportDialogs {

	private static File f1, f2;

	/**
	 * <p>This method opens a dialog to select the texture type to use for the provided texture file.</p>
	 *
	 * @param mcreator <p>The instance of {@link MCreator} to use</p>
	 * @param file     <p>The texture file to import</p>
	 * @param message  <p>The message to display on the option dialog</p>
	 */
	public static void importSingleTexture(final MCreator mcreator, File file, String message) {
		TextureType[] options = TextureType.getSupportedTypes(mcreator.getWorkspace(), false);
		int n = JOptionPane.showOptionDialog(mcreator, message, L10N.t("dialog.textures_import.texture_type"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (n >= 0) {
			saveTextures(mcreator, options[n], new File[] { file });
		}
	}

	public static void importArmor(final MCreator mcreator) {
		JPanel od = new JPanel(new BorderLayout());
		JPanel neno = new JPanel(new GridLayout(3, 2, 4, 4));
		JButton p1 = new JButton("...");
		JButton p2 = new JButton("...");
		neno.add(L10N.label("dialog.textures_import.armor_needs_two_files"));
		neno.add(L10N.label("dialog.textures_import.armor_layers"));
		neno.add(L10N.label("dialog.textures_import.armor_part_one"));
		neno.add(p1);
		neno.add(L10N.label("dialog.textures_import.armor_part_two"));
		neno.add(p2);
		p1.addActionListener(event -> {
			File[] f1a = FileDialogs.getFileChooserDialog(mcreator, FileChooserType.OPEN, false, null,
					new FileChooser.ExtensionFilter("Armor layer 1 texture files", "*layer_1*.png"));
			if (f1a != null && f1a.length > 0)
				f1 = f1a[0];
			else
				f1 = null;
			if (f1 != null)
				p1.setText(FilenameUtilsPatched.removeExtension(
						f1.getName().toLowerCase(Locale.ENGLISH).replace("layer_1", "")) + " P1");
		});
		p2.addActionListener(event -> {
			File[] f2a = FileDialogs.getFileChooserDialog(mcreator, FileChooserType.OPEN, false, null,
					new FileChooser.ExtensionFilter("Armor layer 2 texture files", "*layer_2*.png"));
			if (f2a != null && f2a.length > 0)
				f2 = f2a[0];
			else
				f2 = null;
			if (f2 != null)
				p2.setText(FilenameUtilsPatched.removeExtension(
						f2.getName().toLowerCase(Locale.ENGLISH).replace("layer_2", "")) + " P2");
		});
		od.add("Center", neno);

		int ret = JOptionPane.showConfirmDialog(mcreator, od, L10N.t("dialog.textures_import.import_armor_texture"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (ret == JOptionPane.OK_OPTION)
			if (f1 == null || f2 == null) {
				JOptionPane.showMessageDialog(mcreator,
						L10N.t("dialog.textures_import.error_both_texture_files_not_selected"), null,
						JOptionPane.ERROR_MESSAGE);
			} else {
				String namec = RegistryNameFixer.fix(
						FilenameUtilsPatched.removeExtension(f1.getName().replace("layer_1", "")));
				File[] armor = mcreator.getFolderManager().getArmorTextureFilesForName(namec);
				FileIO.copyFile(f1, armor[0]);
				FileIO.copyFile(f2, armor[1]);

				mcreator.mv.resourcesPan.workspacePanelTextures.reloadElements();
				if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI<?> modElementGUI)
					modElementGUI.reloadDataLists();
			}
	}

	/**
	 * <p>This method will open a file dialog to let the user select textures to import. Those textures then be saved as the provided {@link TextureType}.</p>
	 *
	 * @param mcreator <p>The instance of {@link MCreator} to use</p>
	 * @param type     <p>The texture type to use when importing texture files</p>
	 */
	public static void importMultipleTextures(MCreator mcreator, TextureType type) {
		File[] files = FileDialogs.getMultiOpenDialog(mcreator, new String[] { ".png" });
		if (files != null)
			saveTextures(mcreator, type, files);
	}

	/**
	 * <p>This is a general method to import a group of textures. It executes everything needed to save textures in the appropriate folder
	 * depending on the {@link TextureType}.</p>
	 *
	 * @param mcreator <p>The instance of {@link MCreator} to use</p>
	 * @param type     <p>The texture type to use when saving texture files</p>
	 * @param textures <p>Textures file to import</p>
	 */
	public static void saveTextures(MCreator mcreator, TextureType type, File[] textures) {
		Arrays.stream(textures).forEach(textureFile -> {
			String namec = RegistryNameFixer.fix(FilenameUtilsPatched.removeExtension(textureFile.getName()));
			File file = mcreator.getFolderManager().getTextureFile(namec, type);
			while (file.isFile()) {
				String name = JOptionPane.showInputDialog(mcreator,
						L10N.t("dialog.textures_import.error_texture_already_exists", namec),
						L10N.t("dialog.textures_import.error_texture_import_title"), JOptionPane.WARNING_MESSAGE);
				if (name != null) {
					namec = RegistryNameFixer.fix(FilenameUtilsPatched.removeExtension(name));
					file = mcreator.getFolderManager().getTextureFile(namec, type);
				} else {
					return;
				}
			}
			FileIO.copyFile(textureFile, file);
		});

		mcreator.mv.resourcesPan.workspacePanelTextures.reloadElements();
		if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI<?> modElementGUI)
			modElementGUI.reloadDataLists();
	}

}
