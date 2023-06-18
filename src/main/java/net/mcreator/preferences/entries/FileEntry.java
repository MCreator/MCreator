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
import javafx.stage.FileChooser;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.component.JFileSelector;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.EventObject;
import java.util.Objects;
import java.util.function.Consumer;

public class FileEntry extends PreferencesEntry<File> {

	private final transient boolean isFolder;
	private final transient FileChooser.ExtensionFilter[] filters;
	private final transient boolean allowNullValue;

	/**
	 * The constructor used to add a new preference entry to select the location of a folder.
	 *
	 * @param id    The preference entry's ID
	 * @param value The default value of the entry
	 */
	public FileEntry(String id, File value, boolean allowNullValue) {
		super(id, value);
		this.isFolder = true;
		this.allowNullValue = allowNullValue;
		this.filters = new FileChooser.ExtensionFilter[] {};
	}

	/**
	 * The constructor used to add a new preference entry to select the location of a file
	 *
	 * @param id      The preference entry's ID
	 * @param value   The default value of the entry
	 * @param filters One or multiple filters to apply to the {@link FileChooser} dialog
	 */
	public FileEntry(String id, File value, boolean allowNullValue, FileChooser.ExtensionFilter... filters) {
		super(id, value);
		this.isFolder = false;
		this.allowNullValue = allowNullValue;
		this.filters = filters;
	}

	@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		if (isFolder)
			return new JFileSelector(parent, value, allowNullValue, fct);
		else
			return new JFileSelector(parent, value, allowNullValue, fct, filters);
	}

	@Override public void setValueFromComponent(JComponent component) {
		File file = ((JFileSelector) component).getFile();
		// we don't use null as the old value is kept
		this.value = Objects.requireNonNullElseGet(file, () -> new File("Not specified"));
	}

	@Override public void setValueFromJsonElement(JsonElement object) {
		this.value = new File(object.getAsJsonObject().get("path").getAsString());

	}

	@Override public JsonElement getSerializedValue() {
		return PreferencesManager.gson.toJsonTree(value, File.class);
	}
}
