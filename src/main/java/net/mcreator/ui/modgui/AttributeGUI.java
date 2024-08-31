/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import net.mcreator.element.types.Attribute;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.EntityListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class AttributeGUI extends ModElementGUI<Attribute> {
	
	private final VTextField name = new VTextField(20);
	private final JSpinner defaultValue = new JSpinner(new SpinnerNumberModel(0.0, -Double.MIN_VALUE, Double.MAX_VALUE, 1.0));
	private final JMinMaxSpinner minMaxValue = new JMinMaxSpinner(0, 1, -Double.MIN_VALUE, Double.MAX_VALUE, 1.0);
	private final JCheckBox persists = L10N.checkbox("elementgui.common.enable");

	private final EntityListField entities = new EntityListField(mcreator);
	private final ValidationGroup page1group = new ValidationGroup();

	public AttributeGUI(MCreator mcreator, ModElement element, boolean editingMode) {
		super(mcreator, element, editingMode);
		initGUI();
		super.finalizeGUI();
	}

	protected void initGUI() {
		JPanel pane1 = new JPanel(new BorderLayout());
		JPanel pane2 = new JPanel(new GridLayout(5, 2, 5, 3));

		pane1.setOpaque(false);
		pane2.setOpaque(false);
		persists.setOpaque(false);

		minMaxValue.setPreferredSize(new Dimension(20, 20));
		defaultValue.setPreferredSize(new Dimension(20, 20));

		pane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"), L10N.label("elementgui.attribute.name")));
		pane2.add(name);
		pane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/default_value"), L10N.label("elementgui.attribute.default_value")));
		pane2.add(defaultValue);
		pane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/min_max_value"), L10N.label("elementgui.attribute.min_max_value")));
		pane2.add(minMaxValue);
		pane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/persists"), L10N.label("elementgui.attribute.persists")));
		pane2.add(persists);
		pane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/entities"), L10N.label("elementgui.attribute.entities")));
		pane2.add(entities);

		pane1.add(PanelUtils.totalCenterInPanel(pane2));

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.attribute.needs_name")));
		name.enableRealtimeValidation();
		page1group.addValidationElement(name);

		addPage(pane1);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	public void openInEditingMode(Attribute attribute) {
		name.setText(attribute.name);
		defaultValue.setValue(attribute.defaultValue);
		minMaxValue.setMinValue(attribute.minValue);
		minMaxValue.setMaxValue(attribute.maxValue);
		persists.setSelected(attribute.persists);
		entities.setListElements(attribute.entities);
	}

	public Attribute getElementFromGUI() {
		Attribute attribute = new Attribute(modElement);

		attribute.name = name.getText();
		attribute.defaultValue = (Double) defaultValue.getValue();
		attribute.minValue = (Double) minMaxValue.getMinValue();
		attribute.maxValue = (Double) minMaxValue.getMaxValue();
		attribute.persists = persists.isSelected();
		attribute.entities = entities.getListElements();

		return attribute;
	}

	protected AggregatedValidationResult validatePage(int page) {
		if ((double) minMaxValue.getMinValue() > (double) defaultValue.getValue())
			return new AggregatedValidationResult.FAIL(L10N.t("elementgui.attribute.default_lower_than_min"));
		else if ((double) minMaxValue.getMaxValue() < (double) defaultValue.getValue())
			return new AggregatedValidationResult.FAIL(L10N.t("elementgui.attribute.default_higher_than_max"));
		return new AggregatedValidationResult(page1group);
	}
}
