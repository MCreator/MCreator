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
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.IGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.Dimension;
import java.util.List;

@SuppressWarnings("unused") public class Overlay extends GeneratableElement implements IGUI {

	private static final Logger LOG = LogManager.getLogger(Overlay.class);

	public String priority;
	@ModElementReference @TextureReference(TextureType.SCREEN) public List<GUIComponent> components;

	@TextureReference(TextureType.SCREEN) public String baseTexture;
	public String overlayTarget;

	public Procedure displayCondition;

	public GridSettings gridSettings;

	private Overlay() {
		this(null);
	}

	public Overlay(ModElement element) {
		super(element);

		this.gridSettings = new GridSettings();
		this.overlayTarget = "Ingame";
	}

	public boolean hasTextures() {
		return (this.baseTexture != null && !this.baseTexture.isEmpty()) || !getComponentsOfType("Image").isEmpty();
	}

	public int getBaseTextureWidth() {
		return getBaseTextureSize().width;
	}

	public int getBaseTextureHeight() {
		return getBaseTextureSize().height;
	}

	private Dimension getBaseTextureSize() {
		if (this.baseTexture != null && !this.baseTexture.isEmpty()) {
			try {
				ImageIcon texture = new ImageIcon(getModElement().getFolderManager()
						.getTextureFile(FilenameUtilsPatched.removeExtension(this.baseTexture), TextureType.SCREEN)
						.getAbsolutePath());
				texture.getImage().flush();

				texture = new ImageIcon(getModElement().getFolderManager()
						.getTextureFile(FilenameUtilsPatched.removeExtension(this.baseTexture), TextureType.SCREEN)
						.getAbsolutePath());

				return new Dimension(texture.getIconWidth(), texture.getIconHeight());
			} catch (Exception e) {
				LOG.warn("Failed to determine overlay base image size", e);
			}
		}

		return new Dimension(0, 0);
	}

	@Override public List<GUIComponent> getComponents() {
		return components;
	}

}
