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

package net.mcreator.element.types.bedrock;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.blockly.javascript.BlocklyToJavaScript;
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class BEScript extends GeneratableElement {

	public static final String XML_BASE = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"script_trigger\" deletable=\"false\" x=\"40\" y=\"40\"><field name=\"trigger\">no_ext_trigger</field></block></xml>";

	@BlocklyXML("scripts") public String scriptxml;

	private BEScript() {
		this(null);
	}

	public BEScript(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateProcedurePreviewPicture(scriptxml, List.of());
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyToJavaScript blocklyToJavaScript = getBlocklyToJavaScript(additionalData);

			List<ExternalTrigger> externalTriggers = BlocklyLoader.INSTANCE.getExternalTriggerLoader(
					BlocklyEditorType.SCRIPT).getExternalTriggers();
			ExternalTrigger trigger = null;
			for (ExternalTrigger externalTrigger : externalTriggers) {
				if (externalTrigger.getID().equals(blocklyToJavaScript.getExternalTrigger())) {
					trigger = externalTrigger;
					break;
				}
			}

			additionalData.put("scriptcode", blocklyToJavaScript.getGeneratedCode());
			additionalData.put("scriptblocks", blocklyToJavaScript.getUsedBlocks());
			additionalData.put("extra_templates_code", blocklyToJavaScript.getExtraTemplatesCode());
			additionalData.put("dependencies", blocklyToJavaScript.getDependencies());

			String triggerCode = "";
			if (trigger != null) {
				TemplateGenerator templateGenerator = getModElement().getGenerator()
						.getTemplateGeneratorFromName("jstriggers");
				triggerCode = templateGenerator.generateFromTemplate(trigger.getID() + ".js.ftl", additionalData);
			}
			additionalData.put("trigger_code", triggerCode);

			if (trigger != null)
				this.getModElement().putMetadata("type", trigger.getType());
		};
	}

	public BlocklyToJavaScript getBlocklyToJavaScript(Map<String, Object> additionalData)
			throws TemplateGeneratorException {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
				BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.SCRIPT).getDefinedBlocks(),
				getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.SCRIPT),
				getModElement().getGenerator().getTemplateGeneratorFromName(BlocklyEditorType.SCRIPT.registryName()),
				additionalData).setTemplateExtension("js");

		// load BlocklyToJavaScript with custom generators loaded
		return new BlocklyToJavaScript(this.getModElement().getWorkspace(), this.getModElement(), this.scriptxml,
				getModElement().getGenerator().getTemplateGeneratorFromName(BlocklyEditorType.SCRIPT.registryName()),
				new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
				new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));
	}

}

