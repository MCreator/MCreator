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

package net.mcreator.ui.blockly;

import net.mcreator.blockly.java.BlocklyVariables;
import net.mcreator.blockly.java.ProcedureTemplateIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.modgui.ProcedureGUI;
import net.mcreator.workspace.elements.VariableElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Set;

public class ProcedureTemplateDropdown extends JScrollablePopupMenu {

	private static final Logger LOG = LogManager.getLogger(ProcedureTemplateDropdown.class);

	public ProcedureTemplateDropdown(ProcedureGUI procedureGUI, BlocklyPanel blocklyPanel,
			List<ResourcePointer> templatesSorted) {
		setMaximumVisibleRows(20);
		for (ResourcePointer template : templatesSorted) {
			try {
				JMenuItem modTypeButton = new JMenuItem(template.toString());

				String procedureXml;

				if (template.identifier instanceof String)
					procedureXml = ProcedureTemplateIO.importBlocklyXML("/" + template.identifier);
				else
					procedureXml = ProcedureTemplateIO.importBlocklyXML((File) template.identifier);

				Set<VariableElement> localVariables = ProcedureTemplateIO.tryToExtractVariables(procedureXml);

				modTypeButton.addActionListener(actionEvent -> {
					List<VariableElement> existingLocalVariables = blocklyPanel.getLocalVariablesList();

					for (VariableElement localVariable : localVariables) {
						if (existingLocalVariables.contains(localVariable))
							continue; // skip if variable with this name already exists

						blocklyPanel.addLocalVariable(localVariable.getName(),
								localVariable.getType().getBlocklyVariableType());
						procedureGUI.localVars.addElement(localVariable);
					}

					blocklyPanel.addBlocksFromXML(procedureXml);
				});

				modTypeButton.setOpaque(true);
				ComponentUtils.deriveFont(modTypeButton, 12);
				add(modTypeButton);
			} catch (Exception e) {
				LOG.info("Failed to load template: " + template);
			}
		}
	}

}
