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

package net.mcreator.ui.minecraft.sounds;

import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SingleFileField;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JSoundListEntry extends JSimpleListEntry<SoundElement.Sound> {

	private final MCreator mcreator;
	private final boolean isForBedrock;

	private final JComboBox<String> soundCategory = new JComboBox<>(
			ElementUtil.getDataListAsStringArray("soundcategories"));
	private final JSpinner volume = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 128000.0, 0.1));
	private final JSpinner pitch = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 128000.0, 0.1));
	private final JSpinner weight = new JSpinner(new SpinnerNumberModel(1, 1, 128000, 1));
	private final JCheckBox preload = L10N.checkbox("dialog.sounds.preload");
	private final JSpinner attenuationDistance = new JSpinner(new SpinnerNumberModel(16, 1, 128000, 1));
	private final SingleFileField fileListField;

	private final JCheckBox is3D = L10N.checkbox("dialog.sounds.preload");
	private final JCheckBox interruptible = L10N.checkbox("dialog.sounds.preload");

	public JSoundListEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JSoundListEntry> entryList,
			boolean isForBedrock) {
		super(parent, entryList);

		this.mcreator = mcreator;
		this.isForBedrock = isForBedrock;
		this.fileListField = new SingleFileField(mcreator);
		preload.setOpaque(false);

		line.add(L10N.label("dialog.sounds.file"));
		line.add(fileListField);

		line.add(L10N.label("dialog.sounds.category"));
		line.add(soundCategory);

		line.add(L10N.label("dialog.sounds.volume"));
		line.add(volume);

		line.add(L10N.label("dialog.sounds.pitch"));
		line.add(pitch);

		line.add(L10N.label("dialog.sounds.weight"));
		line.add(weight);

		if (!isForBedrock) {
			line.add(preload);

			line.add(L10N.label("dialog.sounds.attenuation_distance"));
			line.add(attenuationDistance);
		} else {
			line.add(is3D);
			line.add(interruptible);
			is3D.setSelected(true);
			interruptible.setSelected(true);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(soundCategory,
				Arrays.stream(ElementUtil.getDataListAsStringArray("soundcategories")).collect(Collectors.toList()),
				"neutral");
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		fileListField.setEnabled(enabled);
		soundCategory.setEnabled(enabled);
		volume.setEnabled(enabled);
		pitch.setEnabled(enabled);
		weight.setEnabled(enabled);
		if (!isForBedrock) {
			preload.setEnabled(enabled);
			attenuationDistance.setEnabled(enabled);
		} else {
			is3D.setEnabled(enabled);
			interruptible.setEnabled(enabled);
		}
	}

	@Override public SoundElement.Sound getEntry() {
		SoundElement.Sound entry = new SoundElement.Sound(fileListField.getEntry().getName());
		entry.setCategory((String) soundCategory.getSelectedItem());
		entry.setVolume((double) volume.getValue());
		entry.setPitch((double) pitch.getValue());
		entry.setWeight((int) attenuationDistance.getValue());
		if (!isForBedrock) {
			entry.setPreload(preload.isSelected());
			entry.setAttenuationDistance((int) attenuationDistance.getValue());
		} else {
			entry.setBEIs3D(is3D.isSelected());
			entry.setBEInterruptible(interruptible.isSelected());
		}
		return entry;
	}

	@Override public void setEntry(SoundElement.Sound e) {
		fileListField.setEntry(new File(mcreator.getFolderManager().getSoundsDir(), e.toString()));
		fileListField.setEnabled(false);
		soundCategory.setSelectedItem(e.getCategory());
		volume.setValue((double) e.getVolume());
		pitch.setValue((double) e.getPitch());
		weight.setValue(e.getAttenuationDistance());
		if (!isForBedrock) {
			preload.setSelected(e.isPreload());
			attenuationDistance.setValue(e.getAttenuationDistance());
		} else {
			is3D.setSelected(e.isBEIs3D());
			interruptible.setSelected(e.isBEInterruptible());
		}
	}

	public SingleFileField getFileListField() {
		return fileListField;
	}
}
