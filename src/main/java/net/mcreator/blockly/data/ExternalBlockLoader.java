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

package net.mcreator.blockly.data;

import com.google.gson.*;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.init.BlocklyToolboxesLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalBlockLoader {

	private static final Logger LOG = LogManager.getLogger("Blockly loader");

	private static final Pattern blockFormat = Pattern.compile("^[^$].*\\.json");
	private static final Pattern categoryFormat = Pattern.compile("^\\$.*\\.json");

	private static final Pattern translationsMatcher = Pattern.compile("\\$\\{t:([\\w.]+)}");

	private final Map<String, ToolboxBlock> toolboxBlocks;

	private final String blocksJSONString;
	private final Map<String, List<Tuple<ToolboxBlock, String>>> toolbox = new HashMap<>();

	ExternalBlockLoader(String resourceFolder) {
		LOG.debug("Loading blocks for " + resourceFolder);

		List<ToolboxCategory> toolboxCategories = new ArrayList<>();

		final Gson gson = new GsonBuilder().setLenient().create();

		List<ToolboxBlock> toolboxBlocksList = new ArrayList<>();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources(resourceFolder, blockFormat);
		for (String procedureBlock : fileNames) {
			try {
				JsonObject jsonresult = JsonParser.parseString(
						FileIO.readResourceToString(PluginLoader.INSTANCE, procedureBlock)).getAsJsonObject();
				JsonElement blockMCreatorDefinition = jsonresult.get("mcreator");

				ToolboxBlock toolboxBlock = gson.fromJson(blockMCreatorDefinition, ToolboxBlock.class);
				if (toolboxBlock != null) {
					toolboxBlock.machine_name = FilenameUtilsPatched.getBaseName(procedureBlock);

					String localized_message = L10N.t("blockly.block." + toolboxBlock.getMachineName());
					String localized_message_en = L10N.t_en("blockly.block." + toolboxBlock.getMachineName());

					if (localized_message != null) {
						int parameters_count = net.mcreator.util.StringUtils.countRegexMatches(localized_message,
								"%[0-9]+");
						int parameters_count_en = net.mcreator.util.StringUtils.countRegexMatches(localized_message_en,
								"%[0-9]+");

						if (parameters_count == parameters_count_en) {
							jsonresult.add("message0", new JsonPrimitive(localized_message));
						} else {
							LOG.warn("Not all procedure block inputs are defined using %N for block "
									+ toolboxBlock.getMachineName() + " for the selected language");
							if (localized_message_en != null) {
								jsonresult.add("message0", new JsonPrimitive(localized_message_en));
							}
						}
					} else if (localized_message_en != null) {
						jsonresult.add("message0", new JsonPrimitive(localized_message_en));
					}

					String localized_tooltip = L10N.t("blockly.block." + toolboxBlock.getMachineName() + ".tooltip");
					if (localized_tooltip != null) {
						jsonresult.add("tooltip", new JsonPrimitive(localized_tooltip));
					}

					jsonresult.add("type", new JsonPrimitive(toolboxBlock.getMachineName()));

					toolboxBlock.blocklyJSON = jsonresult;
					toolboxBlock.type = jsonresult.get("output") == null ?
							IBlockGenerator.BlockType.PROCEDURAL :
							IBlockGenerator.BlockType.OUTPUT;
					toolboxBlocksList.add(toolboxBlock);
				}
			} catch (Exception e) {
				LOG.error("Failed to load procedure block: " + procedureBlock, e);
			}
		}

		fileNames = PluginLoader.INSTANCE.getResources(resourceFolder, categoryFormat);
		for (String toolboxCategoryName : fileNames) {
			ToolboxCategory toolboxCategory = gson.fromJson(
					FileIO.readResourceToString(PluginLoader.INSTANCE, toolboxCategoryName), ToolboxCategory.class);
			toolboxCategory.id = FilenameUtilsPatched.getBaseName(toolboxCategoryName).replace("$", "");
			toolboxCategories.add(toolboxCategory);
		}
		toolboxCategories.sort(Comparator.comparing(ToolboxCategory::getName));

		for (ToolboxCategory toolboxCategory : toolboxCategories) {
			if (toolboxCategory.parent_category != null) {
				for (ToolboxCategory parent : toolboxCategories) {
					if (parent.id.equals(toolboxCategory.parent_category)) {
						toolboxCategory.parent = parent;
						break;
					}
				}
			}
		}

		// setup lookup cache of loaded blocks
		this.toolboxBlocks = new HashMap<>();
		for (ToolboxBlock toolboxBlock : toolboxBlocksList)
			toolboxBlocks.put(toolboxBlock.getMachineName(), toolboxBlock);

		// generate JSON for loaded blocks
		JsonArray blocksJSON = new JsonArray();
		for (ToolboxBlock toolboxBlock : toolboxBlocksList)
			blocksJSON.add(toolboxBlock.blocklyJSON);
		this.blocksJSONString = blocksJSON.toString();

		// after cache is made, we can load dynamic blocks
		toolboxBlocksList.addAll(DynamicBlockLoader.getDynamicBlocks());

		// and then sort them for toolbox display
		if (PreferencesManager.PREFERENCES.blockly.useSmartSort.get()) {
			toolboxBlocksList.sort(
					Comparator.comparing(ToolboxBlock::getGroupEstimate).thenComparing(ToolboxBlock::getName));
		} else {
			toolboxBlocksList.sort(Comparator.comparing(ToolboxBlock::getName));
		}

		// setup toolbox

		// add default "built-in" categories
		BlocklyLoader.getBuiltinCategories().forEach(name -> toolbox.put(name, new ArrayList<>()));

		// Handle built-in categories
		for (ToolboxBlock toolboxBlock : toolboxBlocksList) {
			for (Map.Entry<String, List<Tuple<ToolboxBlock, String>>> entry : toolbox.entrySet()) {
				if (entry.getKey().equals("other"))
					continue;

				if (toolboxBlock.toolbox_id != null && toolboxBlock.toolbox_id.equals(entry.getKey())) {
					entry.getValue().add(new Tuple<>(toolboxBlock, toolboxBlock.getToolboxXML()));
				}
			}
		}

		// Handle other and API categories
		for (ToolboxCategory category : toolboxCategories) {
			if (category.parent_category == null) {
				String categoryCode = generateCategoryXML(category, toolboxCategories, toolboxBlocksList);
				if (categoryCode.contains("<block type=") || categoryCode.contains("<category name="))
					toolbox.get(category.api ? "apis" : "other").add(new Tuple<>(null, categoryCode));
			}
		}
	}

	private String generateCategoryXML(ToolboxCategory category, List<ToolboxCategory> toolboxCategories,
			List<ToolboxBlock> toolboxBlocksList) {
		StringBuilder builder = new StringBuilder();

		builder.append("<category name=\"").append(escapeTranslationForXMLAndJS(category.getName()))
				.append("\" colour=\"").append(category.color).append("\"");
		String expandCategories = PreferencesManager.PREFERENCES.blockly.expandCategories.get();
		if ((category.is_expanded && expandCategories.equals("Default")) || expandCategories.equals("Always"))
			builder.append(" expanded=\"true\"");
		builder.append(">");

		if (category.getDescription() != null) {
			builder.append("<label text=\"").append(escapeTranslationForXMLAndJS(category.getDescription()))
					.append("\" web-class=\"whlab\"/>");
		}

		for (ToolboxBlock toolboxBlock : toolboxBlocksList) {
			if (toolboxBlock.toolbox_id != null && toolboxBlock.toolbox_id.equals(category.id)) {
				builder.append(toolboxBlock.getToolboxXML());
				toolboxBlock.toolboxCategory = category;
			}
		}

		// Create each nested category that will be added to this current category
		for (ToolboxCategory child : toolboxCategories) {
			if (category.id.equals(child.parent_category)) {
				builder.append(generateCategoryXML(child, toolboxCategories, toolboxBlocksList));
			}
		}

		builder.append("</category>");

		return builder.toString();
	}

	public void loadBlocksAndCategoriesInPanel(BlocklyPanel pane, ToolboxType toolboxType) {
		pane.executeJavaScriptSynchronously("Blockly.defineBlocksWithJsonArray(" + blocksJSONString + ")");

		String toolbox_xml = BlocklyToolboxesLoader.INSTANCE.getToolboxXML(
				toolboxType.name().toLowerCase(Locale.ENGLISH));

		Matcher m = translationsMatcher.matcher(toolbox_xml);
		while (m.find()) {
			String m1 = escapeTranslationForXMLAndJS(L10N.t(m.group(1)));
			if (m1 != null)
				toolbox_xml = toolbox_xml.replace(m.group(), m1);
		}

		for (Map.Entry<String, List<Tuple<ToolboxBlock, String>>> entry : toolbox.entrySet()) {
			StringBuilder categoryBuilderFinal = new StringBuilder();
			for (Tuple<ToolboxBlock, String> tuple : entry.getValue()) {
				if (tuple.x() instanceof DynamicBlockLoader.DynamicToolboxBlock
						&& !((DynamicBlockLoader.DynamicToolboxBlock) tuple.x()).shouldLoad(
						pane.getMCreator().getGeneratorConfiguration()))
					continue;
				categoryBuilderFinal.append(tuple.y());
			}
			toolbox_xml = toolbox_xml.replace("<custom-" + entry.getKey() + "/>", categoryBuilderFinal.toString());
		}

		pane.executeJavaScriptSynchronously(
				"workspace.updateToolbox('" + toolbox_xml.replace("\n", "").replace("\r", "") + "')");
	}

	public Map<String, ToolboxBlock> getDefinedBlocks() {
		return toolboxBlocks;
	}

	private String escapeTranslationForXMLAndJS(String translation) {
		if (translation == null)
			return null;

		return translation.replace("'", "\\'").replace("\"", "&quot;");
	}

}
