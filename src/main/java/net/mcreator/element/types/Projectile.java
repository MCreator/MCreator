/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IEntityWithModel;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

public class Projectile extends GeneratableElement implements IEntityWithModel, ICommonType {

	public MItemBlock projectileItem;
	public boolean showParticles;
	public Sound actionSound;
	public boolean igniteFire;
	public double power;
	public double damage;
	public int knockback;
	public String entityModel;
	@TextureReference(TextureType.ENTITY) public String customModelTexture;

	public Procedure onHitsBlock;
	public Procedure onHitsPlayer;
	public Procedure onHitsEntity;
	public Procedure onFlyingTick;

	public Projectile(ModElement element) {
		super(element);
	}

	@Override public Model getEntityModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (!entityModel.equals("Default"))
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), entityModel, modelType);
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.toBufferedImage(
				MCItem.getBlockIconBasedOnName(getModElement().getWorkspace(), projectileItem.getUnmappedValue())
						.getImage());
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		return Collections.singletonList(BaseType.ENTITY);
	}

}
