package net.mcreator.ui.minecraft.sounds;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SingleFileField;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.SoundElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JSoundsEntriesList extends JSimpleEntriesList<JSoundListEntry, SoundElement.Sound> {
	public JSoundsEntriesList(MCreator mcreator) {
		super(mcreator, null);

		add.setText(L10N.t("dialog.sounds.add_entry"));

		ComponentUtils.makeSection(this, L10N.t("dialog.sounds.spawn_entries"));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.2)));
	}

	@Override protected JSoundListEntry newEntry(JPanel parent, List<JSoundListEntry> entryList, boolean userAction) {
		return new JSoundListEntry(mcreator, parent, entryList);
	}

	public List<SingleFileField> getFiles() {
		List<SingleFileField> files = new ArrayList<>();
		entryList.forEach(entry -> files.add(entry.getFileListField()));
		return files;
	}
}