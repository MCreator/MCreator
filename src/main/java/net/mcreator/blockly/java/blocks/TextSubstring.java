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

package net.mcreator.blockly.java.blocks;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.util.List;

public class TextSubstring implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);
		Element from = null, to = null, text = null;
		for (Element element : elements)
			if (element.getNodeName().equals("value"))
				if (element.getAttribute("name").equals("to"))
					to = element;
				else if (element.getAttribute("name").equals("from"))
					from = element;
				else if (element.getAttribute("name").equals("text"))
					text = element;

		if (text != null && from != null && (to != null || block.getAttribute("type").equals("text_substring_from"))) {
			master.append("(");
			master.processOutputBlock(text);
			master.append(".substring(");
			master.processOutputBlockToInt(from);
			if (to != null) {
				master.append(", ");
				master.processOutputBlockToInt(to);
			}
			master.append("))");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.empty_substring")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "text_substring", "text_substring_from" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

	@Nullable @Override public String[] getBlockJSONDefinitions() {
		return new String[] { """
        {
          "type": "text_substring",
          "args0": [
              {
                  "type": "input_value",
                  "name": "text",
                  "check": "String"
              },
              {
                  "type": "input_value",
                  "name": "from",
                  "check": "Number"
              },
              {
                  "type": "input_value",
                  "name": "to",
                  "check": "Number"
              }
          ],
          "inputsInline": true,
          "output": "String",
          "colour": "%{BKY_TEXTS_HUE}"
        }""", """
        {
          "type": "text_substring_from",
          "args0": [
              {
                  "type": "input_value",
                  "name": "text",
                  "check": "String"
              },
              {
                  "type": "input_value",
                  "name": "from",
                  "check": "Number"
              }
          ],
          "inputsInline": true,
          "output": "String",
          "colour": "%{BKY_TEXTS_HUE}"
        }""" };
	}

	@Nullable @Override public String getToolboxCategory() {
		return "text";
	}
}
