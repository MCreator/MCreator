/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.item;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.states.JPropertyNameField;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.ProcedureSelectorValidator;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JItemPropertiesListEntry extends JPanel implements IValidable {

	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final JPropertyNameField name;

	private final ProcedureSelector value;

	public JItemPropertiesListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JItemPropertiesListEntry> entryList, int propertyId,
			Function<Supplier<String>, UniqueNameValidator> validator,
			Consumer<JItemPropertiesListEntry> editListener) {
		super(new BorderLayout(10, 5));

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		name = new JPropertyNameField("property" + propertyId, () -> editListener.accept(this));
		name.setValidator(validator.apply(name::getPropertyName)
				.wrapValidator(new RegistryNameValidator(name, L10N.t("elementgui.item.custom_property.validator"))));
		name.enableRealtimeValidation();
		add("West", PanelUtils.pullElementUp(PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("item/custom_property_name"),
						L10N.label("elementgui.item.custom_property.name")), name)));

		value = new ProcedureSelector(gui.withEntry("item/custom_property_value"), mcreator,
				L10N.t("elementgui.item.custom_property.value"),
				L10N.t("elementgui.item.custom_property.value") + propertyId, ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.NUMBER,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		value.setDefaultName(L10N.t("elementgui.item.custom_property.value.default"));
		value.setValidator(new ProcedureSelectorValidator(value));
		reloadDataLists(); // we make sure that selector can be properly shown

		add("Center", value);

		JComponent container = PanelUtils.expandHorizontally(this);
		parent.add(container);
		entryList.add(this);

		remove.setText(L10N.t("elementgui.item.custom_property.remove"));
		remove.addActionListener(e -> {
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
			entryList.remove(this);
		});
		add("East", PanelUtils.pullElementUp(remove));

		parent.revalidate();
		parent.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		name.setEnabled(enabled);
		value.setEnabled(enabled);

		remove.setEnabled(enabled);
	}

	public void reloadDataLists() {
		value.refreshListKeepSelected();
	}

	JPropertyNameField getNameField() {
		return name;
	}

	PropertyData.FloatNumber toPropertyData() {
		return new PropertyData.FloatNumber(name.getPropertyName(), 0F, 1000000F);
	}

	public Procedure getEntry() {
		return value.getSelectedProcedure();
	}

	public void setEntry(String name, Procedure value) {
		this.name.renameTo(name);
		this.value.setSelectedProcedure(value);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		Validator.ValidationResult result = name.getValidationStatus();
		if (result != Validator.ValidationResult.PASSED)
			return result;

		return value.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return name.getValidator();
	}
}