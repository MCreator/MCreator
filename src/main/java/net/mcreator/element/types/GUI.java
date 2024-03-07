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
import net.mcreator.element.parts.GridSettings;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.ImageButton;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.IGUI;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@SuppressWarnings("unused") public class GUI extends GeneratableElement implements IGUI {

	public int type;
	public int width, height;

	public int inventoryOffsetX, inventoryOffsetY;

	public boolean renderBgLayer;
	public boolean doesPauseGame;

	@ModElementReference @TextureReference(TextureType.SCREEN) public List<GUIComponent> components;

	public Procedure onOpen;
	public Procedure onTick;
	public Procedure onClosed;

	public GridSettings gridSettings;

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
		this.gridSettings = new GridSettings();
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

	public boolean hasButtonEvents() {
		for (GUIComponent component : components) {
			if (component instanceof Button button) {
				if (button.onClick != null && button.onClick.getName() != null)
					return true;
			} else if (component instanceof ImageButton imageButton) {
				if (imageButton.onClick != null && imageButton.onClick.getName() != null)
					return true;
			}
		}
		return false;
	}

	public boolean hasSlotEvents() {
		for (GUIComponent component : components)
			if (component instanceof Slot)
				if ((((Slot) component).onSlotChanged != null && ((Slot) component).onSlotChanged.getName() != null)
						|| (((Slot) component).onTakenFromSlot != null
						&& ((Slot) component).onTakenFromSlot.getName() != null) || (
						((Slot) component).onStackTransfer != null
								&& ((Slot) component).onStackTransfer.getName() != null))
					return true;
		return false;
	}

	public int getInventorySlotsX() {
		return (int) Math.ceil((width - 176) / 2.0) + inventoryOffsetX;
	}

	public int getInventorySlotsY() {
		return (int) Math.floor((height - 166) / 2.0) + inventoryOffsetY;
	}

	@Override public void finalizeModElementGeneration() {
		if (renderBgLayer) {
			File guiTextureFile = getModElement().getFolderManager()
					.getTextureFile(getModElement().getRegistryName(), TextureType.SCREEN);
			if (type == 0) {
				FileIO.writeImageToPNGFile(MinecraftImageGenerator.generateBackground(width, height), guiTextureFile);
			} else if (type == 1) {
				BufferedImage resizedImage = MinecraftImageGenerator.generateBackground(width, height);
				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(MinecraftImageGenerator.generateInventorySlots(), getInventorySlotsX(),
						getInventorySlotsY(), 176, 166, null);
				for (GUIComponent component : components) {
					if (component instanceof Slot) {
						if (((Slot) component).color == null)
							g.drawImage(MinecraftImageGenerator.generateItemSlot(), component.gx(width),
									component.gy(height), null);
						else
							g.drawImage(ImageUtils.colorize(new ImageIcon(MinecraftImageGenerator.generateItemSlot()),
											((Slot) component).color, true).getImage(), component.gx(width),
									component.gy(height), null);
					}
				}
				g.dispose();

				FileIO.writeImageToPNGFile(resizedImage, guiTextureFile);
			}
		}

		// Create the texture atlas for image buttons that will be used by Minecraft (needed for <= 1.20.1)
		components.stream().filter(c -> c instanceof ImageButton).map(c -> (ImageButton) c).forEach(imageButton -> {
			Image normal = imageButton.getImage(getModElement().getWorkspace());
			Image hovered = imageButton.getHoveredImage(getModElement().getWorkspace());
			FileIO.writeImageToPNGFile(
					ImageUtils.mergeTwoImages(normal, hovered, normal.getWidth(null), normal.getHeight(null) * 2, 0, 0,
							0, normal.getHeight(null)), getModElement().getWorkspace().getFolderManager()
							.getTextureFile("atlas/" + imageButton.getName(), TextureType.SCREEN));
		});
	}

	@Override public List<GUIComponent> getComponents() {
		return components;
	}

}
