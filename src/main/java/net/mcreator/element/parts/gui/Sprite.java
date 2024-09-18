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

import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.references.TextureReference;

import javax.swing.*;
import java.awt.*;

public class Sprite extends GUIComponent {

	@TextureReference(TextureType.SCREEN) public String sprite;

	public int spritesCount;

	public Procedure displayCondition;
	public NumberProcedure spriteIndex;

	public Sprite(int x, int y, String sprite, int spritesCount, Procedure displayCondition, NumberProcedure spriteIndex) {
		super(x, y);
		this.sprite = sprite;
		this.spritesCount = spritesCount;
		this.displayCondition = displayCondition;
		this.spriteIndex = spriteIndex;
	}

	public Sprite(int x, int y, String sprite, int spritesCount, Procedure displayCondition, NumberProcedure spriteIndex, AnchorPoint anchorPoint) {
		this(x, y, sprite, spritesCount, displayCondition, spriteIndex);
		this.anchorPoint = anchorPoint;
	}

	@Override public String getName() {
		return "sprite_" + FilenameUtilsPatched.removeExtension(sprite);
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		java.awt.Image actualImage = this.getImage(wysiwygEditor.mcreator.getWorkspace());

		Workspace workspace = wysiwygEditor.mcreator.getWorkspace();
		int width = this.getWidth(workspace);
		int height = this.getHeight(workspace);
		Rectangle r;

		if (spriteIndex.getName() == null) {
			int index = spriteIndex.getFixedValue().intValue();
			if (actualImage.getWidth(null) > actualImage.getHeight(null)) {
				r = new Rectangle(width * index, 0, width, height);
			} else {
				r = new Rectangle(0, height * index, width, height);
			}
		} else {
			r = new Rectangle(0, 0, width, height);
		}

		java.awt.Image sprite = ImageUtils.crop(ImageUtils.toBufferedImage(actualImage), r);

		int cw = sprite.getWidth(null);
		int ch = sprite.getHeight(null);

		g.drawImage(sprite, cx, cy, cw, ch, wysiwygEditor);
	}

	@SuppressWarnings("unused") public int getTextureWidth(Workspace workspace) {
		return getImage(workspace).getWidth(null); // Return entire texture width
	}

	@SuppressWarnings("unused") public int getTextureHeight(Workspace workspace) {
		return getImage(workspace).getHeight(null); // Return entire texture height
	}

	@Override public int getWidth(Workspace workspace) {
		int width = getImage(workspace).getWidth(null);
		int height = getImage(workspace).getHeight(null);

		return width > height ? width / spritesCount : width;
	}

	@Override public int getHeight(Workspace workspace) {
		int width = getImage(workspace).getWidth(null);
		int height = getImage(workspace).getHeight(null);

		return width < height ? height / spritesCount : height;
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
