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
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;

public class ImageButton extends GUIComponent {

	public String image;
	public int width, height;
	public Procedure onClick;
	public Procedure displayCondition;

	public ImageButton(int x, int y, int width, int height, String image, Procedure onClick,
			Procedure displayCondition) {
		super(x, y);
		this.image = image;
		this.width = width;
		this.height = height;
		this.onClick = onClick;
		this.displayCondition = displayCondition;
	}

	@Override public String getName() {
		return "image_button_" + FilenameUtilsPatched.removeExtension(image);
	}

	public java.awt.Image getImage(Workspace workspace) {
		return new ImageIcon(workspace.getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(image), TextureType.SCREEN)
				.getAbsolutePath()).getImage();
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		Image image = this.getImage(wysiwygEditor.mcreator.getWorkspace());
		g.drawImage(MinecraftImageGenerator.generateButton(this.width, this.height), cx, cy, this.width, this.height,
				wysiwygEditor);
		g.drawImage(image, cx, cy, width, height, wysiwygEditor);

	}

	@Override public int getWidth(Workspace workspace) {
		return width;
	}

	@Override public int getHeight(Workspace workspace) {
		return height;
	}

	@Override public int getWeight() {
		return 6;
	}
}
