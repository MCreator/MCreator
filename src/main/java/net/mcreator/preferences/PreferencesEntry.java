/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.preferences;

import com.google.gson.JsonElement;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * <p>This is the basic and the common class for all preferences of the software.
 * It stores everything the preference needs to have to be used or saved, such as an ID, its value or its {@link JComponent} for {@link PreferencesDialog}.
 * This is also the class used to save each preference inside the user's folder. However, only required (fields without transient) fields are saved.</p>
 *
 * @param <T> <p>The type of the stored value.</p>
 */
public abstract class PreferencesEntry<T> {

	private final String id;
	protected T value;

	private transient final T defaultValue;

	private transient PreferencesSection section;

	public PreferencesEntry(String id, T value) {
		this.id = id;
		this.value = value;
		this.defaultValue = value;
	}

	void setSection(PreferencesSection section) {
		this.section = section;
	}

	/**
	 * <p>Generate a {@link JComponent} for the {@link PreferencesDialog}, so users can change the value.</p>
	 *
	 * @param parent <p>The component's parent, which is the opened {@link PreferencesDialog}.</p>
	 * @param fct    <p>This is the {@link Consumer} used to enable the apply button when the value of the {@link JComponent} is changed.</p>
	 * @return <p>The {@link JComponent} to use inside the {@link PreferencesDialog} for all preference entries using the same type.</p>
	 */
	public abstract JComponent getComponent(Window parent, Consumer<EventObject> fct);

	public abstract void setValueFromComponent(JComponent component);

	public abstract void setValueFromJsonElement(JsonElement object);

	public abstract JsonElement getSerializedValue();

	public String getID() {
		return id;
	}

	public T get() {
		return value;
	}

	public void reset() {
		this.value = defaultValue;
	}

	public void set(T newValue) {
		this.value = newValue;
	}

	public String getSectionKey() {
		return section.getSectionKey();
	}

	public PreferencesSection getSection() {
		return section;
	}

	@Override public String toString() {
		return id + ": value: " + value;
	}

}
