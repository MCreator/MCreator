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

public class Items {
	//Variables used by all item mod elements
	public String elementType;
	public Name name;
	public String texture;
	@Nullable public String toolType;
	@Nullable public String creativeTab;
	@Nullable public double damageVsEntity = 4;
	@Nullable public int harvestLevel;
	@Nullable public double efficiency;
	@Nullable public int enchantability;
	@Nullable public int usageCount;

	public class Name{
		public String name;
		public boolean useTextField;
		@Nullable public String location;
	}
}
