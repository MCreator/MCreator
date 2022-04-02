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

package net.mcreator.ui.blockly;

import com.google.gson.Gson;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.blockly.java.BlocklyVariables;
import net.mcreator.element.BaseType;
import net.mcreator.element.ModElementType;
import net.mcreator.io.OS;
import net.mcreator.minecraft.*;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.AIConditionEditor;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;
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
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlocklyJavascriptBridge {

	private static final Logger LOG = LogManager.getLogger("Blockly JS Bridge");

	private JavaScriptEventListener listener;
	private final Runnable blocklyEvent;
	private final MCreator mcreator;

	BlocklyJavascriptBridge(@Nonnull MCreator mcreator, @Nonnull Runnable blocklyEvent) {
		this.blocklyEvent = blocklyEvent;
		this.mcreator = mcreator;
	}

	// these methods are called from JavaScript so we suppress warnings
	@SuppressWarnings("unused") public void triggerEvent() {
		blocklyEvent.run();
		if (listener != null)
			listener.event();
	}

	@SuppressWarnings("unused") public String getMCItemURI(String name) {
		ImageIcon base = new ImageIcon(ImageUtils.resize(MinecraftImageGenerator.generateItemSlot(), 36, 36));
		ImageIcon image;
		if (name != null && !name.equals("") && !name.equals("null"))
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
		MCItem.ListProvider blocks;
		if ("allblocks".equals(type)) {
			blocks = ElementUtil::loadBlocks;
		} else {
			blocks = ElementUtil::loadBlocksAndItems;
		}

		String retval = null;

		if (SwingUtilities.isEventDispatchThread()
				|| OS.getOS() == OS.MAC) { // on macOS, EventDispatchThread is shared between JFX and SWING
			MCItem selected = MCItemSelectorDialog.openSelectorDialog(mcreator, blocks);
			if (selected != null)
				retval = selected.getName();
		} else {
			FutureTask<MCItem> query = new FutureTask<>(
					() -> MCItemSelectorDialog.openSelectorDialog(mcreator, blocks));
			try {
				SwingUtilities.invokeLater(query);
				MCItem selected = query.get();
				if (selected != null)
					retval = selected.getName();
			} catch (InterruptedException | ExecutionException ignored) {
			}
		}

		callback.call("callback", retval);
	}

	/**
	 * Common method to open an entry selector of either data list entries or strings
	 *
	 * @param type     The type of selector to open
	 * @param callback The Javascript object that passes the "value,readableName" pair to the Blockly editor
	 */
	@SuppressWarnings("unused") public void openEntrySelector(@Nonnull String type, JSObject callback) {
		String retval = switch (type) {
			case "entity" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllEntities(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.entity.message"), L10N.t("dialog.selector.entity.title"));
			case "biome" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllBiomes(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.biome.message"), L10N.t("dialog.selector.biome.title"));
			case "sound" -> openStringEntrySelector(ElementUtil::getAllSounds, L10N.t("dialog.selector.sound.message"),
					L10N.t("dialog.selector.sound.title"));
			case "effect" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllPotionEffects(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.potion_effect.message"), L10N.t("dialog.selector.potion_effect.title"));
			case "potion" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllPotions(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.potion.message"), L10N.t("dialog.selector.potion.title"));
			case "achievement" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllAchievements(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.advancement.message"), L10N.t("dialog.selector.advancement.title"));
			case "particle" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllParticles(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.particle.message"), L10N.t("dialog.selector.particle.title"));
			case "procedure" -> openStringEntrySelector(
					w -> w.getModElements().stream().filter(mel -> mel.getType() == ModElementType.PROCEDURE)
							.map(ModElement::getName).toArray(String[]::new),
					L10N.t("dialog.selector.procedure.message"), L10N.t("dialog.selector.procedure.title"));
			case "enchantment" -> openDataListEntrySelector(
					w -> ElementUtil.loadAllEnchantments(w).stream().filter(e -> e.isSupportedInWorkspace(w)).toList(),
					L10N.t("dialog.selector.enchantment.message"), L10N.t("dialog.selector.enchantment.title"));
			default -> {
				if (type.startsWith("procedure_retval_")) {
					var variableType = VariableTypeLoader.INSTANCE.fromName(
							StringUtils.removeStart(type, "procedure_retval_"));
					yield openStringEntrySelector(w -> ElementUtil.getProceduresOfType(w, variableType),
							L10N.t("dialog.selector.procedure.message"), L10N.t("dialog.selector.procedure.title"));
				}

				if (!DataListLoader.loadDataList(type).isEmpty()) {
					yield openDataListEntrySelector(
							w -> DataListLoader.loadDataList(type).stream().filter(e -> e.isSupportedInWorkspace(w))
									.toList(), L10N.t("dialog.selector." + type + ".message"),
							L10N.t("dialog.selector." + type + ".title"));
				}

				yield "," + L10N.t("blockly.extension.data_list_selector.no_entry");
			}
		};

		callback.call("callback", retval);
	}

	/**
	 * Opens a data list selector window for the searchable Blockly selectors
	 *
	 * @param entryProvider The function that provides the entries from a given workspace
	 * @param message       The message of the data list selector window
	 * @param title         The title of the data list selector window
	 * @return A "value,readable name" pair, or the default entry if no entry was selected
	 */
	private String openDataListEntrySelector(Function<Workspace, List<DataListEntry>> entryProvider, String message,
			String title) {
		String retval = "," + L10N.t("blockly.extension.data_list_selector.no_entry");

		if (SwingUtilities.isEventDispatchThread()
				|| OS.getOS() == OS.MAC) { // on macOS, EventDispatchThread is shared between JFX and SWING
			DataListEntry selected = DataListSelectorDialog.openSelectorDialog(mcreator, entryProvider, title, message);
			if (selected != null)
				retval = selected.getName() + "," + selected.getReadableName();
		} else {
			FutureTask<DataListEntry> query = new FutureTask<>(
					() -> DataListSelectorDialog.openSelectorDialog(mcreator, entryProvider, title, message));
			try {
				SwingUtilities.invokeLater(query);
				DataListEntry selected = query.get();
				if (selected != null)
					retval = selected.getName() + "," + selected.getReadableName();
			} catch (InterruptedException | ExecutionException ignored) {
			}
		}

		return retval;
	}

	/**
	 * Opens a string selector window for the searchable Blockly selectors
	 *
	 * @param entryProvider The function that provides the strings from a given workspace
	 * @param message       The message of the string selector window
	 * @param title         The title of the string selector window
	 * @return A "value,value" pair (strings don't have readable names!), or the default entry if no string was selected
	 */
	private String openStringEntrySelector(Function<Workspace, String[]> entryProvider, String message, String title) {
		String retval = "," + L10N.t("blockly.extension.data_list_selector.no_entry");

		if (SwingUtilities.isEventDispatchThread()
				|| OS.getOS() == OS.MAC) { // on macOS, EventDispatchThread is shared between JFX and SWING
			String selected = StringSelectorDialog.openSelectorDialog(mcreator, entryProvider, title, message);
			if (selected != null)
				retval = selected + "," + selected;
		} else {
			FutureTask<String> query = new FutureTask<>(
					() -> StringSelectorDialog.openSelectorDialog(mcreator, entryProvider, title, message));
			try {
				SwingUtilities.invokeLater(query);
				String selected = query.get();
				if (selected != null)
					retval = selected + "," + selected;
			} catch (InterruptedException | ExecutionException ignored) {
			}
		}

		return retval;
	}

	@SuppressWarnings("unused") public void openAIConditionEditor(String data, JSObject callback) {
		List<String> retval = null;

		if (SwingUtilities.isEventDispatchThread()
				|| OS.getOS() == OS.MAC) { // on macOS, EventDispatchThread is shared between JFX and SWING
			retval = AIConditionEditor.open(mcreator, data.split(","));
		} else {
			FutureTask<List<String>> query = new FutureTask<>(() -> AIConditionEditor.open(mcreator, data.split(",")));
			try {
				SwingUtilities.invokeLater(query);
				retval = query.get();
			} catch (InterruptedException | ExecutionException ignored) {
			}
		}

		callback.call("callback", StringUtils.join(retval, ','));
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
		case "gui":
			retval = ElementUtil.loadBasicGUI(workspace);
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
			return ElementUtil.loadAllFluids(workspace);
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
		case "rangeditem":
			return ElementUtil.loadArrowProjectiles(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "throwableprojectile":
			return ElementUtil.loadThrowableProjectiles().stream().map(DataListEntry::getName).toArray(String[]::new);
		case "fireballprojectile":
			return ElementUtil.loadFireballProjectiles().stream().map(DataListEntry::getName).toArray(String[]::new);
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

		if (retval.size() <= 0)
			return new String[] { "" };

		return retval.toArray(new String[0]);
	}

	@SuppressWarnings("unused") public String[] getReadableListOf(String type) {
		return getReadableListOfForWorkspace(mcreator.getWorkspace(), type);
	}

	@SuppressWarnings("unused") public static String[] getReadableListOfForWorkspace(Workspace workspace, String type) {
		List<String> retval;
		return switch (type) {
			case "entity" -> ElementUtil.loadAllEntities(workspace).stream().map(DataListEntry::getReadableName)
					.toArray(String[]::new);
			case "biome" -> ElementUtil.loadAllBiomes(workspace).stream().map(DataListEntry::getReadableName)
					.toArray(String[]::new);
			case "rangeditem" -> ElementUtil.loadArrowProjectiles(workspace).stream()
					.map(DataListEntry::getReadableName).toArray(String[]::new);
			case "fireballprojectile" -> ElementUtil.loadFireballProjectiles().stream().map(DataListEntry::getReadableName)
					.toArray(String[]::new);
			case "throwableprojectile" -> ElementUtil.loadThrowableProjectiles().stream().map(DataListEntry::getReadableName)
					.toArray(String[]::new);
			default -> getListOfForWorkspace(workspace, type);
		};
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
		String datalist;
		switch (type) {
		case "entity" -> datalist = "entities";
		case "biome" -> datalist = "biomes";
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
