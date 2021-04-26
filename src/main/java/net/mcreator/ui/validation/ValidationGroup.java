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

package net.mcreator.ui.validation;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ValidationGroup {

	private final List<IValidable> validationElements = new ArrayList<>();
	private ValidationGroupListener validationGroupListener = null;

	public <T extends JComponent & IValidable> void addValidationElement(T validable) {
		validationElements.add(validable);
	}

	public <T extends JTextField & IValidable> void addValidationElement(T validable) {
		validationElements.add(validable);
		validable.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (validationGroupListener != null)
					validationGroupListener.validationGroupDataChanged(validateIsErrorFree());
			}
		});
	}

	public void setValidationGroupListener(ValidationGroupListener validationGroupListener) {
		this.validationGroupListener = validationGroupListener;
	}

	public boolean validateIsErrorFree() {

		boolean isErrorFree = true;

		for (IValidable validable : validationElements)
			if (validable.getValidationStatus().getValidationResultType() == Validator.ValidationResultType.ERROR)
				isErrorFree = false;

		return isErrorFree;
	}

	public List<String> getValidationProblemMessages() {
		List<String> retval = new ArrayList<>();

		validationElements.stream().filter((e) -> e.getValidationStatus().getValidationResultType()
				!= Validator.ValidationResultType.PASSED)
				.forEach((e) -> retval.add(e.getValidator().validate().getMessage()));

		return retval;
	}

}
