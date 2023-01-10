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
import net.mcreator.element.parts.ProjectileEntry;
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
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class RangedItemGUI extends ModElementGUI<RangedItem> {

	private TextureHolder texture;

	private final JCheckBox shootConstantly = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(13);

	private final JCheckBox hasGlow = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector glowCondition;

	private final JComboBox<String> animation = new JComboBox<>(
			new String[] { "bow", "block", "crossbow", "drink", "eat", "none", "spear" });

	private ProcedureSelector onRangedItemUsed;
	private ProcedureSelector onEntitySwing;

	private final JTextField specialInfo = new JTextField(20);

	private final DataListComboBox ammoItem = new DataListComboBox(mcreator);

	private final JSpinner usageCount = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(1, 0, 64, 1));

	private final Model normal = new Model.BuiltInModel("Normal");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(new Model[] { normal });

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final ValidationGroup page1group = new ValidationGroup();

	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private ProcedureSelector useCondition;

	public RangedItemGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onRangedItemUsed = new ProcedureSelector(this.withEntry("rangeditem/when_used"), mcreator,
				L10N.t("elementgui.ranged_item.event_on_use"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				L10N.t("elementgui.ranged_item.swinged_by_entity"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		useCondition = new ProcedureSelector(this.withEntry("rangeditem/use_condition"), mcreator,
				L10N.t("elementgui.ranged_item.can_use"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		glowCondition = new ProcedureSelector(this.withEntry("item/condition_glow"), mcreator,
				L10N.t("elementgui.ranged_item.make_glow"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack")).makeInline();

		ComponentUtils.deriveFont(specialInfo, 16);

		JPanel pane1 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));

		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		texture.setOpaque(false);

		hasGlow.setOpaque(false);
		hasGlow.setSelected(false);

		animation.setRenderer(new ItemTexturesComboBoxRenderer());

		ComponentUtils.deriveFont(renderType, 16.0f);
		renderType.setRenderer(new ModelComboBoxRenderer());

		JPanel sbbp2 = new JPanel(new BorderLayout(0, 2));
		sbbp2.setOpaque(false);

		sbbp2.add("North", PanelUtils.centerInPanel(
				ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.ranged_item.texture"))));

		sbbp2.add("South", PanelUtils.westAndEastElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/glowing_effect"),
						L10N.label("elementgui.ranged_item.enable_glowing")), PanelUtils.join(hasGlow, glowCondition)));

		pane1.setOpaque(false);

		pane1.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel selp = new JPanel(new GridLayout(11, 2, 5, 2));
		selp.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		shootConstantly.setOpaque(false);

		selp.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		selp.add(renderType);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
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

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
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

		usageCount.setOpaque(false);

		JPanel slpa = new JPanel(new BorderLayout(0, 10));
		slpa.setOpaque(false);

		JPanel eventsal = new JPanel(new GridLayout(2, 2, 10, 10));
		eventsal.setOpaque(false);

		slpa.add("Center", PanelUtils.centerInPanel(eventsal));

		JPanel itemEvents = new JPanel(new GridLayout(1, 3, 10, 10));
		itemEvents.setOpaque(false);
		itemEvents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		itemEvents.add(useCondition);
		itemEvents.add(onRangedItemUsed);
		itemEvents.add(onEntitySwing);

		JPanel itemEventsWrap = new JPanel(new GridLayout());
		itemEventsWrap.setOpaque(false);
		itemEventsWrap.add(itemEvents);
		itemEventsWrap.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.ranged_item.item_events"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel triggersPanel = new JPanel(new BorderLayout(0, 10));
		triggersPanel.setOpaque(false);
		triggersPanel.add("Center", itemEventsWrap);

		pane3.setOpaque(false);
		pane3.add("Center", PanelUtils.totalCenterInPanel(triggersPanel));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);
		page1group.addValidationElement(name);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.ranged_item.error_item_needs_name")));
		name.enableRealtimeValidation();

		addPage(L10N.t("elementgui.common.page_properties"), pane1);
		addPage(L10N.t("elementgui.common.page_triggers"), pane3);

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
		onEntitySwing.refreshListKeepSelected();

		useCondition.refreshListKeepSelected();
		glowCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("COMBAT"));

		ComboBoxUtil.updateComboBoxContents(ammoItem, ElementUtil.loadArrowProjectiles(mcreator.getWorkspace()),
				new DataListEntry.Dummy("Arrow"));

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Collections.singletonList(normal),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(RangedItem rangedItem) {
		creativeTab.setSelectedItem(rangedItem.creativeTab);
		shootConstantly.setSelected(rangedItem.shootConstantly);
		name.setText(rangedItem.name);
		stackSize.setValue(rangedItem.stackSize);
		texture.setTextureFromTextureName(rangedItem.texture);
		ammoItem.setSelectedItem(rangedItem.ammoItem);
		usageCount.setValue(rangedItem.usageCount);
		onEntitySwing.setSelectedProcedure(rangedItem.onEntitySwing);
		onRangedItemUsed.setSelectedProcedure(rangedItem.onRangedItemUsed);
		hasGlow.setSelected(rangedItem.hasGlow);
		glowCondition.setSelectedProcedure(rangedItem.glowCondition);
		animation.setSelectedItem(rangedItem.animation);
		damageVsEntity.setValue(rangedItem.damageVsEntity);
		enableMeleeDamage.setSelected(rangedItem.enableMeleeDamage);
		specialInfo.setText(
				rangedItem.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		useCondition.setSelectedProcedure(rangedItem.useCondition);

		updateGlowElements();

		Model model2 = rangedItem.getItemModel();
		if (model2 != null)
			renderType.setSelectedItem(model2);
	}

	@Override public RangedItem getElementFromGUI() {
		RangedItem rangedItem = new RangedItem(modElement);
		rangedItem.name = name.getText();
		rangedItem.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		rangedItem.ammoItem = new ProjectileEntry(mcreator.getWorkspace(), ammoItem.getSelectedItem());
		rangedItem.shootConstantly = shootConstantly.isSelected();
		rangedItem.usageCount = (int) usageCount.getValue();
		rangedItem.onRangedItemUsed = onRangedItemUsed.getSelectedProcedure();
		rangedItem.onEntitySwing = onEntitySwing.getSelectedProcedure();
		rangedItem.stackSize = (int) stackSize.getValue();
		rangedItem.hasGlow = hasGlow.isSelected();
		rangedItem.glowCondition = glowCondition.getSelectedProcedure();
		rangedItem.animation = (String) animation.getSelectedItem();
		rangedItem.damageVsEntity = (double) damageVsEntity.getValue();
		rangedItem.enableMeleeDamage = enableMeleeDamage.isSelected();
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

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-gun");
	}

}
