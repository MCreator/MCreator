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
import net.mcreator.ui.component.JColor;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

public class ColorEntry extends PreferencesEntry<Color> {

	private final transient boolean allowNullColor, allowTransparency;

	public ColorEntry(String id, Color value) {
		this(id, value, false, false);
	}

	public ColorEntry(String id, Color value, boolean allowNullColor, boolean allowTransparency) {
		super(id, value);
		this.allowNullColor = allowNullColor;
		this.allowTransparency = allowTransparency;
	}

	@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		JColor box = new JColor(parent, allowNullColor, allowTransparency);
		box.setColor(value);
		box.addColorSelectedListener(fct::accept);
		return box;
	}

	@Override public void setValueFromComponent(JComponent component) {
		this.value = ((JColor) component).getColor();
	}

	@Override public void setValueFromJsonElement(JsonElement object) {
		this.value = PreferencesManager.gson.fromJson(object, Color.class);
	}

	@Override public JsonElement getSerializedValue() {
		return PreferencesManager.gson.toJsonTree(value, Color.class);
	}

}
