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

package net.mcreator.preferences.entry;

public class StringEntry extends PreferenceEntry<String> {

	private final String[] choices;
	private final boolean editable;


	public StringEntry(String id, String value, PreferenceSection section, String... choices) {
		this(id, value, section, false, choices);
	}

	public StringEntry(String id, String value, PreferenceSection section, boolean editable, String... choices) {
		super(id, value, section);
		this.choices = choices;
		this.editable = editable;
	}

	public String[] getChoices() {
		return choices;
	}

	public boolean isEditable() {
		return editable;
	}
}
