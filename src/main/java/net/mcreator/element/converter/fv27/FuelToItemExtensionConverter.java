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

package net.mcreator.element.converter.fv27;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.NumberProcedure;
import net.mcreator.element.types.ItemExtension;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

public class FuelToItemExtensionConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		JsonObject fuel = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		ItemExtension itemExtension = new ItemExtension(
				new ModElement(workspace, input.getModElement().getName(), ModElementType.ITEMEXTENSION));
		itemExtension.item = new MItemBlock(workspace, fuel.get("block").getAsJsonObject().get("value").getAsString());
		itemExtension.enableFuel = true;
		itemExtension.fuelPower = new NumberProcedure(null, fuel.get("power").getAsInt());

		workspace.removeModElement(input.getModElement());
		itemExtension.getModElement().setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
		workspace.getModElementManager().storeModElementPicture(itemExtension);
		workspace.addModElement(itemExtension.getModElement());
		workspace.getGenerator().generateElement(itemExtension);
		workspace.getModElementManager().storeModElement(itemExtension);

		return itemExtension;
	}

	@Override public int getVersionConvertingTo() {
		return 27;
	}
}
