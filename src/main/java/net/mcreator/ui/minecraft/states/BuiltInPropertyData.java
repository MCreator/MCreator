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

package net.mcreator.ui.minecraft.states;

import com.google.gson.JsonElement;
import net.mcreator.ui.MCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

public final class BuiltInPropertyData<T> implements IPropertyData<T> {

	private final PropertyData<T> property;

	public BuiltInPropertyData(PropertyData<T> property) {
		this.property = property;
	}

	@Override public String getName() {
		return property.getName();
	}

	@Override public Class<?> getDataClass() {
		return property.getDataClass();
	}

	@Override @Nonnull public T getDefaultValue() {
		return property.getDefaultValue();
	}

	@Override public String toString(Object value) {
		return property.toString(value);
	}

	@Override public T parseObj(JsonElement value) {
		return property.parseObj(value);
	}

	@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
		return property.getComponent(mcreator, value);
	}

	@Override public T getValue(JComponent component) {
		return property.getValue(component);
	}

	@Override public boolean equals(Object obj) {
		return super.equals(obj) || obj instanceof BuiltInPropertyData<?> that && this.getName().equals(that.getName());
	}

	@Override public int hashCode() {
		return getName().hashCode();
	}

	@Override public String toString() {
		return getName();
	}

	public PropertyData<T> getUnderlyingProperty() {
		return property;
	}

}
