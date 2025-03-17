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

import net.mcreator.element.types.Enchantment;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.EnchantmentListField;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.CompoundValidator;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ItemListFieldSingleTagValidator;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class EnchantmentGUI extends ModElementGUI<Enchantment> {

	private final VTextField name = new VTextField(20);

	private final JSpinner weight = new JSpinner(new SpinnerNumberModel(10, 1, 1024, 1));
	private final JSpinner anvilCost = new JSpinner(new SpinnerNumberModel(1, 1, 1024, 1));

	private final JComboBox<String> supportedSlots = new JComboBox<>(
			new String[] { "any", "mainhand", "offhand", "hand", "feet", "legs", "chest", "head", "armor", "body" });

	private final JSpinner maxLevel = new JSpinner(new SpinnerNumberModel(4, 1, 255, 1));

	private final JSpinner damageModifier = new JSpinner(new SpinnerNumberModel(0, -1024, 1024, 1));

	private final JCheckBox isTreasureEnchantment = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isCurse = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canGenerateInLootTables = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canVillagerTrade = L10N.checkbox("elementgui.common.enable");

	private MCItemListField supportedItems;
	private EnchantmentListField incompatibleEnchantments;

	private final ValidationGroup page1group = new ValidationGroup();

	public EnchantmentGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		supportedItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItemsAndTags, false, true);
		incompatibleEnchantments = new EnchantmentListField(mcreator, true);

		supportedItems.addAdditionalTagSuggestions("enchantable/foot_armor", "enchantable/leg_armor",
				"enchantable/chest_armor", "enchantable/head_armor", "enchantable/armor", "enchantable/sword",
				"enchantable/fire_aspect", "enchantable/sharp_weapon", "enchantable/weapon", "enchantable/mining",
				"enchantable/mining_loot", "enchantable/fishing", "enchantable/trident", "enchantable/durability",
				"enchantable/bow", "enchantable/equippable", "enchantable/crossbow", "enchantable/vanishing",
				"enchantable/mace");

		JPanel pane1 = new JPanel(new BorderLayout());

		pane1.setOpaque(false);

		isCurse.setOpaque(false);
		isTreasureEnchantment.setOpaque(false);
		canGenerateInLootTables.setOpaque(false);
		canVillagerTrade.setOpaque(false);

		canGenerateInLootTables.setSelected(true);
		canVillagerTrade.setSelected(true);

		ComponentUtils.deriveFont(name, 16);

		JPanel selp = new JPanel(new GridLayout(12, 2, 50, 2));
		selp.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/name"),
				L10N.label("elementgui.enchantment.name")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/weight"),
				L10N.label("elementgui.enchantment.weight")));
		selp.add(weight);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/level"),
				L10N.label("elementgui.enchantment.level")));
		selp.add(maxLevel);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/supported_items"),
				L10N.label("elementgui.enchantment.supported_items")));
		selp.add(supportedItems);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/supported_slots"),
				L10N.label("elementgui.enchantment.supported_slots")));
		selp.add(supportedSlots);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/incompatible_enchantments"),
				L10N.label("elementgui.enchantment.incompatible_enchantments")));
		selp.add(incompatibleEnchantments);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/anvil_cost"),
				L10N.label("elementgui.enchantment.anvil_cost")));
		selp.add(anvilCost);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/damage_modifier"),
				L10N.label("elementgui.enchantment.damage_modifier")));
		selp.add(damageModifier);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/treasure_enchantment"),
				L10N.label("elementgui.enchantment.treasure_enchantment")));
		selp.add(isTreasureEnchantment);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/curse"),
				L10N.label("elementgui.enchantment.curse")));
		selp.add(isCurse);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/generate_in_loot_tables"),
				L10N.label("elementgui.enchantment.can_generate_in_loot_tables")));
		selp.add(canGenerateInLootTables);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/villager_trade"),
				L10N.label("elementgui.enchantment.can_villager_trade")));
		selp.add(canVillagerTrade);

		pane1.add(PanelUtils.totalCenterInPanel(selp));

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.enchantment.needs_name")));
		name.enableRealtimeValidation();

		supportedItems.setValidator(new CompoundValidator(
				new ItemListFieldValidator(supportedItems, L10N.t("elementgui.enchantment.supported_items.error")),
				new ItemListFieldSingleTagValidator(supportedItems)));

		incompatibleEnchantments.setValidator(new ItemListFieldSingleTagValidator(incompatibleEnchantments));

		page1group.addValidationElement(name);
		page1group.addValidationElement(supportedItems);
		page1group.addValidationElement(incompatibleEnchantments);

		addPage(pane1);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group);
	}

	@Override public void openInEditingMode(Enchantment enchantment) {
		name.setText(enchantment.name);
		supportedSlots.setSelectedItem(enchantment.supportedSlots);
		weight.setValue(enchantment.weight);
		anvilCost.setValue(enchantment.anvilCost);
		maxLevel.setValue(enchantment.maxLevel);
		damageModifier.setValue(enchantment.damageModifier);
		incompatibleEnchantments.setListElements(enchantment.incompatibleEnchantments);
		supportedItems.setListElements(enchantment.supportedItems);
		isTreasureEnchantment.setSelected(enchantment.isTreasureEnchantment);
		isCurse.setSelected(enchantment.isCurse);
		canGenerateInLootTables.setSelected(enchantment.canGenerateInLootTables);
		canVillagerTrade.setSelected(enchantment.canVillagerTrade);
	}

	@Override public Enchantment getElementFromGUI() {
		Enchantment enchantment = new Enchantment(modElement);
		enchantment.name = name.getText();
		enchantment.supportedSlots = (String) supportedSlots.getSelectedItem();
		enchantment.weight = (int) weight.getValue();
		enchantment.anvilCost = (int) anvilCost.getValue();
		enchantment.maxLevel = (int) maxLevel.getValue();
		enchantment.damageModifier = (int) damageModifier.getValue();
		enchantment.incompatibleEnchantments = incompatibleEnchantments.getListElements();
		enchantment.supportedItems = supportedItems.getListElements();
		enchantment.isTreasureEnchantment = isTreasureEnchantment.isSelected();
		enchantment.isCurse = isCurse.isSelected();
		enchantment.canGenerateInLootTables = canGenerateInLootTables.isSelected();
		enchantment.canVillagerTrade = canVillagerTrade.isSelected();
		return enchantment;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-enchantment");
	}

}
