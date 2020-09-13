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
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Food;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
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
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class FoodGUI extends ModElementGUI<Food> {

	private TextureHolder texture;

	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 0, 64, 1));
	private final JSpinner nutritionalValue = new JSpinner(new SpinnerNumberModel(4, -1000, 1000, 1));
	private final JSpinner saturation = new JSpinner(new SpinnerNumberModel(0.3, -1000, 1000, 0.1));

	private final JSpinner eatingSpeed = new JSpinner(new SpinnerNumberModel(32, 0, 9999, 1));

	private final VTextField name = new VTextField(20);
	private final JComboBox<String> rarity = new JComboBox<>(
			new String[] { "COMMON", "UNCOMMON", "RARE", "EPIC"});

	private final JTextField specialInfo = new JTextField(20);

	private final JCheckBox forDogs = new JCheckBox("Check to enable");
	private final JCheckBox isAlwaysEdible = new JCheckBox("Check to enable");

	private ProcedureSelector onRightClicked;
	private ProcedureSelector onEaten;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onEntitySwing;

	private final JCheckBox hasGlow = new JCheckBox("Check to enable");

	private final JComboBox<String> animation = new JComboBox<>(new String[] { "eat", "drink" });

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final Model normal = new Model.BuiltInModel("Normal");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>();

	public FoodGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onRightClicked = new ProcedureSelector(this.withEntry("item/when_right_clicked"), mcreator,
				"When right clicked in air (player loc.)",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onEaten = new ProcedureSelector(this.withEntry("food/when_eaten"), mcreator, "When food eaten",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onCrafted = new ProcedureSelector(this.withEntry("item/on_crafted"), mcreator, "When item is crafted/smelted",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				"When entity swings item",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		animation.setRenderer(new ItemTexturesComboBoxRenderer());

		JPanel pane1 = new JPanel(new BorderLayout(10, 10));
		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(specialInfo, 16);

		JPanel destal = new JPanel();

		destal.setOpaque(false);
		hasGlow.setOpaque(false);

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Item"));
		texture.setOpaque(false);

		destal.add("Center", ComponentUtils.squareAndBorder(texture, "Food texture"));

		JPanel rent = new JPanel();
		rent.setLayout(new BoxLayout(rent, BoxLayout.PAGE_AXIS));
		rent.setOpaque(false);
		rent.add(PanelUtils.join(HelpUtils.wrapWithHelpButton(this.withEntry("item/model"),
				new JLabel("<html>Item model:<br><small>Select the item model to be used. Supported: JSON, OBJ")),
				PanelUtils.join(renderType)));
		renderType.setFont(renderType.getFont().deriveFont(16.0f));
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());
		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2), "Food 3D model",
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(PanelUtils
				.northAndCenterElement(PanelUtils.join(destal, rent), PanelUtils.gridElements(1, 2, HelpUtils
								.wrapWithHelpButton(this.withEntry("item/special_information"), new JLabel(
										"<html>Special information about the food:<br><small>Separate entries with comma, to use comma in description use \\,")),
						specialInfo))));

		JPanel selp = new JPanel(new GridLayout(11, 2, 10, 2));
		selp.setOpaque(false);

		name.setPreferredSize(new Dimension(120, 31));

		forDogs.setOpaque(false);
		isAlwaysEdible.setOpaque(false);

		stackSize.setOpaque(false);
		nutritionalValue.setOpaque(false);
		saturation.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"), new JLabel("Name in GUI:")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), new JLabel("Rarity:")));
		selp.add(rarity);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/creative_tab"), new JLabel("Creative inventory tab:")));
		selp.add(creativeTab);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/stack_size"), new JLabel("Stack size:")));
		selp.add(stackSize);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("food/nutritional_value"), new JLabel("Nutritional value:")));
		selp.add(nutritionalValue);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("food/saturation"), new JLabel("Saturation:")));
		selp.add(saturation);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("food/is_for_dogs"), new JLabel("Is this food meat?")));
		selp.add(forDogs);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("food/always_edible"), new JLabel("Is always edible?")));
		selp.add(isAlwaysEdible);

		selp.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/glowing_effect"), new JLabel("Has glowing effect?")));
		selp.add(hasGlow);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("food/eating_speed"), new JLabel("Eating speed:")));
		selp.add(eatingSpeed);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("food/animation"), new JLabel("Food animation: ")));
		selp.add(animation);

		pane4.setOpaque(false);
		pane4.add("Center", PanelUtils.totalCenterInPanel(selp));

		pane1.setOpaque(false);

		JPanel events = new JPanel();
		events.setLayout(new GridLayout(2, 3, 10, 10));
		events.add(onRightClicked);
		events.add(onEaten);
		events.add(onCrafted);
		events.add(onEntitySwing);
		events.add(new JEmptyBox());
		events.add(new JEmptyBox());
		events.setOpaque(false);

		JPanel wrap = new JPanel();
		wrap.setOpaque(false);
		wrap.add(events);
		wrap.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1), "Food events", 0,
				0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		pane1.add("Center", PanelUtils.totalCenterInPanel(wrap));

		texture.setValidator(new TileHolderValidator(texture));
		name.setValidator(new TextFieldValidator(name, "Food needs a name"));
		name.enableRealtimeValidation();

		addPage("Visual", pane2);
		addPage("Properties", pane4);
		addPage("Triggers", pane1);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onRightClicked.refreshListKeepSelected();
		onEaten.refreshListKeepSelected();
		onCrafted.refreshListKeepSelected();
		onEntitySwing.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("FOOD"));

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Collections.singletonList(normal),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 1)
			return new AggregatedValidationResult(name);
		else if (page == 0)
			return new AggregatedValidationResult(texture);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Food food) {
		name.setText(food.name);
		rarity.setSelectedItem(food.rarity);
		texture.setTextureFromTextureName(food.texture);
		forDogs.setSelected(food.forDogs);
		isAlwaysEdible.setSelected(food.isAlwaysEdible);
		onRightClicked.setSelectedProcedure(food.onRightClicked);
		onEaten.setSelectedProcedure(food.onEaten);
		onCrafted.setSelectedProcedure(food.onCrafted);
		onEntitySwing.setSelectedProcedure(food.onEntitySwing);
		stackSize.setValue(food.stackSize);
		nutritionalValue.setValue(food.nutritionalValue);
		saturation.setValue(food.saturation);
		eatingSpeed.setValue(food.eatingSpeed);
		animation.setSelectedItem(food.animation);
		hasGlow.setSelected(food.hasGlow);
		creativeTab.setSelectedItem(food.creativeTab);
		specialInfo.setText(
				food.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		Model model = food.getItemModel();
		if (model != null)
			renderType.setSelectedItem(model);
	}

	@Override public Food getElementFromGUI() {
		Food food = new Food(modElement);
		food.name = name.getText();
		food.rarity = (String) rarity.getSelectedItem();
		food.texture = texture.getID();
		food.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		food.stackSize = (int) stackSize.getValue();
		food.nutritionalValue = (int) nutritionalValue.getValue();
		food.eatingSpeed = (int) eatingSpeed.getValue();
		food.saturation = (double) saturation.getValue();
		food.forDogs = forDogs.isSelected();
		food.isAlwaysEdible = isAlwaysEdible.isSelected();
		food.animation = (String) animation.getSelectedItem();
		food.onRightClicked = onRightClicked.getSelectedProcedure();
		food.onEaten = onEaten.getSelectedProcedure();
		food.onCrafted = onCrafted.getSelectedProcedure();
		food.onEntitySwing = onEntitySwing.getSelectedProcedure();
		food.hasGlow = hasGlow.isSelected();
		food.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());

		Model.Type modelType = ((Model) Objects.requireNonNull(renderType.getSelectedItem())).getType();
		food.renderType = 0;
		if (modelType == Model.Type.JSON)
			food.renderType = 1;
		else if (modelType == Model.Type.OBJ)
			food.renderType = 2;
		food.customModelName = ((Model) Objects.requireNonNull(renderType.getSelectedItem())).getReadableName();
		return food;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-food");
	}

}
