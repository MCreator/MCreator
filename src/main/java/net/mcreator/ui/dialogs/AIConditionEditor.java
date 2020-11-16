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
import java.util.Arrays;
import java.util.List;

public class AIConditionEditor {

	public static List<String> open(MCreator parent, @Nullable String[] data) {
		ProcedureSelector startCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("entity/ai_start_condition"), parent, L10N.t("dialog.ai_condition.additional_start"),
				ProcedureSelector.Side.BOTH, false, VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		ProcedureSelector continueCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("entity/ai_continue_condition"), parent, L10N.t("dialog.ai_condition.additional_continue"),
				ProcedureSelector.Side.BOTH, false, VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		startCondition.setDefaultName(L10N.t("dialog.ai_condition.no_additional")).refreshList();
		continueCondition.setDefaultName(L10N.t("dialog.ai_condition.no_additional")).refreshList();

		if (data != null && data.length == 2) {
			startCondition.setSelectedProcedure(data[0]);
			continueCondition.setSelectedProcedure(data[1]);
		}

		JOptionPane pane = new JOptionPane(PanelUtils.gridElements(1, 2, 10, 10, startCondition, continueCondition));
		JDialog dialog = pane.createDialog(parent, L10N.t("dialog.ai_condition.panel_name"));
		dialog.setVisible(true);

		return Arrays.asList(startCondition.getSelectedProcedure() != null ?
				startCondition.getSelectedProcedure().getName() :
				"null", continueCondition.getSelectedProcedure() != null ?
				continueCondition.getSelectedProcedure().getName() :
				"null");
	}

}
