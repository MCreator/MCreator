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

package net.mcreator.element.converter.v2023_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.types.Item;
import net.mcreator.element.types.MusicDisc;
import net.mcreator.element.types.Tool;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemHasGlowConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(ItemHasGlowConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			JsonObject itemDefinition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");
			LogicProcedure glowCondition = null;
			if (itemDefinition.get("hasGlow").getAsBoolean()) {
				if (itemDefinition.get("glowCondition") != null)
					glowCondition = new LogicProcedure(
							itemDefinition.get("glowCondition").getAsJsonObject().get("name").getAsString(), true);
				else
					glowCondition = new LogicProcedure(null, true);
			}
			if (input instanceof Item item) {
				item.glowCondition = glowCondition;
			} else if (input instanceof Tool tool) {
				tool.glowCondition = glowCondition;
			} else if (input instanceof MusicDisc musicDisc) {
				musicDisc.glowCondition = glowCondition;
			}
		} catch (Exception e) {
			LOG.warn("Failed to convert glow condition", e);
		}
		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 51;
	}

}
