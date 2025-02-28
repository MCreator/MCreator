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
import net.mcreator.ui.minecraft.SingleParticleEntryField;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.minecraft.attributemodifiers.JAttributeModifierList;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class PotionEffectGUI extends ModElementGUI<PotionEffect> {

	private final VTextField effectName = new VTextField(20);
	private final JColor color = new JColor(mcreator, false, false);
	private SingleParticleEntryField particle;
	private final SoundSelector onAddedSound = new SoundSelector(mcreator);
	private TextureSelectionButton icon;

	private final JCheckBox isInstant = L10N.checkbox("elementgui.potioneffect.is_instant");
	private final JCheckBox renderStatusInInventory = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox renderStatusInHUD = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isCuredbyHoney = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> mobEffectCategory = new JComboBox<>(
			new String[] { "NEUTRAL", "HARMFUL", "BENEFICIAL" });

	private final ValidationGroup page1group = new ValidationGroup();

	private JAttributeModifierList modifierList;

	private ProcedureSelector onStarted;
	private ProcedureSelector onActiveTick;
	private ProcedureSelector onExpired;
	private ProcedureSelector activeTickCondition;
	private ProcedureSelector onMobHurt;
	private ProcedureSelector onMobRemoved;

	public PotionEffectGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		modifierList = new JAttributeModifierList(mcreator, this);

		onStarted = new ProcedureSelector(this.withEntry("potioneffect/when_potion_applied"), mcreator,
				L10N.t("elementgui.potioneffect.event_potion_applied"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		onActiveTick = new ProcedureSelector(this.withEntry("potioneffect/when_active_tick"), mcreator,
				L10N.t("elementgui.potioneffect.event_potion_tick"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		onExpired = new ProcedureSelector(this.withEntry("potioneffect/when_potion_expires"), mcreator,
				L10N.t("elementgui.potioneffect.event_potion_expires"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));
		activeTickCondition = new ProcedureSelector(this.withEntry("potioneffect/active_tick_condition"), mcreator,
				L10N.t("elementgui.potioneffect.event_tick_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("duration:number/amplifier:number"));
		onMobHurt = new ProcedureSelector(this.withEntry("potioneffect/on_mob_hurt"), mcreator,
				L10N.t("elementgui.potioneffect.event_mob_hurt"), ProcedureSelector.Side.SERVER, Dependency.fromString(
				"entity:entity/x:number/y:number/z:number/world:world/amplifier:number/damagesource:damagesource/amount:number"));
		onMobRemoved = new ProcedureSelector(this.withEntry("potioneffect/on_mob_death"), mcreator,
				L10N.t("elementgui.potioneffect.event_mob_death"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("entity:entity/x:number/y:number/z:number/world:world/amplifier:number"));

		renderStatusInInventory.setSelected(true);
		renderStatusInHUD.setSelected(true);

		JPanel pane3 = new JPanel(new BorderLayout());
		JPanel modifiersPage = new JPanel(new BorderLayout());
		JPanel pane4 = new JPanel(new BorderLayout());

		JPanel selp = new JPanel(new GridLayout(10, 2, 50, 2));

		ComponentUtils.deriveFont(effectName, 16);

		isInstant.setOpaque(false);
		isInstant.addActionListener(e -> particle.setEnabled(!isInstant.isSelected()));
		renderStatusInInventory.setOpaque(false);
		renderStatusInHUD.setOpaque(false);
		isCuredbyHoney.setOpaque(false);

		icon = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.EFFECT));
		icon.setOpaque(false);

		particle = new SingleParticleEntryField(mcreator);
		particle.setDefaultText(L10N.t("elementgui.potioneffect.particles.default"));

		JComponent iconComponent = PanelUtils.totalCenterInPanel(
				ComponentUtils.squareAndBorder(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/icon"), icon),
						L10N.t("elementgui.potioneffect.icon")));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/effect_display_name"),
				L10N.label("elementgui.potioneffect.display_name")));
		selp.add(effectName);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/instant"),
				L10N.label("elementgui.potioneffect.instant")));
		selp.add(isInstant);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/category"),
				L10N.label("elementgui.potioneffect.category")));
		selp.add(mobEffectCategory);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/render_in_inventory"),
				L10N.label("elementgui.potioneffect.render_status_inventory")));
		selp.add(renderStatusInInventory);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/render_in_hud"),
				L10N.label("elementgui.potioneffect.render_status_hud")));
		selp.add(renderStatusInHUD);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/color"),
				L10N.label("elementgui.potioneffect.color")));
		selp.add(color);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/particles"),
				L10N.label("elementgui.potioneffect.particles")));
		selp.add(particle);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/on_added_sound"),
				L10N.label("elementgui.potioneffect.on_added_sound")));
		selp.add(onAddedSound);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/cured_by_honey"),
				L10N.label("elementgui.potioneffect.cured_by_honey")));
		selp.add(isCuredbyHoney);

		selp.setOpaque(false);

		pane3.add("Center",
				PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(iconComponent, selp, 30, 30)));
		pane3.setOpaque(false);

		JComponent modifiersEditor = PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("potioneffect/modifiers"),
						L10N.label("elementgui.potioneffect.modifiers")), modifierList);
		modifiersEditor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		modifiersPage.add("Center", modifiersEditor);
		modifiersPage.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(2, 3, 5, 5));
		events.setOpaque(false);
		events.add(onStarted);
		events.add(activeTickCondition);
		events.add(onActiveTick);
		events.add(onExpired);
		events.add(onMobHurt);
		events.add(onMobRemoved);
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));
		pane4.setOpaque(false);

		icon.setValidator(new TileHolderValidator(icon));
		effectName.setValidator(
				new TextFieldValidator(effectName, L10N.t("elementgui.potioneffect.error_effect_needs_display_name")));
		effectName.enableRealtimeValidation();
		page1group.addValidationElement(effectName);

		page1group.addValidationElement(icon);

		if (!isEditingMode()) {
			String readableNameFromModElement = net.mcreator.util.StringUtils.machineToReadableName(
					modElement.getName());
			effectName.setText(readableNameFromModElement);
		}

		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.potioneffect.page_attribute_modifiers"), modifiersPage);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		modifierList.reloadDataLists();

		onStarted.refreshListKeepSelected();
		onActiveTick.refreshListKeepSelected();
		onExpired.refreshListKeepSelected();
		activeTickCondition.refreshListKeepSelected();
		onMobHurt.refreshListKeepSelected();
		onMobRemoved.refreshListKeepSelected();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0) {
			return new AggregatedValidationResult(page1group);
		} else if (page == 1) {
			return modifierList.getValidationResult();
		}
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(PotionEffect potion) {
		effectName.setText(potion.effectName);
		icon.setTexture(potion.icon);
		color.setColor(potion.color);
		particle.setEntry(potion.particle);
		onAddedSound.setSound(potion.onAddedSound);
		isInstant.setSelected(potion.isInstant);
		mobEffectCategory.setSelectedItem(potion.mobEffectCategory);
		renderStatusInInventory.setSelected(potion.renderStatusInInventory);
		renderStatusInHUD.setSelected(potion.renderStatusInHUD);
		onStarted.setSelectedProcedure(potion.onStarted);
		onActiveTick.setSelectedProcedure(potion.onActiveTick);
		onExpired.setSelectedProcedure(potion.onExpired);
		activeTickCondition.setSelectedProcedure(potion.activeTickCondition);
		onMobHurt.setSelectedProcedure(potion.onMobHurt);
		onMobRemoved.setSelectedProcedure(potion.onMobRemoved);
		isCuredbyHoney.setSelected(potion.isCuredbyHoney);
		modifierList.setEntries(potion.modifiers);

		particle.setEnabled(!isInstant.isSelected());
	}

	@Override public PotionEffect getElementFromGUI() {
		PotionEffect potion = new PotionEffect(modElement);
		potion.effectName = effectName.getText();
		potion.icon = icon.getTextureHolder();
		potion.color = color.getColor();
		potion.particle = particle.getEntry();
		potion.onAddedSound = onAddedSound.getSound();
		potion.isInstant = isInstant.isSelected();
		potion.mobEffectCategory = (String) mobEffectCategory.getSelectedItem();
		potion.renderStatusInInventory = renderStatusInInventory.isSelected();
		potion.renderStatusInHUD = renderStatusInHUD.isSelected();
		potion.onStarted = onStarted.getSelectedProcedure();
		potion.onActiveTick = onActiveTick.getSelectedProcedure();
		potion.onExpired = onExpired.getSelectedProcedure();
		potion.activeTickCondition = activeTickCondition.getSelectedProcedure();
		potion.onMobHurt = onMobHurt.getSelectedProcedure();
		potion.onMobRemoved = onMobRemoved.getSelectedProcedure();
		potion.isCuredbyHoney = isCuredbyHoney.isSelected();
		potion.modifiers = modifierList.getEntries();
		return potion;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-potion");
	}

}
