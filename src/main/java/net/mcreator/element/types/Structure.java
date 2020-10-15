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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Procedure;
import net.mcreator.workspace.elements.ModElement;

import java.util.List;

@SuppressWarnings("unused") public class Structure extends GeneratableElement {

	public String structure;

	public boolean randomlyRotateStructure;

	public String surfaceDetectionType;

	public int spawnProbability;

	public int minCountPerChunk;
	public int maxCountPerChunk;

	public String spawnLocation;
	public int spawnHeightOffset;
	public List<String> spawnWorldTypes;

	public List<MItemBlock> restrictionBlocks;
	public List<BiomeEntry> restrictionBiomes;

	public String ignoreBlocks;

	public Procedure generateCondition;

	public Procedure onStructureGenerated;

	private Structure() {
		this(null);
	}

	public Structure(ModElement element) {
		super(element);

		this.randomlyRotateStructure = true;
		this.spawnHeightOffset = 0;
		this.surfaceDetectionType = "First motion blocking block";

		this.minCountPerChunk = 1;
		this.maxCountPerChunk = 1;

		this.ignoreBlocks = "STRUCTURE_BLOCK";
	}

}
