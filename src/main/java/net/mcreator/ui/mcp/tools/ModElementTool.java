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

import com.google.gson.*;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModElementTool extends MCreatorMcpTool<ModElementTool.Args> {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static class Args {
		public Action actionType;
		public String elementName;
		@Nullable public String elementType;
		@Nullable public String elementJSONDefinition;

		public enum Action {
			READ, ADD, MODIFY, REMOVE
		}
	}

	public ModElementTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, ModElementTool.Args.class);
	}

	@Override public String getName() {
		return "mod_element_definition";
	}

	@Override public String getDescription() {
		return "A tool to read JSON definition, modify, or add mod elements to the workspace. Type and JSON used only for adding.";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ModElementTool.Args input) {
		if (input.actionType == Args.Action.READ) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element not found"));
			}
			String geJSON = safeGeneratableElementToJSON(mcreator, modElement.getGeneratableElement());
			return CompletableFuture.completedFuture(ToolResult.object(JsonParser.parseString(geJSON)));
		} else if (input.actionType == Args.Action.MODIFY) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element not found"));
			}
			try {
				GeneratableElement element = safeJSONtoGeneratableElement(mcreator, modElement,
						input.elementJSONDefinition);
				String json = safeGeneratableElementToJSON(mcreator, element);
				if (!json.equals(input.elementJSONDefinition)) {
					Map<String, Object> response = new HashMap<>();
					response.put("result", "Element modified, but JSON definition was changed during processing");
					response.put("actualJSONDefinition", JsonParser.parseString(json));
					return CompletableFuture.completedFuture(ToolResult.object(response));
				} else {
					return CompletableFuture.completedFuture(ToolResult.text("Element modified"));
				}
			} catch (Exception e) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Failed to modify element: " + e.getMessage()));
			}
		} else if (input.actionType == Args.Action.ADD) {
			if (input.elementType == null || input.elementJSONDefinition == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element type and JSON definition must be provided for adding"));
			}
			ModElementType<?> type = ModElementTypeLoader.getModElementType(input.elementType);
			ModElement modElement = new ModElement(mcreator.getWorkspace(), input.elementName, type);
			mcreator.getWorkspace().addModElement(modElement);
			try {
				GeneratableElement element = safeJSONtoGeneratableElement(mcreator, modElement,
						input.elementJSONDefinition);
				String json = safeGeneratableElementToJSON(mcreator, element);
				if (!json.equals(input.elementJSONDefinition)) {
					Map<String, Object> response = new HashMap<>();
					response.put("result", "Element added, but JSON definition was changed during processing");
					response.put("actualJSONDefinition", JsonParser.parseString(json));
					return CompletableFuture.completedFuture(ToolResult.object(response));
				} else {
					return CompletableFuture.completedFuture(ToolResult.text("Element added"));
				}
			} catch (Exception e) {
				mcreator.getWorkspace().removeModElement(modElement);
				return CompletableFuture.completedFuture(ToolResult.error("Failed to add element: " + e.getMessage()));
			}
		} else if (input.actionType == Args.Action.REMOVE) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element not found"));
			}
			mcreator.getWorkspace().removeModElement(modElement);
			return CompletableFuture.completedFuture(ToolResult.text("Element removed"));
		} else {
			return CompletableFuture.completedFuture(ToolResult.error("Invalid action type"));
		}
	}

	private static GeneratableElement safeJSONtoGeneratableElement(MCreator mcreator, ModElement modElement,
			String json) throws Exception {
		JsonObject root = new JsonObject();
		root.add("_fv", new JsonPrimitive(GeneratableElement.formatVersion));
		root.add("_type", gson.toJsonTree(modElement.getType().getRegistryName()));
		root.add("definition", JsonParser.parseString(json));
		json = gson.toJson(root);

		GeneratableElement element = mcreator.getModElementManager().fromJSONtoGeneratableElement(json, modElement);

		// TODO: validation through UI

		mcreator.getWorkspace().markDirty();
		mcreator.getModElementManager().storeModElement(element);
		mcreator.getGenerator().generateBase(true); // use variant that throws TemplateGeneratorException
		mcreator.getGenerator().generateElement(element, true); // use variant that throws TemplateGeneratorException
		mcreator.getModElementManager().storeModElementPicture(element);
		modElement.reinit(mcreator.getWorkspace());
		return element;
	}

	private static String safeGeneratableElementToJSON(MCreator mcreator, GeneratableElement element) {
		String json = mcreator.getModElementManager().generatableElementToJSON(element);
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		return gson.toJson(jsonObject.get("definition"));
	}

}

