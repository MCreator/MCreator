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
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.Locale;

@SuppressWarnings("unused") public class Command extends GeneratableElement {

	public static final String XML_BASE = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"args_start\" deletable=\"false\" x=\"40\" y=\"40\"><next><block type=\"call_procedure\"><field name=\"procedure\"></field></block></next></block></xml>";

	public String commandName;

	public String type;

	public String permissionLevel;

	@BlocklyXML("cmdargs") public String argsxml;

	private Command() {
		this(null);
	}

	public Command(ModElement element) {
		super(element);

		this.type = "STANDARD";
		this.permissionLevel = "4";
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateCommandPreviewPicture(commandName, argsxml);
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.COMMAND_ARG).getDefinedBlocks(),
					getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.COMMAND_ARG),
					this.getModElement().getGenerator()
							.getTemplateGeneratorFromName(BlocklyEditorType.COMMAND_ARG.registryName()),
					additionalData).setTemplateExtension(
					this.getModElement().getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage().name()
							.toLowerCase(Locale.ENGLISH));
			BlocklyToJava blocklyToJava = new BlocklyToJava(this.getModElement().getWorkspace(), this.getModElement(),
					BlocklyEditorType.COMMAND_ARG, this.argsxml, this.getModElement().getGenerator()
					.getTemplateGeneratorFromName(BlocklyEditorType.COMMAND_ARG.registryName()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));

			additionalData.put("argscode", blocklyToJava.getGeneratedCode());
			additionalData.put("argsblocks", blocklyToJava.getUsedBlocks());
		};
	}

}