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

import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Recipe;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.AdaptiveGridLayout;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.recipemakers.*;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecipeGUI extends ModElementGUI<Recipe> {

	private CraftingRecipeMaker craftingRecipeMaker;
	private SmeltingRecipeMaker smeltingRecipeMaker;
	private BlastFurnaceRecipeMaker blastFurnaceRecipeMaker;
	private SmokerRecipeMaker smokerRecipeMaker;
	private StoneCutterRecipeMaker stoneCutterRecipeMaker;
	private CampfireCookingRecipeMaker campfireCookingRecipeMaker;
	private SmithingRecipeMaker smithingRecipeMaker;
	private BrewingRecipeMaker brewingRecipeMaker;

	private final JCheckBox recipeShapeless = L10N.checkbox("elementgui.common.enable");

	private final JSpinner xpReward = new JSpinner(new SpinnerNumberModel(1.0, 0, 256, 0.1));
	private final JSpinner cookingTime = new JSpinner(new SpinnerNumberModel(200, 0, 1000000, 1));

	private final JComboBox<String> namespace = new JComboBox<>(new String[] { "mod", "minecraft" });

	private final VComboBox<String> name = new VComboBox<>();

	private final VTextField group = new VTextField();

	private final JComboBox<String> recipeType = new JComboBox<>(
			new String[] { "Crafting", "Smelting", "Brewing", "Blasting", "Smoking", "Stone cutting",
					"Campfire cooking", "Smithing" });

	private final JComboBox<String> cookingBookCategory = new JComboBox<>(new String[] { "MISC", "FOOD", "BLOCKS" });

	private final JComboBox<String> craftingBookCategory = new JComboBox<>(
			new String[] { "MISC", "BUILDING", "REDSTONE", "EQUIPMENT" });

	private final CardLayout recipesPanelLayout = new CardLayout();
	private final JPanel recipesPanel = new JPanel(recipesPanelLayout);

	private JComponent namePanel;
	private JComponent namespacePanel;
	private JComponent xpRewardPanel;
	private JComponent cookingTimePanel;
	private JComponent groupPanel;
	private JComponent shapelessPanel;
	private JComponent cookingBookCategoryPanel;
	private JComponent craftingBookCategoryPanel;

	public RecipeGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		craftingRecipeMaker = new CraftingRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		smeltingRecipeMaker = new SmeltingRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		blastFurnaceRecipeMaker = new BlastFurnaceRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		smokerRecipeMaker = new SmokerRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		stoneCutterRecipeMaker = new StoneCutterRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		campfireCookingRecipeMaker = new CampfireCookingRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		smithingRecipeMaker = new SmithingRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTags,
				ElementUtil::loadBlocksAndItems);
		brewingRecipeMaker = new BrewingRecipeMaker(mcreator, ElementUtil::loadBlocksAndItemsAndTagsAndPotions,
				ElementUtil::loadBlocksAndItemsAndTags, ElementUtil::loadBlocksAndItemsAndPotions);

		craftingRecipeMaker.setOpaque(false);
		smeltingRecipeMaker.setOpaque(false);
		blastFurnaceRecipeMaker.setOpaque(false);
		smokerRecipeMaker.setOpaque(false);
		stoneCutterRecipeMaker.setOpaque(false);
		campfireCookingRecipeMaker.setOpaque(false);
		smithingRecipeMaker.setOpaque(false);
		brewingRecipeMaker.setOpaque(false);

		//@formatter:off
		name.setValidator(new UniqueNameValidator(
			L10N.t("modelement.recipe"),
			() -> namespace.getSelectedItem() + ":" + ((JTextField) name.getEditor().getEditorComponent()).getText(),
			() -> mcreator.getWorkspace().getModElements().stream()
				.filter(me -> me.getType() == ModElementType.RECIPE)
				.map(ModElement::getGeneratableElement)
				.filter(Objects::nonNull)
				.map(ge -> ((Recipe) ge).namespace + ":" + ((Recipe) ge).name),
			new RegistryNameValidator(name, L10N.t("modelement.recipe")).setValidChars(Arrays.asList('_', '/'))
		).setIsPresentOnList(this::isEditingMode));
		//@formatter:on
		name.enableRealtimeValidation();
		name.addItem("crafting_table");
		name.addItem("diamond_block");
		name.setEditable(true);

		ComponentUtils.deriveFont(group, 16);

		if (isEditingMode()) {
			name.setEnabled(false);
			namespace.setEnabled(false);
		} else {
			name.getEditor().setItem(RegistryNameFixer.fromCamelCase(modElement.getName()));
		}

		JPanel pane5 = new JPanel(new BorderLayout(10, 10));

		recipeShapeless.setOpaque(false);
		recipeShapeless.addActionListener(event -> craftingRecipeMaker.setShapeless(recipeShapeless.isSelected()));

		recipesPanel.setOpaque(false);

		recipesPanel.add(PanelUtils.totalCenterInPanel(craftingRecipeMaker), "crafting");
		recipesPanel.add(PanelUtils.totalCenterInPanel(smeltingRecipeMaker), "smelting");
		recipesPanel.add(PanelUtils.totalCenterInPanel(blastFurnaceRecipeMaker), "blasting");
		recipesPanel.add(PanelUtils.totalCenterInPanel(smokerRecipeMaker), "smoking");
		recipesPanel.add(PanelUtils.totalCenterInPanel(stoneCutterRecipeMaker), "stone cutting");
		recipesPanel.add(PanelUtils.totalCenterInPanel(campfireCookingRecipeMaker), "campfire cooking");
		recipesPanel.add(PanelUtils.totalCenterInPanel(smithingRecipeMaker), "smithing");
		recipesPanel.add(PanelUtils.totalCenterInPanel(brewingRecipeMaker), "brewing");

		JComponent recwrap = ComponentUtils.applyPadding(recipesPanel, 10, true, true, true, true);
		recwrap.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.recipe.definition"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(),
				Theme.current().getForegroundColor()));

		JPanel northPanel = new JPanel(new AdaptiveGridLayout(-1, 1, 10, 2));
		northPanel.setOpaque(false);

		northPanel.add(PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/type"), L10N.label("elementgui.recipe.type")),
				recipeType));

		northPanel.add(namePanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/registry_name"),
						L10N.label("elementgui.recipe.registry_name")), name));

		northPanel.add(namespacePanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/namespace"),
						L10N.label("elementgui.recipe.name_space")), namespace));

		northPanel.add(groupPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/group_name"),
						L10N.label("elementgui.recipe.group")), group));

		northPanel.add(craftingBookCategoryPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/crafting_book_category"),
						L10N.label("elementgui.recipe.crafting_book_category")), craftingBookCategory));

		northPanel.add(cookingBookCategoryPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/cooking_book_category"),
						L10N.label("elementgui.recipe.cooking_book_category")), cookingBookCategory));

		northPanel.add(shapelessPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/shapeless"),
						L10N.label("elementgui.recipe.is_shapeless")), recipeShapeless));

		northPanel.add(xpRewardPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/xp_reward"),
						L10N.label("elementgui.recipe.xp_reward")), xpReward));

		northPanel.add(cookingTimePanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/cooking_time"),
						L10N.label("elementgui.recipe.cooking_time")), cookingTime));

		pane5.setOpaque(false);
		pane5.add(PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(PanelUtils.pullElementUp(northPanel), PanelUtils.pullElementUp(recwrap),
						15, 15)));

		recipeType.addActionListener(e -> updateUIFields());

		group.enableRealtimeValidation();
		group.setValidator(new RegistryNameValidator(group, "Recipe group").setAllowEmpty(true).setMaxLength(128));

		updateUIFields();

		addPage(pane5);
	}

	private void updateUIFields() {
		String recipeTypeValue = (String) recipeType.getSelectedItem();
		if (recipeTypeValue != null) {
			boolean isCookingRecipe = List.of("Smelting", "Blasting", "Smoking", "Campfire cooking")
					.contains(recipeTypeValue);
			xpRewardPanel.setVisible(isCookingRecipe);
			cookingTimePanel.setVisible(isCookingRecipe);
			cookingBookCategoryPanel.setVisible(isCookingRecipe);

			boolean isRecipeJSON = List.of("Crafting", "Smelting", "Blasting", "Smoking", "Stone cutting",
					"Campfire cooking", "Smithing").contains(recipeTypeValue);
			groupPanel.setVisible(isRecipeJSON);
			namespacePanel.setVisible(isRecipeJSON);
			namePanel.setVisible(isRecipeJSON);

			boolean isRecipeCrafting = recipeTypeValue.equals("Crafting");
			shapelessPanel.setVisible(isRecipeCrafting);
			craftingBookCategoryPanel.setVisible(isRecipeCrafting);

			if (!isEditingMode() && isCookingRecipe) {
				if (recipeTypeValue.equals("Smelting")) {
					cookingTime.setValue(200);
				} else if (recipeTypeValue.equals("Campfire cooking")) {
					cookingTime.setValue(600);
				} else {
					cookingTime.setValue(100);
				}
			}

			recipesPanelLayout.show(recipesPanel, recipeTypeValue.toLowerCase(Locale.ENGLISH));
		}
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if ("Crafting".equals(recipeType.getSelectedItem())) {
			if (!craftingRecipeMaker.cb10.containsItem()) {
				return new AggregatedValidationResult.FAIL(L10N.t("elementgui.recipe.error_crafting_no_result"));
			} else if (!(craftingRecipeMaker.cb1.containsItem() || craftingRecipeMaker.cb2.containsItem()
					|| craftingRecipeMaker.cb3.containsItem() || craftingRecipeMaker.cb4.containsItem()
					|| craftingRecipeMaker.cb5.containsItem() || craftingRecipeMaker.cb6.containsItem()
					|| craftingRecipeMaker.cb7.containsItem() || craftingRecipeMaker.cb8.containsItem()
					|| craftingRecipeMaker.cb9.containsItem())) {
				return new AggregatedValidationResult.FAIL(L10N.t("elementgui.recipe.error_crafting_no_ingredient"));
			}
		} else if ("Smelting".equals(recipeType.getSelectedItem())) {
			if (!smeltingRecipeMaker.cb1.containsItem() || !smeltingRecipeMaker.cb2.containsItem()) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_smelting_no_ingredient_and_result"));
			}
		} else if ("Blasting".equals(recipeType.getSelectedItem())) {
			if (!blastFurnaceRecipeMaker.cb1.containsItem() || !blastFurnaceRecipeMaker.cb2.containsItem()) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_blasting_no_ingredient_and_result"));
			}
		} else if ("Smoking".equals(recipeType.getSelectedItem())) {
			if (!smokerRecipeMaker.cb1.containsItem() || !smokerRecipeMaker.cb2.containsItem()) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_smoking_no_ingredient_and_result"));
			}
		} else if ("Stone cutting".equals(recipeType.getSelectedItem())) {
			if (!stoneCutterRecipeMaker.cb1.containsItem() || !stoneCutterRecipeMaker.cb2.containsItem()) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_stone_cutting_no_ingredient_and_result"));
			}
		} else if ("Campfire cooking".equals(recipeType.getSelectedItem())) {
			if (!campfireCookingRecipeMaker.cb1.containsItem() || !campfireCookingRecipeMaker.cb2.containsItem()) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_campfire_no_ingredient_and_result"));
			}
		} else if ("Smithing".equals(recipeType.getSelectedItem())) {
			if (!smithingRecipeMaker.cb1.containsItem() || !smithingRecipeMaker.cb2.containsItem()
					|| !smithingRecipeMaker.cb3.containsItem() ||
					// We request smithing recipe to have template for new recipes
					(!isEditingMode() && !smithingRecipeMaker.cb4.containsItem())) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_smithing_no_ingredient_addition_and_result"));
			}
		} else if ("Brewing".equals(recipeType.getSelectedItem())) {
			if (!brewingRecipeMaker.cb1.containsItem() || !brewingRecipeMaker.cb2.containsItem()
					|| !brewingRecipeMaker.cb3.containsItem()) {
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.recipe.error_brewing_no_input_ingredient_and_result"));
			}
		}

		return new AggregatedValidationResult(name, group);
	}

	@Override public void openInEditingMode(Recipe recipe) {
		recipeType.setSelectedItem(recipe.recipeType);

		namespace.setSelectedItem(recipe.namespace);
		name.getEditor().setItem(recipe.name);

		group.setText(recipe.group);

		cookingBookCategory.setSelectedItem(recipe.cookingBookCategory);
		craftingBookCategory.setSelectedItem(recipe.craftingBookCategory);

		switch (recipe.recipeType) {
		case "Crafting" -> {
			recipeShapeless.setSelected(recipe.recipeShapeless);
			craftingRecipeMaker.cb1.setBlock(recipe.recipeSlots[0]);
			craftingRecipeMaker.cb2.setBlock(recipe.recipeSlots[3]);
			craftingRecipeMaker.cb3.setBlock(recipe.recipeSlots[6]);
			craftingRecipeMaker.cb4.setBlock(recipe.recipeSlots[1]);
			craftingRecipeMaker.cb5.setBlock(recipe.recipeSlots[4]);
			craftingRecipeMaker.cb6.setBlock(recipe.recipeSlots[7]);
			craftingRecipeMaker.cb7.setBlock(recipe.recipeSlots[2]);
			craftingRecipeMaker.cb8.setBlock(recipe.recipeSlots[5]);
			craftingRecipeMaker.cb9.setBlock(recipe.recipeSlots[8]);
			craftingRecipeMaker.cb10.setBlock(recipe.recipeReturnStack);
			craftingRecipeMaker.sp.setValue(recipe.recipeRetstackSize);
			craftingRecipeMaker.setShapeless(recipeShapeless.isSelected());
		}
		case "Smelting" -> {
			smeltingRecipeMaker.cb1.setBlock(recipe.smeltingInputStack);
			smeltingRecipeMaker.cb2.setBlock(recipe.smeltingReturnStack);
			xpReward.setValue(recipe.xpReward);
			cookingTime.setValue(recipe.cookingTime);
		}
		case "Blasting" -> {
			blastFurnaceRecipeMaker.cb1.setBlock(recipe.blastingInputStack);
			blastFurnaceRecipeMaker.cb2.setBlock(recipe.blastingReturnStack);
			xpReward.setValue(recipe.xpReward);
			cookingTime.setValue(recipe.cookingTime);
		}
		case "Smoking" -> {
			smokerRecipeMaker.cb1.setBlock(recipe.smokingInputStack);
			smokerRecipeMaker.cb2.setBlock(recipe.smokingReturnStack);
			xpReward.setValue(recipe.xpReward);
			cookingTime.setValue(recipe.cookingTime);
		}
		case "Stone cutting" -> {
			stoneCutterRecipeMaker.cb1.setBlock(recipe.stoneCuttingInputStack);
			stoneCutterRecipeMaker.cb2.setBlock(recipe.stoneCuttingReturnStack);
			stoneCutterRecipeMaker.sp.setValue(recipe.recipeRetstackSize);
		}
		case "Campfire cooking" -> {
			campfireCookingRecipeMaker.cb1.setBlock(recipe.campfireCookingInputStack);
			campfireCookingRecipeMaker.cb2.setBlock(recipe.campfireCookingReturnStack);
			xpReward.setValue(recipe.xpReward);
			cookingTime.setValue(recipe.cookingTime);
		}
		case "Smithing" -> {
			smithingRecipeMaker.cb1.setBlock(recipe.smithingInputStack);
			smithingRecipeMaker.cb2.setBlock(recipe.smithingInputAdditionStack);
			smithingRecipeMaker.cb4.setBlock(recipe.smithingInputTemplateStack);
			smithingRecipeMaker.cb3.setBlock(recipe.smithingReturnStack);
		}
		case "Brewing" -> {
			brewingRecipeMaker.cb1.setBlock(recipe.brewingInputStack);
			brewingRecipeMaker.cb2.setBlock(recipe.brewingIngredientStack);
			brewingRecipeMaker.cb3.setBlock(recipe.brewingReturnStack);
		}
		}
	}

	@Override public Recipe getElementFromGUI() {
		Recipe recipe = new Recipe(modElement);
		recipe.recipeType = (String) Objects.requireNonNull(recipeType.getSelectedItem());

		switch (recipe.recipeType) {
		case "Crafting" -> {
			MItemBlock[] recipeSlots = new MItemBlock[9];
			recipeSlots[0] = craftingRecipeMaker.cb1.getBlock();
			recipeSlots[3] = craftingRecipeMaker.cb2.getBlock();
			recipeSlots[6] = craftingRecipeMaker.cb3.getBlock();
			recipeSlots[1] = craftingRecipeMaker.cb4.getBlock();
			recipeSlots[4] = craftingRecipeMaker.cb5.getBlock();
			recipeSlots[7] = craftingRecipeMaker.cb6.getBlock();
			recipeSlots[2] = craftingRecipeMaker.cb7.getBlock();
			recipeSlots[5] = craftingRecipeMaker.cb8.getBlock();
			recipeSlots[8] = craftingRecipeMaker.cb9.getBlock();
			recipe.recipeRetstackSize = (int) craftingRecipeMaker.sp.getValue();
			recipe.recipeShapeless = recipeShapeless.isSelected();
			recipe.recipeReturnStack = craftingRecipeMaker.cb10.getBlock();
			recipe.recipeSlots = recipeSlots;
		}
		case "Smelting" -> {
			recipe.smeltingInputStack = smeltingRecipeMaker.getBlock();
			recipe.smeltingReturnStack = smeltingRecipeMaker.getBlock2();
			recipe.xpReward = (double) xpReward.getValue();
			recipe.cookingTime = (int) cookingTime.getValue();
		}
		case "Blasting" -> {
			recipe.blastingInputStack = blastFurnaceRecipeMaker.getBlock();
			recipe.blastingReturnStack = blastFurnaceRecipeMaker.getBlock2();
			recipe.xpReward = (double) xpReward.getValue();
			recipe.cookingTime = (int) cookingTime.getValue();
		}
		case "Smoking" -> {
			recipe.smokingInputStack = smokerRecipeMaker.getBlock();
			recipe.smokingReturnStack = smokerRecipeMaker.getBlock2();
			recipe.xpReward = (double) xpReward.getValue();
			recipe.cookingTime = (int) cookingTime.getValue();
		}
		case "Stone cutting" -> {
			recipe.recipeRetstackSize = (int) stoneCutterRecipeMaker.sp.getValue();
			recipe.stoneCuttingInputStack = stoneCutterRecipeMaker.getBlock();
			recipe.stoneCuttingReturnStack = stoneCutterRecipeMaker.getBlock2();
		}
		case "Campfire cooking" -> {
			recipe.campfireCookingInputStack = campfireCookingRecipeMaker.getBlock();
			recipe.campfireCookingReturnStack = campfireCookingRecipeMaker.getBlock2();
			recipe.xpReward = (double) xpReward.getValue();
			recipe.cookingTime = (int) cookingTime.getValue();
		}
		case "Smithing" -> {
			recipe.smithingInputStack = smithingRecipeMaker.cb1.getBlock();
			recipe.smithingInputAdditionStack = smithingRecipeMaker.cb2.getBlock();
			recipe.smithingInputTemplateStack = smithingRecipeMaker.cb4.getBlock();
			recipe.smithingReturnStack = smithingRecipeMaker.cb3.getBlock();
		}
		case "Brewing" -> {
			recipe.brewingInputStack = brewingRecipeMaker.cb1.getBlock();
			recipe.brewingIngredientStack = brewingRecipeMaker.cb2.getBlock();
			recipe.brewingReturnStack = brewingRecipeMaker.cb3.getBlock();
		}
		}

		recipe.namespace = (String) namespace.getSelectedItem();
		recipe.name = name.getEditor().getItem().toString();

		recipe.group = group.getText();

		recipe.cookingBookCategory = (String) cookingBookCategory.getSelectedItem();
		recipe.craftingBookCategory = (String) craftingBookCategory.getSelectedItem();

		return recipe;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-recipe");
	}

}
