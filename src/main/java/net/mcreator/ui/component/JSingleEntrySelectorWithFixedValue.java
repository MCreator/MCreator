/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.component;

import net.mcreator.ui.MCreator;

import javax.annotation.Nullable;
import javax.swing.*;

public abstract class JSingleEntrySelectorWithFixedValue<T, E> extends JSingleEntrySelector<T> {

	@Nullable protected final JComponent fixedValueComponent;

	public JSingleEntrySelectorWithFixedValue(MCreator mcreator, @Nullable JComponent fixedValueComponent) {
		super(mcreator);
		this.fixedValueComponent = fixedValueComponent;

		if (fixedValueComponent != null)
			addTrailingComponent(fixedValueComponent);
	}

	@Override public void setEntry(T entry) {
		super.setEntry(entry);
		if (fixedValueComponent != null) {
			fixedValueComponent.setEnabled(this.isEnabled() && entry == null);
		}
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (fixedValueComponent != null)
			fixedValueComponent.setEnabled(enabled && this.getEntry() == null);
	}

	@Nullable protected JComponent fixedValueComponent() {
		return fixedValueComponent;
	}

	public abstract E getFixedValue();

	public abstract void setFixedValue(E value);

	@Override public boolean isEmpty() {
		return fixedValueComponent == null && super.isEmpty();
	}
}
