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

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.ide.CodeEditorView;
import org.fife.ui.rsyntaxtextarea.focusabletip.FocusableTip;

import javax.swing.*;

public class SaveCodeAction extends BasicAction {

	public SaveCodeAction(ActionRegistry actionRegistry) {
		super(actionRegistry, "Save code", actionEvent -> {
			JPanel pan = actionRegistry.getMCreator().mcreatorTabs.getCurrentTab().getContent();
			if (pan instanceof CodeEditorView) {
				CodeEditorView codeEditorView = (CodeEditorView) pan;
				if (!codeEditorView.readOnly) {
					codeEditorView.disableJumpToMode();
					codeEditorView.saveCode();
					if (codeEditorView.mouseEvent != null)
						new FocusableTip(codeEditorView.te, null)
								.toolTipRequested(codeEditorView.mouseEvent, "Code saved");
				}
			}
		});
		setTooltip("Click this to save code in current code editor");
		actionRegistry.getMCreator().mcreatorTabs.addTabShownListener(tab -> setEnabled(
				tab.getContent() instanceof CodeEditorView && !((CodeEditorView) tab.getContent()).readOnly));
	}

}

