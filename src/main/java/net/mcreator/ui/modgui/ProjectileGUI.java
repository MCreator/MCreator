/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.types.Projectile;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectileGUI extends ModElementGUI<Projectile> {

	private MCItemHolder projectileItem;
	private final JCheckBox showParticles = L10N.checkbox("elementgui.common.enable");
	private final SoundSelector actionSound = new SoundSelector(mcreator);
	private final JCheckBox igniteFire = L10N.checkbox("elementgui.common.enable");
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0, 100, 0.1));
	private final JSpinner damage = new JSpinner(new SpinnerNumberModel(5, 0, 10000, 0.1));
	private final JSpinner knockback = new JSpinner(new SpinnerNumberModel(5, 0, 500, 1));

	private final Model modelDefault = new Model.BuiltInModel("Default");
	private final SearchableComboBox<Model> model = new SearchableComboBox<>(new Model[] { modelDefault });
	private final VComboBox<String> customModelTexture = new SearchableComboBox<>();

	private ProcedureSelector onHitsBlock;
	private ProcedureSelector onHitsPlayer;
	private ProcedureSelector onHitsEntity;
	private ProcedureSelector onFlyingTick;

	private final ValidationGroup page1group = new ValidationGroup();

	public ProjectileGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		projectileItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		onHitsBlock = new ProcedureSelector(this.withEntry("projectile/when_hits_block"), mcreator,
				L10N.t("elementgui.projectile.event_hits_block"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/immediatesourceentity:entity"));
		onHitsPlayer = new ProcedureSelector(this.withEntry("projectile/when_hits_player"), mcreator,
				L10N.t("elementgui.projectile.event_hits_player"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/immediatesourceentity:entity"));
		onHitsEntity = new ProcedureSelector(this.withEntry("projectile/when_hits_entity"), mcreator,
				L10N.t("elementgui.projectile.event_hits_entity"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/immediatesourceentity:entity"));
		onFlyingTick = new ProcedureSelector(this.withEntry("projectile/when_flying_tick"), mcreator,
				L10N.t("elementgui.projectile.event_flying_tick"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/immediatesourceentity:entity"));

		customModelTexture.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");
		customModelTexture.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		model.setPreferredSize(new Dimension(400, 42));
		model.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(model, 16);

		actionSound.setText("entity.arrow.shoot");

		power.setOpaque(false);
		igniteFire.setOpaque(false);
		knockback.setOpaque(false);
		damage.setOpaque(false);
		projectileItem.setOpaque(false);
		showParticles.setOpaque(false);

		JPanel propertiesPanel = new JPanel(new GridLayout(9, 2, 2, 2));
		propertiesPanel.setOpaque(false);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/item_texture"),
				L10N.label("elementgui.projectile.item_texture")));
		propertiesPanel.add(PanelUtils.totalCenterInPanel(projectileItem));

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/model"),
				L10N.label("elementgui.projectile.model")));
		propertiesPanel.add(model);

		JButton importEntityTexture = new JButton(UIRES.get("18px.add"));
		importEntityTexture.setToolTipText(L10N.t("elementgui.projectile.model_tooltip"));
		importEntityTexture.setOpaque(false);
		importEntityTexture.addActionListener(e -> {
			TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ENTITY);
			customModelTexture.removeAllItems();
			customModelTexture.addItem("");
			List<File> textures = mcreator.getFolderManager().getTexturesList(TextureType.ENTITY);
			for (File element : textures)
				if (element.getName().endsWith(".png"))
					customModelTexture.addItem(element.getName());
		});

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/model_texture"),
				L10N.label("elementgui.projectile.model_texture")));
		ComponentUtils.deriveFont(customModelTexture, 16);
		propertiesPanel.add(PanelUtils.centerAndEastElement(customModelTexture, importEntityTexture));

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/action_sound"),
				L10N.label("elementgui.projectile.action_sound")));
		propertiesPanel.add(actionSound);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/show_particles"),
				L10N.label("elementgui.projectile.show_particles")));
		propertiesPanel.add(showParticles);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/power"),
				L10N.label("elementgui.projectile.power")));
		propertiesPanel.add(power);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/damage"),
				L10N.label("elementgui.projectile.damage")));
		propertiesPanel.add(damage);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/knockback"),
				L10N.label("elementgui.projectile.knockback")));
		propertiesPanel.add(knockback);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("projectile/ignite_fire"),
				L10N.label("elementgui.projectile.ignite_fire")));
		propertiesPanel.add(igniteFire);

		JPanel triggersPanels = new JPanel(new BorderLayout());
		triggersPanels.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(2, 2, 5, 5));
		events.setOpaque(false);
		events.add(onHitsBlock);
		events.add(onHitsEntity);
		events.add(onHitsPlayer);
		events.add(onFlyingTick);

		triggersPanels.add("Center",
				PanelUtils.totalCenterInPanel(PanelUtils.maxMargin(events, 20, true, true, true, true)));

		customModelTexture.setValidator(() -> {
			if (!modelDefault.equals(model.getSelectedItem()))
				if (customModelTexture.getSelectedItem() == null || customModelTexture.getSelectedItem().isEmpty())
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("elementgui.projectile.error_custom_model_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});
		projectileItem.setValidator(new MCItemHolderValidator(projectileItem));

		page1group.addValidationElement(projectileItem);
		page1group.addValidationElement(customModelTexture);

		addPage(L10N.t("elementgui.common.page_properties"), PanelUtils.totalCenterInPanel(propertiesPanel));
		addPage(L10N.t("elementgui.common.page_triggers"), triggersPanels);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onHitsBlock.refreshListKeepSelected();
		onHitsPlayer.refreshListKeepSelected();
		onHitsEntity.refreshListKeepSelected();
		onFlyingTick.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(customModelTexture, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(Collections.singletonList(modelDefault),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));
	}

	@Override protected void openInEditingMode(Projectile projectile) {
		projectileItem.setBlock(projectile.projectileItem);
		showParticles.setSelected(projectile.showParticles);
		actionSound.setSound(projectile.actionSound);
		power.setValue(projectile.power);
		damage.setValue(projectile.damage);
		knockback.setValue(projectile.knockback);
		igniteFire.setSelected(projectile.igniteFire);
		customModelTexture.setSelectedItem(projectile.customModelTexture);
		onHitsBlock.setSelectedProcedure(projectile.onHitsBlock);
		onHitsEntity.setSelectedProcedure(projectile.onHitsEntity);
		onHitsPlayer.setSelectedProcedure(projectile.onHitsPlayer);
		onFlyingTick.setSelectedProcedure(projectile.onFlyingTick);

		Model entityModel = projectile.getEntityModel();
		if (entityModel != null && entityModel.getType() != null && entityModel.getReadableName() != null)
			model.setSelectedItem(entityModel);
	}

	@Override public Projectile getElementFromGUI() {
		Projectile projectile = new Projectile(modElement);
		projectile.projectileItem = projectileItem.getBlock();
		projectile.showParticles = showParticles.isSelected();
		projectile.actionSound = actionSound.getSound();
		projectile.igniteFire = igniteFire.isSelected();
		projectile.power = (double) power.getValue();
		projectile.damage = (double) damage.getValue();
		projectile.knockback = (int) knockback.getValue();
		projectile.entityModel = (Objects.requireNonNull(model.getSelectedItem())).getReadableName();
		projectile.customModelTexture = customModelTexture.getSelectedItem();
		projectile.onHitsBlock = onHitsBlock.getSelectedProcedure();
		projectile.onHitsEntity = onHitsEntity.getSelectedProcedure();
		projectile.onHitsPlayer = onHitsPlayer.getSelectedProcedure();
		projectile.onFlyingTick = onFlyingTick.getSelectedProcedure();
		return projectile;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-projectile");
	}

}
