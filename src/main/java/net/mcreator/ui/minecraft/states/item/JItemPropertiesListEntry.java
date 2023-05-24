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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.ProcedureSelectorValidator;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JItemPropertiesListEntry extends JPanel implements IValidable {

	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final String propertyName;
	private final PropertyData.NumberType data;

	private final ProcedureSelector value;

	public JItemPropertiesListEntry(JItemPropertiesStatesList listPanel, IHelpContext gui, JPanel propertyEntries,
			List<JItemPropertiesListEntry> propertiesList, String propertyName) {
		super(new BorderLayout(10, 5));
		this.propertyName = propertyName;
		this.data = new PropertyData.NumberType("CUSTOM:" + propertyName);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel nameLabel = new JLabel(propertyName);
		nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		nameLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(nameLabel, 16);

		JPanel namePane = new JPanel(new BorderLayout());
		namePane.setOpaque(false);
		namePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("item/custom_property_name"),
				L10N.label("elementgui.item.custom_property.name")));
		namePane.add("Center", nameLabel);
		namePane.setPreferredSize(new Dimension(240, 0));
		add("West", namePane);

		value = new ProcedureSelector(gui.withEntry("item/custom_property_value"), listPanel.getMCreator(),
				L10N.t("elementgui.item.custom_property.value"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.NUMBER,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		value.setValidator(new ProcedureSelectorValidator(value));
		reloadDataLists(); // we make sure that selector can be properly shown
		add("Center", value);

		remove.setText(L10N.t("elementgui.item.custom_property.remove"));
		remove.addActionListener(e -> listPanel.removeProperty(this));
		add("East", PanelUtils.pullElementUp(remove));

		propertiesList.add(this);
		propertyEntries.add(this);
		propertyEntries.revalidate();
		propertyEntries.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		value.setEnabled(enabled);
		remove.setEnabled(enabled);
	}

	public void reloadDataLists() {
		value.refreshListKeepSelected();
	}

	PropertyData.NumberType toPropertyData() {
		return data;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Procedure getSelectedProcedure() {
		return value.getSelectedProcedure();
	}

	public void setSelectedProcedure(Procedure value) {
		this.value.setSelectedProcedure(value);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return value.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return value.getValidator();
	}
}