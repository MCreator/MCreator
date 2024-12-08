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

import net.mcreator.io.OS;
import net.mcreator.ui.component.SocialButtons;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.workspace.selector.RecentWorkspaceEntry;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class MainMenuBar extends JMenuBar {

	private final JMenu code = new JMenu(L10N.t("menubar.code"));
	private final JMenu imageEditor = new JMenu(L10N.t("menubar.image"));

	private final MCreator mcreator;

	protected MainMenuBar(MCreator mcreator) {
		this.mcreator = mcreator;

		boolean macOSscreenMenuBar =
				OS.getOS() == OS.MAC && "true".equals(System.getProperty("apple.laf.useScreenMenuBar"));

		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getSecondAltBackgroundColor()));

		if (!macOSscreenMenuBar) {
			JMenu logo = new JMenu("MCreator");
			logo.setMnemonic('M');
			logo.add(mcreator.getActionRegistry().mcreatorWebsite);
			logo.add(mcreator.getActionRegistry().mcreatorCommunity);
			SocialButtons socialButtons = new SocialButtons();
			socialButtons.setBorder(BorderFactory.createEmptyBorder(3, 29, 7, 0));
			logo.add(socialButtons);
			logo.addSeparator();
			logo.add(mcreator.getActionRegistry().donate);
			logo.addSeparator();
			logo.add(mcreator.getActionRegistry().mcreatorPublish);
			add(logo);
		}

		JMenu file = L10N.menu("menubar.file");
		file.setMnemonic('F');
		file.add(mcreator.getActionRegistry().newWorkspace);
		file.addSeparator();
		file.add(mcreator.getActionRegistry().openWorkspace);
		if (mcreator.getApplication() != null) {
			JMenu recentWorkspacesList = new JMenu(L10N.t("menubar.file.recent"));
			int number = 0;
			for (RecentWorkspaceEntry recentWorkspaceEntry : mcreator.getApplication().getRecentWorkspaces()) {
				if (recentWorkspaceEntry.getPath().equals(mcreator.getFileManager().getWorkspaceFile()))
					continue;

				JMenuItem recent = new JMenuItem(recentWorkspaceEntry.getName());
				recent.addActionListener(
						e -> mcreator.getApplication().openWorkspaceInMCreator(recentWorkspaceEntry.getPath()));
				recentWorkspacesList.add(recent);

				// limit to max. 10 recent workspaces on the list
				number++;
				if (number >= 10)
					break;

			}
			file.add(recentWorkspacesList);
		}
		file.addSeparator();
		file.add(mcreator.getActionRegistry().importWorkspace);
		file.add(mcreator.getActionRegistry().exportWorkspaceToZIP);
		file.add(mcreator.getActionRegistry().exportWorkspaceToZIPWithRunDir);
		file.addSeparator();
		file.add(mcreator.getActionRegistry().closeWorkspace);
		file.addSeparator();
		file.add(mcreator.getActionRegistry().preferences);
		file.addSeparator();
		file.add(mcreator.getActionRegistry().exitMCreator);
		add(file);

		code.setMnemonic('C');
		code.add(mcreator.getActionRegistry().saveCode);
		code.add(mcreator.getActionRegistry().reloadCode);
		code.addSeparator();
		code.add(mcreator.getActionRegistry().showFindBar);
		code.add(mcreator.getActionRegistry().showReplaceBar);
		code.addSeparator();
		code.add(mcreator.getActionRegistry().reformatCodeAndImports);
		code.add(mcreator.getActionRegistry().reformatCodeOnly);
		add(code);

		imageEditor.setMnemonic('I');
		imageEditor.add(mcreator.getActionRegistry().imageEditorUndo);
		imageEditor.add(mcreator.getActionRegistry().imageEditorRedo);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.getActionRegistry().imageEditorCopy);
		imageEditor.add(mcreator.getActionRegistry().imageEditorCopyAll);
		imageEditor.add(mcreator.getActionRegistry().imageEditorCut);
		imageEditor.add(mcreator.getActionRegistry().imageEditorPaste);
		imageEditor.add(mcreator.getActionRegistry().imageEditorDelete);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.getActionRegistry().imageEditorSave);
		imageEditor.add(mcreator.getActionRegistry().imageEditorSaveAs);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.getActionRegistry().imageEditorPencil);
		imageEditor.add(mcreator.getActionRegistry().imageEditorLine);
		imageEditor.add(mcreator.getActionRegistry().imageEditorShape);
		imageEditor.add(mcreator.getActionRegistry().imageEditorEraser);
		imageEditor.add(mcreator.getActionRegistry().imageEditorStamp);
		imageEditor.add(mcreator.getActionRegistry().imageEditorFloodFill);
		imageEditor.add(mcreator.getActionRegistry().imageEditorColorPicker);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.getActionRegistry().imageEditorColorize);
		imageEditor.add(mcreator.getActionRegistry().imageEditorDesaturate);
		imageEditor.add(mcreator.getActionRegistry().imageEditorHSVNoise);
		imageEditor.addSeparator();
		imageEditor.add(mcreator.getActionRegistry().imageEditorMoveLayer);
		imageEditor.add(mcreator.getActionRegistry().imageEditorSelectLayer);
		imageEditor.add(mcreator.getActionRegistry().imageEditorClearSelection);
		imageEditor.add(mcreator.getActionRegistry().imageEditorResizeLayer);
		imageEditor.add(mcreator.getActionRegistry().imageEditorResizeCanvas);
		add(imageEditor);

		assembleMenuBar(mcreator);

		JMenu window = L10N.menu("menubar.window");
		if (mcreator.hasProjectBrowser()) {
			window.add(mcreator.getActionRegistry().showWorkspaceBrowser);
			window.add(mcreator.getActionRegistry().hideWorkspaceBrowser);
			window.addSeparator();
		}
		window.add(mcreator.getActionRegistry().closeCurrentTab);
		window.add(mcreator.getActionRegistry().closeAllTabs);
		window.addSeparator();
		window.add(mcreator.getActionRegistry().showWorkspaceTab);
		window.add(mcreator.getActionRegistry().showConsoleTab);
		window.setMnemonic('W');
		add(window);

		JMenu help = L10N.menu("menubar.help");
		addHelpSearch(help);
		help.add(mcreator.getActionRegistry().help);
		help.add(mcreator.getActionRegistry().support);
		help.add(mcreator.getActionRegistry().knowledgeBase);
		if (macOSscreenMenuBar) {
			help.addSeparator();
			help.add(mcreator.getActionRegistry().mcreatorWebsite);
			help.add(mcreator.getActionRegistry().mcreatorCommunity);
			help.add(mcreator.getActionRegistry().mcreatorPublish);
		}
		help.addSeparator();
		help.add(mcreator.getActionRegistry().showShortcuts);
		help.addSeparator();
		help.add(mcreator.getActionRegistry().donate);
		help.addSeparator();
		help.add(mcreator.getActionRegistry().checkForUpdates);
		help.add(mcreator.getActionRegistry().checkForPluginUpdates);
		help.add(mcreator.getActionRegistry().aboutMCreator);
		help.setMnemonic('H');
		add(help);
	}

	protected abstract void assembleMenuBar(MCreator mcreator);

	private void addHelpSearch(JMenu help) {
		JTextField searchField = new JTextField(20) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(0x9C9C9C));
				g.setFont(getFont().deriveFont(11.0f));
				if (getText().isEmpty())
					g.drawString(L10N.t("menubar.help.search.tooltip"), 28, 14);
			}
		};
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
		code.setVisible(mcreator.getTabs().getCurrentTab() != null && mcreator.getTabs().getCurrentTab()
				.getContent() instanceof CodeEditorView);
		imageEditor.setVisible(mcreator.getTabs().getCurrentTab() != null && mcreator.getTabs().getCurrentTab()
				.getContent() instanceof ImageMakerView);
	}

}
