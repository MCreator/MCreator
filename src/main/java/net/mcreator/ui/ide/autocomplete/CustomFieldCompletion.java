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

import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import javax.swing.Icon;

public class CustomFieldCompletion extends VariableCompletion {
	private final String name;
	private final String type;
	private final String declaringClass;
	private final String visibility;
	private final boolean isStatic;
	private final boolean isFinal;
	private final boolean isDeprecated;

	public CustomFieldCompletion(CompletionProvider provider, String name, String type, String declaringClass, String visibility, boolean isStatic, boolean isFinal, boolean isDeprecated, boolean isBlocksContext) {
		super(provider, isBlocksContext ? "Blocks." + name : name, type);
		this.name = name;
		this.type = type;
		this.declaringClass = declaringClass;
		this.visibility = visibility;
		this.isStatic = isStatic;
		this.isFinal = isFinal;
		this.isDeprecated = isDeprecated;
		setRelevance(100);
	}

	@Override
	public Icon getIcon() {
		String iconKey = switch (visibility != null ? visibility : "public") {
			case "protected" -> IconFactory.FIELD_PROTECTED_ICON;
			case "private" -> IconFactory.FIELD_PRIVATE_ICON;
			case "package" -> IconFactory.FIELD_DEFAULT_ICON;
			default -> IconFactory.FIELD_PUBLIC_ICON;
		};
		Icon baseIcon = IconFactory.get().getIcon(iconKey);
		if (isStatic || isFinal || isDeprecated) {
			DecoratableIcon dec = new DecoratableIcon(baseIcon);
			if (isDeprecated) {
				dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.DEPRECATED_ICON));
			}
			if (isStatic) {
				dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.STATIC_ICON));
			}
			if (isFinal) {
				dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.FINAL_ICON));
			}
			return dec;
		}
		return baseIcon;
	}

	@Override
	public String getSummary() {
		String safeType = type != null ? type.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : "";
		String safeName = name != null ? name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : "";
		return "<html>" + safeType + " " + safeName + "</html>";
	}

	@Override
	public String toString() {
		String text = name + " : " + type;
		if (declaringClass != null && !declaringClass.isEmpty()) {
			text += " - " + declaringClass;
		}
		return text;
	}
}