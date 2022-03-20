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

package net.mcreator.blockly.feature.blocks;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class SimpleBlockStateProvider implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");
		if (element != null && element.getTextContent() != null && !element.getTextContent().equals("")
				&& !element.getTextContent().equals("null")) {
			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("block", new MItemBlock(master.getWorkspace(), element.getTextContent()));
				String code = master.getTemplateGenerator()
						.generateFromTemplate("blockstate_provider_simple.java.ftl", dataModel);
				master.append(code);
			}
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.empty_mcitem")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "mcitem_allblocks" };
	}

	@Override public IBlockGenerator.BlockType getBlockType() {
		return IBlockGenerator.BlockType.OUTPUT;
	}
}
