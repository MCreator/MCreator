/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.references.TextureReference;

import javax.swing.*;
import java.awt.*;

public class Sprite extends GUIComponent {

	@TextureReference(TextureType.SCREEN) public String sprite;

	public String spriteDirection;

	public Procedure displayCondition;
	public Procedure spriteDisplayedSize;

	public Sprite(int x, int y, String sprite, String spriteDirection, Procedure displayCondition, Procedure spriteDisplayedSize) {
		super(x, y);
		this.sprite = sprite;
		this.spriteDirection = spriteDirection;
		this.displayCondition = displayCondition;
		this.spriteDisplayedSize = spriteDisplayedSize;
	}

	public Sprite(int x, int y, String sprite, String spriteRenderDirection, Procedure displayCondition, Procedure spriteDisplayedSize, AnchorPoint anchorPoint) {
		this(x, y, sprite, spriteRenderDirection, displayCondition, spriteDisplayedSize);
		this.anchorPoint = anchorPoint;
	}

	@Override public String getName() {
		return "sprite_" + FilenameUtilsPatched.removeExtension(sprite);
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		java.awt.Image actualImage = this.getImage(wysiwygEditor.mcreator.getWorkspace());
		int cw = actualImage.getWidth(null);
		int ch = actualImage.getHeight(null);

		g.drawImage(actualImage, cx, cy, cw, ch, wysiwygEditor);
	}

	@Override public int getWidth(Workspace workspace) {
		return getImage(workspace).getWidth(null);
	}

	@Override public int getHeight(Workspace workspace) {
		return getImage(workspace).getHeight(null);
	}

	public java.awt.Image getImage(Workspace workspace) {
		return new ImageIcon(workspace.getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(sprite), TextureType.SCREEN)
				.getAbsolutePath()).getImage();
	}

	@Override public int getWeight() {
		return 45;
	}
}
