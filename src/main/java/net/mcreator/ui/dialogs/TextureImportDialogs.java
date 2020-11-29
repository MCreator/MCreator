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

import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

import static net.mcreator.ui.dialogs.GeneralTextureSelector.*;

public class TextureImportDialogs {

	private static File f1, f2;

	public static void importTextureGeneral(final MCreator mcreator, File file, String message) {
		Object[] options = { "Block", "Entity", "Item", "Other", "Painting" };
		int n = JOptionPane.showOptionDialog(mcreator, message, L10N.t("dialog.textures_import.texture_type"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 0) {
			TextureImportDialogs.importTexturesGeneral(mcreator, TextureType.BLOCK, new File[] { file });
		} else if (n == 1){
			TextureImportDialogs.importTexturesGeneral(mcreator, TextureType.ENTITY, new File[] {file});
		} else if (n == 2) {
			TextureImportDialogs.importTexturesGeneral(mcreator, TextureType.ITEM, new File[] { file });
		} else if (n == 3) {
			TextureImportDialogs.importTexturesGeneral(mcreator, TextureType.OTHER, new File[] { file });
		} else if (n == 4){
			TextureImportDialogs.importTexturesGeneral(mcreator, TextureType.PAINTING, new File[] {file});
		}
	}

	public static void importArmor(final MCreator fra) {
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
			File[] f1a = FileDialogs.getFileChooserDialog(fra, null, FileDialogs.FileChooserType.OPEN, false,
					new javax.swing.filechooser.FileFilter() {
						@Override public boolean accept(File f) {
							return (f.getName().toLowerCase(Locale.ENGLISH).endsWith(".png") && f.getName()
									.toLowerCase(Locale.ENGLISH).contains("layer_1")) || f.isDirectory();
						}

						@Override public String getDescription() {
							return "Armor layer 1 texture files (*layer_1*.png)";
						}
					});
			if (f1a != null && f1a.length > 0)
				f1 = f1a[0];
			else
				f1 = null;
			if (f1 != null)
				p1.setText(
						FilenameUtils.removeExtension(f1.getName().toLowerCase(Locale.ENGLISH).replace("layer_1", ""))
								+ " P1");
		});
		p2.addActionListener(event -> {
			File[] f2a = FileDialogs.getFileChooserDialog(fra, null, FileDialogs.FileChooserType.OPEN, false,
					new javax.swing.filechooser.FileFilter() {
						@Override public boolean accept(File f) {
							return (f.getName().toLowerCase(Locale.ENGLISH).endsWith(".png") && f.getName()
									.toLowerCase(Locale.ENGLISH).contains("layer_2")) || f.isDirectory();
						}

						@Override public String getDescription() {
							return "Armor layer 2 texture files (*layer_2*.png)";
						}
					});
			if (f2a != null && f2a.length > 0)
				f2 = f2a[0];
			else
				f2 = null;
			if (f2 != null)
				p2.setText(
						FilenameUtils.removeExtension(f2.getName().toLowerCase(Locale.ENGLISH).replace("layer_2", ""))
								+ " P2");
		});
		od.add("Center", neno);

		int ret = JOptionPane.showConfirmDialog(fra, od, L10N.t("dialog.textures_import.import_armor_texture"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null);
		if (ret == JOptionPane.OK_OPTION)
			if (f1 == null || f2 == null) {
				JOptionPane
						.showMessageDialog(fra, L10N.t("dialog.textures_import.error_both_texture_files_not_selected"), null,
								JOptionPane.ERROR_MESSAGE);
			} else {
				String namec = RegistryNameFixer
						.fix(FilenameUtils.removeExtension(f1.getName().replace("layer_1", "")));
				File[] armor = fra.getWorkspace().getFolderManager().getArmorTextureFilesForName(namec);
				FileIO.copyFile(f1, armor[0]);
				FileIO.copyFile(f2, armor[1]);
			}
	}

	public static void importTexturesBlockOrItem(MCreator fr, BlockItemTextureSelector.TextureType type) {
		File[] hohe = FileDialogs.getMultiOpenDialog(fr, new String[] { ".png" });
		if (hohe != null)
			importTexturesBlockOrItem(fr, type, hohe);
	}

	public static void importTexturesBlockOrItem(MCreator fr, BlockItemTextureSelector.TextureType type, File[] hohe) {
		Arrays.stream(hohe).forEach(hoh -> {
			String namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(hoh.getName()));
			File file;
			if (type == BlockItemTextureSelector.TextureType.BLOCK) {
				file = fr.getWorkspace().getFolderManager().getBlockTextureFile(namec);
			} else {
				file = fr.getWorkspace().getFolderManager().getItemTextureFile(namec);
			}
			if (file.isFile()) {
				String name = JOptionPane.showInputDialog(fr,
						L10N.t("dialog.textures_import.error_texture_already_exists", namec),
						L10N.t("dialog.textures_import.error_texture_import_title"), JOptionPane.WARNING_MESSAGE);
				if (name != null) {
					namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(name));
					if (type == BlockItemTextureSelector.TextureType.BLOCK) {
						file = fr.getWorkspace().getFolderManager().getBlockTextureFile(namec);
					} else {
						file = fr.getWorkspace().getFolderManager().getItemTextureFile(namec);
					}
				} else {
					return;
				}
			}
			FileIO.copyFile(hoh, file);
			fr.mv.resourcesPan.workspacePanelTextures.reloadElements();
		});
	}

	public static void importTexturesGeneral(MCreator fr, TextureType type) {
		File[] hohe = FileDialogs.getMultiOpenDialog(fr, new String[] { ".png" });
		if (hohe != null)
			importTexturesGeneral(fr, type, hohe);
	}

	public static void importTexturesGeneral(MCreator fr, TextureType type, File[] hohe) {
		Arrays.stream(hohe).forEach(hoh -> {
			TextureFolderDialog.setTextureFolder(fr, hoh, type);
			fr.mv.resourcesPan.workspacePanelTextures.reloadElements();
		});
	}

	public static void importTexturesGeneral(MCreator fr, BlockItemTextureSelector.TextureType type, File[] hohe) {
		Arrays.stream(hohe).forEach(hoh -> {
			String namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(hoh.getName()));
			File file;
			if (type == BlockItemTextureSelector.TextureType.BLOCK) {
				file = fr.getWorkspace().getFolderManager().getBlockTextureFile(namec);
			} else {
				file = fr.getWorkspace().getFolderManager().getItemTextureFile(namec);
			}
			if (file.isFile()) {
				String name = JOptionPane.showInputDialog(fr,
						L10N.t("dialog.textures_import.error_texture_already_exists", namec),
						L10N.t("dialog.textures_import.error_texture_import_title"), JOptionPane.WARNING_MESSAGE);
				if (name != null) {
					namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(name));
					if (type == BlockItemTextureSelector.TextureType.BLOCK) {
						file = fr.getWorkspace().getFolderManager().getBlockTextureFile(namec);
					} else {
						file = fr.getWorkspace().getFolderManager().getItemTextureFile(namec);
					}
				} else {
					return;
				}
			}
			FileIO.copyFile(hoh, file);
			fr.mv.resourcesPan.workspacePanelTextures.reloadElements();
		});
	}

	public static void importOtherTextures(MCreator fr) {
		File[] hohs = FileDialogs.getMultiOpenDialog(fr, new String[] { ".png" });
		if (hohs != null)
			importOtherTextures(fr, hohs);
	}

	public static void importOtherTextures(MCreator fr, File[] hohs) {
		Arrays.stream(hohs).forEach(hoh -> {
			String namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(hoh.getName()));
			File file = fr.getWorkspace().getFolderManager().getOtherTextureFile(namec);
			if (file.isFile()) {
				String name = JOptionPane.showInputDialog(fr,
						L10N.t("dialog.textures_import.error_texture_already_exists", namec),
						L10N.t("dialog.textures_import.error_texture_import_title"), JOptionPane.WARNING_MESSAGE);
				if (name != null) {
					namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(name));
					file = fr.getWorkspace().getFolderManager().getOtherTextureFile(namec);
				} else {
					return;
				}
			}
			FileIO.copyFile(hoh, file);
			fr.mv.resourcesPan.workspacePanelTextures.reloadElements();
		});
	}

}
