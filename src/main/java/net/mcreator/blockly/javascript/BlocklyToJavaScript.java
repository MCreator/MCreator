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

package net.mcreator.blockly.javascript;

import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

public class BlocklyToJavaScript extends BlocklyToCode {

	/**
	 * @param workspace         <p>The {@link Workspace} executing the code</p>
	 * @param blocklyEditorType <p>Blockly editor type</p>
	 * @param sourceXML         <p>The XML code used by Blockly</p>
	 * @param templateGenerator <p>The folder location in each {@link net.mcreator.generator.Generator} containing the code template files<p>
	 */
	public BlocklyToJavaScript(Workspace workspace, ModElement parent, BlocklyEditorType blocklyEditorType,
			String sourceXML, TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		super(workspace, parent, blocklyEditorType, sourceXML, templateGenerator, externalGenerators);
	}

}
