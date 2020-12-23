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

package net.mcreator.ui.dialogs.tools.plugin;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Material;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.*;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.dialogs.tools.plugin.elements.Blocks;
import net.mcreator.ui.dialogs.tools.plugin.elements.Items;
import net.mcreator.ui.dialogs.tools.plugin.elements.Recipes;
import net.mcreator.ui.dialogs.tools.plugin.elements.Tags;
import net.mcreator.ui.dialogs.tools.util.RecipeUtils;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.views.ArmorImageMakerView;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CustomPackMakerTool {

	public static void open(MCreator mcreator, PackMakerTool pmt) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools." + pmt.packID + "_title"),
				true);
		dialog.setLayout(new BorderLayout(10, 10));

		if(pmt.ui.icon != null){
			if(PackMakerToolIcons.CACHE.containsKey(pmt.ui.icon)){
				ImageIcon imageIcon = PackMakerToolIcons.getIconForItem(pmt.ui.icon);
				if (imageIcon != null && imageIcon.getImage() != null)
					dialog.setIconImage(ImageUtils.resize(imageIcon.getImage(), 16));
			}
		}

		dialog.add("North", PanelUtils.centerInPanel(L10N.label("dialog.tools." + pmt.packID + "_info")));

		int i = 1;
		if (pmt.ui.color)
			i++;
		if (pmt.ui.power != null)
			i++;
		if (pmt.ui.baseItem)
			i = i + 2;
		JPanel props = new JPanel(new GridLayout(i, 2, 5, 5));
		JColor color = new JColor(mcreator, false);
		JSpinner power = new JSpinner(
				new SpinnerNumberModel(pmt.ui.power.value, pmt.ui.power.min, pmt.ui.power.max,
						pmt.ui.power.stepSize));
		MCItemHolder base = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		JComboBox<String> type = new JComboBox<>();

		VTextField name = new VTextField(pmt.ui.name.length);
		name.enableRealtimeValidation();
		props.add(L10N.label("dialog.tools." + pmt.packID + "_name"));
		props.add(name);

		if (pmt.ui.color && !pmt.ui.baseItem) {
			props.add(L10N.label("dialog.tools." + pmt.packID + "_color_accent"));
			props.add(color);
		}
		if (pmt.ui.power != null) {
			props.add(L10N.label("dialog.tools." + pmt.packID + "_power_factor"));
			props.add(power);
		}
		if (pmt.ui.baseItem) {
			props.add(L10N.label("dialog.tools." + pmt.packID + "_color_accent"));
			props.add(color);

			props.add(L10N.label("dialog.tools." + pmt.packID + "_base_item"));
			props.add(PanelUtils.centerInPanel(base));

			base.setBlockSelectedListener(e -> {
				try {
					color.setColor(ImageUtils
							.getAverageColor(ImageUtils.toBufferedImage(((ImageIcon) base.getIcon()).getImage()))
							.brighter().brighter());
					if (base.getBlock().getUnmappedValue().startsWith("CUSTOM:")) {
						name.setText(StringUtils
								.machineToReadableName(base.getBlock().getUnmappedValue().replace("CUSTOM:", ""))
								.split(" ")[0]);
					}
				} catch (Exception ignored) {
				}
			});
		}

		dialog.add("Center", PanelUtils.centerInPanel(props));
		JButton ok = L10N.button("dialog.tools." + pmt.packID + "_create");
		JButton cancel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, cancel));

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name));

		JComboBox<String> finalType = type;
		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				addPackToWorkspace(mcreator, mcreator.getWorkspace(), pmt, name.getText(), color.getColor(),
						(Double) power.getValue(), base.getBlock(), (String) finalType.getSelectedItem());
				if(pmt.packs != null) {
					for (String id : pmt.packs) {
						PackMakerTool packMakerTool = PackMakerToolLoader.getPackMakerTool(id);
						addPackToWorkspace(mcreator, mcreator.getWorkspace(), packMakerTool, name.getText(),
								color.getColor(), (Double) power.getValue(), base.getBlock(),
								(String) Objects.requireNonNull(finalType.getSelectedItem()));
					}
				}
				mcreator.mv.updateMods();
				dialog.setCursor(Cursor.getDefaultCursor());
				dialog.setVisible(false);
			}
		});

		dialog.setSize(600, 160 + (i*30));
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

	}

	public static void addPackToWorkspace(MCreator mcreator, Workspace workspace, PackMakerTool pmt, String name, @Nullable Color color,
			double factor, @Nullable MItemBlock base, @Nullable String type) {
		if (pmt.textures != null) {
			for(PackMakerTool.Texture texture : pmt.textures){
				boolean b = (type == null);
				if(!b)
					b = type.equals(texture.condition);
				if(b || texture.condition.equals("")){
					if (!texture.type.equals("armor")) {
						ImageIcon image;
						if(texture.baseTexture != null){
							if(texture.baseTextureTop){
								image = ImageUtils.drawOver(
										ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
														"templates/textures/texturemaker/" + ListUtils
																.getRandomItem(Arrays.asList(texture.textures.toArray())) + ".png")), color,
												true),
										ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/" + ListUtils
												.getRandomItem(Arrays.asList(texture.baseTexture.toArray())) + ".png")));
							} else {
								image = ImageUtils.drawOver(
										ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/" + ListUtils
												.getRandomItem(Arrays.asList(texture.baseTexture.toArray())) + ".png")),
										ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
														"templates/textures/texturemaker/" + ListUtils
																.getRandomItem(Arrays.asList(texture.textures.toArray())) + ".png")), color,
												true));
							}

						} else {
							image = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
											"templates/textures/texturemaker/" + ListUtils
													.getRandomItem(Arrays.asList(texture.textures.toArray())) + ".png")), color,
									true);
						}
						String textureName = (name + texture.name).toLowerCase(Locale.ENGLISH);
						switch (texture.type) {
						case "item":
							FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image.getImage()),
									mcreator.getWorkspace().getFolderManager()
											.getItemTextureFile(RegistryNameFixer.fix(textureName)));
							break;
						case "block":
							FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image.getImage()),
									mcreator.getWorkspace().getFolderManager()
											.getBlockTextureFile(RegistryNameFixer.fix(textureName)));
							break;
						case "entity":
							FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image.getImage()),
									mcreator.getWorkspace().getFolderManager()
											.getEntityTextureFile(RegistryNameFixer.fix(textureName)));
							break;
						case "painting":
							FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image.getImage()),
									mcreator.getWorkspace().getFolderManager()
											.getPaintingTextureFile(RegistryNameFixer.fix(textureName)));
							break;
						case "other":
							FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image.getImage()),
									mcreator.getWorkspace().getFolderManager()
											.getOtherTextureFile(RegistryNameFixer.fix(textureName)));
							break;
						}
					} else {
						ArmorImageMakerView
								.generateArmorImages(workspace, name.toLowerCase(Locale.ENGLISH), "", texture.armorType,
										color, true);
					}
				}
			}
		}

		if (pmt.items != null) {
			for(Items item : pmt.items){

				String itemName = "";
				//"default" uses the name of the text field only
				if (item.name.useTextField){
					if (item.name.location != null) {
						if(item.name.location.equals("after"))
							itemName = name + " " + item.name.name;
						else if(item.name.location.equals("before"))
							itemName = item.name.name + " " + name;
					}
				} else
					itemName = item.name.name;

				//Create the mod element
				switch(item.elementType){
				case "item":
					Item itemElement = (Item) ModElementTypeRegistry.REGISTRY.get(ModElementType.ITEM)
							.getModElement(mcreator, new ModElement(workspace, itemName.replace(" ", ""), ModElementType.ITEM), false)
							.getElementFromGUI();
					itemElement.name = itemName;
					itemElement.texture = name + item.texture;
					if(item.creativeTab != null)
						itemElement.creativeTab = new TabEntry(mcreator.getWorkspace(), item.creativeTab);
					mcreator.getWorkspace().getModElementManager().storeModElementPicture(itemElement);
					mcreator.getWorkspace().addModElement(itemElement.getModElement());
					mcreator.getWorkspace().getGenerator().generateElement(itemElement);
					mcreator.getWorkspace().getModElementManager().storeModElement(itemElement);
					break;
				case "food":
					Food foodElement = (Food) ModElementTypeRegistry.REGISTRY.get(ModElementType.FOOD)
							.getModElement(mcreator, new ModElement(workspace, itemName.replace(" ", ""), ModElementType.FOOD), false)
							.getElementFromGUI();
					foodElement.name = itemName;
					foodElement.texture = name + item.texture;
					if(item.creativeTab != null)
						foodElement.creativeTab = new TabEntry(mcreator.getWorkspace(), item.creativeTab);
					mcreator.getWorkspace().getModElementManager().storeModElementPicture(foodElement);
					mcreator.getWorkspace().addModElement(foodElement.getModElement());
					mcreator.getWorkspace().getGenerator().generateElement(foodElement);
					mcreator.getWorkspace().getModElementManager().storeModElement(foodElement);
					break;
				case "tool":
					Tool toolElement = (Tool) ModElementTypeRegistry.REGISTRY.get(ModElementType.TOOL)
							.getModElement(mcreator, new ModElement(workspace, itemName.replace(" ", ""), ModElementType.TOOL), false)
							.getElementFromGUI();
					toolElement.name = name + " " + itemName;
					toolElement.texture = name + item.texture;
					toolElement.toolType = item.toolType;
					if(item.creativeTab != null) {
						toolElement.creativeTab = new TabEntry(mcreator.getWorkspace(), item.creativeTab);
					}
					toolElement.damageVsEntity = (double) Math.round(item.damageVsEntity * factor);
					toolElement.repairItems = Collections.singletonList(base);
					toolElement.harvestLevel = (int) Math.round(item.harvestLevel * factor);
					toolElement.efficiency = (double) Math.round(item.efficiency * Math.pow(factor, 0.6));
					toolElement.enchantability = (int) Math.round(item.enchantability * factor);
					toolElement.usageCount = (int) Math.round(item.usageCount * Math.pow(factor, 1.4));
					mcreator.getWorkspace().getModElementManager().storeModElementPicture(toolElement);
					mcreator.getWorkspace().addModElement(toolElement.getModElement());
					mcreator.getWorkspace().getGenerator().generateElement(toolElement);
					mcreator.getWorkspace().getModElementManager().storeModElement(toolElement);
					break;
				default:
					PackMakerToolLoader.LOG.error("Unsupported mod element: " + item.elementType);
				}
			}
		}

		if (pmt.blocks != null) {
			for(Blocks block : pmt.blocks){
				String blockName = "";
				//"default" uses the name of the text field only
				if (block.name.useTextField){
					if (block.name.location != null) {
						if(block.name.location.equals("after"))
							blockName = name + " " + block.name.name;
						else if(block.name.location.equals("before"))
							blockName = block.name.name + " " + name;
					}
				} else
					blockName = block.name.name;

				//Create the mod element
				switch(block.elementType){
				case "block":
					Block blockElement = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
							.getModElement(mcreator, new ModElement(workspace, blockName.replace(" ", ""), ModElementType.BLOCK), false)
							.getElementFromGUI();
					blockElement.name = blockName;
					blockElement.texture = name + block.texture;
					if(block.creativeTab != null)
						blockElement.creativeTab = new TabEntry(mcreator.getWorkspace(), block.creativeTab);
					if(block.textureBack != null)
						blockElement.textureBack = name + block.textureBack;
					if(block.textureFront != null)
						blockElement.textureFront = name + block.textureFront;
					if(block.textureLeft != null)
						blockElement.textureLeft = name + block.textureLeft;
					if(block.textureRight != null)
						blockElement.textureRight = name + block.textureRight;
					if(block.textureTop != null)
						blockElement.textureTop = name + block.textureTop;
					blockElement.customModelName = block.customModelName;
					if(block.blockBase != null)
						blockElement.blockBase = block.blockBase;
					blockElement.material = new Material(workspace, block.material);
					blockElement.soundOnStep = new StepSound(workspace, block.soundOnStep);
					blockElement.hardness = block.hardness * factor;
					blockElement.resistance = block.resistance * factor;
					blockElement.destroyTool = block.destroyTool;
					blockElement.breakHarvestLevel = block.breakHarvestLevel;
					blockElement.renderType = block.renderType;
					blockElement.flammability = (int) (block.flammability * factor);
					if(block.plantsGrowOn)
						blockElement.plantsGrowOn = true;
					if(block.spawnWorldTypes != null){
						blockElement.spawnWorldTypes = block.spawnWorldTypes;
						blockElement.minGenerateHeight = block.minGenerateHeight;
						blockElement.maxGenerateHeight = block.maxGenerateHeight;
						blockElement.frequencyPerChunks = block.frequencyPerChunks;
						blockElement.frequencyOnChunk = block.frequencyOnChunk;
					}
					if(block.dropAmount >= 1) {
						blockElement.dropAmount = block.dropAmount;
					}
					mcreator.getWorkspace().getModElementManager().storeModElementPicture(blockElement);
					mcreator.getWorkspace().addModElement(blockElement.getModElement());
					mcreator.getWorkspace().getGenerator().generateElement(blockElement);
					mcreator.getWorkspace().getModElementManager().storeModElement(blockElement);
					break;
				default:
					PackMakerToolLoader.LOG.error("Unexpected value: " + block.elementType);
				}
			}
		}

		if (pmt.recipes != null) {
			for(Recipes recipe : pmt.recipes){
				//Create the mod element using a template
				String blockName = "";
				if (pmt.blocks != null) {
					for(Blocks block : pmt.blocks){
						if(recipe.block.equals(block.name.name)){
							if (block.name.useTextField){
								if (block.name.location != null) {
									if(block.name.location.equals("after"))
										blockName = name + block.name.name;
									else if(block.name.location.equals("before"))
										blockName = block.name.name + name;
								}
							} else
								blockName = block.name.name;
						}
					}
				}

				String resultItemName = recipe.returnItem;
				if(!recipe.returnItem.contains("Items.") || !recipe.returnItem.contains("Blocks.")) {
					if (pmt.blocks != null) {
						for (Blocks block : pmt.blocks) {
							if (recipe.returnItem.equals(block.name.name)) {
								if (block.name.useTextField) {
									if (block.name.location != null) {
										if (block.name.location.equals("after"))
											resultItemName = "CUSTOM:" + name + block.name.name;
										else if (block.name.location.equals("before"))
											resultItemName = "CUSTOM:" + block.name.name + name;
									}
								} else
									resultItemName = "CUSTOM:" + block.name.name;
							}
						}
					}
					if (pmt.items != null) {
						for (Items item : pmt.items) {
							if (recipe.returnItem.equals(item.name.name)) {
								if (item.name.useTextField) {
									if (item.name.location != null) {
										if (item.name.location.equals("after"))
											resultItemName = "CUSTOM:" + name + item.name.name;
										else if (item.name.location.equals("before"))
											resultItemName = "CUSTOM:" + item.name.name + name;
									}
								} else
									resultItemName = "CUSTOM:" + item.name.name;
							}
						}
					}
				}

				switch(recipe.type){
				case "crafting":
					switch(recipe.template){
					case "stairs":
						RecipeUtils.stairs(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "slab":
						RecipeUtils.slab(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "fence":
						RecipeUtils.fence(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "fence_gate":
						RecipeUtils.fenceGate(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "button":
						RecipeUtils.button(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "pressure_plate":
						RecipeUtils.pressurePlate(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "stick":
						RecipeUtils.stick(mcreator, workspace, blockName, recipe.recipeName);
						break;
					case "four_blocks":
						RecipeUtils.fourBlocks(mcreator, workspace, blockName, recipe.recipeName, resultItemName);
						break;
					case "full_block":
						if(recipe.returnItem.contains("Blocks.") || recipe.returnItem.contains("Items."))
							RecipeUtils.fullBlock(mcreator, workspace, blockName, name, recipe.recipeName, recipe.returnItem,
									recipe.stackSize);
						else
							RecipeUtils.fullBlock(mcreator, workspace, blockName, name, recipe.recipeName, resultItemName, recipe.stackSize);
						break;
					case "custom":
						RecipeUtils.custom(mcreator, workspace, recipe, pmt, name, resultItemName, base);
						break;
					}
					break;
				case "smelting":
					RecipeUtils.smelting(mcreator, workspace, recipe, blockName, name, factor);
					break;
				case "blasting":
					RecipeUtils.blasting(mcreator, workspace, recipe, blockName, name, factor);
					break;
				case "smoking":
					RecipeUtils.smoking(mcreator, workspace, recipe, blockName, name, factor);
					break;
				case "campfire_cooking":
					RecipeUtils.campfireCooking(mcreator, workspace, recipe, blockName, name, factor);
					break;
				case "stone_cutting":
					RecipeUtils.stoneCutting(mcreator, workspace, recipe, blockName, name, factor);
					break;
				default:
					PackMakerToolLoader.LOG.error("Unexpected value: " + recipe.template + " for: " + recipe.recipeName);
				}
			}
		}

		if (pmt.tags != null) {
			for (Tags tag : pmt.tags) {
				//Create the mod element
				Tag tagElement = (Tag) ModElementTypeRegistry.REGISTRY.get(ModElementType.TAG)
						.getModElement(mcreator, new ModElement(workspace, name + tag.name, ModElementType.TAG), false)
						.getElementFromGUI();
				tagElement.type = tag.type;
				tagElement.namespace = tag.namespace;
				tagElement.name = RegistryNameFixer.fix(name) + tag.name;
				if(tag.blocks != null){
					tagElement.blocks = new ArrayList<>();
					for(String str : tag.blocks){
						String blockName = str;
						if(!str.contains("Items.") || !str.contains("Blocks.")) {
							for (Blocks block : pmt.blocks) {
								if (str.equals(block.name.name)) {
									if (block.name.useTextField) {
										if (block.name.location != null) {
											if (block.name.location.equals("after"))
												blockName = name + block.name.name;
											else if (block.name.location.equals("before"))
												blockName = block.name.name + name;
										}
									} else
										blockName = block.name.name;
								}
							}
						}
						tagElement.blocks.add(new MItemBlock(workspace,"CUSTOM:" + blockName));
					}
				}
				if(tag.items != null){
					tagElement.items = new ArrayList<>();
					for(String str : tag.items){
						String blockName = "";
						for (Items item : pmt.items) {
							if (str.equals(item.name.name)) {
								if (item.name.useTextField) {
									if (item.name.location != null) {
										if (item.name.location.equals("after"))
											blockName = name + item.name.name;
										else if (item.name.location.equals("before"))
											blockName = item.name.name + name;
									}
								} else
									blockName = item.name.name;
							}
							tagElement.items.add(new MItemBlock(workspace,"CUSTOM:" + blockName));
						}
					}
				}
				mcreator.getWorkspace().getModElementManager().storeModElementPicture(tagElement);
				mcreator.getWorkspace().addModElement(tagElement.getModElement());
				mcreator.getWorkspace().getGenerator().generateElement(tagElement);
				mcreator.getWorkspace().getModElementManager().storeModElement(tagElement);
			}
		}
	}

	public static BasicAction getAction(ActionRegistry actionRegistry, PackMakerTool packMakerTool) {
		return new BasicAction(actionRegistry, L10N.t("dialog.tools." + packMakerTool.packID + "_create"),
				e -> CustomPackMakerTool.open(actionRegistry.getMCreator(), packMakerTool)) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getWorkspace().getGenerator()
						.getGeneratorConfiguration();
				return elements(packMakerTool, gc);
			}
		};
	}

	private static boolean elements(PackMakerTool pmt, GeneratorConfiguration gc){
		boolean b = false;
		for(String element : pmt.mod_elements){
			if(gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.valueOf(element))
					!= GeneratorStats.CoverageStatus.NONE)
				b = true;
			else
				break;
		}
		return b;
	}
}
