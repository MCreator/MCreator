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
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Item;
import net.mcreator.element.types.Projectile;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RangedItemToProjectileAndItemConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(RangedItemToProjectileAndItemConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			JsonObject rangedItem = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			Projectile projectile = new Projectile(
					new ModElement(workspace, input.getModElement().getName() + "Projectile",
							ModElementType.PROJECTILE));

			if (rangedItem.get("bulletItemTexture") != null)
				projectile.projectileItem = new MItemBlock(workspace,
						rangedItem.get("bulletItemTexture").getAsJsonObject().get("value").getAsString());
			else if (rangedItem.get("ammoItem") != null && !rangedItem.get("ammoItem").getAsJsonObject().get("value")
					.getAsString().isEmpty())
				projectile.projectileItem = new MItemBlock(workspace,
						rangedItem.get("ammoItem").getAsJsonObject().get("value").getAsString());
			else
				projectile.projectileItem = new MItemBlock(workspace,
						"Items.ARROW"); // if for some reason a ranged item has no item defined, we need to provide one.
			projectile.showParticles = rangedItem.get("bulletParticles").getAsBoolean();
			if (rangedItem.get("actionSound") != null)
				projectile.actionSound = new Sound(workspace,
						rangedItem.get("actionSound").getAsJsonObject().get("value").getAsString());
			projectile.igniteFire = rangedItem.get("bulletIgnitesFire").getAsBoolean();
			projectile.power = rangedItem.get("bulletPower").getAsDouble();
			projectile.damage = rangedItem.get("bulletDamage").getAsDouble();
			projectile.knockback = rangedItem.get("bulletKnockback").getAsInt();
			if (rangedItem.get("bulletModel") != null)
				projectile.entityModel = rangedItem.get("bulletModel").getAsString();
			if (rangedItem.get("customBulletModelTexture") != null) {
				// We need to include the old fv31 texture converter of ranged items as the mod element has been deleted.
				if (jsonElementInput.getAsJsonObject().get("_fv").getAsInt() < 31 && !rangedItem.get("customBulletModelTexture")
						.getAsString().isEmpty()) {
					FileIO.copyFile(workspace.getFolderManager().getTextureFile(FilenameUtilsPatched.removeExtension(
									rangedItem.get("customBulletModelTexture").getAsString()), TextureType.OTHER),
							workspace.getFolderManager().getTextureFile(FilenameUtilsPatched.removeExtension(
									rangedItem.get("customBulletModelTexture").getAsString()), TextureType.ENTITY));
				}
				projectile.customModelTexture = rangedItem.get("customBulletModelTexture").getAsString();
			}

			if (rangedItem.get("onBulletHitsBlock") != null)
				projectile.onHitsBlock = new Procedure(
						rangedItem.get("onBulletHitsBlock").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("onBulletHitsEntity") != null)
				projectile.onHitsEntity = new Procedure(
						rangedItem.get("onBulletHitsEntity").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("onBulletHitsPlayer") != null)
				projectile.onHitsPlayer = new Procedure(
						rangedItem.get("onBulletHitsPlayer").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("onBulletFlyingTick") != null)
				projectile.onFlyingTick = new Procedure(
						rangedItem.get("onBulletFlyingTick").getAsJsonObject().get("name").getAsString());

			projectile.getModElement()
					.setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
			workspace.getModElementManager().storeModElementPicture(projectile);
			workspace.addModElement(projectile.getModElement());
			workspace.getGenerator().generateElement(projectile);
			workspace.getModElementManager().storeModElement(projectile);

			Item item = new Item(new ModElement(workspace, input.getModElement().getName(), ModElementType.ITEM));
			item.name = rangedItem.get("name").getAsString();
			item.texture = rangedItem.get("texture").getAsString();
			item.renderType = rangedItem.get("renderType").getAsInt();
			if (rangedItem.get("customModelName") != null)
				item.customModelName = rangedItem.get("customModelName").getAsString();
			item.creativeTab = new TabEntry(workspace,
					rangedItem.get("creativeTab").getAsJsonObject().get("value").getAsString());
			List<String> specialInfo = new ArrayList<>();
			if (rangedItem.get("specialInfo") != null)
				rangedItem.getAsJsonArray("specialInfo").iterator()
						.forEachRemaining(element -> specialInfo.add(element.getAsString()));
			item.specialInfo = specialInfo;
			item.stackSize = rangedItem.get("stackSize").getAsInt();
			item.animation = rangedItem.get("animation").getAsString();
			item.hasGlow = rangedItem.get("hasGlow").getAsBoolean();
			if (rangedItem.get("glowCondition") != null)
				item.glowCondition = new Procedure(
						rangedItem.get("glowCondition").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("onEntitySwing") != null)
				item.onEntitySwing = new Procedure(
						rangedItem.get("onEntitySwing").getAsJsonObject().get("name").getAsString());
			item.enableRanged = true;
			item.shootConstantly = rangedItem.get("shootConstantly").getAsBoolean();
			item.projectile = new ProjectileEntry(workspace, "CUSTOM:" + projectile.getModElement().getName());
			if (rangedItem.get("useCondition") != null)
				item.useCondition = new Procedure(
						rangedItem.get("useCondition").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("onRangedItemUsed") != null)
				item.useCondition = new Procedure(
						rangedItem.get("onRangedItemUsed").getAsJsonObject().get("name").getAsString());
			item.enableMeleeDamage = rangedItem.get("enableMeleeDamage").getAsBoolean();
			item.damageVsEntity = rangedItem.get("damageVsEntity").getAsDouble();

			return item;
		} catch (Exception e) {
			LOG.warn("Failed to update ranged item to new format", e);
			return null;
		}
	}

	@Override public int getVersionConvertingTo() {
		return 39;
	}
}
