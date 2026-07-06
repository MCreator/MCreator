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

public class TextFormatNumber implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);
		Element number = null, format = null;
		for (Element element : elements)
			if (element.getNodeName().equals("value"))
				if (element.getAttribute("name").equals("number"))
					number = element;
				else if (element.getAttribute("name").equals("format"))
					format = element;

		if (number != null && format != null) {
			master.append("(new java.text.DecimalFormat(");
			master.processOutputBlockWithoutParentheses(format);
			master.append(").format(");
			master.processOutputBlockWithoutParentheses(number);
			master.append("))");
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.text_format_number_missing_inputs")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "text_format_number" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

	@Nullable @Override public String[] getBlockJSONDefinitions() {
		return new String[] { """
        {
          "type": "text_format_number",
          "args0": [
              {
                  "type": "input_value",
                  "name": "number",
                  "check": "Number"
              },
              {
                  "type": "input_value",
                  "name": "format",
                  "check": "String"
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

	@Nullable @Override public String[][] getToolboxInit() {
		return new String[][] {
				{ "<value name=\"format\"><block type=\"text\"><field name=\"TEXT\">##.##</field></block></value>" } };
	}
}
