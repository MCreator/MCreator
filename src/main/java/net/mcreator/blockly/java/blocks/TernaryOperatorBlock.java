/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.blockly.java.blocks;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.java.JavaKeywordsMap;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TernaryOperatorBlock implements IBlockGenerator {
	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);

		Element condition = null, thenBlock = null, elseBlock = null;
		String markerType = ""; // used to properly map blocks and items
		for (Element element : elements) {
			if (element.getAttribute("name").equals("condition"))
				condition = element;
			else if (element.getAttribute("name").equals("THEN"))
				thenBlock = element;
			else if (element.getAttribute("name").equals("ELSE"))
				elseBlock = element;
			else if (element.hasAttribute("marker"))
				markerType = element.getAttribute("marker");
		}
		if (thenBlock != null && elseBlock != null) {
			if (condition != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("outputMarker", JavaKeywordsMap.MARKER_TYPES.getOrDefault(markerType, ""));
				dataModel.put("condition", BlocklyToCode.directProcessOutputBlock(master, condition));
				dataModel.put("ifTrue", BlocklyToCode.directProcessOutputBlock(master, thenBlock));
				dataModel.put("ifFalse", BlocklyToCode.directProcessOutputBlock(master, elseBlock));

				if (master.getTemplateGenerator() != null) {
					String code = master.getTemplateGenerator()
							.generateFromTemplate("_logic_ternary.java.ftl", dataModel);
					master.append(code);
				}
			} else {
				master.processOutputBlock(thenBlock);
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.ternary_operator.no_condition")));
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.ternary_operator.no_output")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "logic_ternary_op" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
