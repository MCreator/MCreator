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
import net.mcreator.java.JavaConventions;
import net.mcreator.minecraft.api.ModAPI;
import net.mcreator.minecraft.api.ModAPIManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.TextFieldValidatorJSON;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class WorkspaceDialogs {

	public static WorkspaceSettingsChange workspaceSettings(MCreator mcreator, Workspace in) {
		MCreatorDialog workspaceDialog = new MCreatorDialog(mcreator, L10N.t("dialog.workspace_settings.title"), true);

		WorkspaceDialogPanel wdp = new WorkspaceDialogPanel(workspaceDialog, in);
		workspaceDialog.add("Center", wdp);
		JPanel buttons = new JPanel();
		JButton ok = L10N.button("dialog.workspace_settings.save_changes");
		buttons.add(ok);
		workspaceDialog.add("South", buttons);
		ok.addActionListener(e -> {
			if (wdp.validationGroup.validateIsErrorFree())
				workspaceDialog.setVisible(false);
			else
				showErrorsMessage(mcreator, new AggregatedValidationResult(wdp.validationGroup));
		});

		workspaceDialog.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent windowEvent) {
				if (wdp.validationGroup.validateIsErrorFree())
					workspaceDialog.setVisible(false);
				else
					showErrorsMessage(mcreator, new AggregatedValidationResult(wdp.validationGroup));
			}
		});

		workspaceDialog.getRootPane().setDefaultButton(ok);
		workspaceDialog.pack();
		workspaceDialog.setSize(workspaceDialog.getBounds().width, 620);
		workspaceDialog.setLocationRelativeTo(mcreator);
		workspaceDialog.setVisible(true);

		WorkspaceSettings oldsettings = in.getWorkspaceSettings();
		WorkspaceSettings newsettings = wdp.getWorkspaceSettings(in);

		WorkspaceSettingsChange change = new WorkspaceSettingsChange(newsettings, oldsettings);

		if (change.refactorNeeded()) {
			String[] options = new String[] { L10N.t("dialog.workspace_settings.refactor.yes"),
					L10N.t("dialog.workspace_settings.refactor.no") };
			int option = JOptionPane.showOptionDialog(null, L10N.t("dialog.workspace_settings.refactor.text"),
					L10N.t("dialog.workspace_settings.refactor.title"), JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (option == 1)
				return new WorkspaceSettingsChange(oldsettings, null);
		}

		return change; // we are working with existing workspace
	}

	static class WorkspaceDialogPanel extends JPanel {

		private GeneratorFlavor flavorFilter = null;

		JComponent generatorSelector;
		ValidationGroup validationGroup = new ValidationGroup();

		VTextField modName = new VTextField(24);
		VTextField modID = new VTextField(24);

		VTextField packageName = new VTextField(24);
		VTextField credits = new VTextField(24);

		VTextField version = new VTextField(24);
		VTextField description = new VTextField(24);
		VTextField author = new VTextField(24);
		VTextField websiteURL = new VTextField(24);

		JComboBox<String> modPicture = new JComboBox<>();
		JCheckBox lockBaseModFiles = L10N.checkbox("dialog.workspace_settings.lock_base_files");
		JCheckBox serverSideOnly = L10N.checkbox("dialog.workspace_settings.server_side_mod");
		JCheckBox disableForgeVersionCheck = new JCheckBox();
		JTextField updateJSON = new JTextField(24);
		JTextField requiredMods = new JTextField(24);
		JTextField dependencies = new JTextField(24);
		JTextField dependants = new JTextField(24);

		JComboBox<String> license = new JComboBox<>(
				new String[] { "Academic Free License v3.0", "Ace3 Style BSD", "All Rights Reserved",
						"Apache License version 2.0", "Apple Public Source License version 2.0 (APSL)",
						"BSD License Common Development and Distribution License (CDDL)",
						"Creative Commons Attribution-NonCommercial 3.0",
						"Unported GNU Affero General Public License version 3 (AGPLv3)",
						"GNU General Public License version 2 (GPLv2)", "GNU General Public License version 3 (GPLv3)",
						"GNU Lesser General Public License version 2.1 (LGPLv2.1)",
						"GNU Lesser General Public License version 3 (LGPLv3)",
						"ISC License (ISCL) Microsoft Public License (Ms-PL)", "Microsoft Reciprocal License (Ms-RL)",
						"MIT License", "Mozilla Public License 1.0 (MPL)", "Mozilla Public License 1.1 (MPL 1.1)",
						"Mozilla Public License 2.0", "Public Domain", "WTFPL", "Custom license" });

		Map<String, JCheckBox> apis = new HashMap<>();

		JComboBox<GeneratorConfiguration> generator = new JComboBox<>();

		private boolean modIDManuallyEntered = false;
		private boolean packageNameManuallyEntered = false;

		WorkspaceDialogPanel(Window parent, @Nullable Workspace workspace) {
			setLayout(new BorderLayout());

			if (workspace != null) { // prevent modid autofill on existing workspaces
				modIDManuallyEntered = true;
				packageNameManuallyEntered = true;
			}

			generator.setPreferredSize(new Dimension(0, 0));

			Generator.GENERATOR_CACHE.values().forEach(generator::addItem);

			JPanel _basicSettings = new JPanel();
			_basicSettings.setLayout(new BoxLayout(_basicSettings, BoxLayout.PAGE_AXIS));

			JPanel _advancedSettings = new JPanel();
			_advancedSettings.setLayout(new BoxLayout(_advancedSettings, BoxLayout.PAGE_AXIS));

			JPanel _external_apis = new JPanel();
			_external_apis.setLayout(new BoxLayout(_external_apis, BoxLayout.PAGE_AXIS));

			if (workspace != null) {
				JTabbedPane master = new JTabbedPane();
				master.setBorder(BorderFactory.createEmptyBorder());
				master.setForeground(Color.white);
				master.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
					protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
					}
				});
				master.addTab(L10N.t("dialog.workspace_settings.tab.general"),
						PanelUtils.centerInPanel(_basicSettings));
				master.addTab(L10N.t("dialog.workspace_settings.tab.apis"), PanelUtils.centerInPanel(_external_apis));
				master.addTab(L10N.t("dialog.workspace_settings.tab.advanced"),
						PanelUtils.centerInPanel(_advancedSettings));
				add("Center", PanelUtils.centerInPanel(master));
			} else {
				add("Center", _basicSettings);
			}

			// if workspace is null, modid can still be set
			modName.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					if (!modIDManuallyEntered) {
						modID.setText(modName.getText().toLowerCase(Locale.ENGLISH).replace(" ", "_")
								.replaceAll("[^a-z_]+", ""));
						modID.getValidationStatus();
					}
					if (!packageNameManuallyEntered) {
						packageName.setText("net.mcreator." + modID.getText().replaceAll("[^a-z]+", ""));
						packageName.getValidationStatus();
					}
				}
			});

			modID.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					modIDManuallyEntered = !modID.getText().trim().equals("");
					if (!packageNameManuallyEntered) {
						packageName.setText("net.mcreator." + modID.getText().replaceAll("[^a-z]+", ""));
						packageName.getValidationStatus();
					}
				}
			});

			packageName.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					packageNameManuallyEntered = !packageName.getText().trim().equals("");
				}
			});

			modName.setValidator(new Validator() {
				private final Validator parent = new TextFieldValidatorJSON(modName,
						L10N.t("dialog.workspace_settings.mod_name.invalid"), false);

				@Override public ValidationResult validate() {
					if (modName.getText().matches(".*\\d+.*"))
						return new ValidationResult(ValidationResultType.WARNING,
								L10N.t("dialog.workspace_settings.mod_name.verison_in_name"));

					return parent.validate();
				}
			});

			version.setValidator(
					new TextFieldValidatorJSON(version, L10N.t("dialog.workspace_settings.version.error"), false));
			description.setValidator(
					new TextFieldValidatorJSON(description, L10N.t("dialog.workspace_settings.description.error"),
							true));
			author.setValidator(
					new TextFieldValidatorJSON(author, L10N.t("dialog.workspace_settings.author.error"), true));
			websiteURL.setValidator(
					new TextFieldValidatorJSON(author, L10N.t("dialog.workspace_settings.website.error"), true));

			((AbstractDocument) modID.getDocument()).setDocumentFilter(new DocumentFilter() {
				@Override
				public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
						throws BadLocationException {
					fb.insertString(offset, text.toLowerCase(Locale.ENGLISH), attr);
				}

				@Override
				public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
						AttributeSet attrs) throws BadLocationException {
					fb.replace(offset, length, text.toLowerCase(Locale.ENGLISH), attrs);
				}
			});

			((AbstractDocument) packageName.getDocument()).setDocumentFilter(new DocumentFilter() {
				@Override
				public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
						throws BadLocationException {
					fb.insertString(offset, text.toLowerCase(Locale.ENGLISH), attr);
				}

				@Override
				public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
						AttributeSet attrs) throws BadLocationException {
					fb.replace(offset, length, text.toLowerCase(Locale.ENGLISH), attrs);
				}
			});

			license.setEditable(true);
			license.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXX");

			modID.setValidator(new RegistryNameValidator(modID, "Mod ID").setMaxLength(32));

			modName.enableRealtimeValidation();
			modID.enableRealtimeValidation();
			version.enableRealtimeValidation();
			description.enableRealtimeValidation();
			websiteURL.enableRealtimeValidation();
			author.enableRealtimeValidation();

			validationGroup.addValidationElement(modName);
			validationGroup.addValidationElement(modID);
			validationGroup.addValidationElement(version);
			validationGroup.addValidationElement(description);
			validationGroup.addValidationElement(websiteURL);
			validationGroup.addValidationElement(author);

			modPicture.addItem("No picture / default picture");
			if (workspace != null) {
				List<File> other = workspace.getFolderManager().getOtherTexturesList();
				for (File element : other) {
					if (element.getName().endsWith(".png"))
						modPicture.addItem(FilenameUtils.removeExtension(element.getName()));
				}
			}

			websiteURL.setText(MCreatorApplication.SERVER_DOMAIN);
			author.setText(System.getProperty("user.name") + ", MCreator");
			version.setText("1.0.0");

			disableForgeVersionCheck.setSelected(true);

			generator.setUI(new BasicComboBoxUI() {
				@Override protected JButton createArrowButton() {
					return new JButton() {
						@Override public int getWidth() {
							return 0;
						}
					};
				}
			});
			generator.remove(this.getComponent(0));
			generator.setEnabled(false);
			generator.setBorder(
					BorderFactory.createMatteBorder(1, 1, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

			JButton selectGenerator = new JButton(UIRES.get("18px.edit"));
			selectGenerator.setMargin(new Insets(4, 4, 4, 4));
			selectGenerator.addActionListener(e -> {
				GeneratorConfiguration gc = GeneratorSelector
						.getGeneratorSelector(parent, (GeneratorConfiguration) generator.getSelectedItem(),
								workspace != null ?
										workspace.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() :
										flavorFilter);
				if (gc != null)
					generator.setSelectedItem(gc);
			});

			generator.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent mouseEvent) {
					GeneratorConfiguration gc = GeneratorSelector
							.getGeneratorSelector(parent, (GeneratorConfiguration) generator.getSelectedItem(),
									workspace != null ?
											workspace.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() :
											flavorFilter);
					if (gc != null)
						generator.setSelectedItem(gc);
				}
			});

			generatorSelector = PanelUtils.centerAndEastElement(generator, selectGenerator);

			JPanel generalSettings = new JPanel(new GridLayout(4, 2, 5, 2));
			generalSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
					L10N.t("dialog.workspace_settings.section.basic")));
			_basicSettings.add(generalSettings);
			generalSettings.add(L10N.label("dialog.workspace_settings.display_name"));
			generalSettings.add(modName);
			generalSettings.add(L10N.label("dialog.workspace_settings.mod_id"));
			generalSettings.add(modID);
			generalSettings.add(L10N.label("dialog.workspace_settings.package"));
			generalSettings.add(packageName);
			generalSettings.add(L10N.label("dialog.workspace_settings.generator"));
			generalSettings.add(generatorSelector);

			_basicSettings.add(new JEmptyBox(5, 15));

			JPanel descriptionSettings = new JPanel(new GridLayout(workspace != null ? 7 : 2, 2, 5, 2));
			descriptionSettings.setBorder(BorderFactory
					.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
							L10N.t("dialog.workspace_settings.section.details")));
			_basicSettings.add(descriptionSettings);
			descriptionSettings.add(L10N.label("dialog.workspace_settings.version"));
			descriptionSettings.add(version);
			descriptionSettings.add(L10N.label("dialog.workspace_settings.description"));
			descriptionSettings.add(description);
			if (workspace != null) {
				descriptionSettings.add(L10N.label("dialog.workspace_settings.author"));
				descriptionSettings.add(author);
				descriptionSettings.add(L10N.label("dialog.workspace_settings.website"));
				descriptionSettings.add(websiteURL);
				descriptionSettings.add(L10N.label("dialog.workspace_settings.credits"));
				descriptionSettings.add(credits);
				descriptionSettings.add(L10N.label("dialog.workspace_settings.picture"));
				descriptionSettings.add(modPicture);
				descriptionSettings.add(L10N.label("dialog.workspace_settings.license"));
				descriptionSettings.add(license);

				_basicSettings.add(new JEmptyBox(5, 15));

				credits.setValidator(
						new TextFieldValidatorJSON(credits, L10N.t("dialog.workspace_settings.credits.error"), true));
				credits.enableRealtimeValidation();

				validationGroup.addValidationElement(credits);

			}

			validationGroup.addValidationElement(packageName);

			packageName.setPreferredSize(new Dimension(300, 32));
			packageName.setValidator(() -> {
				String text = packageName.getText();
				if (text.length() == 0)
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							"Package name can't be empty");
				if (text.startsWith(".")) {
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							"Package name can't start with a dot");
				}
				if (text.endsWith(".")) {
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							"Package name can't end with a dot");
				}
				char[] chars = text.toCharArray();
				boolean valid = true;
				int id = 0;
				for (char c : chars) {
					if (id == 0 && (c >= '0' && c <= '9')) {
						valid = false;
						break;
					}
					if (!JavaConventions.isLetterOrDigit(c) && c != '_' && c != '.') {
						valid = false;
						break;
					}
					id++;
				}
				if (!valid)
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							"Package name can only contain EN letters, numbers, underscore and dots.");

				if (text.matches(".*\\d+.*"))
					return new Validator.ValidationResult(Validator.ValidationResultType.WARNING,
							"Avoid using numbers in package names");

				return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
			});

			packageName.enableRealtimeValidation();

			if (workspace != null) {
				JPanel apiSettings = new JPanel(new BorderLayout());
				apiSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
						L10N.t("dialog.workspace_settings.section.external_apis")));
				apiSettings.add("North", L10N.label("dialog.workspace_settings.section.external_apis.tooltip"));

				JPanel apiList = new JPanel();
				apiList.setLayout(new BoxLayout(apiList, BoxLayout.PAGE_AXIS));

				List<ModAPI.Implementation> apisSupported = ModAPIManager
						.getModAPIsForGenerator(workspace.getGenerator().getGeneratorName());
				for (ModAPI.Implementation api : apisSupported) {
					JCheckBox apiEnableBox = new JCheckBox();
					apiEnableBox.setName(api.parent.id);
					apiEnableBox.setText(api.parent.name);

					if (api.parent.id.equals("mcreator_link")) {
						apiList.add(PanelUtils.westAndCenterElement(
								ComponentUtils.wrapWithInfoButton(apiEnableBox, "https://mcreator.net/link"),
								new JLabel(UIRES.get("16px.link"))));
					} else {
						apiList.add(PanelUtils.join(FlowLayout.LEFT, apiEnableBox));
					}

					apis.put(api.parent.id, apiEnableBox);
				}

				apiSettings.add("West", apiList);

				JButton explorePlugins = L10N.button("dialog.workspace_settings.explore_plugins");
				explorePlugins.setIcon(UIRES.get("16px.search"));
				explorePlugins.addActionListener(
						e -> DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/plugins"));

				apiSettings.add("South", PanelUtils
						.join(FlowLayout.LEFT, L10N.label("dialog.workspace_settings.plugins_tip"), explorePlugins));

				_external_apis.add(new JEmptyBox(5, 15));
				_external_apis.add(apiSettings);
			}

			JComponent forgeVersionCheckPan = PanelUtils
					.westAndEastElement(L10N.label("dialog.workspace_settings.section.version_check"),
							disableForgeVersionCheck);
			forgeVersionCheckPan.setBorder(BorderFactory
					.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
							L10N.t("dialog.workspace_settings.version_check")));
			_advancedSettings.add(forgeVersionCheckPan);
			_advancedSettings.add(new JEmptyBox(5, 15));

			JPanel advancedSettings = new JPanel(new GridLayout(3, 2, 5, 2));
			advancedSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
					L10N.t("dialog.workspace_settings.section.advanced")));
			_advancedSettings.add(advancedSettings);
			advancedSettings.add(L10N.label("dialog.workspace_settings.server_side_only"));
			advancedSettings.add(serverSideOnly);
			advancedSettings.add(L10N.label("dialog.workspace_settings.lock_base_files_label"));
			advancedSettings.add(lockBaseModFiles);
			advancedSettings.add(L10N.label("dialog.workspace_settings.update_url"));
			advancedSettings.add(updateJSON);

			JPanel dependencySettings = new JPanel(new GridLayout(3, 2, 5, 2));
			dependencySettings.add(L10N.label("dialog.workspace_settings.required_mods"));
			dependencySettings.add(requiredMods);
			dependencySettings.add(L10N.label("dialog.workspace_settings.dependencies"));
			dependencySettings.add(dependencies);
			dependencySettings.add(L10N.label("dialog.workspace_settings.dependants"));
			dependencySettings.add(dependants);

			if (workspace != null) {
				_external_apis.add(new JEmptyBox(5, 15));
				_external_apis.add(dependencySettings);
			}

			if (workspace != null) {
				generator.setSelectedItem(
						Generator.GENERATOR_CACHE.get(workspace.getWorkspaceSettings().getCurrentGenerator()));
				modName.setText(workspace.getWorkspaceSettings().getModName());
				modID.setText(workspace.getWorkspaceSettings().getModID());
				version.setText(workspace.getWorkspaceSettings().getVersion());
				description.setText(workspace.getWorkspaceSettings().getDescription());
				author.setText(workspace.getWorkspaceSettings().getAuthor());
				license.setSelectedItem(workspace.getWorkspaceSettings().getLicense());
				websiteURL.setText(workspace.getWorkspaceSettings().getWebsiteURL());
				modPicture.setSelectedItem(workspace.getWorkspaceSettings().getModPicture() == null ?
						"No picture / default picture" :
						workspace.getWorkspaceSettings().getModPicture());
				serverSideOnly.setSelected(workspace.getWorkspaceSettings().isServerSideOnly());
				lockBaseModFiles.setSelected(workspace.getWorkspaceSettings().isLockBaseModFiles());
				disableForgeVersionCheck.setSelected(workspace.getWorkspaceSettings().isDisableForgeVersionCheck());
				updateJSON.setText(workspace.getWorkspaceSettings().getUpdateURL());
				credits.setText(workspace.getWorkspaceSettings().getCredits());
				packageName.setText(workspace.getWorkspaceSettings().getModElementsPackage());

				if (!workspace.getWorkspaceSettings().requiredMods.isEmpty())
					requiredMods.setText(String.join(", ", workspace.getWorkspaceSettings().requiredMods).trim());
				if (!workspace.getWorkspaceSettings().dependencies.isEmpty())
					dependencies.setText(String.join(", ", workspace.getWorkspaceSettings().dependencies).trim());
				if (!workspace.getWorkspaceSettings().dependants.isEmpty())
					dependants.setText(String.join(", ", workspace.getWorkspaceSettings().dependants).trim());

				for (String mcrdep : workspace.getWorkspaceSettings().getMCreatorDependenciesRaw()) {
					JCheckBox box = apis.get(mcrdep);
					if (box != null) {
						box.setSelected(true);
					}
				}
			}
		}

		public WorkspaceSettings getWorkspaceSettings(@Nullable Workspace workspace) {
			WorkspaceSettings retVal = new WorkspaceSettings(modID.getText());
			retVal.setWorkspace(workspace);
			retVal.setModName(modName.getText());
			retVal.setVersion(version.getText());
			retVal.setDescription(description.getText().equals("") ? null : description.getText());
			retVal.setAuthor(author.getText().equals("") ? null : author.getText());
			retVal.setLicense(license.getEditor().getItem().toString().equals("") ?
					"Not specified" :
					license.getEditor().getItem().toString());
			retVal.setWebsiteURL(websiteURL.getText().equals("") ? null : websiteURL.getText());
			retVal.setCredits(credits.getText().equals("") ? null : credits.getText());
			retVal.setModPicture(Objects.equals(modPicture.getSelectedItem(), "No picture / default picture") ?
					null :
					(String) modPicture.getSelectedItem());
			retVal.setModElementsPackage(packageName.getText().equals("") ? null : packageName.getText());
			retVal.setServerSideOnly(serverSideOnly.isSelected());
			retVal.setLockBaseModFiles(lockBaseModFiles.isSelected());
			retVal.setDisableForgeVersionCheck(disableForgeVersionCheck.isSelected());
			retVal.setUpdateURL(updateJSON.getText().equals("") ? null : updateJSON.getText());
			retVal.setCurrentGenerator(
					((GeneratorConfiguration) Objects.requireNonNull(generator.getSelectedItem())).getGeneratorName());

			retVal.setRequiredMods(
					Arrays.stream(requiredMods.getText().split(",")).filter(text -> !text.trim().equals(""))
							.collect(Collectors.toSet()));
			retVal.setDependencies(
					Arrays.stream(dependencies.getText().split(",")).filter(text -> !text.trim().equals(""))
							.collect(Collectors.toSet()));
			retVal.setDependants(Arrays.stream(dependants.getText().split(",")).filter(text -> !text.trim().equals(""))
					.collect(Collectors.toSet()));

			Set<String> mcreatordeps = new HashSet<>();
			for (JCheckBox box : apis.values()) {
				if (box.isSelected()) {
					mcreatordeps.add(box.getName());
				}
			}
			retVal.setMCreatorDependencies(mcreatordeps);

			return retVal;
		}

		public void setFlavorFilter(GeneratorFlavor flavorFilter) {
			this.flavorFilter = flavorFilter;
		}
	}

	private static void showErrorsMessage(Window w, AggregatedValidationResult validationResult) {
		StringBuilder stringBuilder = new StringBuilder(L10N.t("dialog.workspace_settings.error_list"));
		stringBuilder.append("<ul>");
		int count = 0;
		for (String error : validationResult.getValidationProblemMessages()) {
			stringBuilder.append("<li>").append(error).append("</li>");
			count++;
			if (count > 5) {
				stringBuilder.append("<li>").append("+ ")
						.append(validationResult.getValidationProblemMessages().size() - count).append(" more")
						.append("</li>");
				break;
			}

		}
		stringBuilder.append("</ul>");
		stringBuilder.append(L10N.t("dialog.workspace_settings.dialog.error"));
		JOptionPane
				.showMessageDialog(w, stringBuilder.toString(), L10N.t("dialog.workspace_settings.dialog.error.title"),
						JOptionPane.ERROR_MESSAGE);
	}

}
