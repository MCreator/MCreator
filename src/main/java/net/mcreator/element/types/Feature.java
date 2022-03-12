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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Procedure;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class Feature extends GeneratableElement {

	public String generationType;
	public List<String> spawnWorldTypes;
	public List<BiomeEntry> restrictionBiomes;
	public List<MItemBlock> blocksToReplace;
	public Procedure generateCondition;

	public MItemBlock blockToGenerate;

	// Ore
	public String generationShape;
	public int frequencyPerChunks;
	public int frequencyOnChunk;
	public int minGenerateHeight;
	public int maxGenerateHeight;

	private Feature() {
		this(null);
	}

	public Feature(ModElement element) {
		super(element);
	}

	public boolean isGeneratedInWorld() {
		return !spawnWorldTypes.isEmpty();
	}
	public boolean doesGenerateInWorld() {
		return spawnWorldTypes.size() > 0;
	}
}
