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

package net.mcreator.element.converter.fv37;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.gui.InputSlot;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.types.GUI;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlotInteractionsConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger(SlotInteractionsConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		GUI gui = (GUI) input;
		try {
			JsonArray components = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("components").getAsJsonArray();

			if (components != null && !components.isEmpty()) {
				components.forEach(c -> {
					if (c.getAsJsonObject().get("type").getAsString().equals("inputslot") ||
							c.getAsJsonObject().get("type").getAsString().equals("outputslot")) {
						gui.components.forEach(component -> {
							if (component instanceof Slot slot) {
								if (slot.id == c.getAsJsonObject().get("data").getAsJsonObject().get("id").getAsInt()) {
									slot.disablePickup = new LogicProcedure(null,
											c.getAsJsonObject().get("data").getAsJsonObject().get("disableStackInteraction").getAsBoolean());

									if (slot instanceof InputSlot inputSlot) {
										inputSlot.disablePlacement = new LogicProcedure(null,
												c.getAsJsonObject().get("data").getAsJsonObject().get("disableStackInteraction").getAsBoolean());
									}
								}
							}
						});
					}
				});
			}
		} catch (Exception e) {
			LOG.warn("Could not update slot interaction fields of: " + gui.getModElement().getName(), e);
		}

		return gui;
	}

	@Override public int getVersionConvertingTo() {
		return 37;

	}
}
