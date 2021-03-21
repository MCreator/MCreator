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

package net.mcreator.ui.dialogs;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.workspace.elements.VariableElementType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class CommandProcedureEditor {

	public static List<String> open(MCreator parent, @Nullable String[] data) {
		ProcedureSelector executeProcedure = new ProcedureSelector(
				IHelpContext.NONE.withEntry("command/arg_procedure"), parent,
				L10N.t("dialog.arg_procedure.execute_action"), ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/cmdargs:ctx"));

		executeProcedure.refreshList();

		if (data != null && data.length == 1) {
			executeProcedure.setSelectedProcedure(data[0]);
		}

		JOptionPane pane = new JOptionPane(executeProcedure);
		JDialog dialog = pane.createDialog(parent, L10N.t("dialog.arg_procedure.panel_name"));
		dialog.setVisible(true);

		return Collections.singletonList(executeProcedure.getSelectedProcedure() != null ?
				executeProcedure.getSelectedProcedure().getName() :
				"null");
	}
}
