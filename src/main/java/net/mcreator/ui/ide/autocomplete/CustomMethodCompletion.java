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

import net.mcreator.util.JavadocUtils;

import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.TemplateCompletion;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

public class CustomMethodCompletion extends TemplateCompletion {
	private final String label;
	private final String returnType;
	private final String declaringClass;
	private final String docSummary;
	private final String visibility;
	private final boolean isStatic;
	private final boolean isAbstract;
	private final boolean isDeprecated;
	private final List<ParameterizedCompletion.Parameter> params;

	public CustomMethodCompletion(CompletionProvider provider, String name, String label, String returnType, String declaringClass, String template, String docSummary, String visibility, boolean isStatic, boolean isAbstract, boolean isDeprecated, List<String> paramTypes, List<String> paramNames) {
		super(provider, name, name, template, null, null);
		this.label = label;
		this.returnType = returnType;
		this.declaringClass = declaringClass;
		this.docSummary = docSummary;
		this.visibility = visibility;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.isDeprecated = isDeprecated;

		this.params = new ArrayList<>();
		if (paramTypes != null && paramNames != null && paramTypes.size() == paramNames.size()) {
			for (int i = 0; i < paramTypes.size(); i++) {
				this.params.add(new ParameterizedCompletion.Parameter(paramTypes.get(i), paramNames.get(i)));
			}
		}
		setRelevance(100);
	}

	@Override
	public boolean getShowParameterToolTip() {
		return getParamCount() > 0;
	}

	@Override
	public int getParamCount() {
		if (params != null && !params.isEmpty()) {
			return params.size();
		}
		return super.getParamCount();
	}

	@Override
	public ParameterizedCompletion.Parameter getParam(int index) {
		if (params != null && !params.isEmpty() && index >= 0 && index < params.size()) {
			return params.get(index);
		}
		return super.getParam(index);
	}

	@Override
	public String getDefinitionString() {
		return label != null ? label : super.getDefinitionString();
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
		String formatted = JavadocUtils.formatJavadoc(docSummary);
		if (formatted == null) return null;
		return "<html><b>" + label + "</b><hr>" + formatted + "</html>";
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