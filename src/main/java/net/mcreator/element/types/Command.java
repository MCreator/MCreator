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

import net.mcreator.blockly.BlocklyToAITasks;
import net.mcreator.blockly.BlocklyToCmdArgs;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.datapack.BlocklyToJSONTrigger;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.Procedure;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.Locale;

@SuppressWarnings("unused") public class Command extends GeneratableElement {

	public String commandName;

	public String permissionLevel;

	public String argsxml;

	public Procedure onCommandExecuted;

	private Command() {
		this(null);
	}

	public Command(ModElement element) {
		super(element);

		this.permissionLevel = "4";
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateCommandPreviewPicture(commandName);
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getCmdArgsBlockLoader().getDefinedBlocks(),
					this.getModElement().getGenerator().getCmdArgsGenerator(), additionalData).setTemplateExtension(
					this.getModElement().getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage().name()
							.toLowerCase(Locale.ENGLISH));
			BlocklyToCmdArgs blocklyToJava = new BlocklyToCmdArgs(this.getModElement().getWorkspace(), this.argsxml,
					this.getModElement().getGenerator().getCmdArgsGenerator(),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));

			additionalData.put("argscode", blocklyToJava.getGeneratedCode());
		};
	}

}