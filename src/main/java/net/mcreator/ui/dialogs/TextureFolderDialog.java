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

import net.mcreator.generator.GeneratorUtils;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ResourceNameValidator;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static net.mcreator.ui.dialogs.GeneralTextureSelector.*;

public class TextureFolderDialog{

	public static void setTextureFolder(MCreator mcreator, File file, TextureType type) {
		File originalFile = file;
		JPanel ui = new JPanel(new GridLayout(1, 2, 4, 4));

		VTextField folderName = new VTextField(15);

		folderName.setValidator(new ResourceNameValidator(folderName, "Folder name"));
		folderName.enableRealtimeValidation();
		ui.add(new JLabel("<html>Folder name for " + "<b>" + file.getName().replace(".png", "")
				+ "</b><br><small>Leave blank to use the main default folder."));
		ui.add(folderName);

		int option = JOptionPane
				.showOptionDialog(mcreator, ui, "Import texture", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
						null,
						new String[] { "Add texture", "Cancel" },
						"Save changes");

		if (option == 0) {
			switch (type) {
				case ARMOR: {
					if(!folderName.getText().isEmpty()) {
						File folder = new File(
								mcreator.getWorkspace().getFolderManager().getArmorTexturesDir() + "\\" + folderName
										.getText() + "\\");
						if (!folder.exists())
							folder.mkdirs();
					}

					String path = GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(),
							mcreator.getWorkspace().getGenerator().getGeneratorConfiguration(), "armor_textures_dir")
							.getPath() + "\\";

					if(!folderName.getText().isEmpty())
						path = path + folderName.getText() + "\\";

					File finalFile = new File(path + originalFile.getName());
					try {
						FileUtils.copyFile(originalFile, finalFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				case BLOCK: {
					if(!folderName.getText().isEmpty()) {
						File folder = new File(
								mcreator.getWorkspace().getFolderManager().getBlocksTexturesDir() + "\\" + folderName
										.getText() + "\\");
						if (!folder.exists())
							folder.mkdirs();
					}

					String path = GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(),
							mcreator.getWorkspace().getGenerator().getGeneratorConfiguration(), "block_textures_dir")
							.getPath() + "\\";

					if(!folderName.getText().isEmpty())
						path = path + folderName.getText() + "\\";

					File finalFile = new File(path + originalFile.getName());
					try {
						FileUtils.copyFile(originalFile, finalFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				case ENTITY: {
					if(!folderName.getText().isEmpty()) {
						File folder = new File(
								mcreator.getWorkspace().getFolderManager().getEntitiesTexturesDir() + "\\" + folderName
										.getText() + "\\");
						if (!folder.exists())
							folder.mkdirs();
					}

					String path = GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(),
							mcreator.getWorkspace().getGenerator().getGeneratorConfiguration(), "entity_textures_dir")
							.getPath() + "\\";

					if(!folderName.getText().isEmpty())
						path = path + folderName.getText() + "\\";

					File finalFile = new File(path + originalFile.getName());
					try {
						FileUtils.copyFile(originalFile, finalFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				case PAINTING: {
					if(!folderName.getText().isEmpty()) {
						File folder = new File(
								mcreator.getWorkspace().getFolderManager().getPaintingsTexturesDir() + "\\" + folderName
										.getText() + "\\");
						if (!folder.exists())
							folder.mkdirs();
					}

					String path = GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(),
							mcreator.getWorkspace().getGenerator().getGeneratorConfiguration(), "painting_textures_dir")
							.getPath() + "\\";

					if(!folderName.getText().isEmpty())
						path = path + folderName.getText() + "\\";

					File finalFile = new File(path + originalFile.getName());
					try {
						FileUtils.copyFile(originalFile, finalFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				case ITEM: {
					if(!folderName.getText().isEmpty()) {
						File folder = new File(
								mcreator.getWorkspace().getFolderManager().getItemsTexturesDir() + "\\" + folderName
										.getText() + "\\");
						if (!folder.exists())
							folder.mkdirs();
					}

					String path = GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(),
							mcreator.getWorkspace().getGenerator().getGeneratorConfiguration(), "item_textures_dir")
							.getPath() + "\\";

					if(!folderName.getText().isEmpty())
						path = path + folderName.getText() + "\\";

					File finalFile = new File(path + originalFile.getName());
					try {
						FileUtils.copyFile(originalFile, finalFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			case OTHER: {
				if(!folderName.getText().isEmpty()) {
					File folder = new File(
							mcreator.getWorkspace().getFolderManager().getOtherTexturesDir() + "\\" + folderName
									.getText() + "\\");
					if (!folder.exists())
						folder.mkdirs();
				}

				String path = GeneratorUtils.getSpecificRoot(mcreator.getWorkspace(),
						mcreator.getWorkspace().getGenerator().getGeneratorConfiguration(), "other_textures_dir")
						.getPath() + "\\";

				if(!folderName.getText().isEmpty())
					path = path + folderName.getText() + "\\";

				File finalFile = new File(path + originalFile.getName());
				try {
					FileUtils.copyFile(originalFile, finalFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
				}
		} else {
			try {
				FileUtils.copyFile(originalFile, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
