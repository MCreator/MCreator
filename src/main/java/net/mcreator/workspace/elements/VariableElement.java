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

public class VariableElement implements IElement {

	private final String name;

	private String type;
	private VariableType.Scope scope = VariableType.Scope.GLOBAL_SESSION;
	private Object value;

	public VariableElement(String name) {
		this.name = name;
	}

	@Override public String toString() {
		return getName();
	}

	@Override public boolean equals(Object element) {
		return element instanceof VariableElement && name.equals(((VariableElement) element).getName());
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	public VariableType getType() {
		return VariableTypeLoader.INSTANCE.fromName(type);
	}

	public void setType(VariableType type) {
		this.type = type.getName();
	}

	@Override public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public VariableType.Scope getScope() {
		return scope;
	}

	public void setScope(VariableType.Scope scope) {
		this.scope = scope;
	}
}
