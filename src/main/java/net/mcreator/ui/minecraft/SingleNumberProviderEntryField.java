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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.NumberProviderEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JSingleEntrySelectorWithFixedValue;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import javax.swing.*;

public class SingleNumberProviderEntryField extends JSingleEntrySelectorWithFixedValue<NumberProviderEntry, Number> {

	private final String type;

	public SingleNumberProviderEntryField(MCreator mcreator, String type, JSpinner fixedValueProvider) {
		super(mcreator, fixedValueProvider);
		this.type = type;
	}

	@Override protected NumberProviderEntry openEntrySelector() {
		var entry = DataListSelectorDialog.openSelectorDialog(mcreator,
				w -> ElementUtil.getAllEntriesFor(w, type),
				L10N.t("dialog.selector.title"), L10N.t("dialog.selector.number_providers.message"));
		return entry == null ? null : new NumberProviderEntry(mcreator.getWorkspace(), entry);
	}

	@Override protected JSpinner fixedValueComponent() {
		return (JSpinner) super.fixedValueComponent();
	}

	@Override public Number getFixedValue() {
		JSpinner spinner = fixedValueComponent();
		return spinner == null ? null : (Number) spinner.getValue();
	}

	@Override public void setFixedValue(Number value) {
		JSpinner spinner = fixedValueComponent();
		if (spinner != null && value != null) {
			spinner.setValue(value);
		}
	}
}
