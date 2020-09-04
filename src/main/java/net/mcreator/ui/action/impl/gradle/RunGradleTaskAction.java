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

package net.mcreator.ui.action.impl.gradle;

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;

public class RunGradleTaskAction extends GradleAction {

	public RunGradleTaskAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.gradle.run_gradle_task"), e -> {
			JPanel bas = new JPanel(new BorderLayout(5, 5));
			bas.add("North", new JLabel(L10N.t("action.gradle.run_gradle_task.dialog.instructions")));
			JComboBox<String> ba = new JComboBox<>(new String[] { L10N.t("dialog.gradle.run_gradle_task.options.build"),
					L10N.t("dialog.gradle.run_gradle_task.options.tasks"),
					L10N.t("dialog.gradle.run_gradle_task.options.clean") });
			ba.setEditable(true);
			bas.add("Center", ba);
			int retval = JOptionPane
					.showConfirmDialog(actionRegistry.getMCreator(), bas, L10N.t("dialog.gradle.run_gradle_task.title"),
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if (ba.getSelectedItem() != null && retval != JOptionPane.CANCEL_OPTION)
				actionRegistry.getMCreator().getGradleConsole().exec((String) ba.getSelectedItem());
		});
	}

}
