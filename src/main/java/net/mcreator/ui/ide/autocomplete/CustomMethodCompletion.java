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
import org.fife.ui.autocomplete.TemplateCompletion;

import javax.swing.Icon;

public class CustomMethodCompletion extends TemplateCompletion {
	private final String label;
	private final String returnType;
	private final String declaringClass;
	private final String docSummary;
	private final String visibility;
	private final boolean isStatic;
	private final boolean isAbstract;
	private final boolean isDeprecated;

	public CustomMethodCompletion(CompletionProvider provider, String name, String label, String returnType, String declaringClass, String template, String docSummary, String visibility, boolean isStatic, boolean isAbstract, boolean isDeprecated) {
		super(provider, name, name, template, null, null);
		this.label = label;
		this.returnType = returnType;
		this.declaringClass = declaringClass;
		this.docSummary = docSummary;
		this.visibility = visibility;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.isDeprecated = isDeprecated;
		setRelevance(100);
	}

	@Override
	public Icon getIcon() {
		String iconKey = switch (visibility != null ? visibility : "public") {
			case "protected" -> IconFactory.METHOD_PROTECTED_ICON;
			case "private" -> IconFactory.METHOD_PRIVATE_ICON;
			case "package" -> IconFactory.METHOD_DEFAULT_ICON;
			default -> IconFactory.METHOD_PUBLIC_ICON;
		};
		Icon baseIcon = IconFactory.get().getIcon(iconKey);
		if (isStatic || isAbstract || isDeprecated) {
			DecoratableIcon dec = new DecoratableIcon(baseIcon);
			if (isDeprecated) {
				dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.DEPRECATED_ICON));
			}
			if (isAbstract) {
				dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.ABSTRACT_ICON));
			}
			if (isStatic) {
				dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.STATIC_ICON));
			}
			return dec;
		}
		return baseIcon;
	}

	@Override
	public String getSummary() {
		if (docSummary == null || docSummary.trim().isEmpty()) return null;
		String text = docSummary;

		text = text.replaceAll("(?m)^\\s*/\\*+\\s*", "")
				   .replaceAll("(?m)\\s*\\*/\\s*$", "")
				   .replaceAll("(?m)^\\s*\\*\\s?", "");

		text = text.replaceAll("\\{@code\\s+([^}]+)\\}", "<code>$1</code>");
		text = text.replaceAll("\\{@link\\s+([^}]+)\\}", "<code>$1</code>");
		text = text.replaceAll("\\{@linkplain\\s+([^}]+)\\}", "$1");
		text = text.replaceAll("\\{@literal\\s+([^}]+)\\}", "<code>$1</code>");
		text = text.replaceAll("\\{@value\\s+([^}]+)\\}", "<code>$1</code>");

		text = text.replaceAll("(?m)^@param\\s+<(\\w+)>", "<br><b>Type Parameters:</b><br>&nbsp;&nbsp;<code>&lt;$1&gt;</code> - ");
		text = text.replaceAll("(?m)^@param\\s+(\\w+)", "<br><b>Parameters:</b><br>&nbsp;&nbsp;<code>$1</code> - ");
		text = text.replaceAll("(?m)^@return", "<br><b>Returns:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@throws\\s+(\\S+)", "<br><b>Throws:</b><br>&nbsp;&nbsp;<code>$1</code> - ");
		text = text.replaceAll("(?m)^@exception\\s+(\\S+)", "<br><b>Throws:</b><br>&nbsp;&nbsp;<code>$1</code> - ");
		text = text.replaceAll("(?m)^@see\\s+(\\S+)", "<br><b>See Also:</b><br>&nbsp;&nbsp;<code>$1</code>");
		text = text.replaceAll("(?m)^@since\\s+(.+)", "<br><b>Since:</b><br>&nbsp;&nbsp;$1");
		text = text.replaceAll("(?m)^@deprecated", "<br><b>Deprecated:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@implSpec", "<br><b>Implementation Requirements:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@implNote", "<br><b>Implementation Note:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@apiNote", "<br><b>API Note:</b><br>&nbsp;&nbsp;");

		text = text.replace("\r", "");
		String[] parts = text.split("(?i)(?=<pre>)|(?<=</pre>)");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (part.toLowerCase().startsWith("<pre>")) {
				sb.append(part);
			} else {
				sb.append(part.replace("\n", "<br>"));
			}
		}

		return "<html><b>" + label + "</b><hr>" + sb.toString() + "</html>";
	}

	@Override
	public String toString() {
		String text = label + " : " + returnType;
		if (declaringClass != null && !declaringClass.isEmpty()) {
			text += " - " + declaringClass;
		}
		return text;
	}
}