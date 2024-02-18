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

package net.mcreator.element.converter.v2023_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.ProjectileEntry;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
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

			Projectile projectile = new Projectile(new ModElement(workspace,
					ConverterUtils.findSuitableModElementName(workspace,
							input.getModElement().getName() + "Projectile"), ModElementType.PROJECTILE));

			if (rangedItem.get("bulletItemTexture") != null && !rangedItem.get("bulletItemTexture").getAsJsonObject()
					.get("value").getAsString().isEmpty())
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
				if (jsonElementInput.getAsJsonObject().get("_fv").getAsInt() < 31 && !rangedItem.get(
						"customBulletModelTexture").getAsString().isEmpty()) {
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

			projectile.getModElement().setParentFolder(
					FolderElement.findFolderByPath(input.getModElement().getWorkspace(),
							input.getModElement().getFolderPath()));
			workspace.getModElementManager().storeModElementPicture(projectile);
			workspace.addModElement(projectile.getModElement());
			workspace.getGenerator().generateElement(projectile);
			workspace.getModElementManager().storeModElement(projectile);

			Item item = new Item(new ModElement(workspace, input.getModElement().getName(), ModElementType.ITEM));
			item.name = rangedItem.get("name").getAsString();
			item.texture = rangedItem.get("texture").getAsString();
			item.renderType = rangedItem.get("renderType").getAsInt();
			if (rangedItem.get("customModelName") != null) {
				item.customModelName = rangedItem.get("customModelName").getAsString();
				if (item.customModelName.equals("Normal") && item.renderType == 0)
					item.customModelName = "Ranged item"; // Convert the ranged items' normal model to items' RI model option
			} else {
				item.customModelName = "Ranged item";
			}
			item.creativeTab = new TabEntry(workspace,
					rangedItem.get("creativeTab").getAsJsonObject().get("value").getAsString());

			List<String> infoFixedValues = new ArrayList<>();
			String infoProcedureName = null;
			if (rangedItem.get("specialInfo") != null) {
				rangedItem.getAsJsonArray("specialInfo").iterator()
						.forEachRemaining(element -> infoFixedValues.add(element.getAsString()));
			} else if (rangedItem.get("specialInformation") != null) {
				if (rangedItem.get("specialInformation").getAsJsonObject().get("fixedValue") != null) {
					rangedItem.get("specialInformation").getAsJsonObject().getAsJsonArray("fixedValue")
							.forEach(element -> infoFixedValues.add(element.getAsString()));
				}

				if (rangedItem.get("specialInformation").getAsJsonObject().get("name") != null)
					infoProcedureName = rangedItem.get("specialInformation").getAsJsonObject().get("name")
							.getAsString();
			}
			item.specialInformation = new StringListProcedure(infoProcedureName, infoFixedValues);

			if (rangedItem.get("animation") != null)
				item.animation = rangedItem.get("animation").getAsString();
			else
				item.animation = "bow";

			if (rangedItem.has("glowCondition")) {
				JsonObject rangedGlow = rangedItem.getAsJsonObject("glowCondition");
				String glowConditionProcedureName = rangedGlow.has("name") ?
						rangedGlow.get("name").getAsString() :
						null;
				boolean value = rangedItem.has("hasGlow") ? rangedItem.get("hasGlow").getAsBoolean() : // Old format
						rangedGlow.get("fixedValue").getAsBoolean(); // New format of 2023.4

				item.glowCondition = new LogicProcedure(glowConditionProcedureName, value);
			} else if (rangedItem.has("hasGlow")) {
				item.glowCondition = new LogicProcedure(null, rangedItem.get("hasGlow").getAsBoolean());
			}

			item.enableRanged = true;
			item.shootConstantly = rangedItem.get("shootConstantly").getAsBoolean();
			item.projectile = new ProjectileEntry(workspace, "CUSTOM:" + projectile.getModElement().getName());
			item.enableMeleeDamage = rangedItem.get("enableMeleeDamage").getAsBoolean();
			item.damageVsEntity = rangedItem.get("damageVsEntity").getAsDouble();
			item.stackSize = rangedItem.get("stackSize").getAsInt();
			item.damageCount = rangedItem.get("usageCount").getAsInt();
			item.useDuration = 72000;

			if (rangedItem.get("onEntitySwing") != null)
				item.onEntitySwing = new Procedure(
						rangedItem.get("onEntitySwing").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("useCondition") != null)
				item.rangedUseCondition = new Procedure(
						rangedItem.get("useCondition").getAsJsonObject().get("name").getAsString());
			if (rangedItem.get("onRangedItemUsed") != null)
				item.onRangedItemUsed = new Procedure(
						rangedItem.get("onRangedItemUsed").getAsJsonObject().get("name").getAsString());

			return item;
		} catch (Exception e) {
			LOG.warn("Failed to update ranged item to new format", e);
			return null;
		}
	}

	@Override public int getVersionConvertingTo() {
		return 52;
	}

}
