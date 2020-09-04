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

package net.mcreator.ui.ide.action;

import net.mcreator.io.FileIO;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.ide.CodeEditorView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.io.File;

public class ReloadCodeAction extends BasicAction {

	public ReloadCodeAction(ActionRegistry actionRegistry) {
		super(actionRegistry, "Reload code from file system", actionEvent -> {
			JPanel pan = actionRegistry.getMCreator().mcreatorTabs.getCurrentTab().getContent();
			if (pan instanceof CodeEditorView) {
				CodeEditorView codeEditorView = (CodeEditorView) pan;
				File curr = codeEditorView.fileWorkingOn;
				if (curr.isFile()) {
					int sel = JOptionPane.OK_OPTION;
					if (codeEditorView.changed)
						sel = JOptionPane.showConfirmDialog(actionRegistry.getMCreator(),
								"<html>If you load file from file system, all changes<br>"
										+ "you made to the code will be overwritten!<br>"
										+ "<b>Are you sure you want to continue?", "Overwrite?",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (sel == JOptionPane.OK_OPTION) {
						codeEditorView.te.setText(FileIO.readFileToString(curr));
						codeEditorView.changed = false;
						if (codeEditorView.cl != null)
							codeEditorView.cl.stateChanged(new ChangeEvent(codeEditorView));
					}
				}
			}
		});
		setTooltip("Click this to reload the code from FS in current code editor");
		actionRegistry.getMCreator().mcreatorTabs
				.addTabShownListener(tab -> setEnabled(tab.getContent() instanceof CodeEditorView));
	}

}
