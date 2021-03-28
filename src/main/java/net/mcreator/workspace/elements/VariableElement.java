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

package net.mcreator.workspace.elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VariableElement implements IElement {
	private static final Map<VariableElementType, String> variables = new HashMap<>();

	private String name;
	private VariableElementType type;
	private VariableElementType.Scope scope = VariableElementType.Scope.GLOBAL_SESSION;
	private Object value;

	@Override public String toString() {
		return getName();
	}

	@Override public boolean equals(Object element) {
		return element instanceof VariableElement && name.equals(((VariableElement) element).getName());
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	public VariableElementType getType() {
		return type;
	}

	public static VariableElementType getVariableFromType(String type) {
		for (VariableElementType var : variables.keySet()) {
			if(var.getBlocklyVariableType().equals(type) || var.getType().equals(type.toLowerCase())) {
				return var;
			}
		}
		return null;
	}

	public static Set<VariableElementType> getVariables() {
		return variables.keySet();
	}
	public static void addVariable(VariableElementType var) {
		variables.put(var, var.getType().toUpperCase());
	}

	public static Collection<String> getAllTypes() {
		return variables.values();
	}

	public void setType(VariableElementType type) {
		this.type = type;
	}

	@Override public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public VariableElementType.Scope getScope() {
		return scope;
	}

	public void setScope(VariableElementType.Scope scope) {
		this.scope = scope;
	}
}
