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

package net.mcreator.element.converter.fv13;

import com.google.gson.JsonElement;
import com.sun.media.jfxmediaimpl.HostUtils;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.TreeEntry;
import net.mcreator.element.types.Biome;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeTreeConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger(BiomeTreeConverter.class);
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Biome biome = (Biome) input;

		try {
			if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("treeType").getAsInt() == 0) {

				//Create the tree entry to add
				Biome.SpawnTree spawnTree = new Biome.SpawnTree();

				spawnTree.count = biome.treesPerChunk;

				String name = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
						.get("vanillaTreeType").getAsString();

				if(name.equals("Default")){
					spawnTree.tree = new TreeEntry(workspace, "OAK");
					spawnTree.shape = "NORMAL_TREE";
					biome.spawnTrees.add(spawnTree);
				} else if(name.equals("Big trees")){
					spawnTree.tree = new TreeEntry(workspace, "FANCY");
					spawnTree.shape = "FANCY_TREE";
					biome.spawnTrees.add(spawnTree);
				} else if(name.equals("Birch trees")){
					spawnTree.tree = new TreeEntry(workspace, "BIRCH");
					spawnTree.shape = "NORMAL_TREE";
					biome.spawnTrees.add(spawnTree);
				} else if(name.equals("Savanna trees")){
					spawnTree.tree = new TreeEntry(workspace, "ACACIA");
					spawnTree.shape = "ACACIA_TREE";
					biome.spawnTrees.add(spawnTree);
				} else if(name.equals("Mega pine trees")){
					spawnTree.tree = new TreeEntry(workspace, "MEGA_PINE");
					spawnTree.shape = "MEGA_SPRUCE_TREE";
					biome.spawnTrees.add(spawnTree);
				} else if(name.equals("Mega spruce trees")){
					spawnTree.tree = new TreeEntry(workspace, "MEGA_SPRUCE");
					spawnTree.shape = "MEGA_SPRUCE_TREE";
					biome.spawnTrees.add(spawnTree);
				}

			}
		} catch (Exception e) {
			LOG.warn("Could not convert: " + biome.getModElement().getName());
			LOG.error(e);
		}

		return biome;
	}

	@Override public int getVersionConvertingTo() {
		return 13;
	}
}
