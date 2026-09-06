package net.mcreator.ui.minecraft.sounds;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SingleFileField;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JSoundList extends JSimpleEntriesList<JSoundListEntry, SoundElement.Sound> {

	public JSoundList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui, false);

		add.setText(L10N.t("dialog.sounds.add_entry"));

		ComponentUtils.makeSection(this, L10N.t("dialog.sounds.spawn_entries"));
	}

	@Override protected JSoundListEntry newEntry(JPanel parent, List<JSoundListEntry> entryList, boolean userAction) {
		return new JSoundListEntry(mcreator, gui, parent, entryList,
				mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getGeneratorFlavor()
						== GeneratorFlavor.ADDON);
	}

	public List<SoundElement.Sound> getNewEntries() {
		return entryList.stream().map(e -> e.getSingleFileField().isEnabled() ? e.getEntry() : null)
				.filter(Objects::nonNull).toList();
	}

	public void addNewFiles(List<File> files) {
		List<SoundElement.Sound> sounds = new ArrayList<>();
		files.forEach(file -> sounds.add(new SoundElement.Sound(FilenameUtilsPatched.removeExtension(file.getName()))));
		setEntries(sounds);
		for (int i = 0; i < entryList.size(); i++) {
			SingleFileField singleFileField = entryList.get(i).getSingleFileField();

			singleFileField.setEnabled(true);
			singleFileField.setEntry(files.get(i));
		}
	}

	public boolean areNewEntriesValid() {
		if (entryList.isEmpty())
			return false;

		for (JSoundListEntry entry : entryList) {
			if (entry.getSingleFileField().isEnabled() && entry.getSingleFileField().isEmpty())
				return false;
		}

		return true;
	}

	public List<SingleFileField> getNewFiles() {
		return entryList.stream().map(e -> e.getSingleFileField().isEnabled() ? e.getSingleFileField() : null)
				.filter(Objects::nonNull).toList();
	}
}