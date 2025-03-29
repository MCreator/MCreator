/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.modgui;

import net.mcreator.ui.component.ScrollWheelPassLayer;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.ValidationGroup;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModElementGUIPage {

	private final String id;
	private final JComponent component;

	private final AggregatedValidationResult validator = new AggregatedValidationResult();

	private final List<Supplier<AggregatedValidationResult>> lazyValidators = new ArrayList<>();

	public ModElementGUIPage(String id, JComponent component, boolean scroll) {
		this.id = id;

		if (scroll) {
			JScrollPane splitScroll = new JScrollPane(component);
			splitScroll.setOpaque(false);
			splitScroll.getViewport().setOpaque(false);
			splitScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			splitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			splitScroll.getVerticalScrollBar().setUnitIncrement(15);
			splitScroll.getHorizontalScrollBar().setUnitIncrement(15);
			this.component = new JLayer<>(splitScroll, new ScrollWheelPassLayer());
		} else {
			this.component = component;
		}
	}

	public <T extends JComponent & IValidable> ModElementGUIPage validate(T validable) {
		validator.addValidationElement(validable);
		return this;
	}

	public ModElementGUIPage validate(ValidationGroup validationGroup) {
		if (validationGroup instanceof AggregatedValidationResult) {
			throw new RuntimeException(
					"Can not non-lazily validate aggregated results as they are not lazy-evaluated in all cases");
		}

		validator.addValidationGroup(validationGroup);
		return this;
	}

	/**
	 * Use this method when ValidationGroup or AggregatedValidationResult value is not lazy-evaluated but rather stored in the object itself
	 *
	 * @param validator The supplier of the AggregatedValidationResult that is called every time the validation is needed
	 * @return This class instance
	 */
	public ModElementGUIPage lazyValidate(Supplier<AggregatedValidationResult> validator) {
		lazyValidators.add(validator);
		return this;
	}

	public String getID() {
		return id;
	}

	public JComponent getComponent() {
		return component;
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult retval = new AggregatedValidationResult();
		retval.addValidationGroup(validator);
		lazyValidators.forEach(s -> retval.addValidationGroup(s.get()));
		return retval;
	}

}
