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

import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IMCItemProvider;
import net.mcreator.element.types.interfaces.IPOIProvider;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

@SuppressWarnings("unused") public class Dimension extends GeneratableElement
		implements ICommonType, ITabContainedElement, IMCItemProvider, IPOIProvider {

	@ModElementReference public List<BiomeEntry> biomesInDimension;

	public String worldGenType;

	public MItemBlock mainFillerBlock;
	public MItemBlock fluidBlock;

	public Color airColor;
	public boolean canRespawnHere;
	public boolean hasFog;
	public boolean isDark;
	public boolean doesWaterVaporize;

	public String sleepResult;
	public boolean hasSkyLight;
	public boolean imitateOverworldBehaviour;

	public Procedure onPlayerEntersDimension;
	public Procedure onPlayerLeavesDimension;

	public MItemBlock portalFrame;
	public Particle portalParticles;
	public int portalLuminance;
	public Sound portalSound;
	public boolean enableIgniter;
	public String igniterName;
	public String igniterRarity;
	public StringListProcedure specialInformation;
	public TabEntry igniterTab;
	@TextureReference(TextureType.ITEM) public String texture;
	@TextureReference(TextureType.BLOCK) public String portalTexture;
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
		this.enablePortal = true;
		this.enableIgniter = true;
		this.igniterRarity = "COMMON";
		this.sleepResult = "ALLOW";
	}

	public boolean hasIgniter() {
		// igniter needs portal and igniter enabled
		return enablePortal && enableIgniter;
	}

	public Set<String> getWorldgenBlocks() {
		Set<String> retval = new HashSet<>();
		retval.add(mainFillerBlock.getUnmappedValue());
		for (BiomeEntry biomeEntry : biomesInDimension) {
			if (biomeEntry.getUnmappedValue().startsWith("CUSTOM:")) {
				ModElement biomeElement = getModElement().getWorkspace()
						.getModElementByName(biomeEntry.getUnmappedValue().replace("CUSTOM:", ""));
				if (biomeElement != null) {
					GeneratableElement generatableElement = biomeElement.getGeneratableElement();
					if (generatableElement instanceof Biome biome) {
						retval.add(biome.groundBlock.getUnmappedValue());
						retval.add(biome.undergroundBlock.getUnmappedValue());
					}
				}
			}
		}
		return retval;
	}

	@Override public BufferedImage generateModElementPicture() {
		return this.enablePortal ?
				MinecraftImageGenerator.Preview.generateDimensionPreviewPicture(getModElement().getWorkspace(),
						getModElement().getFolderManager().getTextureFile(portalTexture, TextureType.BLOCK),
						getModElement().getFolderManager().getTextureFile(texture, TextureType.ITEM), portalFrame,
						this.hasIgniter()) :
				null;
	}

	@Override public TabEntry getCreativeTab() {
		return igniterTab;
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		List<BaseType> baseTypes = new ArrayList<>();
		if (enablePortal)
			baseTypes.add(BaseType.BLOCK);
		if (this.hasIgniter())
			baseTypes.add(BaseType.ITEM);
		return baseTypes;
	}

	@Override public List<MCItem> providedMCItems() {
		ArrayList<MCItem> retval = new ArrayList<>();
		if (this.enablePortal)
			retval.add(new MCItem.Custom(this.getModElement(), "portal", "block", "Portal block"));
		if (this.hasIgniter())
			retval.add(new MCItem.Custom(this.getModElement(), null, "item", "Portal igniter"));
		return retval;
	}

	@Override public List<MCItem> getCreativeTabItems() {
		if (this.hasIgniter())
			return List.of(new MCItem.Custom(this.getModElement(), null, "item", "Portal igniter"));
		return Collections.emptyList();
	}

	@Override public ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		if ("portal".equals(suffix))
			return workspace.getFolderManager().getTextureImageIcon(portalTexture, TextureType.BLOCK);
		else
			return workspace.getFolderManager().getTextureImageIcon(texture, TextureType.ITEM);
	}

	@Override public List<MItemBlock> poiBlocks() {
		return List.of(new MItemBlock(this.getModElement().getWorkspace(),
				"CUSTOM:" + this.getModElement().getName() + ".portal"));
	}

}
