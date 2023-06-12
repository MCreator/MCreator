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

package net.mcreator.element.parts.gui;

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.references.TextureReference;

import javax.swing.*;
import java.awt.Image;
import java.awt.*;

public class ImageButton extends GUIComponent {

	public String name;

	@TextureReference(TextureType.SCREEN) public String image, hoveredImage;

	public Procedure onClick, displayCondition;

	private transient Image imageIcon, hoveredImageIcon;

	public ImageButton(String name, int x, int y, String image, String hoveredImage, Procedure onClick,
			Procedure displayCondition) {
		super(x, y);
		this.name = name;
		this.image = image;
		this.hoveredImage = hoveredImage;
		this.onClick = onClick;
		this.displayCondition = displayCondition;
	}

	@Override public String getName() {
		return name;
	}

	public Image getImage(Workspace workspace) {
		if (imageIcon == null) {
			imageIcon = new ImageIcon(workspace.getFolderManager()
					.getTextureFile(FilenameUtilsPatched.removeExtension(image), TextureType.SCREEN)
					.getAbsolutePath()).getImage();
		}
		return imageIcon;
	}

	public Image getHoveredImage(Workspace workspace) {
		Image image = getImage(workspace);

		if (hoveredImageIcon == null) {
			if (hoveredImage != null && !hoveredImage.isEmpty()) {
				Image hovered = new ImageIcon(workspace.getFolderManager()
						.getTextureFile(FilenameUtilsPatched.removeExtension(hoveredImage), TextureType.SCREEN)
						.getAbsolutePath()).getImage();
				hoveredImageIcon = ImageUtils.checkIfSameSize(image, hovered) ? hovered : image;
			} else {
				hoveredImageIcon = image;
			}
		}
		return hoveredImageIcon;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor editor, Graphics2D g) {
		g.drawImage(getImage(editor.mcreator.getWorkspace()), cx, cy, editor);
	}

	@Override public int getWidth(Workspace workspace) {
		return getImage(workspace).getWidth(null);
	}

	@Override public int getHeight(Workspace workspace) {
		return getImage(workspace).getHeight(null);
	}

	@Override public int getWeight() {
		return 25;
	}

}
