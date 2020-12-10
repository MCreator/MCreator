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

public class Blocks {
	//Variables used by all block mod elements
	public String elementType;
	public Items.Name name;
	public String texture;
	@Nullable public String creativeTab;
	@Nullable public String textureTop;
	@Nullable public String textureLeft;
	@Nullable public String textureFront;
	@Nullable public String textureRight;
	@Nullable public String textureBack;
	@Nullable public String blockBase;
	public int renderType;
	public String customModelName;
	public String material;
	public String soundOnStep;
	public double hardness;
	public double resistance;
	public String destroyTool;
	public int breakHarvestLevel;
	public int flammability;
	@Nullable public boolean plantsGrowOn;

	//World generation
	@Nullable public List<String> spawnWorldTypes;
	@Nullable public int minGenerateHeight;
	@Nullable public int maxGenerateHeight;
	@Nullable public int frequencyPerChunks;
	@Nullable public int frequencyOnChunk;
	@Nullable public int dropAmount;
}
