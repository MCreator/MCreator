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
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.mcp.McpJson;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.mcp.tools.utils.JsonDefinitionMergePatch;
import net.mcreator.ui.mcp.tools.utils.ModElementNameValidation;
import net.mcreator.ui.modgui.IBlocklyPanelHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModElementTool extends MCreatorMcpTool<ModElementTool.Args> {

	private static final Gson gson = McpJson.lenientGson();

	public static class Args {
		public Action actionType;
		public String elementName;
		@Nullable public String elementType;
		@SchemaDescription("""
				Mod element JSON definition. Use get_mod_element_schema or read existing elements for format hints.\
				Pass full JSON definition for ADD and REPLACE. Partial definition for PATCH (merged into current definition;\
				null values in the patch remove keys; objects are merged recursively; arrays/scalars are replaced).""")
		@Nullable public Map<String, Object> elementJSONDefinition;

		public enum Action {
			READ, ADD, REPLACE, PATCH, REMOVE
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
				A tool to read mod element (JSON definition and mod element metadata), replace, patch, or add mod elements to the workspace.\
				Type and JSON used only for adding. Names of mod elements are always CamelCaseNames\
				REPLACE action edits element by swapping whole JSON definition.\
				REPLACE and ADD action require full element JSON definition, not just changes.\
				Patch action merges elementJSONDefinition into the current definition (partial JSON, null removes keys).""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected Boolean getDestructiveHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ModElementTool.Args input) {
		if (input.actionType == Args.Action.READ) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element not found. Names usually CamelCase"));
			}
			JsonElement definition = safeGeneratableElementToJsonElement(mcreator, modElement.getGeneratableElement());
			Map<String, Object> response = new HashMap<>();
			response.put("_metadata", modElement);
			response.put("elementJSONDefinition", definition);
			return CompletableFuture.completedFuture(ToolResult.object(response));
		} else if (input.actionType == Args.Action.REPLACE) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element not found. Names usually CamelCase"));
			}
			String suggestedJSON = gson.toJson(input.elementJSONDefinition);
			GeneratableElement original = modElement.getGeneratableElement();
			try {
				List<String> jsonValidationNotes = new ArrayList<>();
				GEResult result = safeJSONtoGeneratableElementAndStoreIt(mcreator, modElement, suggestedJSON,
						jsonValidationNotes::add);
				JsonElement jsonElement = safeGeneratableElementToJsonElement(mcreator, result.generatableElement());

				Map<String, Object> response = new HashMap<>();
				response.put("result", "Element replaced");
				return getResultCompletableFuture(suggestedJSON, jsonValidationNotes, result, jsonElement, response);
			} catch (Exception e) {
				try {
					revertGeneratableElement(mcreator, original);
					return CompletableFuture.completedFuture(ToolResult.error(
							"Failed to replace element: " + e.getMessage() + ". Reverted to original definition.", e));
				} catch (Exception e2) {
					return CompletableFuture.completedFuture(ToolResult.error(
							"Failed to replace element: " + e.getMessage() + ". Reverted to original definition. "
									+ "Failed to revert to original definition: " + e2.getMessage(), e));
				}
			}
		} else if (input.actionType == Args.Action.PATCH) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element not found. Names usually CamelCase"));
			}
			if (input.elementJSONDefinition == null || input.elementJSONDefinition.isEmpty()) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Partial JSON definition must be provided for PATCH action"));
			}
			GeneratableElement original = modElement.getGeneratableElement();
			try {
				JsonElement currentDefinition = safeGeneratableElementToJsonElement(mcreator,
						modElement.getGeneratableElement());
				JsonElement patchedDefinition = JsonDefinitionMergePatch.apply(currentDefinition,
						input.elementJSONDefinition);
				String suggestedJSON = gson.toJson(patchedDefinition);
				List<String> jsonValidationNotes = new ArrayList<>();
				GEResult result = safeJSONtoGeneratableElementAndStoreIt(mcreator, modElement, suggestedJSON,
						jsonValidationNotes::add);
				JsonElement jsonElement = safeGeneratableElementToJsonElement(mcreator, result.generatableElement());

				Map<String, Object> response = new HashMap<>();
				response.put("result", "Element patched");
				return getResultCompletableFuture(suggestedJSON, jsonValidationNotes, result, jsonElement, response);
			} catch (JsonDefinitionMergePatch.MergePatchException e) {
				return CompletableFuture.completedFuture(ToolResult.error("Failed to apply patch: " + e.getMessage()));
			} catch (Exception e) {
				try {
					revertGeneratableElement(mcreator, original);
					return CompletableFuture.completedFuture(ToolResult.error(
							"Failed to patch element: " + e.getMessage() + ". Reverted to original definition.", e));
				} catch (Exception e2) {
					return CompletableFuture.completedFuture(ToolResult.error(
							"Failed to patch element: " + e.getMessage() + ". Reverted to original definition. "
									+ "Failed to revert to original definition: " + e2.getMessage(), e));
				}
			}
		} else if (input.actionType == Args.Action.ADD) {
			if (input.elementType == null || input.elementJSONDefinition == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element type and JSON definition must be provided for adding"));
			}

			String elementName;
			try {
				elementName = ModElementNameValidation.normalizeAndValidateName(mcreator.getWorkspace(),
						input.elementName);
			} catch (IllegalArgumentException e) {
				return CompletableFuture.completedFuture(ToolResult.error(e.getMessage()));
			}

			ModElementType<?> type = ModElementTypeLoader.getModElementType(input.elementType.toLowerCase(Locale.ROOT));
			ModElement modElement = new ModElement(mcreator.getWorkspace(), elementName, type);
			mcreator.getWorkspace().addModElement(modElement);
			try {
				String suggestedJSON = gson.toJson(input.elementJSONDefinition);
				List<String> jsonValidationNotes = new ArrayList<>();
				GEResult result = safeJSONtoGeneratableElementAndStoreIt(mcreator, modElement, suggestedJSON,
						jsonValidationNotes::add);
				JsonElement jsonElement = safeGeneratableElementToJsonElement(mcreator, result.generatableElement());

				Map<String, Object> response = new HashMap<>();
				response.put("result", "Element added");
				return getResultCompletableFuture(suggestedJSON, jsonValidationNotes, result, jsonElement, response);
			} catch (Exception e) {
				mcreator.getWorkspace().removeModElement(modElement);
				return CompletableFuture.completedFuture(
						ToolResult.error("Failed to add element: " + e.getMessage(), e));
			}
		} else if (input.actionType == Args.Action.REMOVE) {
			ModElement modElement = mcreator.getWorkspace().getModElementByName(input.elementName);
			if (modElement == null) {
				return CompletableFuture.completedFuture(
						ToolResult.error("Element not found. Names usually CamelCase"));
			}
			List<String> references = ReferencesFinder.searchModElementUsages(mcreator.getWorkspace(), modElement)
					.stream().filter(reference -> !reference.equals(modElement)).map(ModElement::getName).sorted()
					.toList();
			if (!references.isEmpty()) {
				return CompletableFuture.completedFuture(ToolResult.error(
						"Element not removed because it is used by other mod elements. Remove these usages first: "
								+ String.join(", ", references)));
			}
			mcreator.getWorkspace().removeModElement(modElement);
			ThreadUtil.runOnSwingThreadAndWait(mcreator::reloadWorkspaceTabContents);
			return CompletableFuture.completedFuture(ToolResult.text("Element removed"));
		} else {
			return CompletableFuture.completedFuture(ToolResult.error("Invalid action type"));
		}
	}

	@Nonnull
	private CompletableFuture<ToolResult> getResultCompletableFuture(String suggestedJSON,
			List<String> jsonValidationNotes, GEResult result, JsonElement jsonElement, Map<String, Object> response) {
		if (!result.validationResults().isEmpty()) {
			response.put("validationResults", result.validationResults());
		}
		if (!gson.toJson(jsonElement).equals(suggestedJSON)) {
			response.put("warning", "JSON definition was changed during processing");
			response.put("actualJSONDefinition", jsonElement);
		}
		if (!jsonValidationNotes.isEmpty()) {
			response.put("jsonValidationNotes", jsonValidationNotes);
		}
		return CompletableFuture.completedFuture(ToolResult.object(response));
	}

	private GEResult safeJSONtoGeneratableElementAndStoreIt(MCreator mcreator, ModElement modElement, String json,
			@Nullable Consumer<String> validationLog) throws Exception {
		JsonObject root = new JsonObject();
		root.add("_fv", new JsonPrimitive(GeneratableElement.formatVersion));
		root.add("_type", gson.toJsonTree(modElement.getType().getRegistryName()));
		root.add("definition", JsonParser.parseString(json));
		json = gson.toJson(root);

		GeneratableElement element = mcreator.getModElementManager()
				.fromJSONtoGeneratableElement(json, modElement, validationLog);

		// store GE so ModElementGUI reads the GE correctly
		mcreator.getModElementManager().storeModElement(element);

		GEResult result = validateThroughUI(mcreator, element);
		element = result.generatableElement();

		// Persist GUI-normalized definition (dropdown defaults, computed fields, etc.)
		mcreator.getModElementManager().storeModElement(element);

		mcreator.getGenerator().generateBase(true); // use variant that throws TemplateGeneratorException
		mcreator.getGenerator().generateElement(element, true); // use variant that throws TemplateGeneratorException
		mcreator.getModElementManager().storeModElementPicture(element);
		modElement.reinit(mcreator.getWorkspace());

		mcreator.getWorkspace().markDirty();

		reloadUI(mcreator, modElement);

		return result;
	}

	private void revertGeneratableElement(MCreator mcreator, GeneratableElement element)
			throws TemplateGeneratorException {
		mcreator.getModElementManager().storeModElement(element);

		mcreator.getGenerator().generateBase(true); // use variant that throws TemplateGeneratorException
		mcreator.getGenerator().generateElement(element, true); // use variant that throws TemplateGeneratorException
		mcreator.getModElementManager().storeModElementPicture(element);
		element.getModElement().reinit(mcreator.getWorkspace());

		mcreator.getWorkspace().markDirty();
	}

	private JsonElement safeGeneratableElementToJsonElement(MCreator mcreator, GeneratableElement element) {
		String json = mcreator.getModElementManager().generatableElementToJSON(element);
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		return jsonObject.get("definition");
	}

	private GEResult validateThroughUI(MCreator mcreator, GeneratableElement generatableElement) throws Exception {
		AtomicReference<ModElementGUI<?>> modElementGUIRef = new AtomicReference<>();

		ThreadUtil.runOnSwingThreadAndWait(() -> {
			ModElementGUI<?> modElementGUI = generatableElement.getModElement().getType()
					.getModElementGUI(mcreator, generatableElement.getModElement(), true);

			// Ensure data lists are definitely holding the latest options
			modElementGUI.reloadDataLists();

			modElementGUIRef.set(modElementGUI);
		});

		ModElementGUI<?> modElementGUI = modElementGUIRef.get();

		try {
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

				panelHolder.forceLoadPanels();

				// Give it time for BlocklyPanel(s) to load and propagate the event
				if (!latch.await(10, TimeUnit.SECONDS)) {
					throw new Exception("BlocklyPanel(s) did not respond within 10 seconds");
				}
			}

			AtomicReference<AggregatedValidationResult> validationResultRef = new AtomicReference<>();
			ThreadUtil.runOnSwingThreadAndWait(() -> validationResultRef.set(modElementGUI.validateAllPages()));
			AggregatedValidationResult validationResult = validationResultRef.get();

			List<String> errors = new ArrayList<>();
			List<ValidationResult> validationResults = validationResult.getGroupedValidationResults();
			for (ValidationResult result : validationResults) {
				if (result.type() == ValidationResult.Type.ERROR) {
					errors.add(result.message());
				}
			}

			if (!errors.isEmpty()) {
				throw new Exception("Validation failed: " + String.join(", ", errors));
			}

			AtomicReference<GeneratableElement> elementFromGUIRef = new AtomicReference<>();
			ThreadUtil.runOnSwingThreadAndWait(() -> {
				modElementGUI.afterGeneratableElementGenerated(true);
				elementFromGUIRef.set(modElementGUI.getElementFromGUI());
			});

			return new GEResult(elementFromGUIRef.get(), validationResults);
		} finally {
			// the validation GUI is never shown as a tab, so release its resources (e.g. native Blockly panels) here
			ThreadUtil.runOnSwingThreadAndWait(modElementGUI::onViewClosed);
		}
	}

	private synchronized void reloadUI(MCreator mcreator, ModElement element) {
		ThreadUtil.runOnSwingThreadAndWait(() -> {
			mcreator.reloadWorkspaceTabContents();
			MCreatorTabs.Tab currentTab = mcreator.getTabs().getCurrentTab();
			if (currentTab != null && currentTab.getContent() instanceof ModElementGUI<?> modElementGUI) {
				if (modElementGUI.getModElement().equals(element)) {
					mcreator.getTabs().closeTab(currentTab);
					modElementGUI = element.getType().getModElementGUI(mcreator, element, true);
					if (modElementGUI != null) {
						modElementGUI.showView();
					}
				}
			}
		});
	}

	record GEResult(GeneratableElement generatableElement, List<ValidationResult> validationResults) {}

}

