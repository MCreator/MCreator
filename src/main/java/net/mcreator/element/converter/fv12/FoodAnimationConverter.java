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

package net.mcreator.element.converter.fv12;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Food;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FoodAnimationConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger(FoodAnimationConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Food food = (Food) input;
		try {
			food.animation = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("animation").getAsString().toUpperCase();
		}catch(Exception e){
			LOG.warn("Could not determine animation for: "+ input.getModElement().getName());
		}

		return food;
	}

	@Override public int getVersionConvertingTo() {
		return 12;
	}
}
