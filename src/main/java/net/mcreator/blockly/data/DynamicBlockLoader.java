/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.util.ArrayList;
import java.util.List;

public class DynamicBlockLoader {

	public static List<ToolboxBlock> getDynamicBlocks() {
		List<ToolboxBlock> list = new ArrayList<>();
		for (VariableType varType : VariableTypeLoader.INSTANCE.getAllVariableTypes()) {
			ToolboxBlock getBlock = new DynamicToolboxBlock() {
				@Override public boolean shouldLoad(GeneratorConfiguration configuration) {
					return varType.canBeLocal(configuration) || varType.canBeGlobal(configuration);
				}
			};
			getBlock.machine_name = "variables_get_" + varType.getName();
			getBlock.toolbox_id = "customvariables";
			list.add(getBlock);

			ToolboxBlock setBlock = new DynamicToolboxBlock() {
				@Override public boolean shouldLoad(GeneratorConfiguration configuration) {
					return varType.canBeLocal(configuration) || varType.canBeGlobal(configuration);
				}
			};
			setBlock.machine_name = "variables_set_" + varType.getName();
			setBlock.toolbox_id = "customvariables";
			list.add(setBlock);

			ToolboxBlock customDependencyBlock = new DynamicToolboxBlock();
			customDependencyBlock.machine_name = "custom_dependency_" + varType.getName();
			customDependencyBlock.toolbox_id = "advanced";
			list.add(customDependencyBlock);

			ToolboxBlock procedureRetvalBlock = new DynamicToolboxBlock();
			procedureRetvalBlock.machine_name = "procedure_retval_" + varType.getName();
			procedureRetvalBlock.toolbox_id = "advanced";
			list.add(procedureRetvalBlock);

			ToolboxBlock returnBlock = new DynamicToolboxBlock();
			returnBlock.machine_name = "return_" + varType.getName();
			returnBlock.toolbox_id = "logicloops";
			list.add(returnBlock);
		}
		return list;
	}

	public static class DynamicToolboxBlock extends ToolboxBlock {

		@Override public String getName() {
			return machine_name;
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean shouldLoad(GeneratorConfiguration configuration) {
			return true;
		}

	}

}
