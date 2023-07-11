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

package net.mcreator.ui;

import net.mcreator.io.tree.FileNode;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.views.NBTEditorView;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.util.DesktopUtils;

import java.io.File;

public class FileOpener {

	public static void openFile(MCreator mcreator, Object file) {
		if (CodeEditorView.isFileSupported(file.toString())) {
			if (file instanceof FileNode node)
				ProjectFileOpener.openCodeFileRO(mcreator, node);
			else if (file instanceof File code)
				ProjectFileOpener.openCodeFile(mcreator, code);
		} else if (ImageMakerView.isFileSupported(file.toString())) {
			ImageMakerView imageMakerView = new ImageMakerView(mcreator);
			if (file instanceof FileNode node)
				imageMakerView.openInReadOnlyMode(node);
			else if (file instanceof File pic)
				imageMakerView.openInEditMode(pic);
			else
				return;
			imageMakerView.showView();
		} else if (file instanceof File nbt && nbt.getName().endsWith(".nbt")) {
			NBTEditorView nbtEditorView = new NBTEditorView(mcreator, nbt);
			nbtEditorView.showView();
		} else if (file instanceof File text && text.isFile()) {
			DesktopUtils.openSafe(text);
		}
	}

}
