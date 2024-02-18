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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.types.PotionEffect;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class PotionEffectGUI extends ModElementGUI<PotionEffect> {

	private final VTextField effectName = new VTextField(20);
	private final JColor color = new JColor(mcreator, false, false);
	private TextureHolder icon;

	private final JCheckBox isInstant = L10N.checkbox("elementgui.potioneffect.is_instant");
	private final JCheckBox isBad = L10N.checkbox("elementgui.potioneffect.is_bad");
	private final JCheckBox isBenefitical = L10N.checkbox("elementgui.potioneffect.is_benefitical");
	private final JCheckBox renderStatusInInventory = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox renderStatusInHUD = L10N.checkbox("elementgui.common.enable");

	private final ValidationGroup page1group = new ValidationGroup();

	private ProcedureSelector onStarted;
	private ProcedureSelector onActiveTick;
	private ProcedureSelector onExpired;

	private ProcedureSelector activeTickCondition;

	public PotionEffectGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onStarted = new ProcedureSelector(this.withEntry("potioneffect/when_potion_applied"), mcreator,
				L10N.t("elementgui.potioneffect.event_potion_applied"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		onActiveTick = new ProcedureSelector(this.withEntry("potioneffect/when_active_tick"), mcreator,
				L10N.t("elementgui.potioneffect.event_potion_tick"),
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		onExpired = new ProcedureSelector(this.withEntry("potioneffect/when_potion_expires"), mcreator,
				L10N.t("elementgui.potioneffect.event_potion_expires"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		activeTickCondition = new ProcedureSelector(this.withEntry("potioneffect/active_tick_condition"), mcreator,
				L10N.t("elementgui.potioneffect.event_tick_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("duration:number/amplifier:number"));

		renderStatusInInventory.setSelected(true);
		renderStatusInHUD.setSelected(true);

		JPanel pane3 = new JPanel(new BorderLayout());
		JPanel pane4 = new JPanel(new BorderLayout());

		JPanel selp = new JPanel(new GridLayout(8, 2, 50, 2));

		ComponentUtils.deriveFont(effectName, 16);

		isInstant.setOpaque(false);
		isBad.setOpaque(false);
		isBenefitical.setOpaque(false);
		renderStatusInInventory.setOpaque(false);
		renderStatusInHUD.setOpaque(false);

		icon = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.EFFECT));
		icon.setOpaque(false);

		JComponent iconComponent = PanelUtils.totalCenterInPanel(
				ComponentUtils.squareAndBorder(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/icon"), icon),
						L10N.t("elementgui.potioneffect.icon")));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/effect_display_name"),
				L10N.label("elementgui.potioneffect.display_name")));
		selp.add(effectName);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/instant"),
				L10N.label("elementgui.potioneffect.instant")));
		selp.add(isInstant);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/bad"),
				L10N.label("elementgui.potioneffect.bad")));
		selp.add(isBad);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/benefitical"),
				L10N.label("elementgui.potioneffect.benefitical")));
		selp.add(isBenefitical);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/render_in_inventory"),
				L10N.label("elementgui.potioneffect.render_status_inventory")));
		selp.add(renderStatusInInventory);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/render_in_hud"),
				L10N.label("elementgui.potioneffect.render_status_hud")));
		selp.add(renderStatusInHUD);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/color"),
				L10N.label("elementgui.potioneffect.color")));
		selp.add(color);

		selp.setOpaque(false);

		pane3.add(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(iconComponent, selp, 30, 30)));
		pane3.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(1, 4, 5, 5));
		events.setOpaque(false);
		events.add(onStarted);
		events.add(onExpired);
		events.add(activeTickCondition);
		events.add(onActiveTick);
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));
		pane4.setOpaque(false);

		icon.setValidator(new TileHolderValidator(icon));
		effectName.setValidator(
				new TextFieldValidator(effectName, L10N.t("elementgui.potioneffect.error_effect_needs_display_name")));
		effectName.enableRealtimeValidation();
		page1group.addValidationElement(effectName);

		page1group.addValidationElement(icon);

		if (!isEditingMode()) {
			String readableNameFromModElement = net.mcreator.util.StringUtils.machineToReadableName(modElement.getName());
			effectName.setText(readableNameFromModElement);
		}

		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		onStarted.refreshListKeepSelected();
		onActiveTick.refreshListKeepSelected();
		onExpired.refreshListKeepSelected();
		activeTickCondition.refreshListKeepSelected();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0) {
			return new AggregatedValidationResult(page1group);
		}
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(PotionEffect potion) {
		effectName.setText(potion.effectName);
		icon.setTextureFromTextureName(
				StringUtils.removeEnd(potion.icon, ".png")); // legacy, old workspaces stored name with extension
		color.setColor(potion.color);
		isInstant.setSelected(potion.isInstant);
		isBad.setSelected(potion.isBad);
		isBenefitical.setSelected(potion.isBenefitical);
		renderStatusInInventory.setSelected(potion.renderStatusInInventory);
		renderStatusInHUD.setSelected(potion.renderStatusInHUD);
		onStarted.setSelectedProcedure(potion.onStarted);
		onActiveTick.setSelectedProcedure(potion.onActiveTick);
		onExpired.setSelectedProcedure(potion.onExpired);
		activeTickCondition.setSelectedProcedure(potion.activeTickCondition);
	}

	@Override public PotionEffect getElementFromGUI() {
		PotionEffect potion = new PotionEffect(modElement);
		potion.effectName = effectName.getText();
		potion.icon = icon.getID() + ".png"; // legacy, old workspaces stored name with extension
		potion.color = color.getColor();
		potion.isInstant = isInstant.isSelected();
		potion.isBad = isBad.isSelected();
		potion.isBenefitical = isBenefitical.isSelected();
		potion.renderStatusInInventory = renderStatusInInventory.isSelected();
		potion.renderStatusInHUD = renderStatusInHUD.isSelected();
		potion.onStarted = onStarted.getSelectedProcedure();
		potion.onActiveTick = onActiveTick.getSelectedProcedure();
		potion.onExpired = onExpired.getSelectedProcedure();
		potion.activeTickCondition = activeTickCondition.getSelectedProcedure();
		return potion;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-potion");
	}

}
