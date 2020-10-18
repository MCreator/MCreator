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
import net.mcreator.ui.modgui.ModElementGUI;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class TextureImportDialogs {

	private static File f1, f2;

	public static void importTextureGeneral(final MCreator mcreator, File file, String message) {
		Object[] options = { "Block", "Item", "Other" };
		int n = JOptionPane.showOptionDialog(mcreator, message, "Texture type", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 0) {
			TextureImportDialogs.importTexturesBlockItemOrEntity(mcreator, BlockItemTextureSelector.TextureType.BLOCK, new File[] { file });
		} else if (n == 1) {
			TextureImportDialogs.importTexturesBlockItemOrEntity(mcreator, BlockItemTextureSelector.TextureType.ITEM, new File[] { file });
		} else if (n == 2) {
			TextureImportDialogs.importOtherTextures(mcreator, new File[] { file });
		}
	}

	public static void importArmor(final MCreator fra) {
		JPanel od = new JPanel(new BorderLayout());
		JPanel neno = new JPanel(new GridLayout(3, 2, 4, 4));
		JButton p1 = new JButton("...");
		JButton p2 = new JButton("...");
		neno.add(new JLabel("Armor layer texture needs two files:"));
		neno.add(new JLabel("*layer_1*.png and *layer_2*.png"));
		neno.add(new JLabel("Armor texture part 1 (*layer_1*.png):"));
		neno.add(p1);
		neno.add(new JLabel("Armor texture part 2 (*layer_2*.png):"));
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

		int ret = JOptionPane.showConfirmDialog(fra, od, "Import armor texture", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null);
		if (ret == JOptionPane.OK_OPTION)
			if (f1 == null || f2 == null) {
				JOptionPane
						.showMessageDialog(fra, "<html><font color=red>You haven't selected both texture files!", null,
								JOptionPane.ERROR_MESSAGE);
			} else {
				String namec = RegistryNameFixer
						.fix(FilenameUtils.removeExtension(f1.getName().replace("layer_1", "")));
				File[] armor = fra.getWorkspace().getFolderManager().getArmorTextureFilesForName(namec);
				FileIO.copyFile(f1, armor[0]);
				FileIO.copyFile(f2, armor[1]);
			}
	}

	public static void importTexturesBlockItemOrEntity(MCreator fr, BlockItemTextureSelector.TextureType type) {
		File[] hohe = FileDialogs.getMultiOpenDialog(fr, new String[] { ".png" });
		if (hohe != null)
			importTexturesBlockItemOrEntity(fr, type, hohe);
	}

	public static void importTexturesBlockItemOrEntity(MCreator fr, BlockItemTextureSelector.TextureType type, File[] hohe) {
		Arrays.stream(hohe).forEach(hoh -> {
			String namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(hoh.getName()));
			File file;
			if (type == BlockItemTextureSelector.TextureType.BLOCK) {
				file = fr.getWorkspace().getFolderManager().getBlockTextureFile(namec);
			} else if (type == BlockItemTextureSelector.TextureType.ENTITY){
				file = fr.getWorkspace().getFolderManager().getEntityTextureFile(namec);
			} else{
				file = fr.getWorkspace().getFolderManager().getItemTextureFile(namec);
			}
			if (file.isFile()) {
				String name = JOptionPane.showInputDialog(fr, "<html>Texture " + namec + " already exists!<br>"
						+ "You can enter a new name or cancel the import", "Import error", JOptionPane.WARNING_MESSAGE);
				if (name != null) {
					namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(name));
					if (type == BlockItemTextureSelector.TextureType.BLOCK) {
						file = fr.getWorkspace().getFolderManager().getBlockTextureFile(namec);
					} else if (type == BlockItemTextureSelector.TextureType.ITEM){
						file = fr.getWorkspace().getFolderManager().getItemTextureFile(namec);
					} else if (type == BlockItemTextureSelector.TextureType.ENTITY){
						file = fr.getWorkspace().getFolderManager().getEntityTextureFile(namec);
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
				String name = JOptionPane.showInputDialog(fr, "<html>Texture " + namec + " already exists!<br>"
						+ "You can enter a new name or cancel the import", "Import error", JOptionPane.WARNING_MESSAGE);
				if (name != null) {
					namec = RegistryNameFixer.fix(FilenameUtils.removeExtension(name));
					file = fr.getWorkspace().getFolderManager().getOtherTextureFile(namec);
				} else {
					return;
				}
			}
			FileIO.copyFile(hoh, file);
			fr.mv.resourcesPan.workspacePanelTextures.reloadElements();
			if (fr.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI)
				((ModElementGUI) fr.mcreatorTabs.getCurrentTab().getContent()).reloadDataLists();
		});
	}

}
