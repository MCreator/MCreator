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

package net.mcreator.element.converter.v2022_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.ItemExtension;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemDispenseBehaviorToItemExtensionConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(ItemDispenseBehaviorToItemExtensionConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			String originalName = input.getModElement().getName();
			JsonObject item = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");
			if (item.get("hasDispenseBehavior") != null && item.get("hasDispenseBehavior").getAsBoolean()) {
				ItemExtension itemExtension;

				for (ModElement me : workspace.getModElements().stream()
						.filter(me -> me.getType() == ModElementType.ITEMEXTENSION).toList()) {
					itemExtension = (ItemExtension) me.getGeneratableElement();
					if (itemExtension != null && itemExtension.item.equals(
							new MItemBlock(workspace, item.get("name").getAsString()))) {
						itemExtension.hasDispenseBehavior = true;

						if (item.get("dispenseSuccessCondition") != null)
							itemExtension.dispenseSuccessCondition = new Procedure(
									item.get("dispenseSuccessCondition").getAsJsonObject().get("name").getAsString());
						if (item.get("dispenseResultItemstack") != null)
							itemExtension.dispenseResultItemstack = new Procedure(
									item.get("dispenseResultItemstack").getAsJsonObject().get("name").getAsString());
						return input;
					}
				}

				itemExtension = new ItemExtension(new ModElement(workspace,
						ConverterUtils.findSuitableModElementName(workspace, originalName + "Extension"),
						ModElementType.ITEMEXTENSION));

				itemExtension.item = new MItemBlock(workspace, "CUSTOM:" + input.getModElement().getName());
				itemExtension.hasDispenseBehavior = item.get("hasDispenseBehavior").getAsBoolean();
				if (item.get("dispenseSuccessCondition") != null)
					itemExtension.dispenseSuccessCondition = new Procedure(
							item.get("dispenseSuccessCondition").getAsJsonObject().get("name").getAsString());
				if (item.get("dispenseResultItemstack") != null)
					itemExtension.dispenseResultItemstack = new Procedure(
							item.get("dispenseResultItemstack").getAsJsonObject().get("name").getAsString());

				itemExtension.getModElement().setParentFolder(
						FolderElement.findFolderByPath(input.getModElement().getWorkspace(),
								input.getModElement().getFolderPath()));
				workspace.getModElementManager().storeModElementPicture(itemExtension);
				workspace.addModElement(itemExtension.getModElement());
				workspace.getGenerator().generateElement(itemExtension);
				workspace.getModElementManager().storeModElement(itemExtension);
			}
		} catch (Exception e) {
			LOG.warn("Failed to update item to new format", e);
		}
		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 32;
	}
}
