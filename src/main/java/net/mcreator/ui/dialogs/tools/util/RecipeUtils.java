/*
 * MCToolkit (https://mctoolkit.net/)
 * Copyright (C) 2020 MCToolkit and contributors
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

package net.mcreator.ui.dialogs.tools.util;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Recipe;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.tools.plugin.PackMakerTool;
import net.mcreator.ui.dialogs.tools.plugin.elements.Blocks;
import net.mcreator.ui.dialogs.tools.plugin.elements.Items;
import net.mcreator.ui.dialogs.tools.plugin.elements.Recipes;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;

public class RecipeUtils {

	public static void stairs(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe stairsRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		stairsRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + block);
		stairsRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + block);
		stairsRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + block);
		stairsRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + block);
		stairsRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + block);
		stairsRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + block);
		stairsRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + resultBlock);
		stairsRecipe.recipeRetstackSize = 4;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(stairsRecipe);
		mcreator.getWorkspace().addModElement(stairsRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(stairsRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(stairsRecipe);
	}

	public static void slab(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe slabRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		slabRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + block);
		slabRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + block);
		slabRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + block);
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + resultBlock);
		slabRecipe.recipeRetstackSize = 6;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(slabRecipe);
		mcreator.getWorkspace().addModElement(slabRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(slabRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(slabRecipe);
	}

	public static void fence(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe fenceRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		fenceRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + block);
		fenceRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[5] = new MItemBlock(workspace, "CUSTOM:" + block);
		fenceRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + block);
		fenceRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + block);
		fenceRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + resultBlock);
		fenceRecipe.recipeRetstackSize = 3;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fenceRecipe);
		mcreator.getWorkspace().addModElement(fenceRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fenceRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(fenceRecipe);
	}

	public static void fenceGate(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe fenceGateRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE),
						false).getElementFromGUI();
		fenceGateRecipe.recipeSlots[3] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + block);
		fenceGateRecipe.recipeSlots[5] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[6] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + block);
		fenceGateRecipe.recipeSlots[8] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + resultBlock);
		fenceGateRecipe.recipeRetstackSize = 1;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fenceGateRecipe);
		mcreator.getWorkspace().addModElement(fenceGateRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fenceGateRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(fenceGateRecipe);
	}

	public static void button(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe slabRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		slabRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + block);
		slabRecipe.recipeShapeless = true;
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + resultBlock);
		slabRecipe.recipeRetstackSize = 1;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(slabRecipe);
		mcreator.getWorkspace().addModElement(slabRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(slabRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(slabRecipe);
	}

	public static void pressurePlate(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe slabRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		slabRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + block);
		slabRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + block);
		slabRecipe.recipeShapeless = true;
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + resultBlock);
		slabRecipe.recipeRetstackSize = 1;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(slabRecipe);
		mcreator.getWorkspace().addModElement(slabRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(slabRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(slabRecipe);
	}

	public static void stick(MCreator mcreator, Workspace workspace, String block, String recipeName){
		Recipe stickRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		stickRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + block);
		stickRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + block);
		stickRecipe.recipeReturnStack = new MItemBlock(workspace, "Items.STICK");
		stickRecipe.recipeRetstackSize = 4;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(stickRecipe);
		mcreator.getWorkspace().addModElement(stickRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(stickRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(stickRecipe);
	}

	public static void fourBlocks(MCreator mcreator, Workspace workspace, String block, String recipeName, String resultBlock){
		Recipe fourBlocksRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		fourBlocksRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + block);
		fourBlocksRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + block);
		fourBlocksRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + block);
		fourBlocksRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + block);
		fourBlocksRecipe.recipeReturnStack = new MItemBlock(workspace, resultBlock);
		fourBlocksRecipe.recipeRetstackSize = 4;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fourBlocksRecipe);
		mcreator.getWorkspace().addModElement(fourBlocksRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fourBlocksRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(fourBlocksRecipe);
	}

	public static void fullBlock(MCreator mcreator, Workspace workspace, String block, String textFieldName, String recipeName, String resultBlock, int stackSize){
		Recipe fullBlockRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace,textFieldName + recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		fullBlockRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[2] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[5] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + block);
		fullBlockRecipe.recipeReturnStack = new MItemBlock(workspace, resultBlock);
		fullBlockRecipe.recipeRetstackSize = stackSize;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fullBlockRecipe);
		mcreator.getWorkspace().addModElement(fullBlockRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fullBlockRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(fullBlockRecipe);
	}

	public static void custom(MCreator mcreator, Workspace workspace, Recipes recipe, PackMakerTool pmt, String textFieldName, String resultItemName,
			@Nullable MItemBlock base) {
		Recipe customRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, textFieldName + recipe.recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		int slot = 0;
		for(String line : recipe.pattern){
			char[] values = line.toCharArray();
			for(char c : values){
				if(!String.valueOf(c).equals(" ")) {
					for(Recipes.Key key : recipe.keys){
						if (key.keyName.equals(String.valueOf(c))) {
							if(key.useBaseItem){
								customRecipe.recipeSlots[slot] = base;
							} else if(key.item.contains("Items.") || key.item.contains("Blocks.")) {
								customRecipe.recipeSlots[slot] = new MItemBlock(workspace, key.item);
							} else {
								String blockName = "";
								//Try to get block with the key
								if (pmt.blocks != null) {
									for(Blocks block : pmt.blocks){
										if(key.item.equals(block.name.name)){
											if (block.name.useTextField){
												if (block.name.location != null) {
													if(block.name.location.equals("after"))
														blockName = "CUSTOM:" + textFieldName + block.name.name;
													else if(block.name.location.equals("before"))
														blockName = "CUSTOM:" + block.name.name + textFieldName;
												}
											} else
												blockName = "CUSTOM:" + block.name.name;
										}
									}
								}
								//Try to get the name for a custom item if the key wasn't not a block
								if(blockName.isEmpty()){
									if (pmt.items != null) {
										for(Items item : pmt.items){
											if(key.item.equals(item.name.name)){
												if (item.name.useTextField){
													if (item.name.location != null) {
														if(item.name.location.equals("after"))
															blockName = "CUSTOM:" + textFieldName + item.name.name;
														else if(item.name.location.equals("before"))
															blockName = "CUSTOM:" + item.name.name + textFieldName;
													}
												} else
													blockName = "CUSTOM:" + item.name.name;
											}
										}
									}
								}
								customRecipe.recipeShapeless = recipe.isShapeless;
								customRecipe.recipeSlots[slot] = new MItemBlock(workspace, blockName);
							}

							slot++;
						}
					}
				} else
					slot++;
			}
		}
		customRecipe.recipeReturnStack = new MItemBlock(workspace, resultItemName);
		customRecipe.recipeRetstackSize = recipe.stackSize;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(customRecipe);
		mcreator.getWorkspace().addModElement(customRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(customRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(customRecipe);
	}

	public static void smelting(MCreator mcreator, Workspace workspace, Recipes recipe, String block, String textFieldName, double factor){
		Recipe furnaceRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, textFieldName + recipe.recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		furnaceRecipe.recipeType = "Smelting";
		furnaceRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + block);
		furnaceRecipe.smeltingReturnStack = new MItemBlock(workspace, recipe.returnItem);
		furnaceRecipe.xpReward = recipe.xpReward * factor;
		furnaceRecipe.cookingTime = recipe.cookingTime;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(furnaceRecipe);
		mcreator.getWorkspace().addModElement(furnaceRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(furnaceRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(furnaceRecipe);
	}

	public static void smoking(MCreator mcreator, Workspace workspace, Recipes recipe, String block, String textFieldName, double factor){
		Recipe furnaceRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, textFieldName + recipe.recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		furnaceRecipe.recipeType = "Smoking";
		furnaceRecipe.smokingInputStack = new MItemBlock(workspace, "CUSTOM:" + block);
		furnaceRecipe.smokingReturnStack = new MItemBlock(workspace, recipe.returnItem);
		furnaceRecipe.xpReward = recipe.xpReward * factor;
		furnaceRecipe.cookingTime = recipe.cookingTime;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(furnaceRecipe);
		mcreator.getWorkspace().addModElement(furnaceRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(furnaceRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(furnaceRecipe);
	}

	public static void blasting(MCreator mcreator, Workspace workspace, Recipes recipe, String block, String textFieldName, double factor){
		Recipe furnaceRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, textFieldName + recipe.recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		furnaceRecipe.recipeType = "Blasting";
		furnaceRecipe.blastingInputStack = new MItemBlock(workspace, "CUSTOM:" + block);
		furnaceRecipe.blastingReturnStack = new MItemBlock(workspace, recipe.returnItem);
		furnaceRecipe.xpReward = recipe.xpReward * factor;
		furnaceRecipe.cookingTime = recipe.cookingTime;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(furnaceRecipe);
		mcreator.getWorkspace().addModElement(furnaceRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(furnaceRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(furnaceRecipe);
	}

	public static void campfireCooking(MCreator mcreator, Workspace workspace, Recipes recipe, String block, String textFieldName, double factor){
		Recipe furnaceRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, textFieldName + recipe.recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		furnaceRecipe.recipeType = "Campfire cooking";
		furnaceRecipe.campfireCookingInputStack = new MItemBlock(workspace, "CUSTOM:" + block);
		furnaceRecipe.campfireCookingReturnStack = new MItemBlock(workspace, recipe.returnItem);
		furnaceRecipe.xpReward = recipe.xpReward * factor;
		furnaceRecipe.cookingTime = recipe.cookingTime;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(furnaceRecipe);
		mcreator.getWorkspace().addModElement(furnaceRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(furnaceRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(furnaceRecipe);
	}

	public static void stoneCutting(MCreator mcreator, Workspace workspace, Recipes recipe, String block, String textFieldName, double factor){
		Recipe stoneCuttingRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, textFieldName + recipe.recipeName, ModElementType.RECIPE), false)
				.getElementFromGUI();
		stoneCuttingRecipe.recipeType = "Stone cutting";
		stoneCuttingRecipe.stoneCuttingInputStack = new MItemBlock(workspace, "CUSTOM:" + block);
		stoneCuttingRecipe.stoneCuttingReturnStack = new MItemBlock(workspace, recipe.returnItem);
		stoneCuttingRecipe.recipeRetstackSize = recipe.stackSize;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(stoneCuttingRecipe);
		mcreator.getWorkspace().addModElement(stoneCuttingRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(stoneCuttingRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(stoneCuttingRecipe);
	}
}
