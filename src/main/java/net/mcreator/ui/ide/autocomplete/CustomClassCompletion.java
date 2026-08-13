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

public class CustomClassCompletion extends BasicCompletion {
	private final String className;
	private final String pkg;
	private final boolean isInterface;
	private final boolean isEnum;

	public CustomClassCompletion(CompletionProvider provider, String name, String pkg, boolean isInterface, boolean isEnum) {
		super(provider, name, pkg, "<html>" + (isInterface ? "interface " : (isEnum ? "enum " : "class ")) + name + (pkg != null && !pkg.isEmpty() ? "<br>package " + pkg : "") + "</html>");
		this.className = name;
		this.pkg = pkg;
		this.isInterface = isInterface;
		this.isEnum = isEnum;
		setRelevance(2);
	}

	@Override
	public Icon getIcon() {
		if (isInterface) {
			return IconFactory.get().getIcon(IconFactory.INTERFACE_ICON);
		} else if (isEnum) {
			return IconFactory.get().getIcon(IconFactory.ENUM_ICON);
		}
		return IconFactory.get().getIcon(IconFactory.CLASS_ICON);
	}

	@Override
	public String toString() {
		if (pkg != null && !pkg.isEmpty()) {
			return className + " - " + pkg;
		}
		return className;
	}
}