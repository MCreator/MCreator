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

package net.mcreator.ui.variants.resourcepackmaker;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.resourcepack.ResourcePackInfo;
import net.mcreator.minecraft.resourcepack.ResourcePackStructure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.recourcepack.ResourcePackEditor;
import net.mcreator.ui.workspace.AbstractMainWorkspacePanel;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePackMakerWorkspacePanel extends AbstractMainWorkspacePanel {

	private final ResourcePackEditor vanillaResourcePackEditor;

	private final Map<ResourcePackInfo, ResourcePackEditor> modResourcePackEditors = new ConcurrentHashMap<>();

	private final JTabbedPane tabbedPane;

	ResourcePackMakerWorkspacePanel(MCreator mcreator) {
		super(mcreator, new BorderLayout(3, 3));

		JPanel topPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topPan.setOpaque(false);
		topPan.add(search);

		add("North", topPan);

		vanillaResourcePackEditor = new ResourcePackEditor(mcreator,
				new ResourcePackInfo.Vanilla(mcreator.getWorkspace()), () -> search.getText().trim());

		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setOpaque(false);
		tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ROTATION,
				FlatClientProperties.TABBED_PANE_TAB_ROTATION_AUTO);

		tabbedPane.addTab("", new JLabel());
		tabbedPane.setTabComponentAt(0, new JLabel(UIRES.get("16px.add")));
		tabbedPane.addChangeListener(e -> {
			if (tabbedPane.getSelectedIndex() == 0) { // new texture mapping
				tabbedPane.setSelectedIndex(1);
				File[] files = FileDialogs.getMultiOpenDialog(mcreator, new String[] { ".jar", ".zip" });
				if (files != null) {
					File modsDir = mcreator.getWorkspace().getFolderManager().getModsDir();
					for (File file : files) {
						FileIO.copyFile(file, new File(modsDir, file.getName()));
					}
					reloadWorkspaceTab();
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
				}
			}
		});

		tabbedPane.addTab(L10N.t("mcreator.resourcepack.tab.vanilla"), vanillaResourcePackEditor);
		tabbedPane.setSelectedIndex(1);

		addVerticalTab("mods", L10N.t("workspace.category.resources"), new WorkspacePanelResourcePack(tabbedPane));
	}

	public ResourcePackEditor getCurrentResourcePackEditor() {
		if (tabbedPane.getSelectedComponent() instanceof ResourcePackEditor editor)
			return editor;
		return vanillaResourcePackEditor;
	}

	protected JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	private class WorkspacePanelResourcePack extends AbstractWorkspacePanel {

		private WorkspacePanelResourcePack(JComponent contents) {
			super(ResourcePackMakerWorkspacePanel.this);
			add(contents);
		}

		@Override public void reloadElements() {
			List<ResourcePackInfo> modPacks = ResourcePackInfo.findModResourcePacks(mcreator.getWorkspace());

			// add new mod pack editors
			for (ResourcePackInfo packInfo : modPacks) {
				if (!modResourcePackEditors.containsKey(packInfo)) {
					String tabTitle = L10N.t("mcreator.resourcepack.tab.mod", packInfo.namespace());

					ResourcePackEditor editor = new ResourcePackEditor(mcreator, packInfo,
							() -> search.getText().trim());
					modResourcePackEditors.put(packInfo, editor);
					tabbedPane.addTab(tabTitle, editor);

					JButton button = new JButton(UIRES.get("close_small"));
					button.setContentAreaFilled(false);
					button.setBorder(BorderFactory.createEmptyBorder());
					button.setMargin(new Insets(0, 0, 0, 0));
					button.addActionListener(e -> {
						int n = JOptionPane.showConfirmDialog(mcreator, L10N.t("mcreator.resourcepack.delete_pack"),
								L10N.t("common.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
								null);
						if (n == JOptionPane.YES_OPTION) {
							// Delete pack (mod) file + overrides namespace folder
							File packFile = packInfo.packFile();
							if (packFile != null)
								packFile.delete();
							FileIO.deleteDir(ResourcePackStructure.getResourcePackRoot(mcreator.getWorkspace(),
									packInfo.namespace()));
							reloadWorkspaceTab();
						}
					});
					tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(editor),
							PanelUtils.join(FlowLayout.LEFT, 8, 0, new JLabel(tabTitle), button));
				}
			}

			// remove mod pack editors not in the list from map and tabbed pane
			for (ResourcePackInfo packInfo : modResourcePackEditors.keySet()) {
				if (!modPacks.contains(packInfo)) {
					ResourcePackEditor editor = modResourcePackEditors.get(packInfo);
					modResourcePackEditors.remove(packInfo);
					tabbedPane.remove(editor);
				}
			}

			// reload all editors
			vanillaResourcePackEditor.reloadElements();
			for (ResourcePackEditor editor : modResourcePackEditors.values()) {
				editor.reloadElements();
			}
		}

		@Override public void refilterElements() {
			vanillaResourcePackEditor.refilterElements();
			for (ResourcePackEditor editor : modResourcePackEditors.values()) {
				editor.refilterElements();
			}
		}

	}

}

