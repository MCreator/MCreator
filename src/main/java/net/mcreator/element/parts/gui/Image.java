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

package net.mcreator.element.parts.gui;

import net.mcreator.element.parts.Procedure;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;

public class Image extends GUIComponent {

	public String image;
	public boolean use1Xscale;

	public Procedure displayCondition;

	public Image(String name, int x, int y, String image, boolean use1Xscale, Procedure displayCondition) {
		super(name, x, y);
		this.image = image;
		this.use1Xscale = use1Xscale;
		this.displayCondition = displayCondition;
	}

	@Override public int getWidth(Workspace workspace) {
		if (use1Xscale)
			return getImage(workspace).getWidth(null) / 2;
		else
			return getImage(workspace).getWidth(null);
	}

	@Override public int getHeight(Workspace workspace) {
		if (use1Xscale)
			return getImage(workspace).getHeight(null) / 2;
		else
			return getImage(workspace).getHeight(null);
	}

	public java.awt.Image getImage(Workspace workspace) {
		return new ImageIcon(workspace.getFolderManager().getOtherTextureFile(FilenameUtils.removeExtension(image))
				.getAbsolutePath()).getImage();
	}

	@Override public int getWeight() {
		return 3;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		java.awt.Image actualImage = this.getImage(wysiwygEditor.mcreator.getWorkspace());
		int cw, ch;
		if (this.use1Xscale) {
			cw = actualImage.getWidth(null) / 2;
			ch = actualImage.getHeight(null) / 2;
		} else {
			cw = actualImage.getWidth(null);
			ch = actualImage.getHeight(null);
		}
		g.drawImage(actualImage, cx, cy, cw, ch, wysiwygEditor);
	}

}
