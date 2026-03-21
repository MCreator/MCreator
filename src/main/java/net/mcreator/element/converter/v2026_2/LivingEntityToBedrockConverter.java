/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2026_2;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.element.types.bedrock.BEEntity;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import java.util.Arrays;

public class LivingEntityToBedrockConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		LivingEntity entity = (LivingEntity) input;

		if (workspace.getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON) {
			BEEntity beentity = new BEEntity(new ModElement(workspace, entity.getModElement().getName(), ModElementType.BEENTITY));
			beentity.entityName = entity.mobName;
			beentity.modelName = convertModelName(entity.mobModelName);
			beentity.modelTexture = entity.mobModelTexture;
			beentity.collisionBoxHeight = entity.modelHeight;
			beentity.collisionBoxWidth = entity.modelWidth;
			beentity.isSummonable = true;
			beentity.xpAmountOnDeath = entity.xpAmount;
			beentity.entityDrop = entity.mobDrop;
			beentity.healthValue = entity.health;
			beentity.attackDamage = entity.attackStrength;
			beentity.speedValue = entity.movementSpeed;
			beentity.canFly = entity.flyingMob;
			beentity.flyingSpeedValue = entity.movementSpeed;
			beentity.followRangeValue = entity.trackingRange;
			beentity.isImmuneToFire = entity.immuneToFire;
			beentity.isPushable = !entity.disableCollisions;
			beentity.isPushableByPiston = !entity.disableCollisions;
			beentity.generateEntity = entity.spawnThisMob;
			beentity.populationControl = entity.mobSpawningType;
			beentity.spawningProbability = entity.spawningProbability;
			beentity.minHerdSize = entity.minNumberOfMobsPerGroup;
			beentity.maxHerdSize = entity.maxNumberOfMobsPerGroup;
			beentity.hasSpawnEgg = entity.hasSpawnEgg;
			beentity.spawnEggBaseColor = entity.spawnEggBaseColor;
			beentity.spawnEggDotColor = entity.spawnEggDotColor;
			beentity.aixml = entity.aixml;
			beentity.waterEntity = entity.waterMob;
			beentity.entityBehaviourType = entity.mobBehaviourType.replace("Raider", "Mob");
			beentity.isImmuneToDrowning = entity.immuneToDrowning;
			beentity.isImmuneToFallDamage = entity.immuneToFallDamage;

			return beentity;
		}
		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 86;
	}

	private String convertModelName(String old) {
		String name = old;
		String[] unsupported = new String[] {"Cod", "Ocelot", "Piglin", "Salmon", "Witch"};
		if (Arrays.asList(unsupported).contains(name)) {
			name = "Biped";
		}
		return name;
	}
}
