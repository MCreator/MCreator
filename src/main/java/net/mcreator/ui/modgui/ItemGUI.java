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
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.GUI;
import net.mcreator.element.types.Item;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemGUI extends ModElementGUI<Item> {

	private TextureHolder texture;

	private final JTextField specialInfo = new JTextField(20);

	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 0, 64, 1));
	private final VTextField name = new VTextField(20);
	private final JComboBox<String> rarity = new JComboBox<>(
			new String[] { "COMMON", "UNCOMMON", "RARE", "EPIC"});

	private final JSpinner enchantability = new JSpinner(new SpinnerNumberModel(0, -100, 128000, 1));
	private final JSpinner useDuration = new JSpinner(new SpinnerNumberModel(0, -100, 128000, 1));
	private final JSpinner toolType = new JSpinner(new SpinnerNumberModel(1.0, -100.0, 128000.0, 0.1));
	private final JSpinner damageCount = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 1));

	private final JCheckBox destroyAnyBlock = new JCheckBox("Check to enable");
	private final JCheckBox stayInGridWhenCrafting = new JCheckBox("Check to enable");
	private final JCheckBox damageOnCrafting = new JCheckBox("Check to enable");
	private final JCheckBox hasGlow = new JCheckBox("Check to enable");

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model tool = new Model.BuiltInModel("Tool");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>();

	private ProcedureSelector onRightClickedInAir;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onRightClickedOnBlock;
	private ProcedureSelector onEntityHitWith;
	private ProcedureSelector onItemInInventoryTick;
	private ProcedureSelector onItemInUseTick;
	private ProcedureSelector onStoppedUsing;
	private ProcedureSelector onEntitySwing;

	private final ValidationGroup page1group = new ValidationGroup();

	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private final JComboBox<String> guiBoundTo = new JComboBox<>();
	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));

	public ItemGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onRightClickedInAir = new ProcedureSelector(this.withEntry("item/when_right_clicked"), mcreator,
				"When right clicked in air (player loc.)",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onCrafted = new ProcedureSelector(this.withEntry("item/on_crafted"), mcreator, "When item is crafted/smelted",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRightClickedOnBlock = new ProcedureSelector(this.withEntry("item/when_right_clicked_block"), mcreator,
				"When right clicked on block (hand loc.)", Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/direction:direction"));
		onEntityHitWith = new ProcedureSelector(this.withEntry("item/when_entity_hit"), mcreator,
				"When living entity is hit with item", Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/itemstack:itemstack"));
		onItemInInventoryTick = new ProcedureSelector(this.withEntry("item/inventory_tick"), mcreator,
				"When item in inventory tick", Dependency
				.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onItemInUseTick = new ProcedureSelector(this.withEntry("item/hand_tick"), mcreator, "When item in hand tick",
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onStoppedUsing = new ProcedureSelector(this.withEntry("item/when_stopped_using"), mcreator,
				"On player stopped using", Dependency
				.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/time:number"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				"When entity swings item",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		guiBoundTo.addActionListener(e -> {
			if (!isEditingMode()) {
				String selected = (String) guiBoundTo.getSelectedItem();
				if (selected != null) {
					ModElement element = mcreator.getWorkspace().getModElementByName(selected);
					if (element != null) {
						GeneratableElement generatableElement = element.getGeneratableElement();
						if (generatableElement instanceof GUI) {
							inventorySize.setValue(((GUI) generatableElement).getMaxSlotID() + 1);
						}
					}
				}
			}
		});

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Item"));
		texture.setOpaque(false);

		JPanel destal2 = new JPanel(new BorderLayout(0, 40));
		destal2.setOpaque(false);
		JPanel destal3 = new JPanel(new BorderLayout(15, 15));
		destal3.setOpaque(false);
		destal3.add("West", PanelUtils.totalCenterInPanel(ComponentUtils.squareAndBorder(texture, "Item texture")));
		destal2.add("North", destal3);

		JPanel destal = new JPanel(new GridLayout(2, 2, 15, 15));
		destal.setOpaque(false);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"), new JLabel(
				"<html>Special information about the item:<br><small>Separate entries with comma, to use comma in description use \\,")));
		destal.add(specialInfo);

		hasGlow.setOpaque(false);
		destal.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/glowing_effect"), new JLabel("Has glowing effect?")));
		destal.add(hasGlow);

		destal2.add("Center", PanelUtils.centerInPanel(destal));

		ComponentUtils.deriveFont(specialInfo, 16);

		ComponentUtils.deriveFont(renderType, 16.0f);

		JPanel rent = new JPanel();
		rent.setLayout(new BoxLayout(rent, BoxLayout.PAGE_AXIS));

		rent.setOpaque(false);
		rent.add(PanelUtils.join(HelpUtils.wrapWithHelpButton(this.withEntry("item/model"),
				new JLabel("<html>Item model:<br><small>Select the item model to be used. Supported: JSON, OBJ")),
				PanelUtils.join(renderType)));

		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		destal3.add("Center", rent);

		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2), "Item 3D model",
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel sbbp2 = new JPanel(new BorderLayout());
		sbbp2.setOpaque(false);

		sbbp2.add("West", destal2);

		pane2.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(sbbp2)));

		pane2.setOpaque(false);

		JPanel subpane2 = new JPanel(new GridLayout(11, 2, 45, 2));

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"), new JLabel("Name in GUI:")));
		subpane2.add(name);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), new JLabel("Rarity:")));
		subpane2.add(rarity);

		subpane2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/creative_tab"), new JLabel("Creative inventory tab:")));
		subpane2.add(creativeTab);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/stack_size"), new JLabel("Max stack size:")));
		subpane2.add(stackSize);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/enchantability"), new JLabel("Enchantability:")));
		subpane2.add(enchantability);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/destroy_speed"), new JLabel("Item destroy speed:")));
		subpane2.add(toolType);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
				new JLabel("<html>Damage vs mob/animal (check to enable):<br><small>Melee damage")));
		subpane2.add(PanelUtils.westAndCenterElement(enableMeleeDamage, damageVsEntity));

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"), new JLabel(
				"<html>Item use count / durability (leave 0 to disable damage):<br><small>If you want to make a tool, create a tool instead")));
		subpane2.add(damageCount);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/can_destroy_any_block"),
				new JLabel("Can destroy any block?")));
		subpane2.add(destroyAnyBlock);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item"),
				new JLabel("Does item stay in crafting grid when crafted?")));
		subpane2.add(stayInGridWhenCrafting);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item_damage"), new JLabel(
				"<html>Damage item instead on crafting<br><small>Make sure to enable \"stay in crafting grid\" and that item is damageable")));
		subpane2.add(damageOnCrafting);

		subpane2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("item/use_duration"), new JLabel("Item use animation duration:")));
		subpane2.add(useDuration);

		enchantability.setOpaque(false);
		useDuration.setOpaque(false);
		toolType.setOpaque(false);
		damageCount.setOpaque(false);
		destroyAnyBlock.setOpaque(false);
		stayInGridWhenCrafting.setOpaque(false);
		damageOnCrafting.setOpaque(false);

		subpane2.setOpaque(false);

		pane3.setOpaque(false);

		pane3.add("Center", PanelUtils.totalCenterInPanel(subpane2));

		JPanel events = new JPanel(new GridLayout(3, 3, 10, 10));
		events.setOpaque(false);
		events.add(onRightClickedInAir);
		events.add(onRightClickedOnBlock);
		events.add(onCrafted);
		events.add(onEntityHitWith);
		events.add(onItemInInventoryTick);
		events.add(onItemInUseTick);
		events.add(onStoppedUsing);
		events.add(onEntitySwing);
		pane4.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.maxMargin(events, 20, true, true, true, true)));
		pane4.setOpaque(false);

		JPanel props = new JPanel(new GridLayout(3, 2, 35, 2));
		props.setOpaque(false);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/bind_gui"), new JLabel(
				"<html>Bind this item to GUI:<br>"
						+ "<small>Set to Empty to disable inventory (you want this in most cases)<br>"
						+ "Enabling inventory will make this item unstackable")));
		props.add(guiBoundTo);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/inventory_size"), new JLabel(
				"<html>Size of inventory (slot count):<br><small>"
						+ "Set this value to the <i>biggest slot ID in the GUI</i> + 1")));
		props.add(inventorySize);

		props.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("item/inventory_stack_size"), new JLabel("Max size of stack:")));
		props.add(inventoryStackSize);

		pane5.add(PanelUtils.totalCenterInPanel(props));
		pane5.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);

		name.setValidator(new TextFieldValidator(name, "Item needs a name"));
		name.enableRealtimeValidation();

		addPage("Visual", pane2);
		addPage("Properties", pane3);
		addPage("Inventory", pane5);
		addPage("Triggers", pane4);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onRightClickedInAir.refreshListKeepSelected();
		onCrafted.refreshListKeepSelected();
		onRightClickedOnBlock.refreshListKeepSelected();
		onEntityHitWith.refreshListKeepSelected();
		onItemInInventoryTick.refreshListKeepSelected();
		onItemInUseTick.refreshListKeepSelected();
		onStoppedUsing.refreshListKeepSelected();
		onEntitySwing.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("MISC"));

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(normal, tool),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(guiBoundTo, ListUtils.merge(Collections.singleton("<NONE>"),
				mcreator.getWorkspace().getModElements().stream().filter(var -> var.getType() == ModElementType.GUI)
						.map(ModElement::getName).collect(Collectors.toList())), "<NONE>");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 1)
			return new AggregatedValidationResult(name);
		else if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Item item) {
		name.setText(item.name);
		rarity.setSelectedItem(item.rarity);
		texture.setTextureFromTextureName(item.texture);
		specialInfo.setText(
				item.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		onRightClickedInAir.setSelectedProcedure(item.onRightClickedInAir);
		onRightClickedOnBlock.setSelectedProcedure(item.onRightClickedOnBlock);
		onCrafted.setSelectedProcedure(item.onCrafted);
		onEntityHitWith.setSelectedProcedure(item.onEntityHitWith);
		onItemInInventoryTick.setSelectedProcedure(item.onItemInInventoryTick);
		onItemInUseTick.setSelectedProcedure(item.onItemInUseTick);
		onStoppedUsing.setSelectedProcedure(item.onStoppedUsing);
		onEntitySwing.setSelectedProcedure(item.onEntitySwing);
		creativeTab.setSelectedItem(item.creativeTab);
		stackSize.setValue(item.stackSize);
		enchantability.setValue(item.enchantability);
		toolType.setValue(item.toolType);
		useDuration.setValue(item.useDuration);
		damageCount.setValue(item.damageCount);
		destroyAnyBlock.setSelected(item.destroyAnyBlock);
		stayInGridWhenCrafting.setSelected(item.stayInGridWhenCrafting);
		damageOnCrafting.setSelected(item.damageOnCrafting);
		hasGlow.setSelected(item.hasGlow);
		damageVsEntity.setValue(item.damageVsEntity);
		enableMeleeDamage.setSelected(item.enableMeleeDamage);
		guiBoundTo.setSelectedItem(item.guiBoundTo);
		inventorySize.setValue(item.inventorySize);
		inventoryStackSize.setValue(item.inventoryStackSize);

		Model model = item.getItemModel();
		if (model != null)
			renderType.setSelectedItem(model);
	}

	@Override public Item getElementFromGUI() {
		Item item = new Item(modElement);
		item.name = name.getText();
		item.rarity = (String) rarity.getSelectedItem();
		item.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		item.stackSize = (int) stackSize.getValue();
		item.enchantability = (int) enchantability.getValue();
		item.useDuration = (int) useDuration.getValue();
		item.toolType = (double) toolType.getValue();
		item.damageCount = (int) damageCount.getValue();
		item.destroyAnyBlock = destroyAnyBlock.isSelected();
		item.stayInGridWhenCrafting = stayInGridWhenCrafting.isSelected();
		item.damageOnCrafting = damageOnCrafting.isSelected();
		item.hasGlow = hasGlow.isSelected();
		item.onRightClickedInAir = onRightClickedInAir.getSelectedProcedure();
		item.onRightClickedOnBlock = onRightClickedOnBlock.getSelectedProcedure();
		item.onCrafted = onCrafted.getSelectedProcedure();
		item.onEntityHitWith = onEntityHitWith.getSelectedProcedure();
		item.onItemInInventoryTick = onItemInInventoryTick.getSelectedProcedure();
		item.onItemInUseTick = onItemInUseTick.getSelectedProcedure();
		item.onStoppedUsing = onStoppedUsing.getSelectedProcedure();
		item.onEntitySwing = onEntitySwing.getSelectedProcedure();
		item.damageVsEntity = (double) damageVsEntity.getValue();
		item.enableMeleeDamage = enableMeleeDamage.isSelected();
		item.inventorySize = (int) inventorySize.getValue();
		item.inventoryStackSize = (int) inventoryStackSize.getValue();
		item.guiBoundTo = (String) guiBoundTo.getSelectedItem();

		item.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());

		item.texture = texture.getID();
		Model.Type modelType = ((Model) Objects.requireNonNull(renderType.getSelectedItem())).getType();
		item.renderType = 0;
		if (modelType == Model.Type.JSON)
			item.renderType = 1;
		else if (modelType == Model.Type.OBJ)
			item.renderType = 2;
		item.customModelName = ((Model) Objects.requireNonNull(renderType.getSelectedItem())).getReadableName();

		return item;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-item");
	}

}
