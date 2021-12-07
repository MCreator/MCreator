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

public class ListTemplatesViewer<T extends GeneratableElement> extends JSplitPane {

	private final Map<GeneratorFile, FileCodeViewer<T>> tabsMap = new HashMap<>();
	private final CardLayout filesList;

	private final DefaultListModel<GeneratorFile> listModel = new DefaultListModel<>();
	private final JList<GeneratorFile> files = new JList<>(listModel);
	private final JPanel code;

	public ListTemplatesViewer() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		filesList = new CardLayout();
		code = new JPanel(filesList);

		files.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		files.setCellRenderer(new FileListCellRenderer());
		files.addListSelectionListener(e -> filesList.show(code, files.getSelectedValue().file().getName()));

		setOneTouchExpandable(true);
		setLeftComponent(new JScrollPane(files));
		setRightComponent(code);
		setDividerSize(10);
		setDividerLocation(300);
	}

	public void addFileTab(GeneratorFile generatorFile, FileCodeViewer<T> fileCodeViewer) {
		listModel.addElement(generatorFile);
		tabsMap.put(generatorFile, fileCodeViewer);
		code.setVisible(true);
		code.add(fileCodeViewer, generatorFile.file().getName());
		if (tabsMap.keySet().size() == 1)
			setSelectedFileTab(generatorFile);
	}

	public void setSelectedFileTab(GeneratorFile generatorFile) {
		if (tabsMap.containsKey(generatorFile))
			files.setSelectedValue(generatorFile, true);
	}

	public void removeFileTab(GeneratorFile generatorFile) {
		listModel.removeElement(generatorFile);
		if (tabsMap.get(generatorFile) != null)
			code.remove(tabsMap.remove(generatorFile));
		if (tabsMap.keySet().size() == 0)
			code.setVisible(false);
	}

	public void removeFileTab(File file) {
		tabsMap.keySet().stream().filter(e -> e.file().getPath().equals(file.getPath())).findFirst()
				.ifPresent(this::removeFileTab);
	}

	private static class FileListCellRenderer extends JLabel implements ListCellRenderer<GeneratorFile> {
		@Override
		public Component getListCellRendererComponent(JList<? extends GeneratorFile> list, GeneratorFile value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setOpaque(true);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.MAIN_TINT") :
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
