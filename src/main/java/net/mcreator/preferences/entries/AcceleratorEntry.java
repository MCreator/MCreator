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
import net.mcreator.ui.component.JAccelerator;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

public class AcceleratorEntry extends PreferencesEntry<KeyStroke> {

	public AcceleratorEntry(String id, KeyStroke value) {
		super(id, value);
	}

	@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		return new JAccelerator(value, fct::accept);
	}

	@Override public void setValueFromComponent(JComponent component) {
		this.value = ((JAccelerator) component).getKey();
	}

	@Override public void setValueFromJsonElement(JsonElement object) {
		this.value = PreferencesManager.gson.fromJson(object, KeyStroke.class);
	}

	@Override public JsonElement getSerializedValue() {
		return PreferencesManager.gson.toJsonTree(value, KeyStroke.class);
	}
}
