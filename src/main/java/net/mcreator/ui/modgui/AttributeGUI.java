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
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.SpawnableEntityListField;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ConditionalItemListFieldValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class AttributeGUI extends ModElementGUI<Attribute> {

	private final VTextField name = new VTextField(38);
	private final JSpinner defaultValue = new JSpinner(
			new SpinnerNumberModel(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 1.0));
	private final JMinMaxSpinner minMaxValue = new JMinMaxSpinner(0, 1, -Double.MAX_VALUE, Double.MAX_VALUE, 1.0);
	private final JComboBox<String> sentiment = new JComboBox<>(new String[] { "POSITIVE", "NEUTRAL", "NEGATIVE" });
	private final SpawnableEntityListField entities = new SpawnableEntityListField(mcreator);
	private final JCheckBox addToAllEntities = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox addToPlayers = L10N.checkbox("elementgui.common.enable");
	private final ValidationGroup page1group = new ValidationGroup();

	public AttributeGUI(MCreator mcreator, ModElement element, boolean editingMode) {
		super(mcreator, element, editingMode);
		initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane1 = new JPanel(new GridLayout(7, 2, 5, 2));
		pane1.setOpaque(false);

		minMaxValue.setPreferredSize(new Dimension(20, 20));
		defaultValue.setPreferredSize(new Dimension(20, 20));

		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.attribute.name")));
		pane1.add(name);
		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/default_value"),
				L10N.label("elementgui.attribute.default_value")));
		pane1.add(defaultValue);
		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/min_max_value"),
				L10N.label("elementgui.attribute.min_max_value")));
		pane1.add(minMaxValue);
		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/sentiment"),
				L10N.label("elementgui.attribute.sentiment")));
		pane1.add(sentiment);

		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/add_to_all_entities"),
				L10N.label("elementgui.attribute.add_to_all_entities")));
		pane1.add(addToAllEntities);
		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/add_to_players"),
				L10N.label("elementgui.attribute.add_to_players")));
		pane1.add(addToPlayers);

		pane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("attribute/entities"),
				L10N.label("elementgui.attribute.entities")));
		pane1.add(entities);

		addToAllEntities.addActionListener((e) -> {
			addToPlayers.setEnabled(!addToAllEntities.isSelected());
			entities.setEnabled(!addToAllEntities.isSelected());
		});

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.attribute.needs_name")));
		name.enableRealtimeValidation();

		minMaxValue.setValidator(() -> {
			if (minMaxValue.getMinValue() > (double) defaultValue.getValue())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.attribute.default_lower_than_min"));
			else if (minMaxValue.getMaxValue() < (double) defaultValue.getValue())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.attribute.default_higher_than_max"));
			return Validator.ValidationResult.PASSED;
		});

		page1group.addValidationElement(minMaxValue);
		page1group.addValidationElement(name);

		entities.setValidator(
				new ConditionalItemListFieldValidator(entities, L10N.t("elementgui.attribute.needs_entity"),
						() -> addToAllEntities.isSelected() || addToPlayers.isSelected(), false));
		page1group.addValidationElement(entities);

		addPage(PanelUtils.totalCenterInPanel(pane1)).validate(page1group);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void openInEditingMode(Attribute attribute) {
		name.setText(attribute.name);
		defaultValue.setValue(attribute.defaultValue);
		minMaxValue.setMinValue(attribute.minValue);
		minMaxValue.setMaxValue(attribute.maxValue);
		sentiment.setSelectedItem(attribute.sentiment);
		addToAllEntities.setSelected(attribute.addToAllEntities);
		addToPlayers.setSelected(attribute.addToPlayers);
		entities.setListElements(attribute.entities);

		addToPlayers.setEnabled(!attribute.addToAllEntities);
		entities.setEnabled(!attribute.addToAllEntities);
	}

	@Override public Attribute getElementFromGUI() {
		Attribute attribute = new Attribute(modElement);

		attribute.name = name.getText();
		attribute.defaultValue = (Double) defaultValue.getValue();
		attribute.minValue = minMaxValue.getMinValue();
		attribute.maxValue = minMaxValue.getMaxValue();
		attribute.sentiment = (String) sentiment.getSelectedItem();
		attribute.addToAllEntities = addToAllEntities.isSelected();
		attribute.addToPlayers = addToPlayers.isSelected();
		attribute.entities = entities.getListElements();

		return attribute;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-attribute");
	}

}
