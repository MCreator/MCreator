/*
 * MCToolkit (https://mctoolkit.net/)
 * Copyright (C) 2020 MCToolkit and contributors
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
package net.mcreator.ui.dialogs.tools.plugin.elements;

import javax.annotation.Nullable;
import java.util.List;

public class Recipes {
	public String type;
	@Nullable public String template;
	public String recipeName;
	@Nullable public String block;
	public String returnItem;
	@Nullable public List<String> pattern;
	@Nullable public List<Key> keys;
	@Nullable public int xpReward;
	@Nullable public int cookingTime;
	@Nullable public int stackSize;
	@Nullable public boolean isShapeless;

	public static class Key {
		@Nullable public String keyName;
		public String item;
		@Nullable public boolean useBaseItem;
	}
}
