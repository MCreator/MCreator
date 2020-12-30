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

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;

public class FileDialogs {

	private static File prevDir = new File(System.getProperty("user.home"));

	public static File getOpenDialog(Window f, String[] exp) {
		return getBasicFileChooserDialog(f, FileChooserType.OPEN, exp);
	}

	public static File[] getMultiOpenDialog(Window f, String[] exp) {
		return getFileChooserDialog(f, null, FileChooserType.OPEN, true, getFileFiltersForStringArray(exp));
	}

	public static File getSaveDialog(Window f, String[] exp) {
		return getBasicFileChooserDialog(f, FileChooserType.SAVE, exp);
	}

	private static File getBasicFileChooserDialog(Window f, FileChooserType type, String[] filters) {
		File[] secleted = getFileChooserDialog(f, null, type, false, getFileFiltersForStringArray(filters));
		if (secleted != null)
			return secleted[0];
		return null;
	}

	public static File getWorkspaceDirectorySelectDialog(Window f, File file) {
		JFileChooser fc = new JFileChooser() {
			@Override public void approveSelection() {
				File selectedFile = getSelectedFile();
				if (selectedFile != null && selectedFile.getAbsolutePath()
						.equals(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath())) {
					JOptionPane.showMessageDialog(this, L10N.t("dialog.file.error_save_inside_workspace_root_message"),
							L10N.t("dialog.file.error_save_inside_workspace_root_title"), JOptionPane.ERROR_MESSAGE);
					return;
				} else if (selectedFile != null && selectedFile.isDirectory() && selectedFile.list() != null
						&& Objects.requireNonNull(selectedFile.list()).length > 0) {
					JOptionPane
							.showMessageDialog(this, L10N.t("dialog.file.error_save_inside_folder_not_empty_message"),
									L10N.t("dialog.file.error_save_inside_folder_not_empty_title"),
									JOptionPane.ERROR_MESSAGE);
					return;
				} else if (selectedFile != null && selectedFile.isDirectory() && !selectedFile.getAbsolutePath()
						.matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
					JOptionPane.showMessageDialog(this,
							L10N.t("dialog.file.error_invalid_path", selectedFile.getAbsolutePath()),
							L10N.t("dialog.file.error_invalid_path_title"), JOptionPane.ERROR_MESSAGE);
					return;
				} else if (selectedFile != null && (selectedFile.getName().contains(" ") || selectedFile.getName()
						.contains(":") || selectedFile.getName().contains("\\") || selectedFile.getName().contains("/")
						|| selectedFile.getName().contains("|") || selectedFile.getName().contains("\"") || selectedFile
						.getName().contains("?") || selectedFile.getName().contains("*") || selectedFile.getName()
						.contains(">"))) {
					JOptionPane.showMessageDialog(this, L10N.t("dialog.file.error_invalid_name"),
							L10N.t("dialog.file.error_invalid_name_title"), JOptionPane.ERROR_MESSAGE);
					return;
				} else if (selectedFile != null && !selectedFile.getParentFile().isDirectory()) {
					try {
						if (!selectedFile.getCanonicalPath().startsWith(
								WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getCanonicalPath())) {
							throw new IOException();
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, L10N.t("dialog.file.error_directory_doesnt_exist"),
								L10N.t("dialog.file.error_directory_doesnt_exist_title"), JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else if (selectedFile != null && (!Files.isWritable(selectedFile.getParentFile().toPath()) || !Files
						.isReadable(selectedFile.getParentFile().toPath()))) {
					JOptionPane.showMessageDialog(this, L10N.t("dialog.file.error_no_access"),
							L10N.t("dialog.file.error_no_access_title"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				super.approveSelection();
			}
		};
		fc.setPreferredSize(new Dimension(720, 420));

		if (file == null) {
			fc.setCurrentDirectory(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot());
		} else {
			fc.setCurrentDirectory(file);
		}

		fc.setFileFilter(new FileFilter() {
			@Override public boolean accept(File file) {
				return file.isDirectory();
			}

			@Override public String getDescription() {
				return "Directories";
			}
		});

		fc.setDialogTitle(L10N.t("dialog.file.select_directory_title"));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = fc.showOpenDialog(f);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();

		return null;
	}

	public static File[] getFileChooserDialog(Window f, JComponent accessory, FileChooserType type, boolean multiSelect,
			FileFilter... filters) {
		JFileChooser fc = new JFileChooser() {
			@Override public File getSelectedFile() {
				File selectedFile = super.getSelectedFile();
				if (selectedFile != null && getDialogType() == SAVE_DIALOG) {
					String ext = getFileFilter().getDescription().split(" files")[0].trim().toLowerCase(Locale.ENGLISH);
					if (!selectedFile.getName().endsWith("." + ext)) {
						selectedFile = new File(selectedFile.getAbsolutePath() + "." + ext);
					}
				}
				return selectedFile;
			}

			@Override public void approveSelection() {
				if (getDialogType() == SAVE_DIALOG) {
					File selectedFile = getSelectedFile();
					if ((selectedFile != null) && selectedFile.exists()) {
						int response = JOptionPane.showConfirmDialog(this,
								L10N.t("dialog.file.error_already_exists", selectedFile.getName()),
								L10N.t("dialog.file.error_already_exists_title"), JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
						if (response != JOptionPane.YES_OPTION)
							return;
					}
				}

				super.approveSelection();
			}
		};
		fc.setPreferredSize(new Dimension(720, 420));
		fc.setCurrentDirectory(prevDir);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileView(new FileView() {
			final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

			@Override public Icon getIcon(File f) {
				if (f.isDirectory())
					if (!fileSystemView.isComputerNode(f) && !fileSystemView.isDrive(f))
						return UIRES.get("laf.directory.gif");

				if (f.getName().endsWith(".mcreator"))
					return new ImageIcon(ImageUtils.resize(UIRES.get("mod").getImage(), 16));

				return fileSystemView.getSystemIcon(f);
			}
		});

		if (filters != null)
			for (FileFilter fileFilter : filters)
				if (fileFilter != null)
					fc.addChoosableFileFilter(fileFilter);

		if (accessory != null)
			fc.setAccessory(accessory);

		fc.setMultiSelectionEnabled(multiSelect);

		int returnVal;
		if (type == FileChooserType.SAVE)
			returnVal = fc.showSaveDialog(f);
		else
			returnVal = fc.showOpenDialog(f);

		// store last dir shown after the dialog is closed
		prevDir = fc.getCurrentDirectory();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (multiSelect) {
				File[] files = fc.getSelectedFiles();
				if (files != null && files.length > 0)
					return files;
			} else
				return new File[] { fc.getSelectedFile() };
		}

		return null;
	}

	private static FileFilter[] getFileFiltersForStringArray(String[] filters) {
		FileFilter[] fileFilters = new FileFilter[filters.length];
		int idx = 0;
		for (String extension : filters) {
			extension = extension.toLowerCase(Locale.ENGLISH);

			if (extension.startsWith("."))
				extension = extension.replaceFirst("\\.", "");

			String finalExtension = extension;
			fileFilters[idx] = new FileFilter() {
				@Override public boolean accept(File f) {
					return f.getName().toLowerCase(Locale.ENGLISH).endsWith("." + finalExtension) || f.isDirectory();
				}

				@Override public String getDescription() {
					return finalExtension.toUpperCase(Locale.ENGLISH) + " files (*." + finalExtension
							.toLowerCase(Locale.ENGLISH) + ")";
				}
			};
			idx++;
		}
		return fileFilters;
	}

	public enum FileChooserType {
		SAVE, OPEN
	}
}
