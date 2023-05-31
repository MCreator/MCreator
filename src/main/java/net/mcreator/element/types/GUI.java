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
import net.mcreator.element.parts.gui.*;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.Checkbox;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.IGUI;
import net.mcreator.element.types.interfaces.IOtherModElementsDependent;
import net.mcreator.element.types.interfaces.IResourcesDependent;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public class GUI extends GeneratableElement
		implements IGUI, IOtherModElementsDependent, IResourcesDependent {

	public int type;
	public int width, height;

	public int inventoryOffsetX, inventoryOffsetY;

	public boolean renderBgLayer;
	public boolean doesPauseGame;

	public List<GUIComponent> components;

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

	@Override public void finalizeModElementGeneration() {
		if (renderBgLayer) {
			File guiTextureFile = getModElement().getFolderManager()
					.getTextureFile(getModElement().getRegistryName(), TextureType.SCREEN);

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
		}

		// Create the texture atlas for image buttons that will be used by Minecraft
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

	@Override public Collection<? extends MappableElement> getUsedElementMappings() {
		return getComponentsOfType("InputSlot").stream().map(e -> ((InputSlot) e).inputLimit)
				.filter(e -> e != null && !e.isEmpty()).toList();
	}

	@Override public Collection<? extends Procedure> getUsedProcedures() {
		Collection<Procedure> procedures = new ArrayList<>();
		procedures.add(onOpen);
		procedures.add(onTick);
		procedures.add(onClosed);
		getComponentsOfType("EntityModel").forEach(e -> {
			procedures.add(((EntityModel) e).entityModel);
			procedures.add(((EntityModel) e).displayCondition);
		});
		getComponentsOfType("Label").forEach(e -> {
			procedures.add(((Label) e).text);
			procedures.add(((Label) e).displayCondition);
		});
		getComponentsOfType("Checkbox").forEach(e -> procedures.add(((Checkbox) e).isCheckedProcedure));
		getComponentsOfType("ImageButton").forEach(e -> {
			procedures.add(((ImageButton) e).onClick);
			procedures.add(((ImageButton) e).displayCondition);
		});
		getComponentsOfType("Button").forEach(e -> {
			procedures.add(((Button) e).onClick);
			procedures.add(((Button) e).displayCondition);
		});
		getComponentsOfType("Image").forEach(
				e -> procedures.add(((net.mcreator.element.parts.gui.Image) e).displayCondition));
		getComponentsOfType("InputSlot").forEach(e -> {
			procedures.add(((InputSlot) e).disablePickup);
			procedures.add(((InputSlot) e).disablePlacement);
			procedures.add(((InputSlot) e).onSlotChanged);
			procedures.add(((InputSlot) e).onTakenFromSlot);
			procedures.add(((InputSlot) e).onStackTransfer);
		});
		getComponentsOfType("OutputSlot").forEach(e -> {
			procedures.add(((OutputSlot) e).disablePickup);
			procedures.add(((OutputSlot) e).onSlotChanged);
			procedures.add(((OutputSlot) e).onTakenFromSlot);
			procedures.add(((OutputSlot) e).onStackTransfer);
		});
		return filterProcedures(procedures);
	}

	@Override public Collection<String> getTextures(TextureType type) {
		List<String> textures = new ArrayList<>();
		if (type == TextureType.SCREEN) {
			getComponentsOfType("Image").forEach(e -> textures.add(((net.mcreator.element.parts.gui.Image) e).image));
			getComponentsOfType("ImageButton").forEach(e -> {
				textures.add(((ImageButton) e).image);
				if (!((ImageButton) e).hoveredImage.equals(""))
					textures.add(((ImageButton) e).hoveredImage);
			});
		}
		return textures;
	}
}
