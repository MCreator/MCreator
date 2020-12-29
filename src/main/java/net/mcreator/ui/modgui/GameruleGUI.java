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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.Gamerule;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class GameruleGUI extends ModElementGUI<Gamerule> {

	private final VTextField name = new VTextField(20);
	private final VTextField ID = new VTextField(20);

	private final VTextField description = new VTextField(20);
	private final JComboBox<String> gameruleType = new JComboBox<>(
			new String[] { "Player", "Updates", "Chat", "Drops", "Misc", "Mobs", "Spawning" });

	private final JCheckBox isInteger = L10N.checkbox("elementgui.common.enable");
	private final JSpinner defaultIntegerValue = new JSpinner(new SpinnerNumberModel(0, 0, 64000, 1));

	private final JCheckBox isBoolean = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox defaultBooleanValue = L10N.checkbox("elementgui.gamerule.check_true");

	private final ValidationGroup page1group = new ValidationGroup();

	public GameruleGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		isInteger.setOpaque(false);
		defaultIntegerValue.setOpaque(false);
		isBoolean.setOpaque(false);
		defaultBooleanValue.setOpaque(false);

		JPanel pane3 = new JPanel(new BorderLayout());

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(ID, 16);
		ComponentUtils.deriveFont(description, 16);

		JPanel subpane2 = new JPanel(new GridLayout(8, 2, 45, 8));
		subpane2.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/name"),
				L10N.label("elementgui.gamerule.name")));
		subpane2.add(name);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/id"),
				L10N.label("elementgui.gamerule.id")));
		subpane2.add(ID);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/description"),
				L10N.label("elementgui.gamerule.description")));
		subpane2.add(description);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/type"),
				L10N.label("elementgui.gamerule.type")));
		subpane2.add(gameruleType);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/is_integer"),
				L10N.label("elementgui.gamerule.is_integer")));
		subpane2.add(isInteger);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/integer_value"),
				L10N.label("elementgui.gamerule.default_integer_value")));
		subpane2.add(defaultIntegerValue);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/is_boolean"),
				L10N.label("elementgui.gamerule.is_boolean")));
		subpane2.add(isBoolean);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/boolean_value"),
				L10N.label("elementgui.gamerule.default_boolean_value")));
		subpane2.add(defaultBooleanValue);

		isInteger.addActionListener(e -> refreshGameruleInfo());
		isBoolean.addActionListener(e -> refreshGameruleInfo());
		refreshGameruleInfo();

		page1group.addValidationElement(name);
		page1group.addValidationElement(ID);
		page1group.addValidationElement(description);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.gamerule.gamerule_needs_name")));
		name.enableRealtimeValidation();

		ID.setValidator(new TextFieldValidator(ID, L10N.t("elementgui.gamerule.gamerule_needs_id")));
		ID.enableRealtimeValidation();

		description.setValidator(new TextFieldValidator(description, L10N.t("elementgui.gamerule.gamerule_needs_description")));
		description.enableRealtimeValidation();

		pane3.add(PanelUtils.totalCenterInPanel(subpane2));
		pane3.setOpaque(false);

		addPage(L10N.t("elementgui.common.page_properties"), pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void refreshGameruleInfo() {
		isInteger.setEnabled(!isBoolean.isSelected());
		isBoolean.setEnabled(!isInteger.isSelected());
		defaultIntegerValue.setEnabled(isInteger.isSelected());
		defaultBooleanValue.setEnabled(isBoolean.isSelected());
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Gamerule gamerule) {
		name.setText(gamerule.name);
		ID.setText(gamerule.ID);
		description.setText(gamerule.description);
		gameruleType.setSelectedItem(gamerule.gameruleType);
		isInteger.setSelected(gamerule.isInteger);
		isBoolean.setSelected(gamerule.isBoolean);

		refreshGameruleInfo();
	}

	@Override public Gamerule getElementFromGUI() {
		Gamerule gamerule = new Gamerule(modElement);
		gamerule.name = name.getText();
		gamerule.ID = ID.getText();
		gamerule.description = description.getText();
		gamerule.gameruleType = (String) gameruleType.getSelectedItem();
		gamerule.isInteger = isInteger.isSelected();
		gamerule.isBoolean = isBoolean.isSelected();
		return gamerule;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-music-disc");
	}
}