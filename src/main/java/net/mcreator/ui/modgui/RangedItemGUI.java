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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.RangedItem;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.resources.Model;

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

public class RangedItemGUI extends ModElementGUI<RangedItem> {

	private TextureHolder texture;

	private final JCheckBox shootConstantly = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(13);

	private final JCheckBox bulletParticles = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox bulletIgnitesFire = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox hasGlow = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector glowCondition;

	private final JComboBox<String> animation = new JComboBox<>(
			new String[] { "bow", "block", "crossbow", "drink", "eat", "none", "spear" });

	private ProcedureSelector onBulletHitsBlock;
	private ProcedureSelector onBulletHitsPlayer;
	private ProcedureSelector onBulletHitsEntity;
	private ProcedureSelector onBulletFlyingTick;
	private ProcedureSelector onRangedItemUsed;
	private ProcedureSelector onEntitySwing;

	private final SoundSelector shootSound = new SoundSelector(mcreator);

	private final JTextField specialInfo = new JTextField(20);

	private MCItemHolder ammoItem;
	private MCItemHolder bulletItemTexture;

	private final JSpinner usageCount = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
	private final JSpinner bulletPower = new JSpinner(new SpinnerNumberModel(1, 0, 100, 0.1));
	private final JSpinner bulletDamage = new JSpinner(new SpinnerNumberModel(5, 0, 10000, 0.1));
	private final JSpinner bulletKnockback = new JSpinner(new SpinnerNumberModel(5, 0, 500, 1));

	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(1, 0, 64, 1));

	private final Model normal = new Model.BuiltInModel("Normal");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(new Model[] { normal });

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final Model adefault = new Model.BuiltInModel("Default");
	private final SearchableComboBox<Model> bulletModel = new SearchableComboBox<>();
	private final VComboBox<String> customBulletModelTexture = new SearchableComboBox<>();

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page2group = new ValidationGroup();

	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private ProcedureSelector useCondition;

	public RangedItemGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		ammoItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		bulletItemTexture = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		onBulletHitsBlock = new ProcedureSelector(this.withEntry("rangeditem/when_bullet_hits_block"), mcreator,
				L10N.t("elementgui.ranged_item.event_bullet_hits_block"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onBulletHitsPlayer = new ProcedureSelector(this.withEntry("rangeditem/when_bullet_hits_player"), mcreator,
				L10N.t("elementgui.ranged_item.event_bullet_hits_player"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity"));
		onBulletHitsEntity = new ProcedureSelector(this.withEntry("rangeditem/when_bullet_hits_entity"), mcreator,
				L10N.t("elementgui.ranged_item.event_bullet_hits_entity"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity"));
		onBulletFlyingTick = new ProcedureSelector(this.withEntry("rangeditem/when_bullet_flying_tick"), mcreator,
				L10N.t("elementgui.ranged_item.event_bullet_flying_tick"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onRangedItemUsed = new ProcedureSelector(this.withEntry("rangeditem/when_used"), mcreator,
				L10N.t("elementgui.ranged_item.event_on_use"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				L10N.t("elementgui.ranged_item.swinged_by_entity"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		useCondition = new ProcedureSelector(this.withEntry("rangeditem/use_condition"), mcreator,
				L10N.t("elementgui.ranged_item.can_use"), VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		glowCondition = new ProcedureSelector(this.withEntry("item/condition_glow"), mcreator,
				L10N.t("elementgui.ranged_item.make_glow"), ProcedureSelector.Side.CLIENT, true,
				VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		customBulletModelTexture.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");

		customBulletModelTexture.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));

		bulletModel.setPreferredSize(new Dimension(400, 42));
		bulletModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(bulletModel, 16);
		ComponentUtils.deriveFont(specialInfo, 16);

		JPanel pane1 = new JPanel(new BorderLayout(10, 10));
		JPanel pane2 = new JPanel(new BorderLayout(10, 10));

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM));
		texture.setOpaque(false);

		hasGlow.setOpaque(false);
		hasGlow.setSelected(false);

		animation.setRenderer(new ItemTexturesComboBoxRenderer());

		ComponentUtils.deriveFont(renderType, 16.0f);
		renderType.setRenderer(new ModelComboBoxRenderer());

		JPanel sbbp2 = new JPanel(new BorderLayout(0, 2));
		sbbp2.setOpaque(false);

		sbbp2.add("North", PanelUtils.westAndEastElement(PanelUtils
						.centerInPanel(ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.ranged_item.texture"))),
				PanelUtils.join(useCondition, onRangedItemUsed, onEntitySwing)));

		sbbp2.add("South", PanelUtils.westAndEastElement(HelpUtils
				.wrapWithHelpButton(this.withEntry("item/glowing_effect"),
						L10N.label("elementgui.ranged_item.enable_glowing")), PanelUtils.join(hasGlow, glowCondition)));

		pane1.setOpaque(false);

		pane1.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel selp = new JPanel(new GridLayout(11, 2, 5, 2));
		selp.setOpaque(false);

		JPanel selp2 = new JPanel(new GridLayout(8, 2, 10, 2));
		selp2.setOpaque(false);

		bulletParticles.setOpaque(false);
		bulletIgnitesFire.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		shootConstantly.setOpaque(false);

		selp.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		selp.add(renderType);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/gui_name"), L10N.label("elementgui.common.name_in_gui")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"),
				L10N.label("elementgui.ranged_item.special_informations")));
		selp.add(specialInfo);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		selp.add(creativeTab);

		hasGlow.addActionListener(e -> updateGlowElements());

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/animation"),
				L10N.label("elementgui.ranged_item.item_animation")));
		selp.add(animation);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("item/stack_size"), L10N.label("elementgui.common.max_stack_size")));
		selp.add(stackSize);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
				L10N.label("elementgui.ranged_item.damages_vs_mob")));
		selp.add(PanelUtils.westAndCenterElement(enableMeleeDamage, damageVsEntity));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/ammo_item"),
				L10N.label("elementgui.ranged_item.ammo_item")));
		selp.add(PanelUtils.join(ammoItem));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/shoot_constantly"),
				L10N.label("elementgui.ranged_item.shoot_constantly")));
		selp.add(shootConstantly);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.ranged_item.number_of_uses")));
		selp.add(usageCount);

		sbbp2.add("Center", selp);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_power"),
				L10N.label("elementgui.ranged_item.bullet_power")));
		selp2.add(bulletPower);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_damage"),
				L10N.label("elementgui.ranged_item.bullet_damage")));
		selp2.add(bulletDamage);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_knockback"),
				L10N.label("elementgui.ranged_item.bullet_knockback")));
		selp2.add(bulletKnockback);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_particles"),
				L10N.label("elementgui.ranged_item.bullet_particles")));
		selp2.add(bulletParticles);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_ignite_fire"),
				L10N.label("elementgui.ranged_item.bullet_ignite_fire")));
		selp2.add(bulletIgnitesFire);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_item_texture"),
				L10N.label("elementgui.ranged_item.bullet_item_texture")));
		selp2.add(PanelUtils.centerInPanel(bulletItemTexture));

		shootSound.setText("entity.arrow.shoot");

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/action_sound"),
				L10N.label("elementgui.ranged_item.ranged_action_sound")));
		selp.add(shootSound);

		usageCount.setOpaque(false);
		bulletPower.setOpaque(false);
		bulletDamage.setOpaque(false);
		bulletKnockback.setOpaque(false);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/bullet_model"),
				L10N.label("elementgui.ranged_item.bullet_model")));

		ComponentUtils.deriveFont(customBulletModelTexture, 16);

		selp2.add(bulletModel);

		JButton importmobtexture = new JButton(UIRES.get("18px.add"));
		importmobtexture.setToolTipText(L10N.t("elementgui.ranged_item.bullet_model_tooltip"));
		importmobtexture.setOpaque(false);
		importmobtexture.addActionListener(e -> {
			TextureImportDialogs.importOtherTextures(mcreator);
			customBulletModelTexture.removeAllItems();
			customBulletModelTexture.addItem("");
			List<File> textures = mcreator.getFolderManager().getOtherTexturesList();
			for (File element : textures)
				if (element.getName().endsWith(".png"))
					customBulletModelTexture.addItem(element.getName());
		});

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("rangeditem/model_texture"),
				L10N.label("elementgui.ranged_item.model_texture")));

		selp2.add(PanelUtils.centerAndEastElement(customBulletModelTexture, importmobtexture));

		JPanel slpa = new JPanel(new BorderLayout(0, 10));
		slpa.setOpaque(false);

		JPanel eventsal = new JPanel(new GridLayout(1, 4, 10, 10));
		eventsal.setOpaque(false);

		eventsal.add(onBulletHitsBlock);
		eventsal.add(onBulletHitsPlayer);
		eventsal.add(onBulletHitsEntity);
		eventsal.add(onBulletFlyingTick);

		slpa.add("North", selp2);
		slpa.add("Center", PanelUtils.centerInPanel(eventsal));

		pane2.setOpaque(false);

		pane2.add("Center", PanelUtils.totalCenterInPanel(slpa));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);
		page1group.addValidationElement(name);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.ranged_item.error_item_needs_name")));
		name.enableRealtimeValidation();
		bulletItemTexture.setValidator(() -> {
			if (bulletItemTexture.containsItem() || !adefault.equals(bulletModel.getSelectedItem()))
				return Validator.ValidationResult.PASSED;
			else
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.ranged_item.error_select_element"));
		});

		customBulletModelTexture.setValidator(() -> {
			if (!adefault.equals(bulletModel.getSelectedItem()))
				if (customBulletModelTexture.getSelectedItem() == null || customBulletModelTexture.getSelectedItem()
						.equals(""))
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("elementgui.ranged_item.error_custom_model_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});

		page2group.addValidationElement(bulletItemTexture);
		page2group.addValidationElement(customBulletModelTexture);

		addPage(L10N.t("elementgui.ranged_item.page_ranged_item"), pane1);
		addPage(L10N.t("elementgui.ranged_item.page_bullet"), pane2);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void updateGlowElements() {
		glowCondition.setEnabled(hasGlow.isSelected());
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onBulletHitsBlock.refreshListKeepSelected();
		onBulletHitsPlayer.refreshListKeepSelected();
		onBulletHitsEntity.refreshListKeepSelected();
		onBulletFlyingTick.refreshListKeepSelected();
		onRangedItemUsed.refreshListKeepSelected();
		onEntitySwing.refreshListKeepSelected();

		useCondition.refreshListKeepSelected();
		glowCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(customBulletModelTexture, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(bulletModel, ListUtils.merge(Collections.singletonList(adefault),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("COMBAT"));

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Collections.singletonList(normal),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		else if (page == 1)
			return new AggregatedValidationResult(page2group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(RangedItem rangedItem) {
		creativeTab.setSelectedItem(rangedItem.creativeTab);
		shootConstantly.setSelected(rangedItem.shootConstantly);
		name.setText(rangedItem.name);
		shootSound.setSound(rangedItem.actionSound);
		stackSize.setValue(rangedItem.stackSize);
		texture.setTextureFromTextureName(rangedItem.texture);
		bulletItemTexture.setBlock(rangedItem.bulletItemTexture);
		ammoItem.setBlock(rangedItem.ammoItem);
		usageCount.setValue(rangedItem.usageCount);
		bulletPower.setValue(rangedItem.bulletPower);
		bulletDamage.setValue(rangedItem.bulletDamage);
		bulletKnockback.setValue(rangedItem.bulletKnockback);
		bulletParticles.setSelected(rangedItem.bulletParticles);
		bulletIgnitesFire.setSelected(rangedItem.bulletIgnitesFire);
		onBulletHitsBlock.setSelectedProcedure(rangedItem.onBulletHitsBlock);
		onBulletHitsPlayer.setSelectedProcedure(rangedItem.onBulletHitsPlayer);
		onBulletHitsEntity.setSelectedProcedure(rangedItem.onBulletHitsEntity);
		onBulletFlyingTick.setSelectedProcedure(rangedItem.onBulletFlyingTick);
		onEntitySwing.setSelectedProcedure(rangedItem.onEntitySwing);
		onRangedItemUsed.setSelectedProcedure(rangedItem.onRangedItemUsed);
		hasGlow.setSelected(rangedItem.hasGlow);
		glowCondition.setSelectedProcedure(rangedItem.glowCondition);
		animation.setSelectedItem(rangedItem.animation);
		damageVsEntity.setValue(rangedItem.damageVsEntity);
		enableMeleeDamage.setSelected(rangedItem.enableMeleeDamage);
		specialInfo.setText(
				rangedItem.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		customBulletModelTexture.setSelectedItem(rangedItem.customBulletModelTexture);
		useCondition.setSelectedProcedure(rangedItem.useCondition);

		updateGlowElements();

		Model model = rangedItem.getEntityModel();
		if (model != null && model.getType() != null && model.getReadableName() != null)
			bulletModel.setSelectedItem(model);

		Model model2 = rangedItem.getItemModel();
		if (model2 != null)
			renderType.setSelectedItem(model2);
	}

	@Override public RangedItem getElementFromGUI() {
		RangedItem rangedItem = new RangedItem(modElement);
		rangedItem.name = name.getText();
		rangedItem.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		rangedItem.ammoItem = ammoItem.getBlock();
		rangedItem.shootConstantly = shootConstantly.isSelected();
		rangedItem.usageCount = (int) usageCount.getValue();
		rangedItem.actionSound = shootSound.getSound();
		rangedItem.bulletPower = (double) bulletPower.getValue();
		rangedItem.bulletDamage = (double) bulletDamage.getValue();
		rangedItem.bulletKnockback = (int) bulletKnockback.getValue();
		rangedItem.bulletParticles = bulletParticles.isSelected();
		rangedItem.bulletIgnitesFire = bulletIgnitesFire.isSelected();
		rangedItem.bulletItemTexture = bulletItemTexture.getBlock();
		rangedItem.onBulletHitsBlock = onBulletHitsBlock.getSelectedProcedure();
		rangedItem.onBulletHitsPlayer = onBulletHitsPlayer.getSelectedProcedure();
		rangedItem.onBulletHitsEntity = onBulletHitsEntity.getSelectedProcedure();
		rangedItem.onBulletFlyingTick = onBulletFlyingTick.getSelectedProcedure();
		rangedItem.onRangedItemUsed = onRangedItemUsed.getSelectedProcedure();
		rangedItem.onEntitySwing = onEntitySwing.getSelectedProcedure();
		rangedItem.stackSize = (int) stackSize.getValue();
		rangedItem.hasGlow = hasGlow.isSelected();
		rangedItem.glowCondition = glowCondition.getSelectedProcedure();
		rangedItem.animation = (String) animation.getSelectedItem();
		rangedItem.damageVsEntity = (double) damageVsEntity.getValue();
		rangedItem.enableMeleeDamage = enableMeleeDamage.isSelected();
		rangedItem.bulletModel = (Objects.requireNonNull(bulletModel.getSelectedItem())).getReadableName();
		rangedItem.customBulletModelTexture = customBulletModelTexture.getSelectedItem();
		rangedItem.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());
		rangedItem.useCondition = useCondition.getSelectedProcedure();

		rangedItem.texture = texture.getID();
		Model.Type modelType = (Objects.requireNonNull(renderType.getSelectedItem())).getType();
		rangedItem.renderType = 0;
		if (modelType == Model.Type.JSON)
			rangedItem.renderType = 1;
		else if (modelType == Model.Type.OBJ)
			rangedItem.renderType = 2;
		rangedItem.customModelName = (Objects.requireNonNull(renderType.getSelectedItem())).getReadableName();

		return rangedItem;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-gun");
	}

}
