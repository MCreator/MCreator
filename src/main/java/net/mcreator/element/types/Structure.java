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
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.workspace.elements.ModElement;

import java.util.List;

@SuppressWarnings("unused") public class Structure extends GeneratableElement {

	public String structure;
	public String projection; //UI: dropdown: [rigid, terrain_matching]
	public String ignoreBlocks;

	public int spacing; //UI
	public int separation; //UI

	public List<BiomeEntry> restrictionBiomes; //UI: require at least one entry
	public String surfaceDetectionType;
	public String spawnLocation; //implement

	public List<MItemBlock> restrictionBlocks; //implement
	public Procedure generateCondition; //implement
	public Procedure onStructureGenerated; //implement

	public int spawnHeightOffset; //TODO: removal
	public int spawnProbability; //TODO: removal
	public boolean randomlyRotateStructure; //TODO: removal
	public int minCountPerChunk; //TODO: removal
	public int maxCountPerChunk; //TODO: removal
	public int spawnXOffset; //TODO: removal
	public int spawnZOffset; //TODO: removal
	public List<String> spawnWorldTypes; //TODO: removal

	private Structure() {
		this(null);
	}

	public Structure(ModElement element) {
		super(element);

		this.surfaceDetectionType = "First motion blocking block";
		this.ignoreBlocks = "STRUCTURE_BLOCK";
	}

}
