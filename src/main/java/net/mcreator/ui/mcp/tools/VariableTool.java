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

import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.java.JavaConventions;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class VariableTool extends MCreatorMcpTool<VariableTool.Args> {

	public static class Args {
		public Action actionType;
		public String variableName;
		@Nullable public String variableType;
		@Nullable public VariableType.Scope variableScope;
		@Nullable public String initialValue;

		public enum Action {
			ADD, REMOVE
		}
	}

	public VariableTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "variable";
	}

	@Override public String getDescription() {
		return "Adds or removes a workspace variable. Use list query_workspace MOD_VARIABLES to see existing variables. When adding, fill out all fields."
				+ " initialValue must be true or false for logic variables, a number for number variables, and a direction name for direction variables;"
				+ " other variable types except string do not support custom initial values, so omit initialValue for them to use the default value.";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected Boolean getDestructiveHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (!mcreator.getGeneratorStats().hasBaseCoverage("variables")) {
			return completedError("Generator does not support variables");
		}

		if (input.variableName == null || input.variableName.isBlank()) {
			return completedError("variableName is required");
		}

		String variableName = input.variableName.trim();
		Workspace workspace = mcreator.getWorkspace();

		if (input.actionType == Args.Action.REMOVE) {
			VariableElement element = workspace.getVariableElementByName(variableName);
			if (element == null) {
				return completedError("Variable not found: " + variableName);
			}
			workspace.removeVariableElement(element);
			mcreator.reloadWorkspaceTabContents();
			return completedText("Variable removed");
		}

		if (!isValidVariableName(variableName)) {
			return completedError("Invalid variable name");
		}
		if (workspace.getVariableElementByName(variableName) != null) {
			return completedError("Variable with this name already exists");
		}
		if (input.variableType == null || input.variableType.isBlank()) {
			return completedError("variableType is required for adding");
		}

		VariableType type = VariableTypeLoader.INSTANCE.fromName(input.variableType.trim());
		if (type == null || !VariableTypeLoader.INSTANCE.getGlobalVariableTypes(mcreator.getGeneratorConfiguration())
				.contains(type)) {
			return completedError("Invalid or unsupported variableType");
		}

		VariableType.Scope scope =
				input.variableScope != null ? input.variableScope : VariableType.Scope.GLOBAL_SESSION;

		VariableType.Scope[] supportedScopes = type.getSupportedScopesWithoutLocal(
				mcreator.getGeneratorConfiguration());
		if (!Arrays.asList(supportedScopes).contains(scope)) {
			return completedError("Scope not supported for this variable type");
		}

		Object value;
		if (input.initialValue == null || input.initialValue.isBlank()) {
			value = type.getDefaultValue(workspace);
		} else if (type == VariableTypeLoader.BuiltInTypes.LOGIC) {
			String raw = input.initialValue.trim();
			if (raw.equalsIgnoreCase("true") || raw.equals("1"))
				value = "true";
			else if (raw.equalsIgnoreCase("false") || raw.equals("0"))
				value = "false";
			else
				return completedError("Invalid initialValue for logic variable, use true or false");
		} else if (type == VariableTypeLoader.BuiltInTypes.NUMBER) {
			String raw = input.initialValue.trim();
			try {
				if (!Double.isFinite(Double.parseDouble(raw)))
					return completedError("Invalid initialValue for number variable, must be a finite number");
			} catch (NumberFormatException e) {
				return completedError("Invalid initialValue for number variable, must be a number");
			}
			value = raw;
		} else if (type == VariableTypeLoader.BuiltInTypes.DIRECTION) {
			String raw = input.initialValue.trim();
			value = DataListLoader.loadDataList("directions").stream().map(DataListEntry::getName)
					.filter(direction -> direction.equalsIgnoreCase(raw)).findFirst().orElse(null);
			if (value == null)
				return completedError("Invalid initialValue for direction variable");
		} else if (type == VariableTypeLoader.BuiltInTypes.STRING) {
			value = input.initialValue;
		} else {
			// silently ignore default value if not supported for this variable type
			value = type.getDefaultValue(workspace);
		}

		VariableElement element = new VariableElement(variableName);
		element.setType(type);
		element.setScope(scope);
		element.setValue(value);

		workspace.addVariableElement(element);
		mcreator.reloadWorkspaceTabContents();
		return completedText("Variable added");
	}

	private static boolean isValidVariableName(String name) {
		return JavaConventions.isValidJavaIdentifier(name) && !JavaConventions.isStringReservedJavaWord(name);
	}

}
