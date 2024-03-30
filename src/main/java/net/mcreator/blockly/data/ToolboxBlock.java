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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.blockly.IBlockGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({ "unused", "MismatchedQueryAndUpdateOfCollection" }) public class ToolboxBlock {
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

	@Nullable private List<String> toolbox_init;

	public boolean error_in_statement_blocks = false;

	/* Fields below are not included in block JSON but loaded dynamically */
	transient JsonElement blocklyJSON;
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
		return blocklyJSON.getAsJsonObject();
	}

	public String getName() {
		return blocklyJSON.getAsJsonObject().get("message0").getAsString();
	}

	public String getOutputTypeForTests() {
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

	String getGroupEstimate() {
		if (this.group != null)
			return this.group;
		int a = StringUtils.ordinalIndexOf(this.machine_name, "_", 2);
		if (a > 0)
			return this.machine_name.substring(0, a);
		return this.machine_name.split("_")[0];
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
