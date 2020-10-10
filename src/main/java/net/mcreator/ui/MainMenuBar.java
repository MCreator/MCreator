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

package net.mcreator.ui;

import net.mcreator.ui.component.SocialButtons;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainMenuBar extends JMenuBar {

	private static final Logger LOG = LogManager.getLogger("Menu Bar");

	private final JMenu code = new JMenu("Code");
	private final JMenu imageEditor = new JMenu("Image");

	private final MCreator mcreator;

	public MainMenuBar(MCreator mcreator) {
		this.mcreator = mcreator;

		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));

		JMenu logo = new JMenu("  MCreator");
		logo.setMnemonic('M');
		logo.setIcon(new ImageIcon(ImageUtils.resizeAA(UIRES.get("icon").getImage(), 14, 14)));

		logo.add(mcreator.actionRegistry.mcreatorWebsite);
		logo.add(mcreator.actionRegistry.mcreatorCommunity);
		SocialButtons socialButtons = new SocialButtons();
		socialButtons.setBorder(BorderFactory.createEmptyBorder(3, 29, 7, 0));
		logo.add(socialButtons);
		logo.addSeparator();
		logo.add(mcreator.actionRegistry.donate);
		logo.addSeparator();
		logo.add(mcreator.actionRegistry.mcreatorPublish);

		add(logo);

		JMenu file = new JMenu("File");
		file.setMnemonic('F');
		file.add(mcreator.actionRegistry.newWorkspace);
		file.addSeparator();
		file.add(mcreator.actionRegistry.openWorkspace);

		if (mcreator.getApplication() != null) {
			JMenu recentWorkspacesList = new JMenu("Open recent");
			int number = 0;
			for (WorkspaceSelector.RecentWorkspaceEntry recentWorkspaceEntry : mcreator.getApplication()
					.getRecentWorkspaces()) {
				if (recentWorkspaceEntry.getPath().equals(mcreator.getWorkspace().getFileManager().getWorkspaceFile()))
					continue;

				JMenuItem recent = new JMenuItem(recentWorkspaceEntry.getName());
				recent.addActionListener(
						e -> mcreator.getApplication().openWorkspaceInMCreator(recentWorkspaceEntry.getPath(),
								recentWorkspaceEntry, true));
				recentWorkspacesList.add(recent);

				// limit to max. 10 recent workspaces on the list
				number++;
				if (number >= 10)
					break;

			}
			file.add(recentWorkspacesList);
		}
		file.addSeparator();
		file.add(mcreator.actionRegistry.importWorkspace);
		file.add(mcreator.actionRegistry.exportWorkspaceToZIP);
		file.add(mcreator.actionRegistry.exportWorkspaceToZIPWithRunDir);
		file.addSeparator();
		file.add(mcreator.actionRegistry.closeWorkspace);
		file.addSeparator();
		file.add(mcreator.actionRegistry.preferences);
		file.addSeparator();
		file.add(mcreator.actionRegistry.exitMCreator);
		add(file);

		code.setMnemonic('C');
		code.add(mcreator.actionRegistry.saveCode);
		code.add(mcreator.actionRegistry.reloadCode);
		code.addSeparator();
		code.add(mcreator.actionRegistry.showFindBar);
		code.add(mcreator.actionRegistry.showReplaceBar);
		code.addSeparator();
		code.add(mcreator.actionRegistry.reformatCodeAndImports);
		code.add(mcreator.actionRegistry.reformatCodeOnly);
		add(code);

		imageEditor.setMnemonic('I');
		imageEditor.add(mcreator.actionRegistry.imageEditorUndo);
		imageEditor.add(mcreator.actionRegistry.imageEditorRedo);
		imageEditor.add(mcreator.actionRegistry.imageEditorResizeCanvas);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.actionRegistry.imageEditorSave);
		imageEditor.add(mcreator.actionRegistry.imageEditorSaveAs);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.actionRegistry.imageEditorPencil);
		imageEditor.add(mcreator.actionRegistry.imageEditorShape);
		imageEditor.add(mcreator.actionRegistry.imageEditorEraser);
		imageEditor.add(mcreator.actionRegistry.imageEditorStamp);
		imageEditor.add(mcreator.actionRegistry.imageEditorFloodFill);
		imageEditor.add(mcreator.actionRegistry.imageEditorColorPicker);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.actionRegistry.imageEditorColorize);
		imageEditor.add(mcreator.actionRegistry.imageEditorDesaturate);
		imageEditor.add(mcreator.actionRegistry.imageEditorHSVNoise);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.actionRegistry.imageEditorMoveLayer);
		imageEditor.add(mcreator.actionRegistry.imageEditorResizeLayer);
		add(imageEditor);

		JMenu workspace = new JMenu("Workspace");
		workspace.setMnemonic('S');

		workspace.addSeparator();
		workspace.add(mcreator.actionRegistry.setCreativeTabItemOrder);
		workspace.addSeparator();
		workspace.add(mcreator.actionRegistry.openWorkspaceFolder);
		workspace.addSeparator();
		workspace.add(mcreator.actionRegistry.workspaceSettings);
		workspace.addSeparator();
		workspace.add(mcreator.actionRegistry.exportToDeobfJAR);
		workspace.add(mcreator.actionRegistry.exportToJAR);

		add(workspace);

		JMenu resources = new JMenu("Resources");
		resources.setMnemonic('R');
		resources.add(mcreator.actionRegistry.importBlockTexture);
		resources.add(mcreator.actionRegistry.importItemTexture);
		resources.add(mcreator.actionRegistry.importArmorTexture);
		resources.add(mcreator.actionRegistry.importOtherTexture);
		resources.addSeparator();
		resources.add(mcreator.actionRegistry.importSound);
		resources.addSeparator();
		resources.add(mcreator.actionRegistry.importStructure);
		resources.add(mcreator.actionRegistry.importStructureFromMinecraft);
		resources.addSeparator();
		resources.add(mcreator.actionRegistry.importJavaModel);
		resources.add(mcreator.actionRegistry.importJSONModel);
		resources.add(mcreator.actionRegistry.importOBJModel);
		add(resources);

		JMenu build = new JMenu("Build & run");
		build.setMnemonic('B');
		build.add(mcreator.actionRegistry.buildWorkspace);
		build.add(mcreator.actionRegistry.buildGradleOnly);
		build.add(mcreator.actionRegistry.buildClean);
		build.addSeparator();
		build.add(mcreator.actionRegistry.regenerateCode);
		build.addSeparator();
		build.add(mcreator.actionRegistry.reloadGradleProject);
		build.add(mcreator.actionRegistry.clearAllGradleCaches);
		build.addSeparator();
		build.add(mcreator.actionRegistry.cancelGradleTaskAction);
		build.addSeparator();
		build.add(mcreator.actionRegistry.runGradleTask);
		build.addSeparator();
		build.add(mcreator.actionRegistry.runClient);
		build.add(mcreator.actionRegistry.runServer);
		add(build);

		JMenu tools = new JMenu("Tools");
		tools.setMnemonic('T');
		tools.add(mcreator.actionRegistry.createMCItemTexture);
		tools.add(mcreator.actionRegistry.createArmorTexture);
		tools.add(mcreator.actionRegistry.createAnimatedTexture);
		tools.addSeparator();
		tools.add(mcreator.actionRegistry.openMaterialPackMaker);
		tools.add(mcreator.actionRegistry.openOrePackMaker);
		tools.add(mcreator.actionRegistry.openToolPackMaker);
		tools.add(mcreator.actionRegistry.openArmorPackMaker);
		tools.add(mcreator.actionRegistry.openWoodPackMaker);
		tools.addSeparator();
		tools.add(mcreator.actionRegistry.openJavaEditionFolder);
		tools.add(mcreator.actionRegistry.openBedrockEditionFolder);
		tools.addSeparator();
		JMenu dataLists = new JMenu("Minecraft data lists");
		dataLists.add(mcreator.actionRegistry.showEntityIDList);
		dataLists.add(mcreator.actionRegistry.showItemBlockList);
		dataLists.add(mcreator.actionRegistry.showParticleIDList);
		dataLists.add(mcreator.actionRegistry.showSoundsList);
		dataLists.add(mcreator.actionRegistry.showFuelBurnTimes);
		dataLists.add(mcreator.actionRegistry.showVanillaLootTables);
		tools.add(dataLists);
		add(tools);

		JMenu vcs = new JMenu("Remote workspace");
		vcs.setMnemonic('R');
		vcs.add(mcreator.actionRegistry.setupVCS);
		vcs.addSeparator();
		vcs.add(mcreator.actionRegistry.showUnsyncedChanges);
		vcs.addSeparator();
		vcs.add(mcreator.actionRegistry.syncFromRemote);
		vcs.add(mcreator.actionRegistry.syncToRemote);
		vcs.addSeparator();
		vcs.add(mcreator.actionRegistry.unlinkVCS);
		vcs.add(mcreator.actionRegistry.remoteWorkspaceSettings);
		add(vcs);

		JMenu window = new JMenu("Window");
		window.add(mcreator.actionRegistry.showWorkspaceBrowser);
		window.add(mcreator.actionRegistry.hideWorkspaceBrowser);
		window.addSeparator();
		window.add(mcreator.actionRegistry.closeCurrentTab);
		window.add(mcreator.actionRegistry.closeAllTabs);
		window.addSeparator();
		window.add(mcreator.actionRegistry.showWorkspaceTab);
		window.add(mcreator.actionRegistry.showConsoleTab);
		window.setMnemonic('W');
		add(window);

		JMenu help = new JMenu("Help");
		addHelpSearch(help);
		help.add(mcreator.actionRegistry.help);
		help.add(mcreator.actionRegistry.support);
		help.add(mcreator.actionRegistry.knowledgeBase);
		help.addSeparator();
		help.add(mcreator.actionRegistry.showShortcuts);
		help.addSeparator();
		help.add(mcreator.actionRegistry.donate);
		help.addSeparator();
		help.add(mcreator.actionRegistry.checkForUpdates);
		help.add(mcreator.actionRegistry.aboutMCreator);
		help.setMnemonic('H');
		add(help);
	}

	private void addHelpSearch(JMenu help) {
		JTextField searchField = new JTextField(20) {
			@Override public void paintComponent(Graphics g) {
				g.setColor(new Color(0.3f, 0.3f, 0.3f, 0.4f));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
				g.setColor(new Color(0x9C9C9C));
				g.setFont(getFont().deriveFont(11.0f));
				if (getText().equals(""))
					g.drawString("Search for tutorials, examples, ...", 28, 14);
			}
		};
		searchField.setOpaque(true);
		ComponentUtils.deriveFont(searchField, 13);
		searchField.setBorder(BorderFactory.createEmptyBorder(1, 28, 1, 0));
		searchField.addKeyListener(new KeyAdapter() {
			@Override public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					DesktopUtils.browseSafe(MCreatorApplication.WEB_API.getSearchURL(searchField.getText()));
				}
			}
		});
		searchField.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent focusEvent) {
				super.focusLost(focusEvent);
				searchField.setText("");
			}
		});

		help.add(searchField);

		refreshMenuBar();
	}

	public void refreshMenuBar() {
		code.setVisible(mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof CodeEditorView);
		imageEditor.setVisible(mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof ImageMakerView);
	}

}
