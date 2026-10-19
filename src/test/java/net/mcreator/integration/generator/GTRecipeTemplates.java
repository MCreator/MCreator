/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.integration.generator;

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.tools.quickrecipestool.QuickRecipesTool;
import net.mcreator.ui.init.RecipeTemplatesLoader;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

public class GTRecipeTemplates {

	public static void runTest(Logger LOG, String generatorName, Random random, MCreator mcreator,
			Workspace workspace) {
		int suffix = 0;
		List<MCItem> blocksAndItemsAndTagsNoAir = filterAir(ElementUtil.loadBlocksAndItemsAndTags(workspace));

		for (String templateName : RecipeTemplatesLoader.getTemplateNames()) {
			LOG.info("[{}] Testing recipe template {} ", generatorName, templateName);

			MItemBlock input = new MItemBlock(workspace, getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			MItemBlock result = new MItemBlock(workspace,
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());

			QuickRecipesTool.addRecipeToWorkspace(null, mcreator, workspace, "RecipeTemplate" + suffix, templateName,
					input, result);
			suffix++;
		}
	}

	private static List<MCItem> filterAir(List<MCItem> source) {
		return source.stream()
				.filter(e -> !(e.getName().equals("Blocks.AIR") || e.getName().equals("Blocks.VOID_AIR") || e.getName()
						.equals("Blocks.CAVE_AIR"))).toList();
	}

	private static MCItem getRandomMCItem(Random random, List<MCItem> list) {
		if (list.isEmpty())
			return new MCItem(new DataListEntry.Dummy("Blocks.STONE"));

		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
	}
}
