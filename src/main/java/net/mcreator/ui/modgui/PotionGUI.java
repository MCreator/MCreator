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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.Potion;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.potions.JPotionList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class PotionGUI extends ModElementGUI<Potion> {

	private final VTextField potionName = new VTextField(24);
	private final VTextField splashName = new VTextField(24);
	private final VTextField lingeringName = new VTextField(24);
	private final VTextField arrowName = new VTextField(24);
	private JPotionList effectList;

	private final ValidationGroup page1group = new ValidationGroup();

	public PotionGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		effectList = new JPotionList(mcreator, this);

		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		JPanel northPanel = new JPanel(new GridLayout(4, 2, 0, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/potion_name"),
				L10N.label("elementgui.potion.potion_name")));
		northPanel.add(potionName);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/splash_name"),
				L10N.label("elementgui.potion.splash_name")));
		northPanel.add(splashName);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/lingering_name"),
				L10N.label("elementgui.potion.lingering_name")));
		northPanel.add(lingeringName);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/arrow_name"),
				L10N.label("elementgui.potion.arrow_name")));
		northPanel.add(arrowName);

		ComponentUtils.deriveFont(potionName, 16);
		ComponentUtils.deriveFont(splashName, 16);
		ComponentUtils.deriveFont(lingeringName, 16);
		ComponentUtils.deriveFont(arrowName, 16);

		JComponent mainEditor = PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("potion/effects"), L10N.label("elementgui.potion.effects")),
				effectList);

		mainEditor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		potionName.setValidator(
				new TextFieldValidator(potionName, L10N.t("elementgui.potion.error_potion_needs_display_name")));
		potionName.enableRealtimeValidation();
		page1group.addValidationElement(potionName);

		splashName.setValidator(
				new TextFieldValidator(splashName, L10N.t("elementgui.potion.error_potion_needs_display_name")));
		splashName.enableRealtimeValidation();
		page1group.addValidationElement(splashName);

		lingeringName.setValidator(
				new TextFieldValidator(lingeringName, L10N.t("elementgui.potion.error_potion_needs_display_name")));
		lingeringName.enableRealtimeValidation();
		page1group.addValidationElement(lingeringName);

		arrowName.setValidator(
				new TextFieldValidator(arrowName, L10N.t("elementgui.potion.error_potion_needs_display_name")));
		arrowName.enableRealtimeValidation();
		page1group.addValidationElement(arrowName);

		pane3.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel), mainEditor));
		addPage(pane3, false);

		if (!isEditingMode()) {
			String readableNameFromModElement = "Potion of " + StringUtils.machineToReadableName(modElement.getName());
			potionName.setText(readableNameFromModElement);

			readableNameFromModElement = "Splash Potion of " + StringUtils.machineToReadableName(modElement.getName());
			splashName.setText(readableNameFromModElement);

			readableNameFromModElement =
					"Lingering Potion of " + StringUtils.machineToReadableName(modElement.getName());
			lingeringName.setText(readableNameFromModElement);

			readableNameFromModElement = "Arrow of " + StringUtils.machineToReadableName(modElement.getName());
			arrowName.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		effectList.reloadDataLists();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(Potion potion) {
		potionName.setText(potion.potionName);
		splashName.setText(potion.splashName);
		lingeringName.setText(potion.lingeringName);
		arrowName.setText(potion.arrowName);
		effectList.setEntries(potion.effects);
	}

	@Override public Potion getElementFromGUI() {
		Potion potion = new Potion(modElement);
		potion.potionName = potionName.getText();
		potion.splashName = splashName.getText();
		potion.lingeringName = lingeringName.getText();
		potion.arrowName = arrowName.getText();
		potion.effects = effectList.getEntries();
		return potion;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-potion");
	}

}