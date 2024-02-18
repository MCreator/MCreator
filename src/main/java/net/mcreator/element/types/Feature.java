/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.blockly.feature.BlocklyToFeature;
import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") public class Feature extends GeneratableElement implements ICommonType {

	public static final String XML_BASE = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"feature_container\" deletable=\"false\" x=\"40\" y=\"40\"></block></xml>";

	public String generationStep;
	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	public Procedure generateCondition;
	@BlocklyXML("features") public String featurexml;

	public Feature(ModElement element) {
		super(element);

		this.generationStep = "SURFACE_STRUCTURES";
		this.restrictionBiomes = new ArrayList<>();
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			var blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE).getDefinedBlocks(),
					getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.FEATURE),
					this.getModElement().getGenerator()
							.getTemplateGeneratorFromName(BlocklyEditorType.FEATURE.registryName()),
					additionalData).setTemplateExtension("json");

			var blocklyToFeature = new BlocklyToFeature(this.getModElement().getWorkspace(), this.getModElement(),
					this.featurexml, this.getModElement().getGenerator()
					.getTemplateGeneratorFromName(BlocklyEditorType.FEATURE.registryName()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
					new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));

			additionalData.put("placementcode", blocklyToFeature.getGeneratedCode());
			additionalData.put("configurationcode", blocklyToFeature.getFeatureConfigurationCode());
			additionalData.put("featuretype", blocklyToFeature.getFeatureType());
			additionalData.put("featureblocks", blocklyToFeature.getUsedBlocks());

			this.getModElement().clearMetadata().putMetadata("has_nbt_structure",
					blocklyToFeature.getUsedBlocks().contains("feature_custom_structure") ? true : null);
		};
	}

	public boolean hasGenerationConditions() {
		return generateCondition != null;
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		if (getModElement().getGenerator().getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.FABRIC)
			return List.of(BaseType.FEATURE); // Fabric needs to be handled differently than Forge
		else if (hasGenerationConditions())
			return List.of(BaseType.FEATURE);
		else
			return Collections.emptyList();
	}

}
