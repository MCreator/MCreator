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
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class AIConditionEditor {

	public static List<String> open(MCreator parent, @Nullable String[] data) {
		List<String> retVal = data != null ? Arrays.asList(data) : Arrays.asList("null", "null");

		ProcedureSelector startCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("entity/ai_start_condition"), parent,
				L10N.t("dialog.ai_condition.additional_start"), ProcedureSelector.Side.BOTH, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		ProcedureSelector continueCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("entity/ai_continue_condition"), parent,
				L10N.t("dialog.ai_condition.additional_continue"), ProcedureSelector.Side.BOTH, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		startCondition.setDefaultName(L10N.t("condition.common.no_additional")).refreshList();
		continueCondition.setDefaultName(L10N.t("condition.common.no_additional")).refreshList();

		if (data != null && data.length == 2) {
			startCondition.setSelectedProcedure(data[0]);
			continueCondition.setSelectedProcedure(data[1]);
		}

		MCreatorDialog window = new MCreatorDialog(parent, L10N.t("dialog.ai_condition.panel_name"));
		window.setSize(450, 140);
		window.setLocationRelativeTo(parent);
		window.setModal(true);

		JPanel conditions = new JPanel();
		conditions.add(PanelUtils.centerAndEastElement(startCondition, continueCondition, 20, 5));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		ok.addActionListener(e -> {
			retVal.set(0, startCondition.getSelectedProcedure() != null ?
					startCondition.getSelectedProcedure().getName() :
					"null");
			retVal.set(1, continueCondition.getSelectedProcedure() != null ?
					continueCondition.getSelectedProcedure().getName() :
					"null");
			window.setVisible(false);
		});
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> window.setVisible(false));
		parent.getRootPane().setDefaultButton(ok);
		JPanel options = new JPanel();
		options.add(PanelUtils.join(ok, cancel));

		window.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndSouthElement(conditions, options)));
		window.setVisible(true);

		return retVal;
	}

}
