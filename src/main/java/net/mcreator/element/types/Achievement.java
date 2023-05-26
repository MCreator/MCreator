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
import net.mcreator.element.types.interfaces.IOtherModElementsDependent;
import net.mcreator.element.types.interfaces.IResourcesDependent;
import net.mcreator.element.types.interfaces.IXMLProvider;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.*;

@SuppressWarnings("unused") public class Achievement extends GeneratableElement
		implements IOtherModElementsDependent, IResourcesDependent, IXMLProvider {

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
			if (triggerCode.equals(""))
				triggerCode = "{\"trigger\": \"minecraft:impossible\"}";

			additionalData.put("triggercode", triggerCode);
		};
	}

	@Override public Collection<String> getUsedElementNames() {
		List<String> elements = new ArrayList<>();
		if (rewardFunction != null && !rewardFunction.equals("No function"))
			elements.add("CUSTOM:" + rewardFunction);
		rewardLoot.forEach(e -> elements.add("CUSTOM:" + e));
		rewardRecipes.forEach(e -> elements.add("CUSTOM:" + e));
		return elements;
	}

	@Override public Collection<? extends MappableElement> getUsedElementMappings() {
		if (disableDisplay)
			return Collections.emptyList();
		List<MappableElement> elements = new ArrayList<>();
		elements.add(achievementIcon);
		if (!parent.getUnmappedValue().equals("No parent: root"))
			elements.add(parent);
		return elements;
	}

	@Override public Collection<String> getTextures(TextureType type) {
		return type == TextureType.SCREEN ? Collections.singletonList(background) : Collections.emptyList();
	}

	@Override public String getXML() {
		return triggerxml;
	}
}