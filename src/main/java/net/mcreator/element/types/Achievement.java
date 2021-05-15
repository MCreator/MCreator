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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.datapack.BlocklyToJSONTrigger;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.AchievementEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.List;

@SuppressWarnings("unused") public class Achievement extends GeneratableElement {

	public String achievementName;
	public String achievementDescription;

	public MItemBlock achievementIcon;

	public String background;

	public boolean disableDisplay;

	public boolean showPopup;
	public boolean announceToChat;
	public boolean hideIfNotCompleted;

	public List<String> rewardLoot;
	public List<String> rewardRecipes;
	public String rewardFunction;
	public int rewardXP;

	public String achievementType;
	public AchievementEntry parent;

	public String triggerxml;

	public Achievement(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview
				.generateAchievementPreviewPicture(getModElement().getWorkspace(), achievementIcon, achievementName);
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getJSONTriggerLoader().getDefinedBlocks(),
					this.getModElement().getGenerator().getJSONTriggerGenerator(), additionalData)
					.setTemplateExtension("json");

			// load blocklytojava with custom generators loaded
			BlocklyToJSONTrigger blocklyToJSONTrigger = new BlocklyToJSONTrigger(this.getModElement().getWorkspace(),
					this.triggerxml, this.getModElement().getGenerator().getJSONTriggerGenerator(),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));

			String triggerCode = blocklyToJSONTrigger.getGeneratedCode();
			if (triggerCode.equals(""))
				triggerCode = "{\"trigger\": \"minecraft:impossible\"}";

			additionalData.put("triggercode", triggerCode);
		};
	}

}