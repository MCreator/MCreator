/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.blockly;

import com.google.gson.Gson;
import javafx.application.Platform;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.blockly.java.BlocklyVariables;
import net.mcreator.element.ModElementType;
import net.mcreator.minecraft.*;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.AIConditionEditor;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlocklyJavascriptBridge {

	private static final Logger LOG = LogManager.getLogger("Blockly JS Bridge");

	private JavaScriptEventListener listener;
	private final Supplier<Boolean> blocklyEvent;
	private final MCreator mcreator;

	private final Object NESTED_LOOP_KEY = new Object();

	BlocklyJavascriptBridge(@Nonnull MCreator mcreator, @Nonnull Supplier<Boolean> blocklyEvent) {
		this.blocklyEvent = blocklyEvent;
		this.mcreator = mcreator;
	}

	// these methods are called from JavaScript so we suppress warnings
	@SuppressWarnings("unused") public void triggerEvent() {
		boolean success = blocklyEvent.get();
		if (success && listener != null)
			listener.event();
	}

	@SuppressWarnings("unused") public String getMCItemURI(String name) {
		ImageIcon base = new ImageIcon(ImageUtils.resize(MinecraftImageGenerator.generateItemSlot(), 36, 36));
		ImageIcon image;
		if (name != null && !name.isEmpty() && !name.equals("null"))
			image = ImageUtils.drawOver(base, MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), name), 2, 2, 32,
					32);
		else
			image = base;

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(ImageUtils.toBufferedImage(image.getImage()), "PNG", os);
			return "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(os.toByteArray());
		} catch (Exception ioe) {
			LOG.error(ioe.getMessage(), ioe);
			return "";
		}
	}

	@SuppressWarnings("unused") public void openMCItemSelector(String type, JSObject callback) {
		SwingUtilities.invokeLater(() -> {
			MCItem selected = MCItemSelectorDialog.openSelectorDialog(mcreator,
					"allblocks".equals(type) ? ElementUtil::loadBlocks : ElementUtil::loadBlocksAndItems);
			Platform.runLater(
					() -> Platform.exitNestedEventLoop(NESTED_LOOP_KEY, selected != null ? selected.getName() : null));
		});

		String retval = (String) Platform.enterNestedEventLoop(NESTED_LOOP_KEY);
		callback.call("callback", retval);
	}

	@SuppressWarnings("unused") public void openAIConditionEditor(String data, JSObject callback) {
		SwingUtilities.invokeLater(() -> {
			List<String> retval = AIConditionEditor.open(mcreator, data.split(","));
			Platform.runLater(() -> Platform.exitNestedEventLoop(NESTED_LOOP_KEY, StringUtils.join(retval, ',')));
		});

		String retval = (String) Platform.enterNestedEventLoop(NESTED_LOOP_KEY);
		callback.call("callback", retval);
	}

	/**
	 * Opens a data list selector window for the searchable Blockly selectors
	 *
	 * @param entryProvider The function that provides the entries from a given workspace
	 * @param type          The type of the data list, used for the selector title and message
	 * @return A {"value", "readable name"} pair, or the default entry if no entry was selected
	 */
	private String[] openDataListEntrySelector(Function<Workspace, List<DataListEntry>> entryProvider, String type) {
		SwingUtilities.invokeLater(() -> {
			String[] retval = new String[] { "", L10N.t("blockly.extension.data_list_selector.no_entry") };
			DataListEntry selected = DataListSelectorDialog.openSelectorDialog(mcreator, entryProvider,
					L10N.t("dialog.selector.title"), L10N.t("dialog.selector." + type + ".message"));
			if (selected != null) {
				retval[0] = selected.getName();
				retval[1] = selected.getReadableName();
			}
			Platform.runLater(() -> Platform.exitNestedEventLoop(NESTED_LOOP_KEY, retval));
		});

		return (String[]) Platform.enterNestedEventLoop(NESTED_LOOP_KEY);
	}

	/**
	 * Opens a string selector window for the searchable Blockly selectors
	 *
	 * @param entryProvider The function that provides the strings from a given workspace
	 * @param type          The type of the data list, used for the selector title and message
	 * @return A {"value", "value"} pair (strings don't have readable names!), or the default entry if no string was selected
	 */
	private String[] openStringEntrySelector(Function<Workspace, String[]> entryProvider, String type) {
		SwingUtilities.invokeLater(() -> {
			String[] retval = new String[] { "", L10N.t("blockly.extension.data_list_selector.no_entry") };
			String selected = StringSelectorDialog.openSelectorDialog(mcreator, entryProvider,
					L10N.t("dialog.selector.title"), L10N.t("dialog.selector." + type + ".message"));
			if (selected != null) {
				retval[0] = selected;
				retval[1] = selected;
				Platform.runLater(() -> Platform.exitNestedEventLoop(NESTED_LOOP_KEY, retval));
			}
		});

		return (String[]) Platform.enterNestedEventLoop(NESTED_LOOP_KEY);
	}

	/**
	 * Common method to open an entry selector of either data list entries or strings
	 *
	 * @param type                 The type of selector to open
	 * @param typeFilter           If present, only entries whose type matches this parameter are loaded
	 * @param customEntryProviders If present, the types of the mod elements that provide custom entries
	 * @param callback             The Javascript object that passes the {"value", "readable name"} pair to the Blockly editor
	 */
	@SuppressWarnings("unused") public void openEntrySelector(@Nonnull String type, @Nullable String typeFilter,
			@Nullable String customEntryProviders, JSObject callback) {
		String[] retval = switch (type) {
			case "entity" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllEntities(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					"entity");
			case "spawnableEntity" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllSpawnableEntities(w).stream().filter(e -> e.isSupportedInWorkspace(w))
							.toList(), "entity");
			case "customEntity" -> openDataListEntrySelector(ElementUtil::loadCustomEntities, "entity");
			case "entitydata_logic" -> openStringEntrySelector(
					w -> ElementUtil.loadEntityDataListFromCustomEntity(w, customEntryProviders,
							PropertyData.LogicType.class).toArray(String[]::new), "entity_data");
			case "entitydata_integer" -> openStringEntrySelector(
					w -> ElementUtil.loadEntityDataListFromCustomEntity(w, customEntryProviders,
							PropertyData.IntegerType.class).toArray(String[]::new), "entity_data");
			case "entitydata_string" -> openStringEntrySelector(
					w -> ElementUtil.loadEntityDataListFromCustomEntity(w, customEntryProviders,
							PropertyData.StringType.class).toArray(String[]::new), "entity_data");
			case "gui" -> openStringEntrySelector(w -> ElementUtil.loadBasicGUIs(w).toArray(String[]::new), "gui");
			case "biome" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllBiomes(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					"biome");
			case "dimension" -> openStringEntrySelector(ElementUtil::loadAllDimensions, "dimension");
			case "dimensionCustom" -> openStringEntrySelector(
					w -> w.getModElements().stream().filter(m -> m.getType() == ModElementType.DIMENSION)
							.map(m -> "CUSTOM:" + m.getName()).toArray(String[]::new), "dimension");
			case "fluid" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllFluids(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					"fluids");
			case "gamerulesboolean" -> openDataListEntrySelector(
					w -> ElementUtil.getAllBooleanGameRules(w).stream().filter(e -> e.isSupportedInWorkspace(w))
							.toList(), "gamerules");
			case "gamerulesnumber" -> openDataListEntrySelector(
					w -> ElementUtil.getAllNumberGameRules(w).stream().filter(e -> e.isSupportedInWorkspace(w))
							.toList(), "gamerules");
			case "sound" -> openStringEntrySelector(ElementUtil::getAllSounds, "sound");
			case "structure" ->
					openStringEntrySelector(w -> w.getFolderManager().getStructureList().toArray(String[]::new),
							"structures");
			case "procedure" -> openStringEntrySelector(
					w -> w.getModElements().stream().filter(mel -> mel.getType() == ModElementType.PROCEDURE)
							.map(ModElement::getName).toArray(String[]::new), "procedure");
			case "arrowProjectile" -> openDataListEntrySelector(
					w -> ElementUtil.loadArrowProjectiles(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					"projectiles");
			default -> {
				if (type.startsWith("procedure_retval_")) {
					var variableType = VariableTypeLoader.INSTANCE.fromName(
							StringUtils.removeStart(type, "procedure_retval_"));
					yield openStringEntrySelector(w -> ElementUtil.getProceduresOfType(w, variableType), "procedure");
				}

				if (!DataListLoader.loadDataList(type).isEmpty()) {
					yield openDataListEntrySelector(w -> ElementUtil.loadDataListAndElements(w, type, true, typeFilter,
							StringUtils.split(customEntryProviders, ',')), type);
				}

				yield new String[] { "", L10N.t("blockly.extension.data_list_selector.no_entry") };
			}
		};

		callback.call("callback", retval[0], retval[1]);
	}

	private final Map<String, String> ext_triggers = new LinkedHashMap<>() {{
		put("no_ext_trigger", L10N.t("trigger.no_ext_trigger"));
	}};

	public void addExternalTrigger(ExternalTrigger external_trigger) {
		ext_triggers.put(external_trigger.getID(), external_trigger.getName());
	}

	@SuppressWarnings("unused") public String t(String key) {
		return L10N.t(key);
	}

	@SuppressWarnings("unused") public String getGlobalTriggers() {
		return new Gson().toJson(ext_triggers, Map.class);
	}

	@SuppressWarnings("unused") public String[] getListOf(String type) {
		return getListOfForWorkspace(mcreator.getWorkspace(), type);
	}

	@SuppressWarnings("unused") public static String[] getListOfForWorkspace(Workspace workspace, String type) {
		List<String> retval;
		//We check for general cases
		switch (type) {
		case "procedure":
			retval = workspace.getModElements().stream().filter(mel -> mel.getType() == ModElementType.PROCEDURE)
					.map(ModElement::getName).collect(Collectors.toList());
			break;
		case "entity":
			return ElementUtil.loadAllEntities(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "spawnableEntity":
			return ElementUtil.loadAllSpawnableEntities(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "gui":
			retval = ElementUtil.loadBasicGUIs(workspace);
			break;
		case "achievement":
			return ElementUtil.loadAllAchievements(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "effect":
			return ElementUtil.loadAllPotionEffects(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "potion":
			return ElementUtil.loadAllPotions(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "gamerulesboolean":
			return ElementUtil.getAllBooleanGameRules(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "gamerulesnumber":
			return ElementUtil.getAllNumberGameRules(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "fluid":
			return ElementUtil.loadAllFluids(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "sound":
			return ElementUtil.getAllSounds(workspace);
		case "particle":
			return ElementUtil.loadAllParticles(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "direction":
			return ElementUtil.loadDirections();
		case "schematic":
			retval = workspace.getFolderManager().getStructureList();
			break;
		case "enhancement":
			return ElementUtil.loadAllEnchantments(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "biome":
			return ElementUtil.loadAllBiomes(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "dimension":
			return ElementUtil.loadAllDimensions(workspace);
		case "dimension_custom":
			retval = workspace.getModElements().stream().filter(mu -> mu.getType() == ModElementType.DIMENSION)
					.map(mu -> "CUSTOM:" + mu.getName()).collect(Collectors.toList());
			break;
		case "material":
			retval = ElementUtil.loadMaterials().stream().map(DataListEntry::getName).collect(Collectors.toList());
			break;
		case "villagerprofessions":
			return ElementUtil.loadAllVillagerProfessions(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		default:
			retval = new ArrayList<>();
		}

		// check if the data list exists and returns it if true
		if (!DataListLoader.loadDataList(type).isEmpty())
			return ElementUtil.getDataListAsStringArray(type);

		// check if type is "call procedure with return value"
		if (type.contains("procedure_retval_")) {
			retval = workspace.getModElements().stream().filter(mod -> {
				if (mod.getType() == ModElementType.PROCEDURE) {
					VariableType returnTypeCurrent = mod.getMetadata("return_type") != null ?
							VariableTypeLoader.INSTANCE.fromName((String) mod.getMetadata("return_type")) :
							null;
					return returnTypeCurrent == VariableTypeLoader.INSTANCE.fromName(
							StringUtils.removeStart(type, "procedure_retval_"));
				}
				return false;
			}).map(ModElement::getName).collect(Collectors.toList());
		}

		if (retval.isEmpty())
			return new String[] { "" };

		return retval.toArray(new String[0]);
	}

	@SuppressWarnings("unused") public boolean isPlayerVariable(String field) {
		return BlocklyVariables.isPlayerVariableForWorkspace(mcreator.getWorkspace(), field);
	}

	/**
	 * Gets the readable name of a data list entry from the type of searchable selector
	 *
	 * @param value The value of the data list entry
	 * @param type  The type of the searchable selector
	 * @return The readable name of the passed entry, or an empty string if it can't find a readable name
	 */
	@SuppressWarnings("unused") public String getReadableNameOf(String value, String type) {
		if (value.startsWith("CUSTOM:"))
			return value.substring(7);

		String datalist;
		switch (type) {
		case "entity", "spawnableEntity" -> datalist = "entities";
		case "biome" -> datalist = "biomes";
		case "arrowProjectile", "projectiles" -> datalist = "projectiles";
		default -> {
			return "";
		}
		}
		return DataListLoader.loadDataMap(datalist).containsKey(value) ?
				DataListLoader.loadDataMap(datalist).get(value).getReadableName() :
				"";
	}

	public void setJavaScriptEventListener(JavaScriptEventListener listener) {
		this.listener = listener;
	}

}
