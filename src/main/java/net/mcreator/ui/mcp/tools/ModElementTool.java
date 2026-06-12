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

import net.mcreator.element.GeneratableElement;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModElementTool extends MCreatorMcpTool<ModElementTool.Args> {

	public static class Args {
		public Action actionType;
		public String elementName;
		@Nullable public String elementType;
		@Nullable public String elementJSON;

		public enum Action {
			READ, ADD, MODIFY
		}
	}

	public ModElementTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, ModElementTool.Args.class);
	}

	@Override public String getName() {
		return "mod_element_definition";
	}

	@Override public String getDescription() {
		return "Provides list of specified workspace elements or data list entries";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ModElementTool.Args input) {
		if (input.actionType == Args.Action.READ) {
			ModElement element = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (element == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element not found"));
			}
			GeneratableElement generatableElement = element.getGeneratableElement();
			if (generatableElement == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element is not a generatable element"));
			}
			String geJSON = mcreator.getModElementManager().generatableElementToJSON(generatableElement);
			return CompletableFuture.completedFuture(ToolResult.text(geJSON));
		} else {
			return CompletableFuture.completedFuture(ToolResult.error("Invalid action type"));
		}
	}

}

