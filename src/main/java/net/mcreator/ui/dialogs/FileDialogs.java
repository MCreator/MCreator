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

package net.mcreator.ui.dialogs;

import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.*;
import net.mcreator.ui.component.filebrowser.SynchronousJFXCaller;
import net.mcreator.ui.component.filebrowser.SynchronousJFXDirectoryChooser;
import net.mcreator.ui.component.filebrowser.SynchronousJFXFileChooser;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.swing.*;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FileDialogs {

	private static File prevDir = new File(System.getProperty("user.home"));

	private static Stage stage = null;

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
		File[] secleted = getFileChooserDialog(f, type, false, getFileFiltersForStringArray(filters));
		if (secleted != null)
			return secleted[0];
		return null;
	}

	public static File[] getFileChooserDialog(Window f, FileChooserType type, boolean multiSelect,
			FileChooser.ExtensionFilter... filters) {
		if (multiSelect && type == FileChooserType.SAVE)
			throw new RuntimeException("Invalid file chooser type for multi selection mode");

		initJFX();

		SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(stage, () -> {
			FileChooser ch = new FileChooser();
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

		return null;
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

	public static File getWorkspaceDirectorySelectDialog(Window f, File file) {
		initJFX();

		SynchronousJFXDirectoryChooser chooser = new SynchronousJFXDirectoryChooser(stage, () -> {
			DirectoryChooser ch = new DirectoryChooser();
			ch.setTitle(L10N.t("dialog.file.select_directory_title"));
			ch.setInitialDirectory(file == null ? WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot() : file);
			return ch;
		});

		File selectedFile = chooser.showDialog();
		if (selectedFile == null)
			return null;

		if (selectedFile.getAbsolutePath()
				.equals(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath())) {
			JOptionPane.showMessageDialog(f, L10N.t("dialog.file.error_save_inside_workspace_root_message"),
					L10N.t("dialog.file.error_save_inside_workspace_root_title"), JOptionPane.ERROR_MESSAGE);
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());
		} else if (selectedFile.isDirectory() && selectedFile.list() != null
				&& Objects.requireNonNull(selectedFile.list()).length > 0) {
			JOptionPane.showMessageDialog(f, L10N.t("dialog.file.error_save_inside_folder_not_empty_message"),
					L10N.t("dialog.file.error_save_inside_folder_not_empty_title"), JOptionPane.ERROR_MESSAGE);
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());
		} else if (selectedFile.isDirectory() && !selectedFile.getAbsolutePath()
				.matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
			JOptionPane.showMessageDialog(f, L10N.t("dialog.file.error_invalid_path", selectedFile.getAbsolutePath()),
					L10N.t("dialog.file.error_invalid_path_title"), JOptionPane.ERROR_MESSAGE);
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());
		} else if (selectedFile.getName().contains(" ") || selectedFile.getName().contains(":")
				|| selectedFile.getName().contains("\\") || selectedFile.getName().contains("/")
				|| selectedFile.getName().contains("|") || selectedFile.getName().contains("\"")
				|| selectedFile.getName().contains("?") || selectedFile.getName().contains("*")
				|| selectedFile.getName().contains(">")) {
			JOptionPane.showMessageDialog(f, L10N.t("dialog.file.error_invalid_name"),
					L10N.t("dialog.file.error_invalid_name_title"), JOptionPane.ERROR_MESSAGE);
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());
		} else if (!selectedFile.getParentFile().isDirectory()) {
			try {
				if (!selectedFile.getCanonicalPath()
						.startsWith(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getCanonicalPath())) {
					throw new IOException();
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(f, L10N.t("dialog.file.error_directory_doesnt_exist"),
						L10N.t("dialog.file.error_directory_doesnt_exist_title"), JOptionPane.ERROR_MESSAGE);
				return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());
			}
		} else if (!Files.isWritable(selectedFile.getParentFile().toPath()) || !Files.isReadable(
				selectedFile.getParentFile().toPath())) {
			JOptionPane.showMessageDialog(f, L10N.t("dialog.file.error_no_access"),
					L10N.t("dialog.file.error_no_access_title"), JOptionPane.ERROR_MESSAGE);
			return getWorkspaceDirectorySelectDialog(f, selectedFile.getParentFile());
		}

		return chooser.showDialog();
	}

	private static void initJFX() {
		if (stage == null) {
			try {
				ThreadUtil.runOnSwingThreadAndWait(JFXPanel::new);
				SynchronousJFXCaller<Stage> caller = new SynchronousJFXCaller<>(() -> {
					Stage stage = new Stage(StageStyle.TRANSPARENT);
					stage.getIcons().add(SwingFXUtils.toFXImage(ImageUtils.toBufferedImage(UIRES.getBuiltIn("icon").getImage()), null));
					stage.initModality(Modality.NONE);
					stage.setWidth(0);
					stage.setHeight(0);
					stage.show();
					stage.setIconified(true);
					return stage;
				});
				stage = caller.call(1, TimeUnit.SECONDS);
			} catch (Exception ex) {
				throw new AssertionError("Got unexpected checked exception", ex);
			}
		}
	}

	public enum FileChooserType {
		SAVE, OPEN
	}

}
