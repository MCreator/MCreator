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

package net.mcreator.element.converter.v2023_1;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Dimension;
import net.mcreator.element.types.Feature;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FluidGenToFeatureConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger(FluidGenToFeatureConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			String modElementName = input.getModElement().getName();
			JsonObject fluid = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			// If the list of restriction dimensions is empty, there's no feature to convert
			if (fluid.get("spawnWorldTypes") != null && !fluid.getAsJsonArray("spawnWorldTypes").isEmpty()) {
				Feature feature = new Feature(new ModElement(workspace,
						ConverterUtils.findSuitableModElementName(workspace, modElementName + "Feature"),
						ModElementType.FEATURE));

				int rarity = 5;
				if (fluid.get("frequencyOnChunks") != null) {
					rarity = fluid.get("frequencyOnChunks").getAsInt();
				}

				feature.generationStep = "LAKES";

				if (fluid.get("restrictionBiomes") != null && !fluid.getAsJsonArray("restrictionBiomes").isEmpty()) {
					fluid.getAsJsonArray("restrictionBiomes").iterator().forEachRemaining(
							e -> feature.restrictionBiomes.add(
									new BiomeEntry(workspace, e.getAsJsonObject().get("value").getAsString())));
				} else if (fluid.get("spawnWorldTypes") != null) {
					JsonArray spawnWorldTypes = fluid.get("spawnWorldTypes").getAsJsonArray();
					if (spawnWorldTypes.size() == 1) {
						String spawnWorldType = spawnWorldTypes.get(0).getAsString();
						if (spawnWorldType.equals("Surface")) {
							feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_overworld"));
						} else if (spawnWorldType.equals("Nether")) {
							feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_nether"));
						} else if (spawnWorldType.equals("End")) {
							feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_end"));
						} else if (spawnWorldType.startsWith("CUSTOM:")) {
							ModElement modElement = workspace.getModElementByName(
									spawnWorldType.replaceFirst("CUSTOM:", ""));
							if (modElement != null) {
								GeneratableElement generatableElement = modElement.getGeneratableElement();
								if (generatableElement instanceof Dimension dimension) {
									feature.restrictionBiomes.addAll(dimension.biomesInDimension);
								}
							}
						}
					}
				} else {
					feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_overworld"));
				}

				if (fluid.get("generateCondition") != null) {
					feature.generateCondition = new Procedure(
							fluid.get("generateCondition").getAsJsonObject().get("name").getAsString());
				}

				feature.featurexml = """
						<xml><block type="feature_container" deletable="false" x="40" y="40">
							<value name="feature"><block type="feature_lake">
								<value name="fluid"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
								<value name="border"><block type="mcitem_allblocks"><field name="value">Blocks.AIR</field></block></value>
							</block></value>
						<next><block type="placement_rarity"><field name="rarity">%d</field>
						<next><block type="placement_in_square">
						<next><block type="placement_height_uniform">
							<value name="min"><block type="vertical_anchor_above_bottom"><field name="value">0</field></block></value>
							<value name="max"><block type="vertical_anchor_absolute"><field name="value">256</field></block></value>
						<next><block type="placement_environment_scan">
							<field name="maxSteps">32</field><field name="direction">DOWN</field>
							<value name="condition"><block type="block_predicate_not">
								<value name="condition"><block type="block_predicate_is_air"></block></value>
							</block></value>
						<next><block type="placement_biome_filter">
						</block></next></block></next></block></next></block></next></block></next></block></xml>""".formatted(
						modElementName, rarity);

				feature.getModElement().setParentFolder(
						FolderElement.findFolderByPath(input.getModElement().getWorkspace(),
								input.getModElement().getFolderPath()));
				workspace.getModElementManager().storeModElementPicture(feature);
				workspace.addModElement(feature.getModElement());
				workspace.getGenerator().generateElement(feature);
				workspace.getModElementManager().storeModElement(feature);

				// If porting 1.19.2 FG workspace, there may be biome modifier present that break workspaces
				// 2022.3 does not cache files to remove yet, so we need to do a hacky removal here
				// Fixes #3641
				new File(workspace.getGenerator().getModDataRoot(),
						"forge/biome_modifier/" + input.getModElement().getRegistryName()
								+ "_biome_modifier.json").delete();
			}
		} catch (Exception e) {
			LOG.warn("Failed to move fluid generation settings to feature mod element", e);
		}

		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 39;
	}
}
