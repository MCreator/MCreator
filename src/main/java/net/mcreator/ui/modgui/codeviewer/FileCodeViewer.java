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

package net.mcreator.ui.modgui.codeviewer;

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.io.writer.JSONWriter;
import net.mcreator.java.ImportFormat;
import net.mcreator.ui.ide.CodeEditorView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class FileCodeViewer<T extends GeneratableElement> extends JPanel {

	private final CodeEditorView cev;

	private String oldCode;

	public FileCodeViewer(ModElementCodeViewer<T> modElementCodeViewer, GeneratorFile file) {
		super(new BorderLayout());

		cev = new CodeEditorView(modElementCodeViewer.getModElementGUI().getMCreator(), "", file.file().getName(),
				file.file(), true);
		cev.hideNotice();
		add(cev);

		try {
			String code = format(file);
			cev.te.setText(code);
			oldCode = code;
		} catch (Exception e) {
			oldCode = "";
		}
	}

	public boolean update(GeneratorFile file) throws Exception {
		String code = format(file);
		if (!code.equals(oldCode)) {
			cev.te.setText(code);
			try {
				cev.te.setCaretPosition(StringUtils.indexOfDifference(oldCode, code));
				cev.centerLineInScrollPane();
			} catch (Exception ignored) {
			}
			oldCode = code;
			return true;
		}
		return false;
	}

	private String format(GeneratorFile input) throws Exception {
		if (input.writer() == null || input.writer().equals("java")) {
			String codeformatted = cev.getCodeCleanup().reformatTheCodeOnly(input.contents());
			if (!codeformatted.contains("\t"))
				throw new Exception("Format failed");
			return ImportFormat.removeImports(codeformatted, "\n\n/* imports omitted */\n\n");
		} else if (input.writer().equals("json")) {
			return JSONWriter.formatJSON(input.contents());
		}

		return input.contents();
	}

}
