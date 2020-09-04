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

import net.mcreator.blockly.Dependency;
import net.mcreator.element.types.Potion;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ConditionalTextFieldValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.stream.Collectors;

public class PotionGUI extends ModElementGUI<Potion> {

	private final VTextField name = new VTextField(20);
	private final VTextField effectName = new VTextField(20);
	private final JColor color = new JColor(mcreator);
	private final VComboBox<String> icon = new SearchableComboBox<>();

	private final JCheckBox isInstant = new JCheckBox("Is instant");
	private final JCheckBox isBad = new JCheckBox("Is bad");
	private final JCheckBox isBenefitical = new JCheckBox("Is benefitical");
	private final JCheckBox renderStatusInInventory = new JCheckBox("Enable");
	private final JCheckBox renderStatusInHUD = new JCheckBox("Enable");

	private final JCheckBox registerPotionType = new JCheckBox("Enable bottles");

	private final ValidationGroup page1group = new ValidationGroup();

	private ProcedureSelector onStarted;
	private ProcedureSelector onActiveTick;
	private ProcedureSelector onExpired;

	public PotionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onStarted = new ProcedureSelector(this.withEntry("potion/when_potion_applied"), mcreator,
				"When potion started/applied",
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		onActiveTick = new ProcedureSelector(this.withEntry("potion/when_active_tick"), mcreator,
				"On potion active tick",
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		onExpired = new ProcedureSelector(this.withEntry("potion/when_potion_expires"), mcreator, "When potion expires",
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));

		renderStatusInInventory.setSelected(true);
		renderStatusInHUD.setSelected(true);
		registerPotionType.setSelected(true);

		icon.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));

		icon.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");

		JPanel pane3 = new JPanel(new BorderLayout());
		JPanel pane4 = new JPanel(new BorderLayout());

		JPanel selp = new JPanel(new GridLayout(10, 2, 50, 11));

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(effectName, 16);

		isInstant.setOpaque(false);
		registerPotionType.setOpaque(false);
		isBad.setOpaque(false);
		isBenefitical.setOpaque(false);
		renderStatusInInventory.setOpaque(false);
		renderStatusInHUD.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/effect_display_name"),
				new JLabel("Potion effect display name: ")));
		selp.add(effectName);

		JButton importicontexture = new JButton(UIRES.get("18px.add"));
		importicontexture.setToolTipText("Click this to import potion icon");
		importicontexture.setOpaque(false);
		importicontexture.addActionListener(e -> {
			TextureImportDialogs.importOtherTextures(mcreator);
			icon.removeAllItems();
			icon.addItem("");
			mcreator.getWorkspace().getFolderManager().getOtherTexturesList().forEach(el -> icon.addItem(el.getName()));
		});

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/color"), new JLabel("Potion color: ")));
		selp.add(color);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/icon"), new JLabel("Potion icon:")));
		selp.add(PanelUtils.centerAndEastElement(icon, importicontexture));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/has_bottles"),
				new JLabel("Register potion effect bottles: ")));
		selp.add(registerPotionType);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("potion/display_name"), new JLabel("Potion display name: ")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/instant"), new JLabel(
				"<html>Is this potion instantly applied?<br><small>"
						+ "If this is checked, potion tick procedure will not be called")));
		selp.add(isInstant);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("potion/bad"), new JLabel("Is this potion bad for the player?")));
		selp.add(isBad);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/benefitical"),
				new JLabel("Is this potion benefitical to the player?")));
		selp.add(isBenefitical);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/render_in_inventory"),
				new JLabel("Render active potion status in inventory:")));
		selp.add(renderStatusInInventory);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potion/render_in_hud"),
				new JLabel("Render active potion status in HUD:")));
		selp.add(renderStatusInHUD);

		selp.setOpaque(false);

		pane3.add(PanelUtils.totalCenterInPanel(selp));
		pane3.setOpaque(false);

		JPanel events = new JPanel();
		events.setLayout(new BoxLayout(events, BoxLayout.PAGE_AXIS));
		JPanel events2 = new JPanel(new GridLayout(1, 3, 8, 8));
		events2.setOpaque(false);
		events2.add(onStarted);
		events2.add(onActiveTick);
		events2.add(onExpired);
		events.add(PanelUtils.join(events2));
		events.setOpaque(false);
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));
		pane4.setOpaque(false);
		pane4.setOpaque(false);

		icon.setValidator(() -> {
			if (icon.getSelectedItem() == null || icon.getSelectedItem().equals(""))
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						"Potion needs to have icon");
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		name.setValidator(
				new ConditionalTextFieldValidator(name, "Potion needs a display name", registerPotionType, true));
		name.enableRealtimeValidation();
		page1group.addValidationElement(name);

		effectName.setValidator(new TextFieldValidator(effectName, "Potion effect needs a display name"));
		effectName.enableRealtimeValidation();
		page1group.addValidationElement(effectName);

		page1group.addValidationElement(icon);

		addPage("Properties", pane3);
		addPage("Triggers", pane4);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
			effectName.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		onStarted.refreshListKeepSelected();
		onActiveTick.refreshListKeepSelected();
		onExpired.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(icon, ListUtils.merge(Collections.singleton(""),
				mcreator.getWorkspace().getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.collect(Collectors.toList())), "");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0) {
			return new AggregatedValidationResult(page1group);
		}
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Potion potion) {
		name.setText(potion.name);
		effectName.setText(potion.effectName);
		icon.setSelectedItem(potion.icon);
		color.setColor(potion.color);
		isInstant.setSelected(potion.isInstant);
		isBad.setSelected(potion.isBad);
		registerPotionType.setSelected(potion.registerPotionType);
		isBenefitical.setSelected(potion.isBenefitical);
		renderStatusInInventory.setSelected(potion.renderStatusInInventory);
		renderStatusInHUD.setSelected(potion.renderStatusInHUD);
		onStarted.setSelectedProcedure(potion.onStarted);
		onActiveTick.setSelectedProcedure(potion.onActiveTick);
		onExpired.setSelectedProcedure(potion.onExpired);
	}

	@Override public Potion getElementFromGUI() {
		Potion potion = new Potion(modElement);
		potion.name = name.getText();
		potion.effectName = effectName.getText();
		potion.icon = icon.getSelectedItem();
		potion.color = color.getColor();
		potion.isInstant = isInstant.isSelected();
		potion.registerPotionType = registerPotionType.isSelected();
		potion.isBad = isBad.isSelected();
		potion.isBenefitical = isBenefitical.isSelected();
		potion.renderStatusInInventory = renderStatusInInventory.isSelected();
		potion.renderStatusInHUD = renderStatusInHUD.isSelected();
		potion.onStarted = onStarted.getSelectedProcedure();
		potion.onActiveTick = onActiveTick.getSelectedProcedure();
		potion.onExpired = onExpired.getSelectedProcedure();
		return potion;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-potion");
	}

}
