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

package net.mcreator.ui.dialogs.workspace;

import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.WorkspaceFolderManager;
import net.mcreator.workspace.settings.WorkspaceSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public abstract class AbstractWorkspacePanel extends JPanel {

	protected final WorkspaceDialogs.WorkspaceDialogPanel workspaceDialogPanel;
	protected final ValidationGroup validationGroup = new ValidationGroup();
	protected final VTextField workspaceFolder = new VTextField();
	protected final JButton selectWorkspaceFolder = new JButton("...");

	private boolean workspaceFolderAltered = false;

	public AbstractWorkspacePanel(Window parent) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		workspaceDialogPanel = new WorkspaceDialogs.WorkspaceDialogPanel(parent, null);

		workspaceDialogPanel.modID.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) {
				action();
			}

			@Override public void removeUpdate(DocumentEvent documentEvent) {
				action();
			}

			@Override public void changedUpdate(DocumentEvent documentEvent) {
				action();
			}

			private void action() {
				if (!workspaceFolderAltered) {
					workspaceFolder.setText(
							WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath() + File.separator
									+ workspaceDialogPanel.modID.getText());
					workspaceFolder.getValidationStatus();
				}
			}
		});

		workspaceFolder.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent keyEvent) {
				super.keyReleased(keyEvent);
				workspaceFolderAltered = true;
			}
		});

		workspaceFolder.setText(
				WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath() + File.separator
						+ workspaceDialogPanel.modID.getText());

		selectWorkspaceFolder.addActionListener(actionEvent -> {
			File file = FileDialogs.getWorkspaceDirectorySelectDialog(parent, new File(workspaceFolder.getText()));
			if (file != null) {
				workspaceFolder.setText(file.getAbsolutePath());
				workspaceFolder.getValidationStatus();
				workspaceFolderAltered = true;
			}
		});

		workspaceDialogPanel.modName.setPreferredSize(new Dimension(300, 32));
		workspaceDialogPanel.modID.setPreferredSize(new Dimension(300, 32));
		workspaceDialogPanel.generatorSelector.setPreferredSize(new Dimension(300, 32));
		workspaceDialogPanel.packageName.setPreferredSize(new Dimension(300, 32));
		workspaceFolder.setPreferredSize(new Dimension(330, 32));

		workspaceFolder.enableRealtimeValidation();
		workspaceFolder.setValidator(() -> {
			File selectedFile = new File(workspaceFolder.getText());

			if (selectedFile.getAbsolutePath()
					.equals(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath())) {
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						"You can not make a workspace inside workspaces root folder. Make a subdirectory here first!");
			} else if (selectedFile.isDirectory() && selectedFile.list() != null
					&& Objects.requireNonNull(selectedFile.list()).length > 0) {
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						"The directory you have selected for the workspace is not empty!");
			} else if (!workspaceFolder.getText().matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						"Only English letters, numbers, <kbd>_/+-\\:()[].,@$=`</kbd> and whitespace are allowed in the path.");
			} else if (selectedFile.getName().contains(" ") || selectedFile.getName().contains(":") || selectedFile
					.getName().contains("\\") || selectedFile.getName().contains("/") || selectedFile.getName()
					.contains("|") || selectedFile.getName().contains("\"") || selectedFile.getName().contains("?")
					|| selectedFile.getName().contains("*") || selectedFile.getName().contains(">")) {
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						"Workspace folder should not contain the following characters: /\\:>\"?*| and whitespace");
			} else if (!selectedFile.getParentFile().isDirectory()) {
				try {
					if (!selectedFile.getCanonicalPath()
							.startsWith(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getCanonicalPath())) {
						throw new IOException();
					}
				} catch (IOException e) {
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							"Directory in which you are trying to make a workspace does not exist!");
				}
			} else if (!Files.isWritable(selectedFile.getParentFile().toPath()) || !Files
					.isReadable(selectedFile.getParentFile().toPath())) {
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						"MCreator does not have permissions to write or read in selected directory!");
			}

			return Validator.ValidationResult.PASSED;
		});
	}

	public String getWorkspaceFolder() {
		return workspaceFolder.getText();
	}

	public WorkspaceSettings getWorkspaceSettings() {
		workspaceFolder.validate();
		if (validationGroup.validateIsErrorFree()) {
			return workspaceDialogPanel.getWorkspaceSettings(null);
		} else {
			return null;
		}
	}

	public AggregatedValidationResult getValidationResult() {
		return new AggregatedValidationResult(validationGroup);
	}

	public void focusMainField() {
		workspaceDialogPanel.modName.requestFocusInWindow();
	}

}
