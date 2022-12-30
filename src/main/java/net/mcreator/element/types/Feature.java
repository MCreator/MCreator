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
import net.mcreator.blockly.feature.BlocklyToFeature;
import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public class Feature extends GeneratableElement implements ICommonType {

	public String generationStep;
	public List<String> restrictionDimensions;
	public List<BiomeEntry> restrictionBiomes;
	public Procedure generateCondition;
	public String featurexml;

	public Feature(ModElement element) {
		super(element);

		this.generationStep = "SURFACE_STRUCTURES";
		this.restrictionDimensions = new ArrayList<>();
		this.restrictionBiomes = new ArrayList<>();
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			var blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE).getDefinedBlocks(),
					this.getModElement().getGenerator().getTemplateGeneratorFromName(BlocklyEditorType.FEATURE.folder()), additionalData);

			var blocklyToFeature = new BlocklyToFeature(this.getModElement().getWorkspace(), this.getModElement(),
					this.featurexml, this.getModElement().getGenerator().getTemplateGeneratorFromName(BlocklyEditorType.FEATURE.folder()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
					new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));

			additionalData.put("placementcode", blocklyToFeature.getGeneratedCode());
			additionalData.put("configurationcode", blocklyToFeature.getFeatureConfigurationCode());
			additionalData.put("featuretype", blocklyToFeature.getFeatureType());
		};
	}

	public boolean hasGenerationConditions() {
		return restrictionDimensions.size() > 0 || generateCondition != null;
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		return List.of(BaseType.FEATURE);
	}
}
