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
import net.mcreator.ui.component.FileListField;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ResourceNameValidator;
import net.mcreator.workspace.elements.SoundElement;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SoundElementDialog {

	public static SoundElement soundDialog(MCreator mcreator, @Nullable SoundElement element, @Nullable File[] files) {
		JPanel ui = new JPanel(new GridLayout(4, 2, 10, 10));

		VTextField soundName = new VTextField(20);

		soundName.setValidator(new ResourceNameValidator(soundName, L10N.t("dialog.sounds.name")));
		soundName.enableRealtimeValidation();

		JTextField subtitle = new JTextField();

		FileListField fileListField = new FileListField(mcreator);

		if (element == null && files != null) {
			fileListField.setListElements(
					Arrays.stream(files).filter(Objects::nonNull).filter(e -> e.getName().endsWith(".ogg"))
							.collect(Collectors.toList()));
		}

		JComboBox<String> soundCategory = new JComboBox<>(
				new String[] { "master", "ambient", "player", "neutral", "hostile", "block", "record", "music" });

		ui.add(L10N.label("dialog.sounds.registry_name"));
		ui.add(soundName);

		ui.add(L10N.label("dialog.sounds.files"));
		if (element == null) {
			ui.add(fileListField);
		} else {
			ui.add(new JLabel(String.join(", ", element.getFiles())));
		}

		ui.add(L10N.label("dialog.sounds.category"));
		ui.add(soundCategory);

		ui.add(L10N.label("dialog.sounds.subtitle"));
		ui.add(subtitle);

		if (element != null) {
			soundCategory.setSelectedItem(element.getCategory());
			soundName.setText(element.getName());
			subtitle.setText(element.getSubtitle());
		}

		int option = JOptionPane
				.showOptionDialog(mcreator, ui, L10N.t("dialog.sounds.edit"), JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE, null,
						element != null ? new String[] { "Save changes" } : new String[] { "Add sound", "Cancel" },
						element != null ? "Save changes" : "Add sound");

		if (option == 0) {
			if (soundName.getValidationStatus().getValidationResultType() == Validator.ValidationResultType.ERROR) {
				JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.sounds.error_name_not_valid"),
						L10N.t("dialog.sounds.error_name_not_valid_title"), JOptionPane.ERROR_MESSAGE);
				return element;
			} else {
				if (element == null) { // new sound element
					if (fileListField.getListElements().isEmpty()) {
						JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.sounds.error_select_valid_file"),
								L10N.t("dialog.sounds.error_select_valid_file_title"), JOptionPane.ERROR_MESSAGE);
						return null;
					} else {
						List<String> fileNames = new ArrayList<>();

						fileListField.getListElements().forEach(file -> {
							String fileName = RegistryNameFixer.fix(file.getName());
							FileIO.copyFile(file, new File(mcreator.getFolderManager().getSoundsDir(), fileName));
							fileNames.add(FilenameUtils.removeExtension(fileName));
						});

						String registryname = RegistryNameFixer.fix(soundName.getText());

						mcreator.getWorkspace().setLocalization("subtitles." + registryname, subtitle.getText());

						return new SoundElement(registryname, fileNames, (String) soundCategory.getSelectedItem(),
								subtitle.getText());
					}
				} else { // existing sound element
					String registryname = RegistryNameFixer.fix(soundName.getText());

					mcreator.getWorkspace().setLocalization("subtitles." + registryname, subtitle.getText());

					return new SoundElement(registryname, element.getFiles(), (String) soundCategory.getSelectedItem(),
							subtitle.getText());
				}
			}
		} else {
			return element;
		}
	}

	public static void importSound(MCreator mcreator) {
		SoundElement soundElement = soundDialog(mcreator, null, null);
		if (soundElement != null) {
			mcreator.getWorkspace().addSoundElement(soundElement);
			mcreator.mv.resourcesPan.workspacePanelSounds.reloadElements();
		}
	}

	public static void importSound(MCreator mcreator, File[] musics) {
		SoundElement soundElement = soundDialog(mcreator, null, musics);
		if (soundElement != null) {
			mcreator.getWorkspace().addSoundElement(soundElement);
			mcreator.mv.resourcesPan.workspacePanelSounds.reloadElements();
		}
	}

}
