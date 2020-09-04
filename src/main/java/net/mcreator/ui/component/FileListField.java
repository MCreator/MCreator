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

package net.mcreator.ui.component;

import net.mcreator.ui.dialogs.FileDialogs;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileListField extends JItemListField<File> {

	private final Window window;

	public FileListField(Window window) {
		this.window = window;
	}

	@Override protected List<File> getElementsToAdd() {
		return Arrays.asList(FileDialogs.getMultiOpenDialog(window, new String[] { ".ogg" }));
	}

}
