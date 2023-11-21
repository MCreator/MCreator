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
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class WorkspacePanelResources extends AbstractWorkspacePanel {

	public final WorkspacePanelTextures workspacePanelTextures;
	public final WorkspacePanelSounds workspacePanelSounds;
	public final WorkspacePanelModels workspacePanelModels;
	public final WorkspacePanelStructures workspacePanelStructures;
	public final WorkspacePanelScreenshots workspacePanelScreenshots;

	private final JTabbedPane resourceTabs;

	public WorkspacePanelResources(WorkspacePanel workspacePanel) {
		super(workspacePanel);
		resourceTabs = new JTabbedPane() {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};

		this.workspacePanelTextures = new WorkspacePanelTextures(workspacePanel);
		this.workspacePanelSounds = new WorkspacePanelSounds(workspacePanel);
		this.workspacePanelModels = new WorkspacePanelModels(workspacePanel);
		this.workspacePanelStructures = new WorkspacePanelStructures(workspacePanel);
		this.workspacePanelScreenshots = new WorkspacePanelScreenshots(workspacePanel);

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("textures")
				!= GeneratorStats.CoverageStatus.NONE)
			addResourcesTab(L10N.t("workspace.resources.tab.textures"), workspacePanelTextures);

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_json")
				!= GeneratorStats.CoverageStatus.NONE
				|| workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_java")
				!= GeneratorStats.CoverageStatus.NONE
				|| workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_obj")
				!= GeneratorStats.CoverageStatus.NONE)
			addResourcesTab(L10N.t("workspace.resources.tab.3d_models"), workspacePanelModels);

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("sounds")
				!= GeneratorStats.CoverageStatus.NONE)
			addResourcesTab(L10N.t("workspace.resources.tab.sounds"), workspacePanelSounds);

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("structures")
				!= GeneratorStats.CoverageStatus.NONE)
			addResourcesTab(L10N.t("workspace.resources.tab.structures"), workspacePanelStructures);

		if (workspacePanel.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_client") != null
				&& !workspacePanel.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_client")
				.contains("@"))
			addResourcesTab(L10N.t("workspace.resources.tab.screenshots"), workspacePanelScreenshots);

		for (int i = 0; i < resourceTabs.getTabCount(); i++) {
			resourceTabs.setBackgroundAt(i, new Color(0, 0, 0, 0));
		}

		resourceTabs.setOpaque(false);
		resourceTabs.setUI(new BasicTabbedPaneUI() {
			@Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
			}
		});
		resourceTabs.addChangeListener(changeEvent -> reloadElements());
		add(resourceTabs);
	}

	/**
	 * Adds a new resource section to this panel.
	 *
	 * @param title     The name of the section shown in the workspace.
	 * @param component The panel representing contents of the resources tab being added.
	 */
	public void addResourcesTab(String title, Component component) {
		resourceTabs.addTab(title, component);
	}

	@Override public boolean isSupportedInWorkspace() {
		return resourceTabs.getTabCount() > 0;
	}

	@Override public void reloadElements() {
		if (resourceTabs.getSelectedIndex() < 0)
			return;

		Component component = resourceTabs.getComponentAt(resourceTabs.getSelectedIndex());
		if (component instanceof IReloadableFilterable) {
			((IReloadableFilterable) component).reloadElements();
		}
	}

	@Override public void refilterElements() {
		if (resourceTabs.getSelectedIndex() < 0)
			return;

		Component component = resourceTabs.getComponentAt(resourceTabs.getSelectedIndex());
		if (component instanceof IReloadableFilterable) {
			((IReloadableFilterable) component).refilterElements();
		}
	}

}
