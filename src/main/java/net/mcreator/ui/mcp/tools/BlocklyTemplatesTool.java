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

package net.mcreator.ui.mcp.tools;

import net.mcreator.blockly.BlocklyTemplateIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BlocklyTemplatesTool extends MCreatorMcpTool<BlocklyTemplatesTool.Args> {

	public static class Args {
		public QueryType type;
		@Nullable public String blocklyEditorType;
		@Nullable public String templateName;

		public enum QueryType {
			LIST_TEMPLATES, GET_TEMPLATE
		}
	}

	public BlocklyTemplatesTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "blockly_templates";
	}

	@Override public String getDescription() {
		return """
				Lists Blockly template/example names for blocklyEditorType %s or returns template XML for templateName.\
				Good to discover example block assemblies before building custom Blockly setups.\
				Templates represent block assemblies, but not whole Blockly XML setup with start blocks.
				""".formatted(
				getTemplateSupportedEditorTypes());
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case LIST_TEMPLATES -> {
				BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
				if (editorType == null) {
					yield completedError("Invalid or missing blocklyEditorType");
				}
				if (editorType.extension() == null) {
					yield completedError("blocklyEditorType does not support templates: " + editorType.registryName());
				}
				yield completed(ToolResult.collection(
						loadTemplates(editorType).stream().map(ResourcePointer::toString).sorted(Comparator.naturalOrder())
								.toList()));
			}
			case GET_TEMPLATE -> {
				if (input.templateName == null || input.templateName.isBlank()) {
					yield completedError("templateName is required");
				}
				BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
				if (editorType == null) {
					yield completedError("Invalid or missing blocklyEditorType");
				}
				if (editorType.extension() == null) {
					yield completedError("blocklyEditorType does not support templates: " + editorType.registryName());
				}
				ResourcePointer template = findTemplate(editorType, input.templateName.trim());
				if (template == null) {
					yield completedError("Unknown template: " + input.templateName);
				}
				try {
					String templateXml = template.identifier instanceof String resourcePath ?
							BlocklyTemplateIO.importBlocklyXML("/" + resourcePath) :
							BlocklyTemplateIO.importBlocklyXML((File) template.identifier);
					yield completedText(templateXml);
				} catch (Exception e) {
					yield completedError("Failed to load template: " + e.getMessage(), e);
				}
			}
		};
	}

	@Nullable private static BlocklyEditorType parseEditorType(@Nullable String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		return BlocklyEditorType.fromName(raw.trim());
	}

	private static List<ResourcePointer> loadTemplates(BlocklyEditorType editorType) {
		return TemplatesLoader.loadTemplates(editorType.extension(), editorType.extension());
	}

	@Nullable private static ResourcePointer findTemplate(BlocklyEditorType editorType, String templateName) {
		for (ResourcePointer template : loadTemplates(editorType)) {
			if (template.toString().equalsIgnoreCase(templateName)) {
				return template;
			}
		}
		return null;
	}

	private static List<String> getTemplateSupportedEditorTypes() {
		return BlocklyEditorType.getTypes().stream().map(BlocklyEditorType::fromName).filter(type -> type.extension() != null)
				.map(BlocklyEditorType::registryName).sorted().toList();
	}

}
