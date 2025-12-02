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

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.ValidationResult;
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

public abstract class AbstractWorkspacePanel {

	final WorkspaceDialogs.WorkspaceDialogPanel workspaceDialogPanel;
	final ValidationGroup validationGroup = new ValidationGroup();
	final VTextField workspaceFolder = new VTextField();

	private boolean workspaceFolderAltered = false;

	protected final JButton selectWorkspaceFolder = new JButton("<html>&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;&nbsp;");

	private final GeneratorFlavor generatorFlavor;

	private final JPanel panel = new JPanel();
	private final JPanel topPanel = new JPanel();
	private final JPanel notices = new JPanel();

	public AbstractWorkspacePanel(Window parent, GeneratorFlavor generatorFlavor) {
		panel.setLayout(new BorderLayout(0, 120));
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(notices, BorderLayout.SOUTH);

		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		notices.setLayout(new BoxLayout(notices, BoxLayout.Y_AXIS));

		this.generatorFlavor = generatorFlavor;

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

		selectWorkspaceFolder.setMargin(new Insets(0, 50, 0, 50));
		selectWorkspaceFolder.setBorder(
				BorderFactory.createMatteBorder(1, 0, 1, 1, UIManager.getColor("Component.borderColor")));

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
				return new ValidationResult(ValidationResult.Type.ERROR,
						L10N.t("dialog.file.error_save_inside_workspace_root_message"));
			} else if (selectedFile.isDirectory() && selectedFile.list() != null
					&& Objects.requireNonNull(selectedFile.list()).length > 0) {
				return new ValidationResult(ValidationResult.Type.ERROR,
						L10N.t("dialog.file.error_save_inside_folder_not_empty_message"));
			} else if (!workspaceFolder.getText().matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
				return new ValidationResult(ValidationResult.Type.ERROR, L10N.t("dialog.new_workspace.letters_valid"));
			} else if (selectedFile.getName().contains(" ") || selectedFile.getName().contains(":")
					|| selectedFile.getName().contains("\\") || selectedFile.getName().contains("/")
					|| selectedFile.getName().contains("|") || selectedFile.getName().contains("\"")
					|| selectedFile.getName().contains("?") || selectedFile.getName().contains("*")
					|| selectedFile.getName().contains(">")) {
				return new ValidationResult(ValidationResult.Type.ERROR,
						L10N.t("dialog.new_workspace.valid_characters"));
			} else if (!selectedFile.getParentFile().isDirectory()) {
				try {
					if (!selectedFile.getCanonicalPath()
							.startsWith(WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getCanonicalPath())) {
						throw new IOException();
					}
				} catch (IOException e) {
					return new ValidationResult(ValidationResult.Type.ERROR,
							L10N.t("dialog.file.error_directory_doesnt_exist"));
				}
			} else if (!Files.isWritable(selectedFile.getParentFile().toPath()) || !Files.isReadable(
					selectedFile.getParentFile().toPath())) {
				return new ValidationResult(ValidationResult.Type.ERROR,
						L10N.t("dialog.new_workspace.file_permission_problem"));
			}

			return ValidationResult.PASSED;
		});

		workspaceDialogPanel.setFlavorFilter(generatorFlavor);

		workspaceDialogPanel.generator.removeAllItems();
		Generator.GENERATOR_CACHE.values().stream().filter(gc -> gc.getGeneratorFlavor() == generatorFlavor)
				.forEach(workspaceDialogPanel.generator::addItem);

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForFlavor(
				Generator.GENERATOR_CACHE.values(), generatorFlavor);
		workspaceDialogPanel.generator.setSelectedItem(generatorConfiguration);
	}

	public void addFormElement(Component component) {
		topPanel.add(component);
	}

	public void addNotice(ImageIcon icon, String textKey) {
		notices.add(PanelUtils.join(FlowLayout.LEFT, new JLabel(icon), new JEmptyBox(0, 0), L10N.label(textKey)));
	}

	public JPanel getContainer() {
		return panel;
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

	public GeneratorFlavor getGeneratorFlavor() {
		return generatorFlavor;
	}

}
