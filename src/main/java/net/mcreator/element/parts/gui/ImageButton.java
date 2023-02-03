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

import javax.swing.*;
import java.awt.Image;
import java.awt.*;

public class ImageButton extends GUIComponent {

	public String image;
	public String hoveredImage;

	public Procedure onClick;
	public Procedure displayCondition;

	public ImageButton(int x, int y, String image, String hoveredImage, Procedure onClick,
			Procedure displayCondition) {
		super(x, y);
		this.image = image;
		this.hoveredImage = hoveredImage;
		this.onClick = onClick;
		this.displayCondition = displayCondition;
	}

	@Override public String getName() {
		return "image_button_" + FilenameUtilsPatched.removeExtension(image);
	}

	public Image getImage(Workspace workspace) {
		return new ImageIcon(workspace.getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(image), TextureType.SCREEN)
				.getAbsolutePath()).getImage();
	}

	public Image getHoveredImage(Workspace workspace) {
		if (hoveredImage != null && !hoveredImage.isEmpty()) {
			Image hovered = new ImageIcon(workspace.getFolderManager()
					.getTextureFile(FilenameUtilsPatched.removeExtension(hoveredImage), TextureType.SCREEN)
					.getAbsolutePath()).getImage();

			return ImageUtils.checkIfSameSize(getImage(workspace), hovered) ? hovered : getImage(workspace);
		}

		return getImage(workspace);
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		Image image = this.getImage(wysiwygEditor.mcreator.getWorkspace());
		g.drawImage(image, cx, cy, wysiwygEditor);
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
