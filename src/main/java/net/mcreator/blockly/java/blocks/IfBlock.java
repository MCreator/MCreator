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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

public class IfBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		// number of branches is determined from the inputs that are present
		int elseIfCount = 0;
		for (Element input : XMLUtil.getDirectChildren(block)) {
			String name = input.getAttribute("name");
			if (name.startsWith("IF") || name.startsWith("DO"))
				elseIfCount = Math.max(elseIfCount, Integer.parseInt(name.substring(2)));
		}
		boolean allBranchesTerminate = true;
		// if block without any body generates no code
		boolean hasBody = !XMLUtil.getChildrenWithName(block, "statement").isEmpty();
		// branches with empty body are generated too, so the branches after them stay valid
		for (int ifindex = 0; hasBody && ifindex <= elseIfCount; ifindex++) {
			Element condition = getInput(block, "value", "IF" + ifindex);
			Element body = getInput(block, "statement", "DO" + ifindex);
			if (ifindex > 0 && condition == null && body == null)
				continue; // else if without condition and body is skipped

			master.append(ifindex == 0 ? "if (" : "else if (");
			if (condition != null) {
				master.processOutputBlockWithoutParentheses(condition);
			} else {
				master.append("true");
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.if_no_condition")));
			}
			master.append(") ");
			allBranchesTerminate &= processBranch(master, body);
		}

		Element elseBranch = getInput(block, "statement", "ELSE");
		if (elseBranch != null) {
			master.append("else");
			allBranchesTerminate &= processBranch(master, elseBranch);
		}

		if (getInput(block, "statement", "DO0") == null) {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.if_no_main_body")));
		}

		// if-else where every branch terminates can not complete normally, if without else can
		if (elseBranch != null && allBranchesTerminate)
			master.markBlockTerminating();
	}

	private static Element getInput(Element block, String type, String name) {
		for (Element input : XMLUtil.getChildrenWithName(block, type)) {
			if (input.getAttribute("name").equals(name))
				return input;
		}
		return null;
	}

	private static boolean processBranch(BlocklyToCode master, Element branch) throws TemplateGeneratorException {
		boolean terminates = false;
		master.append("{");
		if (branch != null)
			terminates = master.processBlockProcedure(BlocklyBlockUtil.getBlockProcedureStartingWithBlock(branch));
		master.append("}");
		return terminates;
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_if" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
