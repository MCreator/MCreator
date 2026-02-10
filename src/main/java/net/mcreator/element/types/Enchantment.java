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
import net.mcreator.blockly.datapack.BlocklyToEnchantmentEffects;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class Enchantment extends GeneratableElement {

	public static final String XML_BASE = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"enchantment_effects_start\" deletable=\"false\" x=\"40\" y=\"80\"></block></xml>";

	public String name;

	public String supportedSlots;

	public int weight;
	public int anvilCost;

	public int maxLevel;

	public int damageModifier;

	@ModElementReference public List<net.mcreator.element.parts.Enchantment> incompatibleEnchantments;

	@ModElementReference public List<MItemBlock> supportedItems;

	public boolean isTreasureEnchantment;
	public boolean isCurse;
	public boolean canGenerateInLootTables;
	public boolean canVillagerTrade;

	@BlocklyXML("enchantmenteffects") public String effectsxml;

	private Enchantment() {
		this(null);
	}

	public Enchantment(ModElement element) {
		super(element);

		canGenerateInLootTables = true;
		canVillagerTrade = true;

		supportedSlots = "any";

		incompatibleEnchantments = new ArrayList<>();
		supportedItems = new ArrayList<>();

		effectsxml = XML_BASE;
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.ENCHANTMENT_EFFECTS).getDefinedBlocks(),
					getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.ENCHANTMENT_EFFECTS),
					this.getModElement().getGenerator()
							.getTemplateGeneratorFromName(BlocklyEditorType.ENCHANTMENT_EFFECTS.registryName()),
					additionalData).setTemplateExtension("json");

			// load Blockly2EnchantmentEffects with custom generators loaded
			var blocklyToEnchantmentEffects = new BlocklyToEnchantmentEffects(this.getModElement().getWorkspace(),
					this.getModElement(), this.effectsxml, this.getModElement().getGenerator()
					.getTemplateGeneratorFromName(BlocklyEditorType.ENCHANTMENT_EFFECTS.registryName()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
					new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));

			String effectCode = blocklyToEnchantmentEffects.getGeneratedCode();

			additionalData.put("effectcode", effectCode);
			additionalData.put("effectblocks", blocklyToEnchantmentEffects.getUsedBlocks());
		};
	}

}
