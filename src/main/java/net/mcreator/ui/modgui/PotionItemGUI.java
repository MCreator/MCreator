/*
 * MCToolkit (https://mctoolkit.net/)
 * Copyright (C) 2020 MCToolkit and contributors
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

import net.mcreator.element.types.PotionItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.potions.JPotionList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ConditionalTextFieldValidator;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PotionItemGUI extends ModElementGUI<PotionItem> {

	private final VTextField potionName = new VTextField(40);
	private final VTextField splashName = new VTextField(40);
	private final VTextField lingeringName = new VTextField(40);
	private final VTextField arrowName = new VTextField(40);
	private final JPotionList effectList = new JPotionList(mcreator);

	private final JPanel effects = new JPanel(new GridLayout(0, 1, 5, 5));

	private final ValidationGroup page1group = new ValidationGroup();

	public PotionItemGUI(MCreator mcreator, @NotNull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		JPanel northPanel = new JPanel(new GridLayout(4, 2, 0, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potionitem/display_name"),
				L10N.label("elementgui.potionitem.potion_name")));
		northPanel.add(potionName);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potionitem/display_name"),
				L10N.label("elementgui.potionitem.splash_name")));
		northPanel.add(splashName);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potionitem/display_name"),
				L10N.label("elementgui.potionitem.lingering_name")));
		northPanel.add(lingeringName);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("potionitem/display_name"),
				L10N.label("elementgui.potionitem.arrow_name")));
		northPanel.add(arrowName);

		JPanel mainEditor = new JPanel(new GridLayout());

		JComponent component = PanelUtils.northAndCenterElement(HelpUtils
				.wrapWithHelpButton(this.withEntry("potionitem/effects"),
						L10N.label("elementgui.potionitem.effects")), effectList);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		mainEditor.add(component);

		mainEditor.setOpaque(false);

		potionName.setValidator(
				new TextFieldValidator(potionName, L10N.t("elementgui.potionitem.error_potion_needs_display_name")));
		potionName.enableRealtimeValidation();
		page1group.addValidationElement(potionName);

		splashName.setValidator(
				new TextFieldValidator(splashName, L10N.t("elementgui.potionitem.error_potion_needs_display_name")));
		splashName.enableRealtimeValidation();
		page1group.addValidationElement(splashName);

		lingeringName.setValidator(
				new TextFieldValidator(lingeringName, L10N.t("elementgui.potionitem.error_potion_needs_display_name")));
		lingeringName.enableRealtimeValidation();
		page1group.addValidationElement(lingeringName);

		arrowName.setValidator(
				new TextFieldValidator(arrowName, L10N.t("elementgui.potionitem.error_potion_needs_display_name")));
		arrowName.enableRealtimeValidation();
		page1group.addValidationElement(arrowName);

		pane3.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel), mainEditor));
		addPage(pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = "Potion of " + StringUtils.machineToReadableName(modElement.getName());
			potionName.setText(readableNameFromModElement);

			readableNameFromModElement = "Splash Potion of " + StringUtils.machineToReadableName(modElement.getName());
			splashName.setText(readableNameFromModElement);

			readableNameFromModElement = "Lingering Potion of " + StringUtils.machineToReadableName(modElement.getName());
			lingeringName.setText(readableNameFromModElement);

			readableNameFromModElement = "Arrow of " + StringUtils.machineToReadableName(modElement.getName());
			arrowName.setText(readableNameFromModElement);
		}
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(PotionItem potion) {
		potionName.setText(potion.potionName);
		splashName.setText(potion.splashName);
		lingeringName.setText(potion.lingeringName);
		arrowName.setText(potion.arrowName);
		effectList.setEffects(potion.effects);
	}

	@Override public PotionItem getElementFromGUI() {
		PotionItem potion = new PotionItem(modElement);
		potion.potionName = potionName.getText();
		potion.splashName = splashName.getText();
		potion.lingeringName = lingeringName.getText();
		potion.arrowName = arrowName.getText();
		potion.effects = effectList.getEffects();
		return potion;
	}
}
