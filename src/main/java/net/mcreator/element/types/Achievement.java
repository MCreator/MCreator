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
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.blockly.datapack.BlocklyToJSONTrigger;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.AchievementEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class Achievement extends GeneratableElement {

	public String achievementName;
	public String achievementDescription;

	public MItemBlock achievementIcon;

	@TextureReference(value = TextureType.SCREEN, defaultValues = "Default") public String background;

	public boolean disableDisplay;

	public boolean showPopup;
	public boolean announceToChat;
	public boolean hideIfNotCompleted;

	@ModElementReference public List<String> rewardLoot;
	@ModElementReference public List<String> rewardRecipes;
	@ModElementReference(defaultValues = "No function") public String rewardFunction;
	public int rewardXP;

	public String achievementType;
	public AchievementEntry parent;

	@BlocklyXML("jsontriggers") public String triggerxml;

	private Achievement() {
		this(null);
	}

	public Achievement(ModElement element) {
		super(element);

		rewardLoot = new ArrayList<>();
		rewardRecipes = new ArrayList<>();
	}

	public boolean hasRewards() {
		return rewardXP > 0 || (rewardLoot != null && !rewardLoot.isEmpty()) || (rewardRecipes != null
				&& !rewardRecipes.isEmpty()) || (rewardFunction != null && !rewardFunction.equals("No function"));
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateAchievementPreviewPicture(getModElement().getWorkspace(),
				achievementIcon, achievementName);
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.JSON_TRIGGER).getDefinedBlocks(),
					getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.JSON_TRIGGER),
					this.getModElement().getGenerator()
							.getTemplateGeneratorFromName(BlocklyEditorType.JSON_TRIGGER.registryName()),
					additionalData).setTemplateExtension("json");

			// load blocklytojsontrigger with custom generators loaded
			BlocklyToJSONTrigger blocklyToJSONTrigger = new BlocklyToJSONTrigger(this.getModElement().getWorkspace(),
					this.getModElement(), this.triggerxml, this.getModElement().getGenerator()
					.getTemplateGeneratorFromName(BlocklyEditorType.JSON_TRIGGER.registryName()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));

			String triggerCode = blocklyToJSONTrigger.getGeneratedCode();
			if (triggerCode.isEmpty())
				triggerCode = "\"%s\": {\"trigger\": \"minecraft:impossible\"}".formatted(
						getModElement().getRegistryName());

			additionalData.put("triggercode", triggerCode);
			additionalData.put("triggerblocks", blocklyToJSONTrigger.getUsedBlocks());
		};
	}

}