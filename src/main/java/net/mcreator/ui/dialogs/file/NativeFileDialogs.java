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

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.mcreator.ui.component.filebrowser.SynchronousJFXDirectoryChooser;
import net.mcreator.ui.component.filebrowser.SynchronousJFXFileChooser;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static net.mcreator.ui.dialogs.file.FileDialogs.prevDir;

/**
 * Should only be used on Windows or macOS at the time of writing due to Threading issues
 */
class NativeFileDialogs {

	protected static File[] getFileChooserDialog(FileChooserType type, boolean multiSelect,
			@Nullable String suggestedFileName, FileChooser.ExtensionFilter... filters) {
		if (multiSelect && type == FileChooserType.SAVE)
			throw new RuntimeException("Invalid file chooser type for multi selection mode");

		SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(null, () -> {
			FileChooser ch = new FileChooser();

			if (suggestedFileName != null)
				ch.setInitialFileName(suggestedFileName);

			if (prevDir != null && prevDir.isDirectory())
				ch.setInitialDirectory(prevDir);

			if (filters != null) {
				ch.getExtensionFilters().clear();
				ch.getExtensionFilters().addAll(Arrays.asList(filters));
			}
			return ch;
		});

		if (multiSelect) {
			List<File> files = chooser.showOpenMultipleDialog();
			if (files != null && !files.isEmpty()) {
				prevDir = files.get(0).getParentFile();
				return files.toArray(File[]::new);
			}
		} else {
			File retval = type == FileChooserType.SAVE ? chooser.showSaveDialog() : chooser.showOpenDialog();
			if (retval != null) {
				prevDir = retval.getParentFile();
				return new File[] { retval };
			}
		}

		return new File[] {};
	}

	protected static File getWorkspaceDirectorySelectDialog(Window f, File file) {
		SynchronousJFXDirectoryChooser chooser = new SynchronousJFXDirectoryChooser(null, () -> {
			DirectoryChooser ch = new DirectoryChooser();
			ch.setTitle(L10N.t("dialog.file.select_directory_title"));

			File initialDir = file == null ? WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot() : file;
			if (initialDir != null && initialDir.isDirectory())
				ch.setInitialDirectory(initialDir);
			return ch;
		});

		File selectedFile = chooser.showDialog();

		if (selectedFile == null)
			return null;

		if (FileDialogs.isWorkspaceFolderInvalid(f, selectedFile))
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());

		return selectedFile;
	}

}
