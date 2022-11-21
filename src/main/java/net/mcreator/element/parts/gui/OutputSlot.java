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

package net.mcreator.element.parts.gui;

import net.mcreator.element.parts.procedure.Procedure;

import java.awt.*;

public class OutputSlot extends Slot {

	// for deserialization use only, to specify default values
	@SuppressWarnings("unused") private OutputSlot() {
		this.dropItemsWhenNotBound = true;
	}

	public OutputSlot(int id, int x, int y, Color color, boolean canTakeStack,
			boolean dropItemsWhenNotBound, Procedure onSlotChanged, Procedure onTakenFromSlot,
			Procedure onStackTransfer) {
		super(id, x, y, color, canTakeStack, dropItemsWhenNotBound, onSlotChanged, onTakenFromSlot,
				onStackTransfer);
	}

	@Override public String getName() {
		return "output_slot_" + id;
	}

}
