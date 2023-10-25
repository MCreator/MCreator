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

import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.ui.component.JSelectableList;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

class WorkspacePanelScreenshots extends JPanel implements IReloadableFilterable {

	private final WorkspacePanel workspacePanel;

	private final ResourceFilterModel<File> filterModel;
	private final JSelectableList<File> screenshotsList;

	WorkspacePanelScreenshots(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspacePanel = workspacePanel;
		filterModel = new ResourceFilterModel<>(workspacePanel, File::getName);
		screenshotsList = new JSelectableList<>(filterModel);

		screenshotsList.setOpaque(false);
		screenshotsList.setCellRenderer(new Render());
		screenshotsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		screenshotsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		screenshotsList.setVisibleRowCount(-1);

		JScrollPane sp = new JScrollPane(screenshotsList);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		add("Center", sp);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton edit = L10N.button("workspace.screenshots.export_selected");
		edit.setIcon(UIRES.get("16px.ext.gif"));
		edit.setOpaque(false);
		edit.setContentAreaFilled(false);
		edit.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(edit);
		edit.addActionListener(e -> exportSelectedScreenshots());

		JButton useasbg = L10N.button("workspace.screenshots.use_as_background");
		useasbg.setIcon(UIRES.get("16px.textures"));
		useasbg.setOpaque(false);
		useasbg.setContentAreaFilled(false);
		useasbg.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(useasbg);
		useasbg.addActionListener(e -> useSelectedAsBackgrounds());

		JButton del = L10N.button("workspace.screenshots.delete_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);
		del.addActionListener(e -> {
			screenshotsList.getSelectedValuesList().forEach(File::delete);
			reloadElements();
		});
		screenshotsList.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					screenshotsList.getSelectedValuesList().forEach(File::delete);
					reloadElements();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					exportSelectedScreenshots();
				}
			}
		});

		screenshotsList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					exportSelectedScreenshots();
			}
		});

		add("North", bar);
	}

	private void useSelectedAsBackgrounds() {
		screenshotsList.getSelectedValuesList().forEach(
				f -> FileIO.copyFile(f, new File(UserFolderManager.getFileFromUserFolder("backgrounds"), f.getName())));
		JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
				L10N.t("workspace.screenshots.use_background_message"), L10N.t("workspace.screenshots.action_complete"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void exportSelectedScreenshots() {
		screenshotsList.getSelectedValuesList().forEach(f -> {
			File to = FileDialogs.getSaveDialog(workspacePanel.getMCreator(), new String[] { ".png" });
			if (to != null)
				FileIO.copyFile(f, to);
		});
	}

	@Override public void reloadElements() {
		List<File> selected = screenshotsList.getSelectedValuesList();

		filterModel.removeAllElements();
		File[] screenshots = new File(workspacePanel.getMCreator().getWorkspaceFolder(),
				"run/screenshots/").listFiles();
		if (screenshots != null)
			Arrays.stream(screenshots).forEach(filterModel::addElement);

		ListUtil.setSelectedValues(screenshotsList, selected);

		refilterElements();
	}

	@Override public void refilterElements() {
		filterModel.refilter();
	}

	static class Render extends JLabel implements ListCellRenderer<File> {

		@Override
		public JLabel getListCellRendererComponent(JList<? extends File> list, File ma, int index, boolean isSelected,
				boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setText(ma.getName());
			ComponentUtils.deriveFont(this, 11);
			setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			setVerticalTextPosition(BOTTOM);
			setHorizontalTextPosition(CENTER);
			setHorizontalAlignment(CENTER);

			setIcon(new ImageIcon(ImageUtils.resize(new ImageIcon(ma.getAbsolutePath()).getImage(), 145, 82)));

			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return this;
		}

	}

}
