/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.types.interfaces.IPOIProvider;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;

import java.io.File;
import java.util.List;

public class VillagerProfession extends GeneratableElement implements IPOIProvider {

	public String displayName;
	public MItemBlock pointOfInterest;
	public Sound actionSound;
	public String hat;
	@TextureReference(TextureType.ENTITY) public String professionTextureFile;
	@TextureReference(TextureType.ENTITY) public String zombifiedProfessionTextureFile;

	public VillagerProfession(ModElement element) {
		super(element);
		this.hat = "None";
	}

	public boolean isHatEnabled() {
		return !"None".equals(this.hat);
	}

	@Override public void finalizeModElementGeneration() {
		File originalTextureFileLocation = getModElement().getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(professionTextureFile), TextureType.ENTITY);
		File newLocation = new File(getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER),
				"entity/villager/profession/" + getModElement().getRegistryName() + ".png");
		FileIO.copyFile(originalTextureFileLocation, newLocation);

		File originalZombifiedTextureFileLocation = getModElement().getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(zombifiedProfessionTextureFile),
						TextureType.ENTITY);
		File newZombifiedLocation = new File(getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER),
				"entity/zombie_villager/profession/" + getModElement().getRegistryName() + ".png");
		FileIO.copyFile(originalZombifiedTextureFileLocation, newZombifiedLocation);
	}

	@Override public List<MItemBlock> poiBlocks() {
		return List.of(pointOfInterest);
	}

}
