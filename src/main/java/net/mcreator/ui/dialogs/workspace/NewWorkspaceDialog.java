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

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.io.OS;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.workspace.selector.WorkspaceSelector;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.stream.Collectors;

public class NewWorkspaceDialog extends MCreatorDialog {

	private File workspaceFile = null;

	private AbstractWorkspacePanel current = null;

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel workspacePanels = new JPanel(cardLayout);

	private final JToggleButton forge = new JToggleButton(L10N.t("dialog.new_workspace.forge.toggle"),
			UIRES.get("16px.forge"));
	private final JToggleButton neoforge = new JToggleButton(L10N.t("dialog.new_workspace.neoforge.toggle"),
			UIRES.get("16px.neoforge"));
	private final JToggleButton fabric = new JToggleButton(L10N.t("dialog.new_workspace.fabric.toggle"),
			UIRES.get("16px.fabric"));
	private final JToggleButton quilt = new JToggleButton(L10N.t("dialog.new_workspace.quilt.toggle"),
			UIRES.get("16px.quilt"));
	private final JToggleButton spigot = new JToggleButton(L10N.t("dialog.new_workspace.spigot.toggle"),
			UIRES.get("16px.spigot"));
	private final JToggleButton datapack = new JToggleButton(L10N.t("dialog.new_workspace.datapack.toggle"),
			UIRES.get("16px.datapack"));
	private final JToggleButton addon = new JToggleButton(L10N.t("dialog.new_workspace.addon.toggle"),
			UIRES.get("16px.bedrock"));

	public NewWorkspaceDialog(Window w) {
		super(w, null, true);

		AbstractWorkspacePanel forgeWorkspacePanel = new ForgeWorkspacePanel(this);
		AbstractWorkspacePanel neoforgeWorkspacePanel = new NeoForgeWorkspacePanel(this);
		AbstractWorkspacePanel fabricWorkspacePanel = new FabricWorkspacePanel(this);
		AbstractWorkspacePanel quiltWorkspacePanel = new QuiltWorkspacePanel(this);
		AbstractWorkspacePanel spigotWorkspacePanel = new SpigotWorkspacePanel(this);
		AbstractWorkspacePanel datapackWorkspacePanel = new DatapackWorkspacePanel(this);
		AbstractWorkspacePanel addonWorkspacePanel = new AddonWorkspacePanel(this);

		JPanel buttons = new JPanel();

		JButton ok = L10N.button("dialog.new_workspace.button_new");
		buttons.add(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		buttons.add(cancel);

		buttons.add(new JEmptyBox(2, 2));

		JButton help = L10N.button("common.help");
		buttons.add(help);

		buttons.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		add("South", buttons);

		ok.addActionListener(e -> {
			WorkspaceSettings workspaceSettings = current.getWorkspaceSettings();
			if (workspaceSettings != null) {
				if (w instanceof WorkspaceSelector workspaceSelector) {
					if (workspaceSelector.getApplication() != null)
						workspaceSelector.getApplication().getAnalytics()
								.trackEvent(AnalyticsConstants.EVENT_NEW_WORKSPACE,
										workspaceSettings.getCurrentGenerator());
				} else if (w instanceof MCreator mcreator) {
					mcreator.getApplication().getAnalytics().trackEvent(AnalyticsConstants.EVENT_NEW_WORKSPACE,
							workspaceSettings.getCurrentGenerator());
				}

				workspaceFile = new File(new File(current.getWorkspaceFolder()),
						workspaceSettings.getModID() + ".mcreator");
				Workspace workspace = Workspace.createWorkspace(workspaceFile, workspaceSettings);
				workspace.close();
				setVisible(false);
			} else {
				showErrorsMessage(w, current.getValidationResult());
			}
		});

		cancel.addActionListener(actionEvent -> {
			workspaceFile = null;
			setVisible(false);
		});

		help.addActionListener(actionEvent -> DesktopUtils.browseSafe(
				MCreatorApplication.SERVER_DOMAIN + "/wiki/create-new-workspace-window"));

		workspacePanels.add("forge", PanelUtils.pullElementUp(forgeWorkspacePanel));
		workspacePanels.add("neoforge", PanelUtils.pullElementUp(neoforgeWorkspacePanel));
		workspacePanels.add("fabric", PanelUtils.pullElementUp(fabricWorkspacePanel));
		workspacePanels.add("quilt", PanelUtils.pullElementUp(quiltWorkspacePanel));
		workspacePanels.add("spigot", PanelUtils.pullElementUp(spigotWorkspacePanel));
		workspacePanels.add("datapack", PanelUtils.pullElementUp(datapackWorkspacePanel));
		workspacePanels.add("addon", PanelUtils.pullElementUp(addonWorkspacePanel));

		JComponent center = PanelUtils.centerInPanel(workspacePanels);
		center.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.current().getAltBackgroundColor()));

		add("Center", center);

		JLabel label = new JLabel(UIRES.get("wrk_add"));
		label.setText(L10N.t("dialog.new_workspace.main_title_html"));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		add("North", label);

		JPanel workspaceType = new JPanel(new GridLayout(0, 1));

		ButtonGroup buttonGroup = new ButtonGroup();

		forge.setHorizontalAlignment(SwingConstants.LEFT);
		forge.setBackground(Theme.current().getBackgroundColor());
		forge.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(forge);
		forge.addActionListener(e -> {
			current = forgeWorkspacePanel;
			cardLayout.show(workspacePanels, "forge");
		});

		neoforge.setHorizontalAlignment(SwingConstants.LEFT);
		neoforge.setBackground(Theme.current().getBackgroundColor());
		neoforge.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(neoforge);
		neoforge.addActionListener(e -> {
			current = neoforgeWorkspacePanel;
			cardLayout.show(workspacePanels, "neoforge");
		});

		fabric.setHorizontalAlignment(SwingConstants.LEFT);
		fabric.setBackground(Theme.current().getBackgroundColor());
		fabric.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(fabric);
		fabric.addActionListener(e -> {
			current = fabricWorkspacePanel;
			cardLayout.show(workspacePanels, "fabric");
		});

		quilt.setHorizontalAlignment(SwingConstants.LEFT);
		quilt.setBackground(Theme.current().getBackgroundColor());
		quilt.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(quilt);
		quilt.addActionListener(e -> {
			current = quiltWorkspacePanel;
			cardLayout.show(workspacePanels, "quilt");
		});

		spigot.setHorizontalAlignment(SwingConstants.LEFT);
		spigot.setBackground(Theme.current().getBackgroundColor());
		spigot.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(spigot);
		spigot.addActionListener(e -> {
			current = spigotWorkspacePanel;
			cardLayout.show(workspacePanels, "spigot");
		});

		datapack.setHorizontalAlignment(SwingConstants.LEFT);
		datapack.setBackground(Theme.current().getBackgroundColor());
		datapack.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(datapack);
		datapack.addActionListener(e -> {
			current = datapackWorkspacePanel;
			cardLayout.show(workspacePanels, "datapack");
		});

		addon.setHorizontalAlignment(SwingConstants.LEFT);
		addon.setBackground(Theme.current().getBackgroundColor());
		addon.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(addon);
		addon.addActionListener(e -> {
			current = addonWorkspacePanel;
			cardLayout.show(workspacePanels, "addon");
		});

		JLabel wt = L10N.label("dialog.new_workspace.type");
		ComponentUtils.deriveFont(wt, 10);
		wt.setForeground(Theme.current().getAltForegroundColor());
		wt.setBorder(BorderFactory.createEmptyBorder(8, 8, 3, 30));

		workspaceType.add(wt);
		workspaceType.add(neoforge);
		workspaceType.add(forge);
		workspaceType.add(fabric);
		workspaceType.add(quilt);
		workspaceType.add(spigot);
		workspaceType.add(addon);
		workspaceType.add(datapack);

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.FORGE)) {
			disableType(forge);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.NEOFORGE)) {
			disableType(neoforge);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.FABRIC)) {
			disableType(fabric);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.QUILT)) {
			disableType(quilt);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.SPIGOT)) {
			disableType(spigot);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.ADDON)) {
			disableType(addon);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.DATAPACK)) {
			disableType(datapack);
		}

		JComponent wrapPan = PanelUtils.northAndCenterElement(workspaceType, new JEmptyBox());
		wrapPan.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Theme.current().getAltBackgroundColor()));
		add("West", wrapPan);

		if (OS.getOS() == OS.WINDOWS) {
			getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
		} else if (OS.getOS() == OS.MAC && SystemInfo.isMacFullWindowContentSupported) {
			getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
			getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
			getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
			getRootPane().setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
		}

		getRootPane().setDefaultButton(ok);
		pack();
		setLocationRelativeTo(w);

		GeneratorConfiguration suggestedGenerator = GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(
				Generator.GENERATOR_CACHE.values().stream()
						.filter(e -> GeneratorFlavor.OFFICIAL_FLAVORS.contains(e.getGeneratorFlavor()))
						.collect(Collectors.toSet()), GeneratorFlavor.BaseLanguage.JAVA);
		if (suggestedGenerator != null) {
			selectType(suggestedGenerator.getGeneratorFlavor());
		} else {
			selectType(GeneratorFlavor.FORGE);
		}
		this.current.focusMainField();

		setVisible(true);
	}

	private void selectType(GeneratorFlavor flavor) {
		switch (flavor) {
		case FORGE:
			forge.doClick();
			break;
		case NEOFORGE:
			neoforge.doClick();
			break;
		case FABRIC:
			fabric.doClick();
			break;
		case QUILT:
			quilt.doClick();
			break;
		case SPIGOT:
			spigot.doClick();
			break;
		case ADDON:
			addon.doClick();
			break;
		case DATAPACK:
			datapack.doClick();
			break;
		}
	}

	private void disableType(JToggleButton button) {
		button.setEnabled(false);
		button.setToolTipText(L10N.t("dialog.new_workspace.disabled.tooltip"));
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseReleased(e);
				int option = JOptionPane.showConfirmDialog(null,
						L10N.t("dialog.new_workspace.dialog_plugin_needed.text"),
						L10N.t("dialog.new_workspace.dialog_plugin_needed.title"), JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null);
				if (option == JOptionPane.YES_OPTION) {
					DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/plugins");
				}
			}
		});
	}

	private void showErrorsMessage(Window w, AggregatedValidationResult validationResult) {
		StringBuilder stringBuilder = new StringBuilder(L10N.t("dialog.new_workspace.error_list"));
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

	public File getWorkspaceFile() {
		return workspaceFile;
	}

}
