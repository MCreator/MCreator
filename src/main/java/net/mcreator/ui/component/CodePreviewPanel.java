/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.component;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.ide.RSyntaxTextAreaStyler;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CodePreviewPanel extends JPanel {

	public final RSyntaxTextArea te = new RSyntaxTextArea();

	public CodePreviewPanel(String code, File file) {
		super(new BorderLayout());

		te.requestFocusInWindow();
		te.setMarkOccurrences(true);
		te.setCodeFoldingEnabled(true);
		te.setClearWhitespaceLinesEnabled(true);
		te.setAutoIndentEnabled(true);
		te.setEnabled(false);
		if (file.getName().endsWith(".json") || file.getName().endsWith(".mcmeta")) {
			te.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
		} else {
			te.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
		}
		te.setText(code);

		te.setTabSize(4);

		RTextScrollPane sp = new RTextScrollPane(te, PreferencesManager.PREFERENCES.ide.lineNumbers.get());

		RSyntaxTextAreaStyler.style(te, sp, PreferencesManager.PREFERENCES.ide.fontSize.get());

		sp.setFoldIndicatorEnabled(true);

		sp.getGutter().setFoldBackground(getBackground());
		sp.getGutter().setBorderColor(getBackground());

		sp.setIconRowHeaderEnabled(true);

		sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());
		sp.setBorder(null);

		add("Center", sp);
	}

}
