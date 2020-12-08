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
import net.mcreator.element.ITabContainedElement;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.*;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@SuppressWarnings("unused") public class Dimension extends GeneratableElement implements ITabContainedElement {

	public List<BiomeEntry> biomesInDimension;

	public String worldGenType;

	public MItemBlock mainFillerBlock;//
	public MItemBlock fluidBlock;//

	public Color airColor;//
	public boolean canRespawnHere;//
	public boolean hasFog;//
	public boolean hasWeather;//
	public boolean isDark;//
	public boolean doesWaterVaporize;//

	public String sleepResult;//
	public boolean hasSkyLight;//
	public boolean imitateOverworldBehaviour;//

	public Procedure onPlayerEntersDimension;//
	public Procedure onPlayerLeavesDimension;//

	public MItemBlock portalFrame;
	public Particle portalParticles;
	public double portalLuminance;
	public Sound portalSound;
	public String igniterName;
	public TabEntry igniterTab;
	public String texture;
	public String portalTexture;
	public boolean enablePortal;
	public Procedure portalMakeCondition;
	public Procedure portalUseCondition;
	public Procedure whenPortaTriggerlUsed;
	public Procedure onPortalTickUpdate;

	private Dimension() {
		this(null);
	}

	public Dimension(ModElement element) {
		super(element);

		// DEFAULT VALUES
		this.hasWeather = true;
		this.enablePortal = true;
		this.sleepResult = "ALLOW";
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateDimensionPreviewPicture(getModElement().getWorkspace(),
				getModElement().getWorkspace().getFolderManager().getBlockTextureFile(portalTexture),
				getModElement().getWorkspace().getFolderManager().getItemTextureFile(texture), portalFrame);
	}

	@Override public TabEntry getCreativeTab() {
		return igniterTab;
	}

}
