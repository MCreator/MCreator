/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.modgui.codeviewer;

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.ui.laf.FileIcons;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ListTemplatesChooser<T extends GeneratableElement> extends JPanel {

	private final Map<GeneratorFile, FileCodeViewer<T>> tabsMap = new HashMap<>();
	private final CardLayout filesList;

	private final DefaultListModel<GeneratorFile> listModel = new DefaultListModel<>();
	private final JList<GeneratorFile> files = new JList<>(listModel);
	private final JPanel code;

	public ListTemplatesChooser() {
		super(new BorderLayout());

		files.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		files.setVisibleRowCount(1);
		files.setLayoutOrientation(JList.VERTICAL_WRAP);
		files.setCellRenderer(new FileListCellRenderer());

		filesList = new CardLayout();
		code = new JPanel(filesList);
		files.addListSelectionListener(e -> filesList.show(code, files.getSelectedValue().file().getName()));

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, files, code);
		split.setOneTouchExpandable(true);
		split.setDividerSize(10);
		split.setDividerLocation(0.2);
	}

	public void addFileTab(GeneratorFile generatorFile, FileCodeViewer<T> fileCodeViewer) {
		listModel.addElement(generatorFile);
		tabsMap.put(generatorFile, fileCodeViewer);
		code.add(fileCodeViewer, generatorFile.file().getName());
	}

	private void fileSelected(GeneratorFile generatorFile) {
		files.setSelectedValue(generatorFile, true);
		filesList.show(code, generatorFile.file().getName());
	}

	public void setSelectedFileTab(GeneratorFile generatorFile) {
		if (tabsMap.containsKey(generatorFile))
			fileSelected(generatorFile);
	}

	public int getFilesCount() {
		return tabsMap.size();
	}

	public void removeFileTab(GeneratorFile generatorFile) {
		listModel.removeElement(generatorFile);
		remove(tabsMap.remove(generatorFile));
	}

	public void removeFileTab(File file) {
		for (int i = 0; i < listModel.size(); i++) {
			if (listModel.get(i).file() == file) {
				removeFileTab(listModel.get(i));
				break;
			}
		}
	}

	private static class FileListCellRenderer extends JLabel implements ListCellRenderer<GeneratorFile> {
		@Override
		public Component getListCellRendererComponent(JList<? extends GeneratorFile> list, GeneratorFile value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setOpaque(true);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") :
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);

			setIcon(FileIcons.getIconForFile(value.file()));
			setText(value.file().getName());

			return this;
		}
	}
}
