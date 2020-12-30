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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@SuppressWarnings("unused") public class GUI extends GeneratableElement {

	public int type;
	public int width, height;

	public int inventoryOffsetX, inventoryOffsetY;

	public boolean renderBgLayer;
	public boolean doesPauseGame;

	public List<GUIComponent> components;

	public Procedure onOpen;
	public Procedure onTick;
	public Procedure onClosed;

	public final transient int W;
	public final transient int H;

	private GUI() {
		this(null);
	}

	public GUI(ModElement element) {
		super(element);

		this.W = WYSIWYG.W;
		this.H = WYSIWYG.H;
		this.renderBgLayer = true;
	}

	public int getMaxSlotID() {
		int currentMax = -1;
		for (GUIComponent component : components) {
			if (component instanceof Slot) {
				int id = ((Slot) component).id;
				if (id > currentMax)
					currentMax = id;
			}
		}
		return currentMax;
	}

	@Override public void finalizeModElementGeneration() {
		File guiTextureFile = getModElement().getFolderManager()
				.getOtherTextureFile(getModElement().getRegistryName());

		if (renderBgLayer) {
			int mx = WYSIWYG.W - width;
			int my = WYSIWYG.H - height;

			if (type == 0) {
				FileIO.writeImageToPNGFile(MinecraftImageGenerator.generateBackground(width, height), guiTextureFile);
			} else if (type == 1) {
				BufferedImage resizedImage = MinecraftImageGenerator.generateBackground(width, height);
				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(MinecraftImageGenerator.generateInventorySlots(), (width - 176) / 2 + inventoryOffsetX,
						(height - 166) / 2 + inventoryOffsetY, 176, 166, null);
				for (GUIComponent component : components) {
					if (component instanceof Slot) {
						int elPosX = (int) (component.getX() - mx / 2.0);
						int elPosy = (int) (component.getY() - my / 2.0);
						if (((Slot) component).color == null)
							g.drawImage(MinecraftImageGenerator.generateItemSlot(), elPosX, elPosy, null);
						else
							g.drawImage(ImageUtils.colorize(new ImageIcon(MinecraftImageGenerator.generateItemSlot()),
									((Slot) component).color, true).getImage(), elPosX, elPosy, null);
					}
				}
				g.dispose();

				FileIO.writeImageToPNGFile(resizedImage, guiTextureFile);
			}
		} else {
			guiTextureFile.delete();
		}
	}
}
