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
import net.mcreator.blockly.IBlockGenerator;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused") public class ToolboxBlock {
	String toolbox_id;
	public String machine_name;
	public IBlockGenerator.BlockType type;

	@Nullable List<String> toolbox_init;

	@Nullable public List<String> fields;
	@Nullable public List<String> inputs;
	@Nullable public List<StatementInput> statements;
	@Nullable public List<Dependency> dependencies;

	@Nullable public List<String> required_apis;

	JsonElement blocklyJSON;

	@Nullable public String toolboxXML;
	@Nullable public ToolboxCategory toolboxCategory;

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

	String getGroupEstimate() {
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
