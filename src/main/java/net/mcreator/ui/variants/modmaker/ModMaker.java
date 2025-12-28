/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.variants.modmaker;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.gradle.GradleResultCode;
import net.mcreator.gradle.GradleStateListener;
import net.mcreator.ui.*;
import net.mcreator.ui.component.DynamicContentPanel;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.debug.DebugPanel;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.ui.workspace.resources.WorkspacePanelResources;
import net.mcreator.ui.workspace.resources.WorkspacePanelTextures;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

public final class ModMaker extends MCreator {

	public static final String DOCK_DEBUGGER = "debugger";
	public static final String DOCK_CODE_VIEWER = "code_viewer";

	private WorkspacePanel workspacePanel;

	private final DebugPanel debugPanel;
	private final DynamicContentPanel codeViewer;

	public ModMaker(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		super(application, workspace);

		this.debugPanel = new DebugPanel(this);
		this.codeViewer = new DynamicContentPanel(ComponentUtils.bigCenteredText("dock.code_viewer.empty"));

		new ModMakerDropTarget(this);

		getGradleConsole().addGradleStateListener(new GradleStateListener() {
			@Override public void taskStarted(String taskName) {
				workspacePanel.disableRemoving();
			}

			@Override public void taskFinished(GradleResultCode result) {
				workspacePanel.enableRemoving();
			}
		});

		getTabs().addTabShownListener(tab -> {
			if (tab.getContent() instanceof ModElementGUI<?> elementGUI) {
				codeViewer.setCurrentComponent(elementGUI.getModElementCodeViewer());
			} else {
				codeViewer.clear();
			}
		});

		getBottomDockRegion().addDock(DOCK_CODE_VIEWER, 360, L10N.t("dock.code_viewer"), UIRES.get("16px.dock_inspect"),
				codeViewer);

		if (workspace.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA) {
			getBottomDockRegion().addDock(DOCK_DEBUGGER, 300, L10N.t("dock.debugger"), UIRES.get("16px.dock_debug"),
					debugPanel);
		}
	}

	@Override public MainMenuBar createMenuBar() {
		return new ModMakerMenuBar(this);
	}

	@Override public MainToolBar createToolBar() {
		return new ModMakerToolBar(this);
	}

	@Override protected JPanel createWorkspaceTabContent() {
		workspacePanel = new WorkspacePanel(this);
		return ComponentUtils.applyPadding(workspacePanel, 5, true, true, true, true);
	}

	@Override public WorkspacePanel getWorkspacePanel() {
		return workspacePanel;
	}

	@Override public void workspaceGeneratorSwitched() {
		super.workspaceGeneratorSwitched();
		WorkspacePanelResources workspacePanelResources = workspacePanel.getVerticalTab(WorkspacePanelResources.class);
		if (workspacePanelResources != null) {
			WorkspacePanelTextures workspacePanelTextures = workspacePanelResources.getResourcePanel(
					WorkspacePanelTextures.class);
			if (workspacePanelTextures != null) {
				workspacePanelTextures.attachGeneratorFileWatcher();
			}
		}
	}

	public DebugPanel getDebugPanel() {
		return debugPanel;
	}

}
