/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs.file;

import com.formdev.flatlaf.util.SystemFileChooser;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;

import static net.mcreator.ui.dialogs.file.FileDialogs.prevDir;

class NativeFileDialogs {

	protected static File[] getFileChooserDialog(Window f, FileChooserType type, boolean multiSelect,
			@Nullable String suggestedFileName, ExtensionFilter... filters) {
		if (multiSelect && type == FileChooserType.SAVE)
			throw new RuntimeException("Invalid file chooser type for multi selection mode");

		SystemFileChooser fileChooser = new SystemFileChooser();

		if (suggestedFileName != null)
			fileChooser.setSelectedFile(new File(prevDir, suggestedFileName));

		if (prevDir != null && prevDir.isDirectory())
			fileChooser.setCurrentDirectory(prevDir);

		if (filters != null) {
			for (ExtensionFilter filter : filters) {
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(new SystemFileChooser.FileNameExtensionFilter(filter.description(),
						filter.extensions().stream().map(e -> e.replace("*.", "")).toArray(String[]::new)));
			}
		}

		if (multiSelect) {
			fileChooser.setMultiSelectionEnabled(true);
			if (fileChooser.showOpenDialog(f) == SystemFileChooser.APPROVE_OPTION) {
				prevDir = fileChooser.getCurrentDirectory();
				File[] files = fileChooser.getSelectedFiles();
				if (files != null)
					return files;
			} else {
				prevDir = fileChooser.getCurrentDirectory();
			}
		} else {
			if ((type == FileChooserType.SAVE ? fileChooser.showSaveDialog(f) : fileChooser.showOpenDialog(f))
					== SystemFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					prevDir = file.getParentFile();
					return new File[] { file };
				}
			} else {
				prevDir = fileChooser.getCurrentDirectory();
			}
		}

		return new File[] {};
	}

	protected static File getWorkspaceDirectorySelectDialog(Window f, File file) {
		SystemFileChooser fileChooser = new SystemFileChooser();
		fileChooser.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle(L10N.t("dialog.file.select_directory_title"));

		File initialDir = file == null ? WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot() : file;
		if (initialDir != null && initialDir.isDirectory())
			fileChooser.setCurrentDirectory(initialDir);

		File selectedFile = null;
		if (fileChooser.showOpenDialog(f) == SystemFileChooser.APPROVE_OPTION)
			selectedFile = fileChooser.getSelectedFile();

		if (selectedFile == null)
			return null;

		if (FileDialogs.isWorkspaceFolderInvalid(f, selectedFile))
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());

		return selectedFile;
	}

}
