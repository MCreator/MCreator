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
import net.mcreator.element.parts.Procedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class JItemPropertiesListEntry extends JPanel implements IValidable {

	final VTextField name = new VTextField(20);
	String nameString;
	private final ProcedureSelector.Dynamic value;
	private final JComponent container;

	public JItemPropertiesListEntry(MCreator mcreator, JPanel parent, List<JItemPropertiesListEntry> entryMap,
			int propertyId) {
		super(new FlowLayout(FlowLayout.LEFT));
		nameString = "property" + propertyId;
		name.setText(nameString);

		value = new ProcedureSelector.Dynamic(IHelpContext.NONE.withEntry("item/custom_property_value"), mcreator,
				L10N.t("elementgui.item.custom_property.value"),
				L10N.t("elementgui.item.custom_property.value") + propertyId, VariableTypeLoader.BuiltInTypes.NUMBER,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		value.setDefaultName(L10N.t("elementgui.item.custom_property.value.default"));
		reloadDataLists(); // we make sure that selector can be properly shown

		container = PanelUtils.expandHorizontally(this);

		add(L10N.label("elementgui.item.custom_property.name"));
		add(name);
		add(value);

		parent.add(container);
		entryMap.add(this);
		revalidate();
		repaint();

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.item.custom_properties.remove"));
		remove.addActionListener(e -> removeProperty(parent, entryMap));
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public void removeProperty(JPanel parent, List<JItemPropertiesListEntry> entryList) {
		entryList.remove(this);
		parent.remove(container);
		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		value.refreshListKeepSelected();
	}

	public void addEntry(Map<String, Procedure> map) {
		if (this.name.getName() != null && !this.name.getName().equals("") && this.value.getSelectedProcedure() != null)
			map.put(this.name.getName(), this.value.getSelectedProcedure());
	}

	public void setEntry(String name, Procedure value) {
		this.name.setText(name);
		this.value.setSelectedProcedure(value);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return name.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
		name.setValidator(validator);
	}

	@Override public Validator getValidator() {
		return name.getValidator();
	}
}