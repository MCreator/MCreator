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

package net.mcreator.element.converter.fv39;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.ProjectileEntry;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Projectile;
import net.mcreator.element.types.RangedItem;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RangedItemToProjectileConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(RangedItemToProjectileConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		RangedItem rangedItem = (RangedItem) input;
		try {
			Projectile projectile = new Projectile(
					new ModElement(workspace, input.getModElement().getName() + "Projectile", ModElementType.PROJECTILE));

			JsonObject rangedItemJSON = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			if (rangedItemJSON.get("bulletItemTexture") != null)
				projectile.projectileItem = new MItemBlock(workspace,
						rangedItemJSON.get("bulletItemTexture").getAsJsonObject().get("value").getAsString());
			else
				projectile.projectileItem = new MItemBlock(workspace,
						rangedItemJSON.get("ammoItem").getAsJsonObject().get("value").getAsString()); // in case ancient workspaces don't have the bulletItemTexture parameter
			projectile.showParticles = rangedItemJSON.get("bulletParticles").getAsBoolean();
			if (rangedItemJSON.get("actionSound") != null)
				projectile.actionSound = new Sound(workspace,
						rangedItemJSON.get("actionSound").getAsJsonObject().get("value").getAsString());
			projectile.igniteFire = rangedItemJSON.get("bulletIgnitesFire").getAsBoolean();
			projectile.power = rangedItemJSON.get("bulletPower").getAsDouble();
			projectile.damage = rangedItemJSON.get("bulletDamage").getAsDouble();
			projectile.knockback = rangedItemJSON.get("bulletKnockback").getAsInt();
			if (rangedItemJSON.get("bulletModel") != null)
				projectile.entityModel = rangedItemJSON.get("bulletModel").getAsString();
			if (rangedItemJSON.get("customBulletModelTexture") != null)
				projectile.customModelTexture = rangedItemJSON.get("customBulletModelTexture").getAsString();

			if (rangedItemJSON.get("onBulletHitsBlock") != null)
				projectile.onHitsBlock = new Procedure(
						rangedItemJSON.get("onBulletHitsBlock").getAsJsonObject().get("name").getAsString());
			if (rangedItemJSON.get("onBulletHitsEntity") != null)
				projectile.onHitsEntity = new Procedure(
						rangedItemJSON.get("onBulletHitsEntity").getAsJsonObject().get("name").getAsString());
			if (rangedItemJSON.get("onBulletHitsPlayer") != null)
				projectile.onHitsPlayer = new Procedure(
						rangedItemJSON.get("onBulletHitsPlayer").getAsJsonObject().get("name").getAsString());
			if (rangedItemJSON.get("onBulletFlyingTick") != null)
				projectile.onFlyingTick = new Procedure(
						rangedItemJSON.get("onBulletFlyingTick").getAsJsonObject().get("name").getAsString());

			projectile.getModElement()
					.setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
			workspace.getModElementManager().storeModElementPicture(projectile);
			workspace.addModElement(projectile.getModElement());
			workspace.getGenerator().generateElement(projectile);
			workspace.getModElementManager().storeModElement(projectile);

			rangedItem.projectile = new ProjectileEntry(workspace, "CUSTOM:" + projectile.getModElement().getName());
		} catch (Exception exception) {
			LOG.warn("Failed to update ranged item's projectile to new format", exception);
		}
		return rangedItem;
	}

	@Override public int getVersionConvertingTo() {
		return 39;
	}
}
