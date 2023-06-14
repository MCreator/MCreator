/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.component;

import javafx.stage.FileChooser;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileChooserType;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.EventObject;
import java.util.function.Consumer;

public class JFileSelector extends JPanel {

	private File value;

	/**
	 * Create a custom file selector component to allow or not the usage of a null value for the desired file or folder.
	 *
	 * @param parent The file chooser's parent
	 * @param defaultValue The value that will be set to the component at its creation
	 * @param allowNullValue If true, another button will be added to make the returned value null
	 * @param isFolder If true, the user will only be able to choose a folder.
	 * @param fct This is mainly used to change the status of the {@link PreferencesDialog} buttons, so we can click on "Apply"
	 * @param filters If the user needs to choose a file, you can define the file chooser's filters
	 */
	public JFileSelector(Window parent, File defaultValue, boolean allowNullValue, boolean isFolder,
			Consumer<EventObject> fct, FileChooser.ExtensionFilter... filters) {
		this.value = defaultValue;

		setLayout(new BorderLayout(2, 0));
		setOpaque(false);

		JTextField pathField = new JTextField(15);
		pathField.setEditable(false);
		pathField.setHorizontalAlignment(JTextField.CENTER);
		pathField.setOpaque(true);

		if (value == null || value.getAbsolutePath().endsWith("Not specified"))
			pathField.setText(L10N.t("common.not_specified"));
		else
			pathField.setText(StringUtils.abbreviateStringInverse(value.getAbsolutePath(), 35));

		JButton button = new JButton("...");
		button.addActionListener(actionEvent -> {
			File file = null;
			if (isFolder) {
				file = FileDialogs.getDirectoryChooser(value);
			} else {
				File[] files = FileDialogs.getFileChooserDialog(parent, FileChooserType.OPEN, false, filters);
				if (files != null)
					file = files[0];
			}

			if (file != null && file.exists()) {
				if ((isFolder && file.isDirectory()) || (!isFolder && file.isFile())) {
					value = file;
					pathField.setText(file.getAbsolutePath());
					if (fct != null)
						fct.accept(actionEvent);
				}
			}
		});

		add("Center", PanelUtils.westAndCenterElement(pathField, button));

		if (allowNullValue) {
			JButton nullButton = new JButton(UIRES.get("16px.delete.gif"));
			nullButton.setMargin(new Insets(0, 0, 0, 0));
			nullButton.setOpaque(false);
			nullButton.addActionListener(actionEvent -> {
				value = null;
				pathField.setText(L10N.t("common.not_specified"));
				if (fct != null)
					fct.accept(actionEvent);
			});
			add("East", nullButton);
		}
	}

	public File getFile() {
		return value;
	}

}
