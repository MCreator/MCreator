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

package net.mcreator.ui.chromium;

import net.mcreator.ui.init.L10N;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefJSDialogCallback;
import org.cef.handler.CefJSDialogHandlerAdapter;
import org.cef.misc.BoolRef;

import javax.swing.*;

public class SwingJSDialogHandler extends CefJSDialogHandlerAdapter {

	private final JComponent parent;

	public SwingJSDialogHandler(JComponent parent) {
		this.parent = parent;
	}

	@Override
	public boolean onJSDialog(CefBrowser browser, String origin_url, JSDialogType dialog_type, String message_text,
			String default_prompt_text, CefJSDialogCallback callback, BoolRef suppress_message) {

		// Route to the Event Dispatch Thread for Swing component safety
		SwingUtilities.invokeLater(() -> {
			switch (dialog_type) {
			case JSDIALOGTYPE_ALERT:
				JOptionPane.showMessageDialog(parent, message_text,
						UIManager.getString("OptionPane.messageDialogTitle"), JOptionPane.WARNING_MESSAGE);
				callback.Continue(true, "");
				break;

			case JSDIALOGTYPE_CONFIRM:
				int result = JOptionPane.showConfirmDialog(parent, message_text, L10N.t("common.confirmation"),
						JOptionPane.OK_CANCEL_OPTION);
				callback.Continue(result == JOptionPane.OK_OPTION, "");
				break;

			case JSDIALOGTYPE_PROMPT:
				String input = JOptionPane.showInputDialog(parent, message_text, default_prompt_text);
				if (input != null) {
					callback.Continue(true, input);
				} else {
					callback.Continue(false, "");
				}
				break;
			}
		});

		// Returning true informs JCEF that we are handling the dialog asynchronously,
		// and it should wait for callback.Continue() to be invoked.
		return true;
	}
}
