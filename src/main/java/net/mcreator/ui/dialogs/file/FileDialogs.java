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

import javafx.stage.FileChooser;
import net.mcreator.io.OS;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;

public class FileDialogs {

	protected static File prevDir = new File(System.getProperty("user.home"));

	public static File getOpenDialog(Window f, String[] exp) {
		return getBasicFileChooserDialog(f, FileChooserType.OPEN, exp);
	}

	public static File[] getMultiOpenDialog(Window f, String[] exp) {
		return getFileChooserDialog(f, FileChooserType.OPEN, true, getFileFiltersForStringArray(exp));
	}

	public static File getSaveDialog(Window f, String[] exp) {
		return getBasicFileChooserDialog(f, FileChooserType.SAVE, exp);
	}

	private static File getBasicFileChooserDialog(Window f, FileChooserType type, String[] filters) {
		File[] selected = getFileChooserDialog(f, type, false, getFileFiltersForStringArray(filters));
		if (selected != null && selected.length > 0)
			return selected[0];
		return null;
	}

	public static File[] getFileChooserDialog(Window f, FileChooserType type, boolean multiSelect,
			FileChooser.ExtensionFilter... filters) {
		if (useNativeFileChooser()) {
			return NativeFileDialogs.getFileChooserDialog(type, multiSelect, filters);
		} else {
			return JavaFileDialogs.getFileChooserDialog(f, type, multiSelect, filters);
		}
	}

	public static File getWorkspaceDirectorySelectDialog(Window f, File file) {
		if (useNativeFileChooser()) {
			return NativeFileDialogs.getWorkspaceDirectorySelectDialog(f, file);
		} else {
			return JavaFileDialogs.getWorkspaceDirectorySelectDialog(f, file);
		}
	}

	protected static boolean isWorkspaceFolderInvalid(Window parent, File selectedFile) {
		if (selectedFile != null && selectedFile.getAbsolutePath()
				.equals(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath())) {
			JOptionPane.showMessageDialog(parent, L10N.t("dialog.file.error_save_inside_workspace_root_message"),
					L10N.t("dialog.file.error_save_inside_workspace_root_title"), JOptionPane.ERROR_MESSAGE);
			return true;
		} else if (selectedFile != null && selectedFile.isDirectory() && selectedFile.list() != null
				&& Objects.requireNonNull(selectedFile.list()).length > 0) {
			JOptionPane.showMessageDialog(parent, L10N.t("dialog.file.error_save_inside_folder_not_empty_message"),
					L10N.t("dialog.file.error_save_inside_folder_not_empty_title"), JOptionPane.ERROR_MESSAGE);
			return true;
		} else if (selectedFile != null && selectedFile.isDirectory() && !selectedFile.getAbsolutePath()
				.matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
			JOptionPane.showMessageDialog(parent,
					L10N.t("dialog.file.error_invalid_path", selectedFile.getAbsolutePath()),
					L10N.t("dialog.file.error_invalid_path_title"), JOptionPane.ERROR_MESSAGE);
			return true;
		} else if (selectedFile != null && (selectedFile.getName().contains(" ") || selectedFile.getName().contains(":")
				|| selectedFile.getName().contains("\\") || selectedFile.getName().contains("/")
				|| selectedFile.getName().contains("|") || selectedFile.getName().contains("\"")
				|| selectedFile.getName().contains("?") || selectedFile.getName().contains("*")
				|| selectedFile.getName().contains(">"))) {
			JOptionPane.showMessageDialog(parent, L10N.t("dialog.file.error_invalid_name"),
					L10N.t("dialog.file.error_invalid_name_title"), JOptionPane.ERROR_MESSAGE);
			return true;
		} else if (selectedFile != null && !selectedFile.getParentFile().isDirectory()) {
			try {
				if (!selectedFile.getCanonicalPath()
						.startsWith(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getCanonicalPath())) {
					throw new IOException();
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(parent, L10N.t("dialog.file.error_directory_doesnt_exist"),
						L10N.t("dialog.file.error_directory_doesnt_exist_title"), JOptionPane.ERROR_MESSAGE);
				return true;
			}
		} else if (selectedFile != null && (!Files.isWritable(selectedFile.getParentFile().toPath())
				|| !Files.isReadable(selectedFile.getParentFile().toPath()))) {
			JOptionPane.showMessageDialog(parent, L10N.t("dialog.file.error_no_access"),
					L10N.t("dialog.file.error_no_access_title"), JOptionPane.ERROR_MESSAGE);
			return true;
		}

		return false;
	}

	private static FileChooser.ExtensionFilter[] getFileFiltersForStringArray(String[] filters) {
		FileChooser.ExtensionFilter[] fileFilters = new FileChooser.ExtensionFilter[filters.length];
		int idx = 0;
		for (String extension : filters) {
			extension = extension.toLowerCase(Locale.ROOT);
			if (extension.startsWith("."))
				extension = extension.replaceFirst("\\.", "");
			fileFilters[idx++] = new FileChooser.ExtensionFilter(extension.toUpperCase(Locale.ROOT) + " files",
					"*." + extension);
		}
		return fileFilters;
	}

	private static boolean useNativeFileChooser() {
		return PreferencesManager.PREFERENCES.ui.useNativeFileChooser && OS.getOS() == OS.WINDOWS;
	}

}
