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

package net.mcreator.ui.workspace.resources;

import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.awt.*;

public class WorkspacePanelResources extends JTabbedPane {

	public WorkspacePanelTextures workspacePanelTextures;
	public WorkspacePanelSounds workspacePanelSounds;
	public WorkspacePanelModels workspacePanelModels;
	public WorkspacePanelStructures workspacePanelStructures;
	private final WorkspacePanelScreenshots workspacePanelScreenshots;

	public WorkspacePanelResources(WorkspacePanel workspacePanel) {
		setOpaque(false);

		this.workspacePanelTextures = new WorkspacePanelTextures(workspacePanel);
		this.workspacePanelSounds = new WorkspacePanelSounds(workspacePanel);
		this.workspacePanelModels = new WorkspacePanelModels(workspacePanel);
		this.workspacePanelStructures = new WorkspacePanelStructures(workspacePanel);
		this.workspacePanelScreenshots = new WorkspacePanelScreenshots(workspacePanel);

		if (workspacePanel.getMcreator().getGeneratorStats().getBaseCoverageInfo().get("textures")
				!= GeneratorStats.CoverageStatus.NONE)
			addTab(L10N.t("workspace.resources.tab.textures"), workspacePanelTextures);

		if (workspacePanel.getMcreator().getGeneratorStats().getBaseCoverageInfo().get("model_json")
				!= GeneratorStats.CoverageStatus.NONE
				|| workspacePanel.getMcreator().getGeneratorStats().getBaseCoverageInfo().get("model_java")
				!= GeneratorStats.CoverageStatus.NONE
				|| workspacePanel.getMcreator().getGeneratorStats().getBaseCoverageInfo().get("model_obj")
				!= GeneratorStats.CoverageStatus.NONE)
			addTab(L10N.t("workspace.resources.tab.3d_models"), workspacePanelModels);

		if (workspacePanel.getMcreator().getGeneratorStats().getBaseCoverageInfo().get("sounds")
				!= GeneratorStats.CoverageStatus.NONE)
			addTab(L10N.t("workspace.resources.tab.sounds"), workspacePanelSounds);

		if (workspacePanel.getMcreator().getGeneratorStats().getBaseCoverageInfo().get("structures")
				!= GeneratorStats.CoverageStatus.NONE)
			addTab(L10N.t("workspace.resources.tab.structures"), workspacePanelStructures);

		if (workspacePanel.getMcreator().getGeneratorConfiguration().getGradleTaskFor("run_client") != null
				&& !workspacePanel.getMcreator().getGeneratorConfiguration().getGradleTaskFor("run_client")
				.contains("@"))
			addTab(L10N.t("workspace.resources.tab.screenshots"), workspacePanelScreenshots);

		for (int i = 0; i < getTabCount(); i++) {
			setBackgroundAt(i, new Color(0, 0, 0, 0));
		}

		setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
			@Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
			}
		});

		addChangeListener(changeEvent -> reloadElements());
	}

	@Override protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.dispose();
		super.paintComponent(g);
	}

	public void reloadElements() {
		if (getSelectedIndex() < 0)
			return;

		Component component = getComponentAt(getSelectedIndex());
		if (component instanceof IReloadableFilterable) {
			((IReloadableFilterable) component).reloadElements();
		}
	}

	public void refilter() {
		if (getSelectedIndex() < 0)
			return;

		Component component = getComponentAt(getSelectedIndex());
		if (component instanceof IReloadableFilterable) {
			((IReloadableFilterable) component).refilterElements();
		}
	}

}
