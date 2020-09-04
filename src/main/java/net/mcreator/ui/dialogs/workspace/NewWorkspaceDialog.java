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
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class NewWorkspaceDialog extends MCreatorDialog {

	private File workspaceFile = null;

	private AbstractWorkspacePanel current = null;

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel workspacePanels = new JPanel(cardLayout);

	public NewWorkspaceDialog(Window w) {
		super(w, "New workspace", true);

		AbstractWorkspacePanel forgeWorkspacePanel = new ForgeWorkspacePanel(this);
		AbstractWorkspacePanel fabricWorkspacePanel = new FabricWorkspacePanel(this);
		AbstractWorkspacePanel spigotWorkspacePanel = new SpigotWorkspacePanel(this);
		AbstractWorkspacePanel datapackWorkspacePanel = new DatapackWorkspacePanel(this);
		AbstractWorkspacePanel addonWorkspacePanel = new AddonWorkspacePanel(this);

		JPanel buttons = new JPanel();

		JButton ok = new JButton("Create new workspace");
		buttons.add(ok);

		JButton cancel = new JButton("Cancel");
		buttons.add(cancel);

		buttons.add(new JEmptyBox(2, 2));

		JButton help = new JButton("Help");
		buttons.add(help);

		buttons.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		add("South", buttons);

		ok.addActionListener(e -> {
			WorkspaceSettings workspaceSettings = current.getWorkspaceSettings();
			if (workspaceSettings != null) {
				workspaceFile = new File(new File(current.getWorkspaceFolder()),
						workspaceSettings.getModID() + ".mcreator");
				Workspace.createWorkspace(workspaceFile, workspaceSettings);
				setVisible(false);
			} else {
				showErrorsMessage(w, current.getValidationResult());
			}
		});

		cancel.addActionListener(actionEvent -> {
			workspaceFile = null;
			setVisible(false);
		});

		help.addActionListener(actionEvent -> DesktopUtils
				.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/wiki/create-new-workspace-window"));

		workspacePanels.add("forge", PanelUtils.pullElementUp(forgeWorkspacePanel));
		workspacePanels.add("fabric", PanelUtils.pullElementUp(fabricWorkspacePanel));
		workspacePanels.add("spigot", PanelUtils.pullElementUp(spigotWorkspacePanel));
		workspacePanels.add("datapack", PanelUtils.pullElementUp(datapackWorkspacePanel));
		workspacePanels.add("addon", PanelUtils.pullElementUp(addonWorkspacePanel));

		JComponent center = PanelUtils.centerInPanel(workspacePanels);
		center.setBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		add("Center", center);

		JLabel label = new JLabel(UIRES.get("addwrk"));
		label.setText(
				"<html><font style=\"font-size: 16px;\">Create new workspace</font><br><small>Enter the workspace details below");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		add("North", label);

		JPanel workspaceType = new JPanel(new GridLayout(0, 1));

		ButtonGroup buttonGroup = new ButtonGroup();

		JToggleButton forge = new JToggleButton(" Minecraft Forge mod", UIRES.get("16px.forge"));
		forge.setHorizontalAlignment(SwingConstants.LEFT);
		forge.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(forge);
		forge.addActionListener(e -> {
			current = forgeWorkspacePanel;
			cardLayout.show(workspacePanels, "forge");
		});

		JToggleButton fabric = new JToggleButton(" Fabric Minecraft mod", UIRES.get("16px.fabric"));
		fabric.setHorizontalAlignment(SwingConstants.LEFT);
		fabric.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(fabric);
		fabric.addActionListener(e -> {
			current = fabricWorkspacePanel;
			cardLayout.show(workspacePanels, "fabric");
		});

		JToggleButton spigot = new JToggleButton(" Spigot server plugin", UIRES.get("16px.spigot"));
		spigot.setHorizontalAlignment(SwingConstants.LEFT);
		spigot.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(spigot);
		spigot.addActionListener(e -> {
			current = spigotWorkspacePanel;
			cardLayout.show(workspacePanels, "spigot");
		});

		JToggleButton datapack = new JToggleButton(" Minecraft data pack", UIRES.get("16px.datapack"));
		datapack.setHorizontalAlignment(SwingConstants.LEFT);
		datapack.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(datapack);
		datapack.addActionListener(e -> {
			current = datapackWorkspacePanel;
			cardLayout.show(workspacePanels, "datapack");
		});

		JToggleButton addon = new JToggleButton(" Minecraft add-on", UIRES.get("16px.bedrock"));
		addon.setHorizontalAlignment(SwingConstants.LEFT);
		addon.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
				BorderFactory.createEmptyBorder(8, 8, 8, 30)));
		buttonGroup.add(addon);
		addon.addActionListener(e -> {
			current = addonWorkspacePanel;
			cardLayout.show(workspacePanels, "addon");
		});

		JLabel wt = new JLabel("Workspace type:");
		ComponentUtils.deriveFont(wt, 10);
		wt.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		wt.setBorder(BorderFactory.createEmptyBorder(8, 8, 3, 30));

		workspaceType.add(wt);
		workspaceType.add(forge);
		workspaceType.add(fabric);
		workspaceType.add(spigot);
		workspaceType.add(addon);
		workspaceType.add(datapack);

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.FORGE)) {
			disableType(forge);
		}

		if (Generator.GENERATOR_CACHE.values().stream()
				.noneMatch(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.FABRIC)) {
			disableType(fabric);
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
		wrapPan.setBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 1, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));
		add("West", wrapPan);

		getRootPane().setDefaultButton(ok);
		setSize(710, 640);
		setLocationRelativeTo(w);
		setResizable(false);

		forge.setSelected(true);
		forgeWorkspacePanel.focusMainField();
		this.current = forgeWorkspacePanel;

		setVisible(true);
	}

	private void disableType(JToggleButton button) {
		button.setEnabled(false);
		button.setToolTipText("You need to have at least one plugin supporting this generator type installed.");
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseReleased(e);
				int option = JOptionPane.showConfirmDialog(null,
						"<html>No plugins supporting this generator type are currently installed."
								+ "<br>Do you want to browse MCreator's plugin library for plugins?", "Plugin needed",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
				if (option == JOptionPane.YES_OPTION) {
					DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/plugins");
				}
			}
		});
	}

	private void showErrorsMessage(Window w, AggregatedValidationResult validationResult) {
		StringBuilder stringBuilder = new StringBuilder("<html>Your workspace setup has the following issues:");
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
		stringBuilder
				.append("<small>Errors were marked on the window with red colors and error icons so you can locate them and fix<br>"
						+ "them based on notes here.");
		JOptionPane.showMessageDialog(w, stringBuilder.toString(), "Invalid workspace settings",
				JOptionPane.ERROR_MESSAGE);
	}

	public File getWorkspaceFile() {
		return workspaceFile;
	}

}
