package net.mcreator.ui.minecraft.sounds;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SingleFileField;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

	public boolean areFilesValid() {
		for (JSoundListEntry entry : entryList) {
			if (entry.getSingleFileField().isEmpty())
				return false;
		}

		return true;
	}

	public List<SingleFileField> getFiles() {
		List<SingleFileField> files = new ArrayList<>();
		entryList.forEach(entry -> files.add(entry.getSingleFileField()));
		return files;
	}
}