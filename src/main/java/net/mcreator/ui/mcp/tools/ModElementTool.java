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
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.modgui.IBlocklyPanelHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
		return """
				A tool to read mod element (JSON definition and mod element metadata), modify, or add mod elements to the workspace.\
				Type and JSON used only for adding. Names of mod elements are always CamelCaseNames\
				Modify action requires full element JSON definition, not just changes.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ModElementTool.Args input) {
		if (input.actionType == Args.Action.READ) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element not found"));
			}
			String geJSON = safeGeneratableElementToJSON(mcreator, modElement.getGeneratableElement());
			Map<String, Object> response = new HashMap<>();
			response.put("_metadata", modElement);
			response.put("elementJSONDefinition", geJSON);
			return CompletableFuture.completedFuture(ToolResult.object(response));
		} else if (input.actionType == Args.Action.MODIFY) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element not found"));
			}
			try {
				GeneratableElement element = safeJSONtoGeneratableElement(mcreator, modElement,
						input.elementJSONDefinition);
				String json = safeGeneratableElementToJSON(mcreator, element);
				mcreator.reloadWorkspaceTabContents();
				if (!json.equals(input.elementJSONDefinition)) {
					Map<String, Object> response = new HashMap<>();
					response.put("result", "Element modified, but JSON definition was changed during processing. "
							+ "Verify modified JSON if any unintended changes or removals happened.");
					response.put("actualJSONDefinition", json);
					return CompletableFuture.completedFuture(ToolResult.object(response));
				} else {
					return CompletableFuture.completedFuture(ToolResult.text("Element modified"));
				}
			} catch (Exception e) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Failed to modify element: " + e.getMessage(), e));
			}
		} else if (input.actionType == Args.Action.ADD) {
			if (input.elementType == null || input.elementJSONDefinition == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element type and JSON definition must be provided for adding"));
			}
			if (mcreator.getWorkspace().getModElementByName(input.elementName) != null) {
				return CompletableFuture.completedFuture(ToolResult.error("Element with this name already exists"));
			}

			ModElementType<?> type = ModElementTypeLoader.getModElementType(input.elementType.toLowerCase(Locale.ROOT));
			ModElement modElement = new ModElement(mcreator.getWorkspace(), input.elementName, type);
			mcreator.getWorkspace().addModElement(modElement);
			try {
				GeneratableElement element = safeJSONtoGeneratableElement(mcreator, modElement,
						input.elementJSONDefinition);
				String json = safeGeneratableElementToJSON(mcreator, element);
				mcreator.reloadWorkspaceTabContents();
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
				return CompletableFuture.completedFuture(
						ToolResult.error("Failed to add element: " + e.getMessage(), e));
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

		mcreator.getModElementManager().storeModElement(element);

		element = validateThroughUI(mcreator, element);

		mcreator.getGenerator().generateBase(true); // use variant that throws TemplateGeneratorException
		mcreator.getGenerator().generateElement(element, true); // use variant that throws TemplateGeneratorException
		mcreator.getModElementManager().storeModElementPicture(element);
		modElement.reinit(mcreator.getWorkspace());

		mcreator.getWorkspace().markDirty();

		return element;
	}

	private static String safeGeneratableElementToJSON(MCreator mcreator, GeneratableElement element) {
		String json = mcreator.getModElementManager().generatableElementToJSON(element);
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		return gson.toJson(jsonObject.get("definition"));
	}

	private static GeneratableElement validateThroughUI(MCreator mcreator, GeneratableElement generatableElement)
			throws Exception {
		ModElementGUI<?> modElementGUI = generatableElement.getModElement().getType()
				.getModElementGUI(mcreator, generatableElement.getModElement(), true);

		// Verify that BlocklyPanels are fully loaded
		if (modElementGUI instanceof IBlocklyPanelHolder panelHolder) {
			CountDownLatch latch = new CountDownLatch(1);

			// Prepare a listener to detect if BlocklyPanel(s) are responding
			Set<BlocklyPanel> blocklyPanels = new HashSet<>();
			panelHolder.addBlocklyChangedListener((blocklyPanel, jsEventTriggeredChange) -> {
				if (jsEventTriggeredChange) {
					blocklyPanels.add(blocklyPanel);
					if (blocklyPanels.equals(panelHolder.getBlocklyPanels()))
						latch.countDown();
				}
			});

			// Give it time for BlocklyPanel(s) to load and propagate the event
			latch.await(10, TimeUnit.SECONDS);
		}

		AggregatedValidationResult validationResult = modElementGUI.validateAllPages();

		List<String> errors = new ArrayList<>();
		for (ValidationResult result : validationResult.getGroupedValidationResults()) {
			if (result.type() == ValidationResult.Type.ERROR) {
				errors.add(result.message());
				break;
			}
		}

		if (!errors.isEmpty()) {
			throw new Exception("Validation failed: " + String.join(", ", errors));
		}

		return modElementGUI.getElementFromGUI();
	}

}

