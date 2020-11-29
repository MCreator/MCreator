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

package net.mcreator.ui.dialogs.tools.plugin;

import net.mcreator.ui.dialogs.tools.plugin.elements.Blocks;
import net.mcreator.ui.dialogs.tools.plugin.elements.Items;
import net.mcreator.ui.dialogs.tools.plugin.elements.Recipes;
import net.mcreator.ui.dialogs.tools.plugin.elements.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class PackMakerTool {

	transient public String packID;
	public UI ui;

	@Nullable public List<Texture> textures;
	@Nullable public List<Items> items;
	@Nullable public List<Blocks> blocks;
	@Nullable public List<Recipes> recipes;
	@Nullable public List<Tags> tags;
	@Nullable public List<String> packs;
	public List<String> mod_elements;

	public class UI {
		@Nullable public String icon;
		public NameField name;
		@Nullable public boolean color;
		@Nullable public PowerSpinner power;
		@Nullable public boolean baseItem;

		public class NameField{
			public short length;
	}
		public class PowerSpinner{
			public double value;
			public double min;
			public double max;
			public double stepSize;
		}
	}

	public class Texture{
		@Nullable public List<String> baseTexture;
		public List<String> textures;
		@Nullable public boolean baseTextureTop;
		public String type;
		@Nullable public String armorType;
		public String name;
		@Nullable public String condition = "";
	}
}
