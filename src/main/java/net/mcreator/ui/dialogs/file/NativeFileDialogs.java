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
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

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
			fileChooser.setAcceptAllFileFilterUsed(false);
			for (ExtensionFilter filter : filters) {
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(new SystemFileChooser.FileNameExtensionFilter(filter.description(),
						filter.extensions().stream().map(e -> e.replace("*.", "")).toArray(String[]::new)));
			}
		}

		if (type == FileChooserType.SAVE) {
			fileChooser.setApproveCallback((selectedFiles, context) -> {
				File selectedFile = selectedFiles != null ? selectedFiles[0] : null;
				if (selectedFile != null) {
					selectedFile = addExtensionIfNeeded(selectedFile, fileChooser);
					if (selectedFile.exists()) {
						int option = context.showMessageDialog(JOptionPane.WARNING_MESSAGE,
								L10N.t("dialog.file.error_already_exists", selectedFile.getName()), null, 1,
								UIManager.getString("OptionPane.yesButtonText"),
								UIManager.getString("OptionPane.noButtonText"));
						if (option != 0) { // if anything but yes button (index 0)
							return SystemFileChooser.CANCEL_OPTION;
						}
					}
				}
				return SystemFileChooser.APPROVE_OPTION;
			});
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

				if (type == FileChooserType.SAVE) {
					file = addExtensionIfNeeded(file, fileChooser);
				}

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

	private static File addExtensionIfNeeded(File file, SystemFileChooser fileChooser) {
		// getFileFilter does not work, so this only works for one extension
		// see https://github.com/JFormDesigner/FlatLaf/issues/1065
		SystemFileChooser.FileFilter filter = fileChooser.getFileFilter();
		if (filter instanceof SystemFileChooser.FileNameExtensionFilter extensionFilter) {
			String extension = FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.ENGLISH);
			if (Arrays.stream(extensionFilter.getExtensions()).map(e -> e.toLowerCase(Locale.ENGLISH))
					.noneMatch(e -> e.equals(extension))) {
				return new File(file.getAbsolutePath() + "." + extensionFilter.getExtensions()[0]);
			}
		}
		return file;
	}

}
