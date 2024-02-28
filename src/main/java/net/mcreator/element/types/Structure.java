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
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.ResourceReference;

import java.util.List;

@SuppressWarnings("unused") public class Structure extends GeneratableElement {

	@ResourceReference("structure") public String structure;
	public String projection;
	@ModElementReference public List<MItemBlock> ignoredBlocks;

	public int spacing;
	public int separation;

	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	public String surfaceDetectionType;
	public String terrainAdaptation;
	public String generationStep;

	private Structure() {
		this(null);
	}

	public Structure(ModElement element) {
		super(element);
	}

}
