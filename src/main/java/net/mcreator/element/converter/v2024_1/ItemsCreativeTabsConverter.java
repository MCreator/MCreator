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

package net.mcreator.element.converter.v2024_1;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Dimension;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class ItemsCreativeTabsConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(ItemsCreativeTabsConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			JsonElement tab = jsonElementInput.getAsJsonObject().getAsJsonObject("definition")
					.get(input instanceof Dimension ? "igniterTab" : "creativeTab");
			if (tab != null && !tab.getAsJsonObject().get("value").getAsString().equals("No creative tab entry")) {
				Field specialInformationField = input.getClass().getDeclaredField("creativeTabs");
				specialInformationField.setAccessible(true);
				specialInformationField.set(input, List.of(new TabEntry(workspace,
						tab.getAsJsonObject().get("value").getAsString())));
			}
		} catch (Exception e) {
			LOG.warn("Failed to convert creative tabs for " + input.getModElement().getName(), e);
		}

		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 59;
	}
}
