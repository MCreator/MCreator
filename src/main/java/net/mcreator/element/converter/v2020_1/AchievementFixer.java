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

package net.mcreator.element.converter.v2020_1;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Achievement;
import net.mcreator.workspace.Workspace;

public class AchievementFixer implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Achievement achievement = (Achievement) input;
		achievement.showPopup = true;
		achievement.announceToChat = true;
		achievement.triggerxml = "<xml><block type=\"advancement_trigger\" deletable=\"false\" x=\"40\" y=\"80\"><next>"
				+ "<block type=\"custom_trigger\"/></next></block></xml>";
		return achievement;
	}

	@Override public int getVersionConvertingTo() {
		return 5;
	}

}