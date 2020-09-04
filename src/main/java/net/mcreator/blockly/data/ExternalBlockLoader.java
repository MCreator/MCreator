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
import net.mcreator.blockly.Dependency;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.init.L10N;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalBlockLoader {

	private static final Logger LOG = LogManager.getLogger("Blockly loader");

	private static final Pattern blockFormat = Pattern.compile("^[^$].*\\.json");
	private static final Pattern categoryFormat = Pattern.compile("^\\$.*\\.json");

	private final Map<String, ToolboxBlock> toolboxBlocks;

	private final String blocksJSONString;
	private final Map<String, StringBuilder> toolbox = new HashMap<>();

	ExternalBlockLoader(String resourceFolder) {
		LOG.debug("Loading blocks for " + resourceFolder);

		List<ToolboxCategory> toolboxCategories = new ArrayList<>();

		final Gson gson = new GsonBuilder().setLenient().create();

		List<ToolboxBlock> toolboxBlocksList = new ArrayList<>();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources(resourceFolder, blockFormat);
		for (String procedureBlock : fileNames) {
			try {
				JsonObject jsonresult = JsonParser
						.parseString(FileIO.readResourceToString(PluginLoader.INSTANCE, procedureBlock))
						.getAsJsonObject();
				JsonElement blockMCreatorDefinition = jsonresult.get("mcreator");

				ToolboxBlock toolboxBlock = gson.fromJson(blockMCreatorDefinition, ToolboxBlock.class);
				if (toolboxBlock != null) {
					toolboxBlock.machine_name = FilenameUtils.getBaseName(procedureBlock);

					String localized_message = L10N.t("blockly.block." + toolboxBlock.machine_name);
					if (localized_message != null) {
						jsonresult.add("message0", new JsonPrimitive(localized_message));
					}

					String localized_tooltip = L10N.t("blockly.block." + toolboxBlock.machine_name + ".tooltip");
					if (localized_tooltip != null) {
						jsonresult.add("tooltip", new JsonPrimitive(localized_tooltip));
					}

					jsonresult.add("type", new JsonPrimitive(toolboxBlock.machine_name));

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
			ToolboxCategory toolboxCategory = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, toolboxCategoryName),
							ToolboxCategory.class);
			toolboxCategory.id = FilenameUtils.getBaseName(toolboxCategoryName).replace("$", "");
			toolboxCategories.add(toolboxCategory);
		}

		if (PreferencesManager.PREFERENCES.blockly.useSmartSort) {
			toolboxBlocksList
					.sort(Comparator.comparing(ToolboxBlock::getGroupEstimate).thenComparing(ToolboxBlock::getName));
		} else {
			toolboxBlocksList.sort(Comparator.comparing(ToolboxBlock::getName));
		}

		toolboxCategories.sort(Comparator.comparing(ToolboxCategory::getName));

		// setup lookup cache
		this.toolboxBlocks = new LinkedHashMap<>();
		for (ToolboxBlock toolboxBlock : toolboxBlocksList) {
			toolboxBlocks.put(toolboxBlock.machine_name, toolboxBlock);
		}

		// setup toolbox

		// add default "built-in" categories
		toolbox.put("other", new StringBuilder());
		toolbox.put("apis", new StringBuilder());
		toolbox.put("mcelements", new StringBuilder());
		toolbox.put("mcvariables", new StringBuilder());
		toolbox.put("customvariables", new StringBuilder());
		toolbox.put("logicloops", new StringBuilder());
		toolbox.put("logicoperations", new StringBuilder());
		toolbox.put("math", new StringBuilder());
		toolbox.put("text", new StringBuilder());
		toolbox.put("advanced", new StringBuilder());

		for (ToolboxCategory category : toolboxCategories) {
			StringBuilder categoryBuilder = new StringBuilder();
			categoryBuilder.append("<category name=\"").append(category.getName()).append("\" colour=\"")
					.append(category.color).append("\">");
			if (category.description != null) {
				categoryBuilder.append("<label text=\"").append(category.description)
						.append("\" web-class=\"whlab\"/>");
			}
			for (ToolboxBlock toolboxBlock : toolboxBlocks.values()) {
				if (toolboxBlock.toolbox_id != null && toolboxBlock.toolbox_id.equals(category.id)) {
					StringBuilder toolboxXML = new StringBuilder();

					toolboxXML.append("<block type=\"").append(toolboxBlock.machine_name).append("\">");
					if (toolboxBlock.toolbox_init != null)
						toolboxBlock.toolbox_init.forEach(toolboxXML::append);
					toolboxXML.append("</block>");

					categoryBuilder.append(toolboxXML);
					toolboxBlock.toolboxXML = toolboxXML.toString();
					toolboxBlock.toolboxCategory = category;
				}
			}
			categoryBuilder.append("</category>");
			if (categoryBuilder.toString().contains("<block type=")) {
				if (category.api) {
					toolbox.get("apis").append(categoryBuilder);
				} else {
					toolbox.get("other").append(categoryBuilder);
				}
			}
		}

		JsonArray blocksJSON = new JsonArray();
		for (ToolboxBlock toolboxBlock : toolboxBlocks.values()) {
			for (Map.Entry<String, StringBuilder> entry : toolbox.entrySet()) {
				if (entry.getKey().equals("other"))
					continue;

				if (toolboxBlock.toolbox_id != null && toolboxBlock.toolbox_id.equals(entry.getKey())) {
					StringBuilder toolboxXML = new StringBuilder();

					toolboxXML.append("<block type=\"").append(toolboxBlock.machine_name).append("\">");
					if (toolboxBlock.toolbox_init != null)
						toolboxBlock.toolbox_init.forEach(toolboxXML::append);
					toolboxXML.append("</block>");

					entry.getValue().append(toolboxXML);
					toolboxBlock.toolboxXML = toolboxXML.toString();
				}
			}
			blocksJSON.add(toolboxBlock.blocklyJSON);
		}

		this.blocksJSONString = blocksJSON.toString();
	}

	public void loadBlocksAndCategoriesInPanel(BlocklyPanel pane, ToolboxType toolboxType) {
		pane.executeJavaScriptSynchronously("Blockly.defineBlocksWithJsonArray(" + blocksJSONString + ")");

		String toolbox_xml = FileIO
				.readResourceToString("/blockly/toolbox_" + toolboxType.name().toLowerCase(Locale.ENGLISH) + ".xml");

		Matcher m = Pattern.compile("\\$\\{t:([\\w.]+)}").matcher(toolbox_xml);
		while (m.find()) {
			String m1 = L10N.t(m.group(1));
			if (m1 != null)
				toolbox_xml = toolbox_xml.replace(m.group(), m1);
		}

		for (Map.Entry<String, StringBuilder> entry : toolbox.entrySet()) {
			toolbox_xml = toolbox_xml.replace("<custom-" + entry.getKey() + "/>", entry.getValue().toString());
		}

		pane.executeJavaScriptSynchronously(
				"workspace.updateToolbox('" + toolbox_xml.replaceAll("[\n\r]", "\\\\\n") + "')");
	}

	public Map<String, ToolboxBlock> getDefinedBlocks() {
		return toolboxBlocks;
	}

	@SuppressWarnings("unused") public static class ToolboxCategory {
		private String id, name, description;
		private int color;
		private boolean api;

		public String getName() {
			String l10nname = L10N.t("blockly.category." + id);
			if (l10nname != null)
				return l10nname;

			return name;
		}

		public Color getColor() {
			return Color.getHSBColor(color / 360f, 0.37f, 0.6f);
		}
	}

	@SuppressWarnings("unused") public static class ToolboxBlock {
		private String toolbox_id;
		public String machine_name;
		public IBlockGenerator.BlockType type;

		@Nullable private List<String> toolbox_init;

		@Nullable public List<String> fields;
		@Nullable public List<String> inputs;
		@Nullable public List<Dependency> dependencies;

		@Nullable public List<String> required_apis;

		private JsonElement blocklyJSON;

		@Nullable public String toolboxXML;
		@Nullable public ToolboxCategory toolboxCategory;

		public String getGroupEstimate() {
			int a = StringUtils.ordinalIndexOf(this.machine_name, "_", 2);
			if (a > 0)
				return this.machine_name.substring(0, a);
			return this.machine_name.split("_")[0];
		}

		public String getName() {
			return blocklyJSON.getAsJsonObject().get("message0").getAsString();
		}

		public String getOutputType() {
			if (type == IBlockGenerator.BlockType.OUTPUT) {
				return blocklyJSON.getAsJsonObject().get("output").getAsString();
			} else {
				return null;
			}
		}

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			ToolboxBlock block = (ToolboxBlock) o;
			return machine_name.equals(block.machine_name);
		}

		@Override public int hashCode() {
			return machine_name.hashCode();
		}
	}

	public enum ToolboxType {
		PROCEDURE, EMPTY
	}

}
