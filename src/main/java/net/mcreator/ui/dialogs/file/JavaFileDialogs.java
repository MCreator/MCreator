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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.io.File;
import java.util.Locale;

import static net.mcreator.ui.dialogs.file.FileDialogs.prevDir;

class JavaFileDialogs {

	private static final Dimension FILEDIALOG_SIZE = new Dimension(720, 420);

	protected static File[] getFileChooserDialog(Window f, FileChooserType type, boolean multiSelect,
			@Nullable String suggestedFileName, FileChooser.ExtensionFilter... filters) {
		JFileChooser fc = new JFileChooser() {
			@Override public File getSelectedFile() {
				File selectedFile = super.getSelectedFile();
				if (selectedFile != null && getDialogType() == SAVE_DIALOG) {
					String ext = getFileFilter().getDescription().split(" files")[0].trim().toLowerCase(Locale.ROOT);
					if (!selectedFile.getName().toLowerCase(Locale.ROOT).endsWith("." + ext)) {
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
		fc.setPreferredSize(FILEDIALOG_SIZE);
		fc.setCurrentDirectory(prevDir);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileView(new FileView() {
			final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

			@Override public Icon getIcon(File f) {
				if (f.isDirectory())
					if (!fileSystemView.isComputerNode(f) && !fileSystemView.isDrive(f))
						return UIRES.get("laf.directory");

				if (f.getName().endsWith(".mcreator"))
					return new ImageIcon(ImageUtils.resize(UIRES.get("mod").getImage(), 16));

				return fileSystemView.getSystemIcon(f);
			}
		});

		if (filters != null) {
			for (FileChooser.ExtensionFilter extensionFilter : filters) {
				if (extensionFilter != null) {
					fc.addChoosableFileFilter(extensionToFileFilter(extensionFilter));
				}
			}
		}

		fc.setMultiSelectionEnabled(multiSelect);

		if (suggestedFileName != null)
			fc.setSelectedFile(new File(prevDir, suggestedFileName));

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

	protected static File getWorkspaceDirectorySelectDialog(Window f, File file) {
		JFileChooser fc = new JFileChooser() {
			@Override public void approveSelection() {
				File selectedFile = getSelectedFile();
				if (FileDialogs.isWorkspaceFolderInvalid(f, selectedFile))
					return;
				super.approveSelection();
			}
		};

		fc.setPreferredSize(FILEDIALOG_SIZE);

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

	private static FileFilter extensionToFileFilter(FileChooser.ExtensionFilter extensionFilter) {
		return new FileFilter() {
			@Override public boolean accept(File f) {
				if (f.isFile()) {
					String filename = f.getName().toLowerCase(Locale.ROOT);
					for (String ext : extensionFilter.getExtensions()) {
						if (filename.matches(ext.replace(".", "\\.").replace("*", ".*") + "$"))
							return true;
					}
				}

				return f.isDirectory();
			}

			@Override public String getDescription() {
				return extensionFilter.getDescription() + " " + extensionFilter.getExtensions().toString()
						.replace('[', '(').replace(']', ')');
			}
		};
	}

}
