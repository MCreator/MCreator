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
import net.mcreator.ui.init.L10N;
import net.mcreator.util.TestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "unused", "MismatchedQueryAndUpdateOfCollection" }) public class ToolboxBlock {

	private static final Logger LOG = LogManager.getLogger(ToolboxBlock.class);

	String machine_name;
	String toolbox_id;
	IBlockGenerator.BlockType type;

	@Nullable private List<String> fields;
	@Nullable private List<RepeatingField> repeating_fields;
	@Nullable private List<IInput> inputs;
	@Nullable private List<StatementInput> statements;
	@Nullable private List<IInput> repeating_inputs;
	@Nullable private List<StatementInput> repeating_statements;

	@Nullable private List<Dependency> dependencies;
	@Nullable private List<String> warnings;
	@Nullable private List<String> required_apis;
	@Nullable private String group;

	@Nullable protected List<String> toolbox_init;

	public boolean error_in_statement_blocks = false;

	/* Fields below are not included in block JSON but loaded dynamically */
	transient JsonObject blocklyJSON;
	@Nullable private transient String toolboxXML;
	@Nullable private transient String toolboxTestXML; // XML setup used in tests
	@Nullable transient ToolboxCategory toolboxCategory;

	@Nullable public List<String> getFields() {
		return fields;
	}

	@Nullable public List<RepeatingField> getRepeatingFields() {
		return repeating_fields;
	}

	public List<String> getInputs() {
		return inputs != null ?
				inputs.stream().filter(e -> e instanceof NamedInput).map(IInput::name).toList() :
				Collections.emptyList();
	}

	public List<AdvancedInput> getAdvancedInputs() {
		return inputs != null ?
				inputs.stream().filter(e -> e instanceof AdvancedInput).map(e -> (AdvancedInput) e).toList() :
				Collections.emptyList();
	}

	public List<String> getAllInputs() {
		return inputs != null ? inputs.stream().map(IInput::name).toList() : Collections.emptyList();
	}

	@Nullable public List<StatementInput> getStatements() {
		return statements;
	}

	public List<String> getRepeatingInputs() {
		return repeating_inputs != null ?
				repeating_inputs.stream().filter(e -> e instanceof NamedInput).map(IInput::name).toList() :
				Collections.emptyList();
	}

	public List<AdvancedInput> getRepeatingAdvancedInputs() {
		return repeating_inputs != null ?
				repeating_inputs.stream().filter(e -> e instanceof AdvancedInput).map(e -> (AdvancedInput) e).toList() :
				Collections.emptyList();
	}

	public List<String> getAllRepeatingInputs() {
		return repeating_inputs != null ?
				repeating_inputs.stream().map(IInput::name).toList() :
				Collections.emptyList();
	}

	@Nullable public List<StatementInput> getRepeatingStatements() {
		return repeating_statements;
	}

	@Nullable public List<Dependency> getDependencies() {
		return dependencies;
	}

	@Nullable public List<String> getWarnings() {
		return warnings;
	}

	@Nullable public List<String> getRequiredAPIs() {
		return required_apis;
	}

	@Nullable public ToolboxCategory getToolboxCategory() {
		return toolboxCategory;
	}

	@Nullable public String getToolboxCategoryRaw() {
		return toolbox_id;
	}

	public IBlockGenerator.BlockType getType() {
		return type;
	}

	public String getMachineName() {
		return machine_name;
	}

	@Nullable public List<String> getToolboxInitStatements() {
		return toolbox_init;
	}

	public String getToolboxXML() {
		if (toolboxXML == null) {
			StringBuilder toolboxXMLBuilder = new StringBuilder();
			toolboxXMLBuilder.append("<block type=\"").append(machine_name).append("\">");
			if (toolbox_init != null)
				toolbox_init.stream().filter(Objects::nonNull).filter(e -> !e.startsWith("~"))
						.forEach(toolboxXMLBuilder::append);
			toolboxXMLBuilder.append("</block>");
			toolboxXML = toolboxXMLBuilder.toString();
		}

		return toolboxXML;
	}

	public String getToolboxTestXML() {
		if (toolboxTestXML == null) {
			StringBuilder toolboxXMLBuilder = new StringBuilder();
			toolboxXMLBuilder.append("<block type=\"").append(machine_name).append("\">");
			if (toolbox_init != null)
				toolbox_init.stream().filter(Objects::nonNull).map(e -> e.startsWith("~") ? e.substring(1) : e)
						.forEach(toolboxXMLBuilder::append);
			toolboxXMLBuilder.append("</block>");
			toolboxTestXML = toolboxXMLBuilder.toString();
		}

		return toolboxTestXML;
	}

	public JsonObject getBlocklyJSON() {
		return blocklyJSON;
	}

	public String getName() {
		return blocklyJSON.get("message0").getAsString();
	}

	/**
	 * @return Output type String in Blockly format. Null if the block is not an output block.
	 */
	public String getOutputType() {
		if (type == IBlockGenerator.BlockType.OUTPUT) {
			JsonElement output = blocklyJSON.getAsJsonObject().get("output");
			if (output.isJsonArray()) {
				return output.getAsJsonArray().get(0).getAsString();
			} else {
				return output.getAsString();
			}
		} else {
			return null;
		}
	}

	/**
	 * @return Previous connection type String in Blockly format. Null if the block is not a procedural block or if
	 * connection type is not specified.
	 */
	public String getPreviousStatementConnectionType() {
		if (type == IBlockGenerator.BlockType.PROCEDURAL) {
			JsonElement output = blocklyJSON.getAsJsonObject().get("previousStatement");
			if (output.isJsonArray()) {
				return output.getAsJsonArray().get(0).getAsString();
			} else if (output.isJsonNull()) {
				return null;
			} else {
				return output.getAsString();
			}
		} else {
			return null;
		}
	}

	/**
	 * @param fieldName Field name to get type of
	 * @return Field type String in Blockly format. Null if the field does not exist, or we can't determine its type.
	 */
	@Nullable public String getFieldType(String fieldName) {
		if (blocklyJSON.getAsJsonObject().has("args0")) {
			JsonArray args0 = blocklyJSON.getAsJsonObject().get("args0").getAsJsonArray();
			for (int i = 0; i < args0.size(); i++) {
				JsonObject arg = args0.get(i).getAsJsonObject();
				if (arg.has("name") && arg.get("name").getAsString().equals(fieldName) && arg.has("type")) {
					return arg.get("type").getAsString();
				}
			}
		}
		return null;
	}

	/**
	 * @param fieldName Field name to get data list of
	 * @return Field data list in JSON format. Null if the field does not exist, or the type of field does not specify a data list.
	 */
	@Nullable public String getFieldDataList(String fieldName) {
		String fieldType = getFieldType(fieldName);
		if (fieldType != null && fieldType.equals("field_mcitem_selector")) {
			return "blocksitems";
		}

		if (blocklyJSON.getAsJsonObject().has("args0")) {
			JsonArray args0 = blocklyJSON.getAsJsonObject().get("args0").getAsJsonArray();
			for (int i = 0; i < args0.size(); i++) {
				JsonObject arg = args0.get(i).getAsJsonObject();
				if (arg.has("name") && arg.get("name").getAsString().equals(fieldName) && arg.has("datalist")) {
					return arg.get("datalist").getAsString();
				}
			}
		}
		return null;
	}

	String getGroupEstimate() {
		if (this.group != null)
			return this.group;

		// Try to remove commonly used namespaces for better estimation
		String machineNameNoNamespace = this.machine_name.replace("be_", "");

		int a = StringUtils.ordinalIndexOf(machineNameNoNamespace, "_", 2);
		if (a > 0)
			return machineNameNoNamespace.substring(0, a);
		return machineNameNoNamespace.split("_")[0];
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

	public static List<ToolboxBlock> getToolboxBlocksFor(IBlockGenerator blockGenerator) {
		String[] supportedBlocks = blockGenerator.getSupportedBlocks();
		String[] blockDefinitions = blockGenerator.getBlockJSONDefinitions();

		List<String>[] toolboxInits = blockGenerator.getToolboxInit();
		if (blockDefinitions == null)
			return Collections.emptyList();

		if (blockDefinitions.length != supportedBlocks.length) {
			LOG.warn("Mismatch between supported blocks and block definitions for generator: {}",
					blockGenerator.getClass().getName());
			TestUtil.failIfTestingEnvironment();
		}

		String toolboxCategoryId = blockGenerator.getToolboxCategory();
		@Nullable ToolboxCategory toolboxCategory =
				toolboxCategoryId != null ? ToolboxCategory.tryGetBuiltin(toolboxCategoryId) : null;

		List<ToolboxBlock> blocks = new ArrayList<>(supportedBlocks.length);
		for (int i = 0; i < supportedBlocks.length; i++) {
			ToolboxBlock toolboxBlock = new ToolboxBlock();
			toolboxBlock.machine_name = supportedBlocks[i];
			toolboxBlock.type = blockGenerator.getBlockType();

			JsonObject blocklyJSON = JsonParser.parseString(blockDefinitions[i]).getAsJsonObject();
			handleTranslations(toolboxBlock, blocklyJSON);

			toolboxBlock.blocklyJSON = blocklyJSON;
			if (toolboxCategoryId != null) {
				toolboxBlock.toolbox_id = toolboxCategoryId;
				toolboxBlock.toolboxCategory = toolboxCategory;
			}
			if (toolboxInits != null) {
				toolboxBlock.toolbox_init = toolboxInits[i];
			}
			blocks.add(toolboxBlock);
		}
		return blocks;
	}

	static void handleTranslations(ToolboxBlock toolboxBlock, JsonObject blocklyJSON) {
		String localized_message = L10N.t("blockly.block." + toolboxBlock.getMachineName());
		String localized_message_en = L10N.t_en("blockly.block." + toolboxBlock.getMachineName());

		if (localized_message != null) {
			try {
				validateTranslation(localized_message, localized_message_en);
				blocklyJSON.add("message0", new JsonPrimitive(localized_message));
			} catch (ParseException e) {
				LOG.warn("Block {} translation \"{}\" for the selected language is not valid. Reason: {}",
						toolboxBlock.getMachineName(), localized_message, e.getMessage());
				TestUtil.failIfTestingEnvironment();
				if (localized_message_en != null) {
					blocklyJSON.add("message0", new JsonPrimitive(localized_message_en));
				}
			}
		} else if (localized_message_en != null) {
			blocklyJSON.add("message0", new JsonPrimitive(localized_message_en));
		}

		String localized_tooltip = L10N.t("blockly.block." + toolboxBlock.getMachineName() + ".tooltip");
		if (localized_tooltip != null) {
			blocklyJSON.add("tooltip", new JsonPrimitive(localized_tooltip));
		}
	}

	private final static Pattern N_PLACEHOLDER_MATCHER = Pattern.compile("%\\d+"); // Matches %1, %2, etc.

	private static void validateTranslation(@Nullable String localized_message, @Nullable String localized_message_en)
			throws ParseException {
		if (localized_message == null)
			return; // Nothing to validate

		// Make sure original string and translation have the same number of parameters
		if (localized_message_en != null) {
			int parameters_count = net.mcreator.util.StringUtils.countRegexMatches(localized_message, "%[0-9]+");
			int parameters_count_en = net.mcreator.util.StringUtils.countRegexMatches(localized_message_en, "%[0-9]+");
			if (parameters_count != parameters_count_en) {
				throw new ParseException("%N placeholder count mismatch", 0);
			}
		}

		// Make sure all parameters are only used once
		Matcher matcher = N_PLACEHOLDER_MATCHER.matcher(localized_message);
		Set<String> seen = new HashSet<>();
		while (matcher.find()) {
			String placeholder = matcher.group();
			if (!seen.add(placeholder)) {
				throw new ParseException("Duplicate %N placeholder", 0);
			}
		}
	}

}
