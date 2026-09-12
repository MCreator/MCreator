/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.ide.autocomplete;

import org.fife.rsta.ac.java.IconFactory;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import javax.swing.Icon;

public class CustomVariableCompletion extends BasicCompletion {
	private final String name;
	private final String type;

	public CustomVariableCompletion(CompletionProvider provider, String name, String type) {
		super(provider, name, buildDefinedName(type), name);
		this.name = name;
		this.type = type;
		setRelevance(1);
	}

	private static String buildDefinedName(String type) {
		if (type != null && !type.isBlank()) {
			return type;
		}
		return "Local symbol";
	}

	@Override public String toString() {
		if (type != null && !type.isBlank()) {
			return name + " : " + type + " - Local symbol";
		}
		return name + " - Local symbol";
	}

	@Override public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.LOCAL_VARIABLE_ICON);
	}
}
