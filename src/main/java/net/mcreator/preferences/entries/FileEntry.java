/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.preferences.entries;

import com.google.gson.JsonElement;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.EventObject;
import java.util.function.Consumer;

public class FileEntry extends PreferencesEntry<File> {

	private final transient boolean isFolder;

	public FileEntry(String id, String path, boolean isFolder) {
		this(id, new File(path), isFolder);
	}

	public FileEntry(String id, File value, boolean isFolder) {
		super(id, value);
		this.isFolder = isFolder;
	}

	@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		String path = StringUtils.abbreviateStringInverse(value.getAbsolutePath(), 35);
		JButton button = new JButton(path);
		button.setToolTipText(path);
		button.addActionListener(actionEvent -> {
			File file = FileDialogs.getDirectoryChooser(new File(button.getText()));
			if (file != null && file.exists()) {
				if ((isFolder && file.isDirectory()) || (!isFolder && file.isFile())) {
					fct.accept(actionEvent);
					button.setText(StringUtils.abbreviateStringInverse(file.getAbsolutePath(), 35));
					button.setToolTipText(file.getAbsolutePath());
				}
			}
		});

		return button;
	}

	@Override public void setValueFromComponent(JComponent component) {
		this.value = new File(component.getToolTipText());
	}

	@Override public void setValueFromJsonElement(JsonElement object) {
		this.value = new File(object.getAsJsonObject().get("path").getAsString());

	}

	@Override public JsonElement getSerializedValue() {
		return PreferencesManager.gson.toJsonTree(value, File.class);
	}
}
