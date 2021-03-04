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
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.List;

public class MusicDisc extends GeneratableElement implements ITabContainedElement {

	public String texture;
	public String name;
	public String description;
	public TabEntry creativeTab;
	public Sound music;
	public List<String> specialInfo;
	public boolean hasGlow;

	public Procedure onRightClickedInAir;
	public Procedure onRightClickedOnBlock;
	public Procedure onCrafted;
	public Procedure onEntityHitWith;
	public Procedure onItemInInventoryTick;
	public Procedure onItemInUseTick;
	public Procedure onStoppedUsing;
	public Procedure onEntitySwing;

	public MusicDisc(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(getModElement().getFolderManager().getItemImageIcon(texture).getImage(), 32);
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

}
