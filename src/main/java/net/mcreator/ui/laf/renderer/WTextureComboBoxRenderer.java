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

package net.mcreator.ui.laf.renderer;

import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Function;

public class WTextureComboBoxRenderer extends JLabel implements ListCellRenderer<String> {

	private final Function<String, ImageIcon> textureProvider;

	public WTextureComboBoxRenderer(Function<String, ImageIcon> textureProvider) {
		this.textureProvider = textureProvider;

		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setText(value);

		ImageIcon imageIcon = textureProvider.apply(value);
		if (imageIcon != null) {
			setIcon(new ImageIcon(ImageUtils.resize(imageIcon.getImage(), 30)));
		} else {
			setIcon(new EmptyIcon(30, 30));
		}

		setHorizontalTextPosition(SwingConstants.RIGHT);
		setHorizontalAlignment(SwingConstants.LEFT);

		return this;
	}

	public static class OtherTextures extends WTextureComboBoxRenderer {

		public OtherTextures(Workspace workspace) {
			super(element -> {
				File file = workspace.getFolderManager().getOtherTextureFile(FilenameUtils.removeExtension(element));
				if (file.isFile())
					return new ImageIcon(file.getAbsolutePath());
				return null;
			});
		}
	}

}
