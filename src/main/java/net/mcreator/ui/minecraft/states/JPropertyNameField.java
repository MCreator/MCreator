/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.BiConsumer;

public class JPropertyNameField extends VTextField {
	private String cachedName;

	public JPropertyNameField(String initialPropertyName, BiConsumer<String, String> editListener) {
		super(20);

		setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(this, 16);

		addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				if (getValidationStatus() == Validator.ValidationResult.PASSED) {
					editListener.accept(cachedName, getText());
					renameTo(getText());
				} else {
					setText(cachedName);
				}
				getValidationStatus();
			}
		});
		renameTo(initialPropertyName);
	}

	public String getCachedName() {
		return cachedName;
	}

	public void renameTo(String newName) {
		this.setText(cachedName = newName);
	}
}
