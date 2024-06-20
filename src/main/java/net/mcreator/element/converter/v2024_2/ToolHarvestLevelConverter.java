/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2024_2;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Tool;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToolHarvestLevelConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(ToolHarvestLevelConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Tool tool = (Tool) input;
		try {
			if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("harvestLevel") != null) {
				int harvestLevel = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
						.get("harvestLevel").getAsInt();
				if (harvestLevel == 0) {
					tool.blockDropsTier = "WOOD";
				} else if (harvestLevel == 1) {
					tool.blockDropsTier = "STONE";
				} else if (harvestLevel == 2) {
					tool.blockDropsTier = "IRON";
				} else if (harvestLevel == 3) {
					tool.blockDropsTier = "DIAMOND";
				} else {
					tool.blockDropsTier = "NETHERITE";
				}
			}
		} catch (Exception e) {
			LOG.warn("Could convert harvestLevel of: {}", tool.getModElement().getName());
		}
		return tool;
	}

	@Override public int getVersionConvertingTo() {
		return 64;
	}

}
