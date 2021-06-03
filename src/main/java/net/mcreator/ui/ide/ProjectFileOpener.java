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

package net.mcreator.ui.ide;

import net.mcreator.io.FileIO;
import net.mcreator.io.tree.FileNode;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.io.File;

public class ProjectFileOpener {

	public static CodeEditorView openCodeFile(MCreator mcreator, File file) {
		if (file.isFile()) {
			CodeEditorView cev = new CodeEditorView(mcreator, file);
			ModElement owner = mcreator.getGenerator().getModElementThisFileBelongsTo(file);
			if (owner != null)
				cev.setFileOwnerModElement(owner);
			return (CodeEditorView) cev.showView();
		}
		return null;
	}

	public static void openCodeFileRO(MCreator mcreator, FileNode node) {
		if (CodeEditorView.isFileSupported(node.incrementalPath)) {
			File libFile = new File(node.incrementalPath.split(":%:")[0]);
			String path = node.incrementalPath.split(":%:")[1];
			if (path.startsWith("/"))
				path = path.substring(1);

			String code = ZipIO.readCodeInZip(libFile, path);
			CodeEditorView cev = new CodeEditorView(mcreator, code, node.data, new File(path), true);
			cev.showView();
		}
	}

	public static void openFileAtLine(MCreator mcreator, File file, int linenum) {
		if (file.isFile()) {
			CodeEditorView tarea = openCodeFile(mcreator, file);
			if (tarea != null)
				tarea.jumpToLine(linenum);
		}
	}

	public static CodeEditorView openFileSpecific(MCreator mcreator, File file, boolean readOnly, int carret,
			File virtualFile) {
		if (file.isFile()) {
			String code = FileIO.readFileToString(file);
			CodeEditorView cev = new CodeEditorView(mcreator, code, virtualFile.getName(), virtualFile, readOnly);
			cev = (CodeEditorView) cev.showView();
			cev.te.setCaretPosition(carret);
			cev.te.requestFocus();
			SwingUtilities.invokeLater(cev::centerLineInScrollPane);
			return cev;

		}
		return null;
	}

}
