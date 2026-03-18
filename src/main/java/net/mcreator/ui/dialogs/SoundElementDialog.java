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

import net.mcreator.element.types.Biome;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.SingleFileField;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.sounds.JSoundList;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ResourceNameValidator;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.SoundElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SoundElementDialog {

	public static SoundElement soundDialog(MCreator mcreator, @Nullable SoundElement element, @Nullable File[] files) {
		if (mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getGeneratorFlavor().getGamePlatform()
				== GeneratorFlavor.GamePlatform.JAVAEDITION) {
			JPanel ui = new JPanel(new GridLayout(2, 2, 10, 2));

			VTextField soundName = new VTextField(26);
			soundName.setValidator(new ResourceNameValidator(soundName, L10N.t("dialog.sounds.name")));
			soundName.enableRealtimeValidation();

			JTextField subtitle = new JTextField();

			ui.add(L10N.label("dialog.sounds.registry_name"));
			ui.add(soundName);

			ui.add(L10N.label("dialog.sounds.subtitle"));
			ui.add(subtitle);

			JPanel pane1 = new JPanel(new BorderLayout());

			JSoundList soundsEntries = new JSoundList(mcreator, IHelpContext.NONE);

			JComponent component = PanelUtils.northAndCenterElement(L10N.label("dialog.sounds.declarations"),
					soundsEntries);

			component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			pane1.add(component, BorderLayout.CENTER);
			pane1.setOpaque(false);

			if (element == null && files != null) {
				List<SoundElement.Sound> sounds = new ArrayList<>();
				Arrays.stream(files).filter(Objects::nonNull).filter(e -> e.getName().endsWith(".ogg")).toList()
						.forEach(file -> sounds.add(new SoundElement.Sound(file.getName())));
				soundsEntries.setEntries(sounds);
			}

			if (element != null) {
				soundName.setText(element.getName());
				soundName.setEnabled(false);
				subtitle.setText(element.getSubtitle());
				soundsEntries.setEntries(element.getFiles());
			}

			int option = JOptionPane.showOptionDialog(mcreator, PanelUtils.northAndCenterElement(ui, pane1),
					L10N.t("dialog.sounds.edit"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					element != null ? new String[] { "Save changes" } : new String[] { "Add sound", "Cancel" },
					element != null ? "Save changes" : "Add sound");

			if (option == 0) {
				if (soundName.getValidationStatus().type() == ValidationResult.Type.ERROR) {
					JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.sounds.error_name_not_valid"),
							L10N.t("dialog.sounds.error_name_not_valid_title"), JOptionPane.ERROR_MESSAGE);
					return element;
				} else {
					if (!soundsEntries.areFilesValid()) {
						JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.sounds.error_select_valid_file"),
								L10N.t("dialog.sounds.error_select_valid_file_title"), JOptionPane.ERROR_MESSAGE);
						return element;
					} else {
						List<SoundElement.Sound> sounds = soundsEntries.getEntries();
						List<SingleFileField> entriesFiles = soundsEntries.getFiles();

						for (int i = 0; i < entriesFiles.size(); i++) {
							SingleFileField field = entriesFiles.get(i);

							if (!field.isEnabled())
								continue;

							File file = field.getEntry();
							FileIO.copyFile(file, new File(mcreator.getFolderManager().getSoundsDir(),
									RegistryNameFixer.fix(file.getName())));

							sounds.get(i).setName(FilenameUtilsPatched.removeExtension(
									RegistryNameFixer.fix(sounds.get(i).getName())));
						}

						String registryname = RegistryNameFixer.fix(soundName.getText());

						mcreator.getWorkspace().setLocalization("subtitles." + registryname, subtitle.getText());

						if (element != null) {
							element.setSubtitle(subtitle.getText());
							element.setFiles(sounds);
							return element;
						}

						return new SoundElement(registryname, sounds, subtitle.getText());
					}
				}
			} else {
				return element;
			}
		} else {
			JPanel ui = new JPanel(new GridLayout(4, 2, 10, 2));

			VTextField soundName = new VTextField(20);
			JComboBox<String> soundCategory = new JComboBox<>(ElementUtil.getDataListAsStringArray("soundcategories"));
			JMinMaxSpinner jMinMaxSpinner = new JMinMaxSpinner(0, 0, 0, 64000.0, 1.0).allowEqualValues();
			soundName.setValidator(new ResourceNameValidator(soundName, L10N.t("dialog.sounds.name")));
			soundName.enableRealtimeValidation();
			soundCategory.addActionListener(
					e -> jMinMaxSpinner.setEnabled(!soundCategory.getSelectedItem().equals("ui")));

			JTextField subtitle = new JTextField();

			ui.add(L10N.label("dialog.sounds.registry_name"));
			ui.add(soundName);

			ui.add(L10N.label("dialog.sounds.category"));
			ui.add(soundCategory);

			ui.add(L10N.label("dialog.sounds.attenuation_distance"));
			ui.add(jMinMaxSpinner);

			ui.add(L10N.label("dialog.sounds.subtitle"));
			ui.add(subtitle);

			JPanel pane1 = new JPanel(new BorderLayout());

			JSoundList soundsEntries = new JSoundList(mcreator, IHelpContext.NONE, true);

			JComponent component = PanelUtils.northAndCenterElement(L10N.label("dialog.sounds.declarations"),
					soundsEntries);

			component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			pane1.add(component, BorderLayout.CENTER);
			pane1.setOpaque(false);

			if (element == null && files != null) {
				List<SoundElement.Sound> sounds = new ArrayList<>();
				Arrays.stream(files).filter(Objects::nonNull).filter(e -> e.getName().endsWith(".ogg")).toList()
						.forEach(file -> sounds.add(new SoundElement.Sound(file.getName())));
				soundsEntries.setEntries(sounds);
			}

			if (element != null) {
				soundName.setText(element.getName());
				soundName.setEnabled(false);
				soundCategory.setSelectedItem(element.getBECategory());
				jMinMaxSpinner.setMinValue(element.getBEAttenuationDistance().min);
				jMinMaxSpinner.setMaxValue(element.getBEAttenuationDistance().max);
				subtitle.setText(element.getSubtitle());
				soundsEntries.setEntries(element.getFiles());
			}

			int option = JOptionPane.showOptionDialog(mcreator, PanelUtils.northAndCenterElement(ui, pane1),
					L10N.t("dialog.sounds.edit"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					element != null ? new String[] { "Save changes" } : new String[] { "Add sound", "Cancel" },
					element != null ? "Save changes" : "Add sound");

			if (option == 0) {
				if (soundName.getValidationStatus().type() == ValidationResult.Type.ERROR) {
					JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.sounds.error_name_not_valid"),
							L10N.t("dialog.sounds.error_name_not_valid_title"), JOptionPane.ERROR_MESSAGE);
					return element;
				} else {
					if (!soundsEntries.areFilesValid()) {
						JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.sounds.error_select_valid_file"),
								L10N.t("dialog.sounds.error_select_valid_file_title"), JOptionPane.ERROR_MESSAGE);
						return element;
					} else {
						List<SoundElement.Sound> sounds = soundsEntries.getEntries();
						List<SingleFileField> entriesFiles = soundsEntries.getFiles();

						for (int i = 0; i < entriesFiles.size(); i++) {
							SingleFileField field = entriesFiles.get(i);

							if (!field.isEnabled())
								continue;

							File file = field.getEntry();
							FileIO.copyFile(file, new File(mcreator.getFolderManager().getSoundsDir(),
									RegistryNameFixer.fix(file.getName())));

							sounds.get(i).setName(FilenameUtilsPatched.removeExtension(
									RegistryNameFixer.fix(sounds.get(i).getName())));
						}

						String registryname = RegistryNameFixer.fix(soundName.getText());

						mcreator.getWorkspace().setLocalization("subtitles." + registryname, subtitle.getText());

						if (element != null) {
							element.setBECategory((String) soundCategory.getSelectedItem());
							element.setBEAttenuationDistance(
									new Biome.ClimatePoint(jMinMaxSpinner.getMinValue(), jMinMaxSpinner.getMaxValue()));
							element.setSubtitle(subtitle.getText());
							element.setFiles(sounds);
							return element;
						}

						return new SoundElement(registryname, (String) soundCategory.getSelectedItem(),
								new Biome.ClimatePoint(jMinMaxSpinner.getMinValue(), jMinMaxSpinner.getMaxValue()),
								sounds, subtitle.getText());
					}
				}
			} else {
				return element;
			}
		}
	}

	public static void importSound(MCreator mcreator) {
		SoundElement soundElement = soundDialog(mcreator, null, null);
		if (soundElement != null) {
			mcreator.getWorkspace().addSoundElement(soundElement);
			mcreator.reloadWorkspaceTabContents();
		}
	}

	public static void importSound(MCreator mcreator, File[] musics) {
		SoundElement soundElement = soundDialog(mcreator, null, musics);
		if (soundElement != null) {
			mcreator.getWorkspace().addSoundElement(soundElement);
			mcreator.reloadWorkspaceTabContents();
		}
	}

}
