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
import net.mcreator.element.types.interfaces.NumericParameter;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.ResourceReference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") public class Structure extends GeneratableElement {

	@ResourceReference("structure") public String structure;
	public String projection;
	@ModElementReference public List<MItemBlock> ignoredBlocks;

	@NumericParameter(init = 5, min = 0, max = 1000000, step = 1) public int spacing;
	@NumericParameter(init = 2, min = 0, max = 1000000, step = 1) public int separation;

	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	public String terrainAdaptation;
	public String generationStep;

	public String surfaceDetectionType;
	public boolean useStartHeight;
	public String startHeightProviderType;
	@NumericParameter(init = 0, min = -1024, max = 1024, step = 1) public int startHeightMin;
	@NumericParameter(init = 128, min = -1024, max = 1024, step = 1) public int startHeightMax;

	@NumericParameter(init = 1, min = 0, max = 20, step = 1) public int size;
	@NumericParameter(init = 64, min = 1, max = 128, step = 1) public int maxDistanceFromCenter;
	@ModElementReference @ResourceReference("structure") public List<JigsawPool> jigsawPools;

	private Structure() {
		this(null);
	}

	public Structure(ModElement element) {
		super(element);

		this.size = 1;
		this.maxDistanceFromCenter = 64;
		this.jigsawPools = new ArrayList<>();

		this.useStartHeight = false;
		this.startHeightProviderType = "UNIFORM";
		this.startHeightMin = 0;
		this.startHeightMax = 128;
	}

	public List<JigsawPool.JigsawPart> getPoolParts() {
		JigsawPool.JigsawPart part = new JigsawPool.JigsawPart();
		part.weight = 1;
		part.structure = structure;
		part.projection = projection;
		part.ignoredBlocks = ignoredBlocks;
		return Collections.singletonList(part);
	}

	public static class JigsawPool {

		public String poolName;
		public String fallbackPool;
		@ModElementReference @ResourceReference("structure") @Nullable public List<JigsawPart> poolParts;

		@Nullable public List<JigsawPart> getPoolParts() {
			return poolParts;
		}

		public static class JigsawPart {

			@NumericParameter(init = 1, min = 1, max = 150, step = 1) public int weight;
			@ResourceReference("structure") public String structure;
			public String projection;
			@ModElementReference public List<MItemBlock> ignoredBlocks;

		}

	}

}
