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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.IItem;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

@SuppressWarnings("unused") public class Armor extends GeneratableElement implements IItem, ITabContainedElement {

	public boolean enableHelmet;
	@TextureReference(TextureType.ITEM) public String textureHelmet;
	public boolean enableBody;
	@TextureReference(TextureType.ITEM) public String textureBody;
	public boolean enableLeggings;
	@TextureReference(TextureType.ITEM) public String textureLeggings;
	public boolean enableBoots;
	@TextureReference(TextureType.ITEM) public String textureBoots;

	public Procedure onHelmetTick;
	public Procedure onBodyTick;
	public Procedure onLeggingsTick;
	public Procedure onBootsTick;

	public TabEntry creativeTab;
	@TextureReference(value = TextureType.ARMOR, files = { "%s_layer_1", "%s_layer_2" }) public String armorTextureFile;

	public String helmetName;
	public String bodyName;
	public String leggingsName;
	public String bootsName;

	public StringListProcedure helmetSpecialInformation;
	public StringListProcedure bodySpecialInformation;
	public StringListProcedure leggingsSpecialInformation;
	public StringListProcedure bootsSpecialInformation;

	public String helmetModelName;
	public String helmetModelPart;
	@TextureReference(value = TextureType.ENTITY, defaultValues = "From armor") public String helmetModelTexture;

	public String bodyModelName;
	public String bodyModelPart;
	public String armsModelPartL;
	public String armsModelPartR;
	@TextureReference(value = TextureType.ENTITY, defaultValues = "From armor") public String bodyModelTexture;

	public String leggingsModelName;
	public String leggingsModelPartL;
	public String leggingsModelPartR;
	@TextureReference(value = TextureType.ENTITY, defaultValues = "From armor") public String leggingsModelTexture;

	public String bootsModelName;
	public String bootsModelPartL;
	public String bootsModelPartR;
	@TextureReference(value = TextureType.ENTITY, defaultValues = "From armor") public String bootsModelTexture;

	public int helmetItemRenderType;
	public String helmetItemCustomModelName;
	public int bodyItemRenderType;
	public String bodyItemCustomModelName;
	public int leggingsItemRenderType;
	public String leggingsItemCustomModelName;
	public int bootsItemRenderType;
	public String bootsItemCustomModelName;

	public boolean helmetImmuneToFire;
	public boolean bodyImmuneToFire;
	public boolean leggingsImmuneToFire;
	public boolean bootsImmuneToFire;

	public LogicProcedure helmetPiglinNeutral;
	public LogicProcedure bodyPiglinNeutral;
	public LogicProcedure leggingsPiglinNeutral;
	public LogicProcedure bootsPiglinNeutral;

	public int maxDamage;
	public int damageValueHelmet;
	public int damageValueBody;
	public int damageValueLeggings;
	public int damageValueBoots;
	public int enchantability;
	public double toughness;
	public double knockbackResistance;
	public Sound equipSound;
	@ModElementReference public List<MItemBlock> repairItems;

	private Armor() {
		this(null);
	}

	public Armor(ModElement element) {
		super(element);

		this.helmetModelName = "Default";
		this.bodyModelName = "Default";
		this.leggingsModelName = "Default";
		this.bootsModelName = "Default";

		this.helmetItemRenderType = 0;
		this.helmetItemCustomModelName = "Normal";
		this.bodyItemRenderType = 0;
		this.bodyItemCustomModelName = "Normal";
		this.leggingsItemRenderType = 0;
		this.leggingsItemCustomModelName = "Normal";
		this.bootsItemRenderType = 0;
		this.bootsItemCustomModelName = "Normal";
	}

	@Override public BufferedImage generateModElementPicture() {
		ArrayList<File> armorPieces = new ArrayList<>();
		if (enableHelmet)
			armorPieces.add(getModElement().getFolderManager().getTextureFile(textureHelmet, TextureType.ITEM));
		if (enableBody)
			armorPieces.add(getModElement().getFolderManager().getTextureFile(textureBody, TextureType.ITEM));
		if (enableLeggings)
			armorPieces.add(getModElement().getFolderManager().getTextureFile(textureLeggings, TextureType.ITEM));
		if (enableBoots)
			armorPieces.add(getModElement().getFolderManager().getTextureFile(textureBoots, TextureType.ITEM));

		return MinecraftImageGenerator.Preview.generateArmorPreviewPicture(armorPieces);
	}

	public Model getHelmetModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (!helmetModelName.equals("Default"))
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), helmetModelName, modelType);
	}

	public Model getBodyModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (!bodyModelName.equals("Default"))
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), bodyModelName, modelType);
	}

	public Model getLeggingsModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (!leggingsModelName.equals("Default"))
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), leggingsModelName, modelType);
	}

	public Model getBootsModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (!bootsModelName.equals("Default"))
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), bootsModelName, modelType);
	}

	public Model getHelmetItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (helmetItemRenderType == 1)
			modelType = Model.Type.JSON;
		else if (helmetItemRenderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), helmetItemCustomModelName, modelType);
	}

	public Model getBodyItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (bodyItemRenderType == 1)
			modelType = Model.Type.JSON;
		else if (bodyItemRenderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), bodyItemCustomModelName, modelType);
	}

	public Model getLeggingsItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (leggingsItemRenderType == 1)
			modelType = Model.Type.JSON;
		else if (leggingsItemRenderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), leggingsItemCustomModelName, modelType);
	}

	public Model getBootsItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (bootsItemRenderType == 1)
			modelType = Model.Type.JSON;
		else if (bootsItemRenderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), bootsItemCustomModelName, modelType);
	}

	public String getItemCustomModelNameFor(String part) {
		return switch (part) {
			case "helmet" -> helmetItemCustomModelName.split(":")[0];
			case "body" -> bodyItemCustomModelName.split(":")[0];
			case "leggings" -> leggingsItemCustomModelName.split(":")[0];
			case "boots" -> bootsItemCustomModelName.split(":")[0];
			default -> "";
		};
	}

	public Map<String, String> getItemModelTextureMap(String part) {
		Model model = switch (part) {
			case "helmet" -> getHelmetItemModel();
			case "body" -> getBodyItemModel();
			case "leggings" -> getLeggingsItemModel();
			case "boots" -> getBootsItemModel();
			default -> null;
		};
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return new HashMap<>();
	}

	public String getItemTextureFor(String part) {
		return switch (part) {
			case "helmet" -> textureHelmet;
			case "body" -> textureBody;
			case "leggings" -> textureLeggings;
			case "boots" -> textureBoots;
			default -> null;
		};
	}

	public boolean hasHelmetNormalModel() {
		return getHelmetItemModel().getType() == Model.Type.BUILTIN && getHelmetItemModel().getReadableName()
				.equals("Normal");
	}

	public boolean hasHelmetToolModel() {
		return getHelmetItemModel().getType() == Model.Type.BUILTIN && getHelmetItemModel().getReadableName()
				.equals("Tool");
	}

	public boolean hasBodyNormalModel() {
		return getBodyItemModel().getType() == Model.Type.BUILTIN && getBodyItemModel().getReadableName()
				.equals("Normal");
	}

	public boolean hasBodyToolModel() {
		return getBodyItemModel().getType() == Model.Type.BUILTIN && getBodyItemModel().getReadableName()
				.equals("Tool");
	}

	public boolean hasLeggingsNormalModel() {
		return getLeggingsItemModel().getType() == Model.Type.BUILTIN && getLeggingsItemModel().getReadableName()
				.equals("Normal");
	}

	public boolean hasLeggingsToolModel() {
		return getLeggingsItemModel().getType() == Model.Type.BUILTIN && getLeggingsItemModel().getReadableName()
				.equals("Tool");
	}

	public boolean hasBootsNormalModel() {
		return getBootsItemModel().getType() == Model.Type.BUILTIN && getBootsItemModel().getReadableName()
				.equals("Normal");
	}

	public boolean hasBootsToolModel() {
		return getBootsItemModel().getType() == Model.Type.BUILTIN && getBootsItemModel().getReadableName()
				.equals("Tool");
	}

	public String getArmorModelsCode() {
		Set<Model> models = new HashSet<>();

		Model model1 = getHelmetModel();
		if (model1 != null && model1.getType() == Model.Type.JAVA)
			models.add(model1);
		Model model2 = getBodyModel();
		if (model2 != null && model2.getType() == Model.Type.JAVA)
			models.add(model2);
		Model model3 = getLeggingsModel();
		if (model3 != null && model3.getType() == Model.Type.JAVA)
			models.add(model3);
		Model model4 = getBootsModel();
		if (model4 != null && model4.getType() == Model.Type.JAVA)
			models.add(model4);

		StringBuilder modelsCode = new StringBuilder();

		for (Model model : models)
			modelsCode.append(FileIO.readFileToString(model.getFile())).append("\n\n");

		return modelsCode.toString();
	}

	@Override public List<MCItem> providedMCItems() {
		ArrayList<MCItem> retval = new ArrayList<>();
		if (this.enableHelmet)
			retval.add(new MCItem.Custom(this.getModElement(), "helmet", "item", "Helmet"));
		if (this.enableBody)
			retval.add(new MCItem.Custom(this.getModElement(), "body", "item", "Chestplate"));
		if (this.enableLeggings)
			retval.add(new MCItem.Custom(this.getModElement(), "legs", "item", "Leggings"));
		if (this.enableBoots)
			retval.add(new MCItem.Custom(this.getModElement(), "boots", "item", "Boots"));
		return retval;
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return providedMCItems();
	}

	@Override public ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		return switch (suffix) {
			case "helmet" -> workspace.getFolderManager().getTextureImageIcon(textureHelmet, TextureType.ITEM);
			case "body" -> workspace.getFolderManager().getTextureImageIcon(textureBody, TextureType.ITEM);
			case "legs" -> workspace.getFolderManager().getTextureImageIcon(textureLeggings, TextureType.ITEM);
			case "boots" -> workspace.getFolderManager().getTextureImageIcon(textureBoots, TextureType.ITEM);
			default -> null;
		};
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

}
