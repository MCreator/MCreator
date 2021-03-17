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

import net.mcreator.element.types.GameRule;
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

public class GameRuleGUI extends ModElementGUI<GameRule> {

	private final VTextField name = new VTextField(30);
	private final VTextField displayName = new VTextField(30);
	private final VTextField description = new VTextField(30);

	private final JComboBox<String> gameruleCategory = new JComboBox<>(
			new String[] { "PLAYER", "UPDATES", "CHAT", "DROPS", "MISC", "MOBS", "SPAWNING" });
	private final JComboBox<String> gameruleType = new JComboBox<>(new String[] { "Number", "Logic" });

	private final JComboBox<String> defaultValueLogic = new JComboBox<>(new String[] { "false", "true" });
	private final JSpinner defaultValueNumber = new JSpinner(
			new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

	private final ValidationGroup page1group = new ValidationGroup();

	private final CardLayout cl = new CardLayout();
	private final JPanel defalutValue = new JPanel(cl);

	public GameRuleGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(displayName, 16);
		ComponentUtils.deriveFont(description, 16);

		JPanel subpane2 = new JPanel(new GridLayout(6, 2, 0, 2));
		subpane2.setOpaque(false);

		name.setEnabled(false);

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/name"), L10N.label("elementgui.gamerule.name")));
		subpane2.add(name);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/display_name"),
				L10N.label("elementgui.gamerule.display_name")));
		subpane2.add(displayName);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/description"),
				L10N.label("elementgui.gamerule.description")));
		subpane2.add(description);

		subpane2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("gamerule/category"), L10N.label("elementgui.gamerule.category")));
		subpane2.add(gameruleCategory);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/type"), L10N.label("elementgui.gamerule.type")));
		subpane2.add(gameruleType);

		defalutValue.add(defaultValueLogic, "Logic");
		defalutValue.add(defaultValueNumber, "Number");

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/default_value"),
				L10N.label("elementgui.gamerule.default_value")));
		subpane2.add(defalutValue);

		page1group.addValidationElement(name);
		page1group.addValidationElement(displayName);
		page1group.addValidationElement(description);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.gamerule.gamerule_needs_name")));
		name.enableRealtimeValidation();

		displayName.setValidator(new TextFieldValidator(displayName, L10N.t("elementgui.gamerule.gamerule_needs_display_name")));
		displayName.enableRealtimeValidation();

		description.setValidator(
				new TextFieldValidator(description, L10N.t("elementgui.gamerule.gamerule_needs_description")));
		description.enableRealtimeValidation();

		pane3.add(PanelUtils.totalCenterInPanel(subpane2));
		pane3.setOpaque(false);

		gameruleType.addActionListener(e -> updateDefaultValueUI());

		addPage(L10N.t("elementgui.common.page_properties"), pane3);

		if (!isEditingMode()) {
			name.setText(StringUtils.lowercaseFirstLetter(getModElement().getName()));

			updateDefaultValueUI();
		}
	}

	private void updateDefaultValueUI() {
		cl.show(defalutValue, (String) gameruleType.getSelectedItem());
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(GameRule gamerule) {
		displayName.setText(gamerule.displayName);
		description.setText(gamerule.description);
		gameruleCategory.setSelectedItem(gamerule.category);
		gameruleType.setSelectedItem(gamerule.type);
		defaultValueLogic.setSelectedItem(Boolean.toString(gamerule.defaultValueLogic));
		defaultValueNumber.setValue(gamerule.defaultValueNumber);

		name.setText(StringUtils.lowercaseFirstLetter(getModElement().getName()));

		updateDefaultValueUI();
	}

	@Override public GameRule getElementFromGUI() {
		GameRule gamerule = new GameRule(modElement);
		gamerule.displayName = displayName.getText();
		gamerule.description = description.getText();
		gamerule.category = (String) gameruleCategory.getSelectedItem();
		gamerule.type = (String) gameruleType.getSelectedItem();
		gamerule.defaultValueLogic = Boolean.parseBoolean((String) defaultValueLogic.getSelectedItem());
		gamerule.defaultValueNumber = (int) defaultValueNumber.getValue();
		return gamerule;
	}

	@Override protected void beforeGeneratableElementGenerated() {
		super.beforeGeneratableElementGenerated();
		modElement.setRegistryName(StringUtils.lowercaseFirstLetter(getModElement().getName()));
	}

	@Override protected void afterGeneratableElementStored() {
		super.afterGeneratableElementStored();
		modElement.clearMetadata();
		modElement.putMetadata("type", "Number".equals(gameruleType.getSelectedItem()) ? "number" : "boolean");
		modElement.reinit();
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-game-rule");
	}
}