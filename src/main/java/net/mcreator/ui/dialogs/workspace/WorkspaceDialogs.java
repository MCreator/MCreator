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
import net.mcreator.plugin.modapis.ModAPIImplementation;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.NamespaceValidator;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.TextFieldValidatorJSON;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.MissingGeneratorFeaturesException;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceUtils;
import net.mcreator.workspace.settings.WorkspaceSettings;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkspaceDialogs {

	@Nullable public static WorkspaceSettingsChange workspaceSettings(MCreator mcreator, Workspace in) {
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

		AtomicBoolean canceled = new AtomicBoolean(false);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		buttons.add(cancel);
		cancel.addActionListener(e -> {
			canceled.set(true);
			workspaceDialog.setVisible(false);
		});

		workspaceDialog.getRootPane().setDefaultButton(ok);
		workspaceDialog.pack();
		workspaceDialog.setSize(680, 620);
		workspaceDialog.setLocationRelativeTo(mcreator);

		workspaceDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				canceled.set(true);
			}
		});

		workspaceDialog.setVisible(true);

		if (canceled.get()) {
			return null; // no workspace setting change
		} else {
			WorkspaceSettings oldsettings = in.getWorkspaceSettings();
			WorkspaceSettings newsettings = wdp.getWorkspaceSettings(in);

			WorkspaceSettingsChange change = new WorkspaceSettingsChange(newsettings, oldsettings);

			try {
				verifyWorkspaceForCompatibilityWithGeneratorAndPlugins(mcreator, in,
						(GeneratorConfiguration) wdp.generator.getSelectedItem());
			} catch (MissingGeneratorFeaturesException e) {
				return null;
			}

			if (change.refactorNeeded()) {
				String[] options = new String[] { L10N.t("dialog.workspace_settings.refactor.yes"),
						L10N.t("dialog.workspace_settings.refactor.no") };
				int option = JOptionPane.showOptionDialog(mcreator, change.generatorFlavorChanged ?
								L10N.t("dialog.workspace_settings.refactor.text_flavor_switch") :
								L10N.t("dialog.workspace_settings.refactor.text"),
						L10N.t("dialog.workspace_settings.refactor.title"), JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				if (option == 1)
					return null; // no workspace setting change
			}

			return change; // we are working with existing workspace
		}
	}

	static class WorkspaceDialogPanel extends JPanel {

		private GeneratorFlavor flavorFilter = null;

		final JComponent generatorSelector;
		final ValidationGroup validationGroup = new ValidationGroup();

		final VTextField modName = new VTextField(24);
		final VTextField modID = new VTextField(24);

		final VTextField packageName = new VTextField(24);
		final VTextField credits = new VTextField(24);

		final VTextField version = new VTextField(24);
		final VTextField description = new VTextField(24);
		final VTextField author = new VTextField(24);
		final VTextField websiteURL = new VTextField(24);

		final JComboBox<String> modPicture = new JComboBox<>();
		final JCheckBox serverSideOnly = L10N.checkbox("dialog.workspace_settings.server_side_mod");
		final JTextField updateJSON = new JTextField(24);
		final JStringListField requiredMods;
		final JStringListField dependencies;
		final JStringListField dependants;

		final JComboBox<String> license = new JComboBox<>(
				new String[] { "Not specified", "Academic Free License v3.0", "Ace3 Style BSD", "All Rights Reserved",
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

		final Map<String, JCheckBox> apis = new HashMap<>();

		final JComboBox<GeneratorConfiguration> generator = new JComboBox<>();

		private boolean modIDManuallyEntered = false;
		private boolean packageNameManuallyEntered = false;

		WorkspaceDialogPanel(Window parent, @Nullable Workspace workspace) {
			setLayout(new BorderLayout());

			requiredMods = new JStringListField(parent, NamespaceValidator::new).setUniqueEntries(true);
			dependencies = new JStringListField(parent, NamespaceValidator::new).setUniqueEntries(true);
			dependants = new JStringListField(parent, NamespaceValidator::new).setUniqueEntries(true);

			if (workspace != null) { // prevent modid autofill on existing workspaces
				modIDManuallyEntered = true;
				packageNameManuallyEntered = true;
			}

			generator.setPreferredSize(new Dimension(0, 0));

			Generator.GENERATOR_CACHE.values().forEach(generator::addItem);

			JPanel _basicSettings = new JPanel();
			_basicSettings.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			_basicSettings.setLayout(new BoxLayout(_basicSettings, BoxLayout.PAGE_AXIS));

			JPanel _advancedSettings = new JPanel();
			_advancedSettings.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			_advancedSettings.setLayout(new BoxLayout(_advancedSettings, BoxLayout.PAGE_AXIS));

			JPanel _external_apis = new JPanel();
			_external_apis.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			_external_apis.setLayout(new BoxLayout(_external_apis, BoxLayout.PAGE_AXIS));

			if (workspace != null) {
				JTabbedPane master = new JTabbedPane();
				master.addTab(L10N.t("dialog.workspace_settings.tab.general"),
						PanelUtils.pullElementUp(_basicSettings));
				master.addTab(L10N.t("dialog.workspace_settings.tab.apis"), PanelUtils.pullElementUp(_external_apis));
				master.addTab(L10N.t("dialog.workspace_settings.tab.advanced"),
						PanelUtils.pullElementUp(_advancedSettings));
				add("Center", master);
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
					modIDManuallyEntered = !modID.getText().isBlank();
					if (!packageNameManuallyEntered) {
						packageName.setText("net.mcreator." + modID.getText().replaceAll("[^a-z]+", ""));
						packageName.getValidationStatus();
					}
				}
			});

			packageName.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					packageNameManuallyEntered = !packageName.getText().isBlank();
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
					new TextFieldValidatorJSON(version, L10N.t("dialog.workspace_settings.version.error"), false) {
						@Override public ValidationResult validate() {
							try {
								ModuleDescriptor.Version.parse(version.getText());
							} catch (Exception e) {
								return new ValidationResult(ValidationResultType.ERROR,
										L10N.t("dialog.workspace_settings.version.error2", e.getMessage()));
							}

							return super.validate();
						}
					});

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

			modID.setValidator(
					new RegistryNameValidator(modID, L10N.t("dialog.workspace.settings.workspace_modid")).setMaxLength(
							32));

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

			modPicture.addItem(L10N.t("dialog.workspace.settings.workspace_nopic_default"));
			if (workspace != null) {
				List<File> other = workspace.getFolderManager().getTexturesList(TextureType.OTHER);
				for (File element : other) {
					if (element.getName().endsWith(".png"))
						modPicture.addItem(FilenameUtilsPatched.removeExtension(element.getName()));
				}
			}

			websiteURL.setText(MCreatorApplication.SERVER_DOMAIN);
			author.setText(System.getProperty("user.name") + ", MCreator");
			version.setText("1.0.0");

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
			generator.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Theme.current().getAltBackgroundColor()));

			JButton selectGenerator = new JButton(UIRES.get("18px.edit"));
			selectGenerator.setMargin(new Insets(4, 4, 4, 4));
			selectGenerator.addActionListener(e -> {
				GeneratorConfiguration gc = GeneratorSelector.getGeneratorSelector(parent,
						(GeneratorConfiguration) generator.getSelectedItem(),
						workspace != null ? workspace.getGeneratorConfiguration().getGeneratorFlavor() : flavorFilter,
						workspace == null);
				if (gc != null)
					generator.setSelectedItem(gc);
			});

			generator.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent mouseEvent) {
					GeneratorConfiguration gc = GeneratorSelector.getGeneratorSelector(parent,
							(GeneratorConfiguration) generator.getSelectedItem(), workspace != null ?
									workspace.getGeneratorConfiguration().getGeneratorFlavor() :
									flavorFilter, workspace == null);
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

			_basicSettings.add(new JEmptyBox(5, 5));

			JPanel descriptionSettings = new JPanel(new GridLayout(workspace != null ? 7 : 2, 2, 5, 2));
			descriptionSettings.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
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

				_basicSettings.add(new JEmptyBox(5, 5));

				credits.setValidator(
						new TextFieldValidatorJSON(credits, L10N.t("dialog.workspace_settings.credits.error"), true));
				credits.enableRealtimeValidation();

				validationGroup.addValidationElement(credits);

			}

			validationGroup.addValidationElement(packageName);

			packageName.setPreferredSize(new Dimension(300, 32));
			packageName.setValidator(() -> {
				String text = packageName.getText();
				if (text.isEmpty())
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("dialog.workspace.settings.workspace_package_empty"));
				if (text.startsWith(".")) {
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("dialog.workspace.settings.workspace_package_startdot"));
				}
				if (text.endsWith(".")) {
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("dialog.workspace.settings.workspace_package_enddot"));
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
							L10N.t("dialog.workspace.settings.workspace_package_pattern"));

				if (text.matches(".*\\d+.*"))
					return new Validator.ValidationResult(Validator.ValidationResultType.WARNING,
							L10N.t("dialog.workspace.settings.workspace_package_avoid_numbers"));

				return Validator.ValidationResult.PASSED;
			});

			packageName.enableRealtimeValidation();

			if (workspace != null) {
				JPanel apiSettings = new JPanel(new BorderLayout());
				apiSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
						L10N.t("dialog.workspace_settings.section.external_apis")));
				apiSettings.add("North", L10N.label("dialog.workspace_settings.section.external_apis.tooltip"));

				JPanel apiList = new JPanel(new GridLayout(0, 1, 0, 0));

				List<ModAPIImplementation> apisSupported = ModAPIManager.getModAPIsForGenerator(
						workspace.getGenerator().getGeneratorName());
				for (ModAPIImplementation api : apisSupported) {
					JCheckBox apiEnableBox = new JCheckBox();
					apiEnableBox.setName(api.parent().id());
					apiEnableBox.setText(api.parent().name());

					if (api.parent().id().equals("mcreator_link")) {
						apiList.add(PanelUtils.join(FlowLayout.LEFT,
								ComponentUtils.wrapWithInfoButton(apiEnableBox, "https://mcreator.net/link"),
								new JLabel(UIRES.get("16px.link"))));
					} else {
						apiList.add(PanelUtils.join(FlowLayout.LEFT, apiEnableBox));
					}

					apis.put(api.parent().id(), apiEnableBox);
				}

				JScrollPane scrollPane = new JScrollPane(PanelUtils.pullElementUp(apiList));
				scrollPane.getVerticalScrollBar().setUnitIncrement(11);
				scrollPane.setPreferredSize(new Dimension(-1, 160));

				apiSettings.add("Center", scrollPane);

				JButton explorePlugins = L10N.button("dialog.workspace_settings.explore_plugins");
				explorePlugins.setIcon(UIRES.get("16px.search"));
				explorePlugins.addActionListener(
						e -> DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/plugins"));

				apiSettings.add("South",
						PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.workspace_settings.plugins_tip"),
								explorePlugins));

				_external_apis.add(apiSettings);
			}

			JPanel advancedSettings = new JPanel(new GridLayout(2, 2, 5, 2));
			advancedSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
					L10N.t("dialog.workspace_settings.section.advanced")));
			_advancedSettings.add(advancedSettings);
			advancedSettings.add(L10N.label("dialog.workspace_settings.server_side_only"));
			advancedSettings.add(serverSideOnly);
			advancedSettings.add(L10N.label("dialog.workspace_settings.update_url"));
			advancedSettings.add(updateJSON);

			JPanel dependencySettings = new JPanel(new GridLayout(3, 2, 7, 5));
			dependencySettings.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			dependencySettings.setBackground(Theme.current().getAltBackgroundColor());
			dependencySettings.add(L10N.label("dialog.workspace_settings.required_mods"));
			dependencySettings.add(requiredMods);
			dependencySettings.add(L10N.label("dialog.workspace_settings.dependencies"));
			dependencySettings.add(dependencies);
			dependencySettings.add(L10N.label("dialog.workspace_settings.dependants"));
			dependencySettings.add(dependants);

			if (workspace != null) {
				_external_apis.add(new JEmptyBox(5, 5));
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
						L10N.t("dialog.workspace.settings.workspace_nopic_default") :
						workspace.getWorkspaceSettings().getModPicture());
				serverSideOnly.setSelected(workspace.getWorkspaceSettings().isServerSideOnly());
				updateJSON.setText(workspace.getWorkspaceSettings().getUpdateURL());
				credits.setText(workspace.getWorkspaceSettings().getCredits());
				packageName.setText(workspace.getWorkspaceSettings().getModElementsPackage());

				requiredMods.setTextList(workspace.getWorkspaceSettings().requiredMods);
				dependencies.setTextList(workspace.getWorkspaceSettings().dependencies);
				dependants.setTextList(workspace.getWorkspaceSettings().dependants);

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
			retVal.setDescription(description.getText().isEmpty() ? null : description.getText());
			retVal.setAuthor(author.getText().isEmpty() ? null : author.getText());
			retVal.setLicense(license.getEditor().getItem().toString().isEmpty() ?
					null :
					license.getEditor().getItem().toString());
			retVal.setWebsiteURL(websiteURL.getText().isEmpty() ? null : websiteURL.getText());
			retVal.setCredits(credits.getText().isEmpty() ? null : credits.getText());
			retVal.setModPicture(Objects.equals(modPicture.getSelectedItem(),
					L10N.t("dialog.workspace.settings.workspace_nopic_default")) ?
					null :
					(String) modPicture.getSelectedItem());
			retVal.setModElementsPackage(packageName.getText().isEmpty() ? null : packageName.getText());
			retVal.setServerSideOnly(serverSideOnly.isSelected());
			retVal.setUpdateURL(updateJSON.getText().isEmpty() ? null : updateJSON.getText());
			retVal.setCurrentGenerator(
					((GeneratorConfiguration) Objects.requireNonNull(generator.getSelectedItem())).getGeneratorName());

			retVal.setRequiredMods(new HashSet<>(requiredMods.getTextList()));
			retVal.setDependencies(new HashSet<>(dependencies.getTextList()));
			retVal.setDependants(new HashSet<>(dependants.getTextList()));

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
		JOptionPane.showMessageDialog(w, stringBuilder.toString(),
				L10N.t("dialog.workspace_settings.dialog.error.title"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Verifies workspace for compatibility with the suggested generator and plugins.
	 *
	 * @param parent                 the parent, null if no UI message should be shown
	 * @param workspace              the workspace to verify
	 * @param generatorConfiguration the generator configuration to verify against
	 * @throws MissingGeneratorFeaturesException the missing workspace plugins exception
	 */
	public static void verifyWorkspaceForCompatibilityWithGeneratorAndPlugins(@Nullable Window parent,
			Workspace workspace, GeneratorConfiguration generatorConfiguration)
			throws MissingGeneratorFeaturesException {
		try {
			WorkspaceUtils.verifyPluginRequirements(workspace, generatorConfiguration);
		} catch (MissingGeneratorFeaturesException e) {
			if (parent != null) {
				ThreadUtil.runOnSwingThreadAndWait(() -> {
					StringBuilder problems = new StringBuilder();
					for (Map.Entry<String, Collection<String>> entry : e.getMissingDefinitions().entrySet()) {
						problems.append("<b>").append(entry.getKey()).append(":</b> ")
								.append(String.join(", ", entry.getValue())).append("<br>");
					}
					JOptionPane.showMessageDialog(parent, L10N.t("dialog.workspace.missing_plugins_message", problems),
							L10N.t("dialog.workspace.missing_plugins_title"), JOptionPane.ERROR_MESSAGE);
				});
			}
			throw e;
		}
	}

}
