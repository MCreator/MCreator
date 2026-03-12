package net.mcreator.ui.minecraft.sounds;

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
	private final boolean isForBedrock;

	public JSoundList(MCreator mcreator, IHelpContext gui, boolean isForBedrock) {
		super(mcreator, gui);

		this.isForBedrock = isForBedrock;

		add.setText(L10N.t("dialog.sounds.add_entry"));

		ComponentUtils.makeSection(this, L10N.t("dialog.sounds.spawn_entries"));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.2)));
	}

	public JSoundList(MCreator mcreator, IHelpContext gui) {
		this(mcreator, gui, false);
	}

	@Override protected JSoundListEntry newEntry(JPanel parent, List<JSoundListEntry> entryList, boolean userAction) {
		return new JSoundListEntry(mcreator, gui, parent, entryList, isForBedrock);
	}

	public boolean areFilesValid() {
		for (JSoundListEntry entry : entryList) {
			if (entry.getFileListField().isEmpty())
				return false;
		}

		return true;
	}

	public List<SingleFileField> getFiles() {
		List<SingleFileField> files = new ArrayList<>();
		entryList.forEach(entry -> files.add(entry.getFileListField()));
		return files;
	}
}