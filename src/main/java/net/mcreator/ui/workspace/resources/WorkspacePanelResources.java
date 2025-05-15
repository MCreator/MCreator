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
import net.mcreator.minecraft.resourcepack.ResourcePackInfo;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.recourcepack.ResourcePackEditor;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WorkspacePanelResources extends AbstractWorkspacePanel {

	private final JTabbedPane resourceTabs;

	private final Map<Class<? extends JPanel>, JPanel> resourcePanels = new HashMap<>();

	public WorkspacePanelResources(WorkspacePanel workspacePanel) {
		super(workspacePanel);
		resourceTabs = new JTabbedPane();
		resourceTabs.setOpaque(false);

		WorkspacePanelTextures workspacePanelTextures = new WorkspacePanelTextures(workspacePanel);
		WorkspacePanelSounds workspacePanelSounds = new WorkspacePanelSounds(workspacePanel);
		WorkspacePanelModels workspacePanelModels = new WorkspacePanelModels(workspacePanel);
		WorkspacePanelAnimations workspacePanelAnimations = new WorkspacePanelAnimations(workspacePanel);
		WorkspacePanelStructures workspacePanelStructures = new WorkspacePanelStructures(workspacePanel);
		WorkspacePanelScreenshots workspacePanelScreenshots = new WorkspacePanelScreenshots(workspacePanel);

		ResourcePackEditor resourcePackEditor = new ResourcePackEditor(workspacePanel.getMCreator(),
				new ResourcePackInfo.Vanilla(workspacePanel.getMCreator().getWorkspace()),
				() -> workspacePanel.getSearchTerm().trim());

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

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_animations_java")
				!= GeneratorStats.CoverageStatus.NONE)
			addResourcesTab(L10N.t("workspace.resources.tab.animations"), workspacePanelAnimations);

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

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("vanilla_resources")
				!= GeneratorStats.CoverageStatus.NONE)
			addResourcesTab(L10N.t("workspace.resources.tab.resource_pack"), resourcePackEditor);

		resourceTabs.addChangeListener(changeEvent -> reloadElements());
		add(resourceTabs);
	}

	/**
	 * Adds a new resource section to this panel.
	 *
	 * @param title     The name of the section shown in the workspace.
	 * @param component The panel representing contents of the resources tab being added.
	 */
	public void addResourcesTab(String title, JPanel component) {
		resourceTabs.addTab(title, component);
		resourcePanels.put(component.getClass(), component);
	}

	/**
	 * Gets the resource panel by its key.
	 *
	 * @param key The key of the resource panel.
	 * @return The resource panel.
	 */
	public <T extends JPanel> T getResourcePanel(Class<T> key) {
		//noinspection unchecked
		return (T) resourcePanels.get(key);
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
