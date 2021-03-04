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
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused") public class Armor extends GeneratableElement implements ITabContainedElement {

	public boolean enableHelmet;
	public String textureHelmet;
	public boolean enableBody;
	public String textureBody;
	public boolean enableLeggings;
	public String textureLeggings;
	public boolean enableBoots;
	public String textureBoots;

	public Procedure onHelmetTick;
	public Procedure onBodyTick;
	public Procedure onLeggingsTick;
	public Procedure onBootsTick;

	public TabEntry creativeTab;
	public String armorTextureFile;

	public String helmetName;
	public String bodyName;
	public String leggingsName;
	public String bootsName;

	public List<String> helmetSpecialInfo;
	public List<String> bodySpecialInfo;
	public List<String> leggingsSpecialInfo;
	public List<String> bootsSpecialInfo;

	public String helmetModelName;
	public String helmetModelPart;
	public String helmetModelTexture;

	public String bodyModelName;
	public String bodyModelPart;
	public String armsModelPartL;
	public String armsModelPartR;
	public String bodyModelTexture;

	public String leggingsModelName;
	public String leggingsModelPartL;
	public String leggingsModelPartR;
	public String leggingsModelTexture;

	public String bootsModelName;
	public String bootsModelPartL;
	public String bootsModelPartR;
	public String bootsModelTexture;

	public int maxDamage;
	public int damageValueHelmet;
	public int damageValueBody;
	public int damageValueLeggings;
	public int damageValueBoots;
	public int enchantability;
	public double toughness;
	public double knockbackResistance;
	public Sound equipSound;
	public List<MItemBlock> repairItems;

	private Armor() {
		this(null);
	}

	public Armor(ModElement element) {
		super(element);

		this.helmetModelName = "Default";
		this.bodyModelName = "Default";
		this.leggingsModelName = "Default";
		this.bootsModelName = "Default";

		this.helmetSpecialInfo = new ArrayList<>();
		this.bodySpecialInfo = new ArrayList<>();
		this.leggingsSpecialInfo = new ArrayList<>();
		this.bootsSpecialInfo = new ArrayList<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		ArrayList<File> armorPieces = new ArrayList<>();
		if (enableHelmet)
			armorPieces.add(getModElement().getFolderManager().getItemTextureFile(textureHelmet));
		if (enableBody)
			armorPieces.add(getModElement().getFolderManager().getItemTextureFile(textureBody));
		if (enableLeggings)
			armorPieces.add(getModElement().getFolderManager().getItemTextureFile(textureLeggings));
		if (enableBoots)
			armorPieces.add(getModElement().getFolderManager().getItemTextureFile(textureBoots));

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

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

}
