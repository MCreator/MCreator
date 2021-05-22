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
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.Dimension;
import java.util.List;

@SuppressWarnings("unused") public class Overlay extends GeneratableElement {

	private static final Logger LOG = LogManager.getLogger(Overlay.class);

	public String priority;
	public List<GUIComponent> components;

	public String baseTexture;

	public Procedure displayCondition;

	public GridSettings gridSettings;

	private Overlay() {
		this(null);

		this.gridSettings = new GridSettings();
	}

	public Overlay(ModElement element) {
		super(element);
	}

	public int getBaseTextureWidth() {
		return getBaseTextureSize().width;
	}

	public int getBaseTextureHeight() {
		return getBaseTextureSize().height;
	}

	private Dimension getBaseTextureSize() {
		if (this.baseTexture != null && !this.baseTexture.equals("")) {
			try {
				ImageIcon texture = new ImageIcon(getModElement().getFolderManager()
						.getOtherTextureFile(FilenameUtils.removeExtension(this.baseTexture)).getAbsolutePath());
				texture.getImage().flush();

				texture = new ImageIcon(getModElement().getFolderManager()
						.getOtherTextureFile(FilenameUtils.removeExtension(this.baseTexture)).getAbsolutePath());

				return new Dimension(texture.getIconWidth(), texture.getIconHeight());
			} catch (Exception e) {
				LOG.warn("Failed to determine overlay base image size", e);
			}
		}

		return new Dimension(0, 0);
	}

}
