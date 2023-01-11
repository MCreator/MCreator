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

package net.mcreator.element.converter.fv39;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.element.types.RangedItem;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

public class EntitiesRangedAttackConverter implements IConverter {
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		LivingEntity entity = (LivingEntity) input;
		if (!entity.rangedItemType.equals("Default item")) {
			for (ModElement me : workspace.getModElements()) {
				if (me.getName().equals(entity.rangedItemType) && me.getGeneratableElement() != null) {
					RangedItem rangedItem = (RangedItem) me.getGeneratableElement();
					entity.rangedItemType = rangedItem.projectile.getUnmappedValue().replace("CUSTOM:", "");
					break;
				}
			}
		}

		return entity;
	}

	@Override public int getVersionConvertingTo() {
		return 39;
	}
}
