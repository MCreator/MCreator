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
import net.mcreator.element.BaseType;
import net.mcreator.element.ModElementType;
import net.mcreator.io.OS;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.AIConditionEditor;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.elements.VariableElementTypeLoader;
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

	// this methods are called from JavaScript so we suppress warnings
	@SuppressWarnings("unused") public void triggerEvent() {
		blocklyEvent.run();
		if (listener != null)
			listener.event();
	}

	@SuppressWarnings("unused") public String getMCItemURI(String name) {
		ImageIcon base = new ImageIcon(ImageUtils.resize(MinecraftImageGenerator.generateItemSlot(), 36, 36));
		ImageIcon image;
		if (name != null && !name.equals("") && !name.equals("null"))
			image = ImageUtils
					.drawOver(base, MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), name), 2, 2, 32, 32);
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

	private final Map<String, String> ext_triggers = new LinkedHashMap<String, String>() {{
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
		case "gamemode":
			return ElementUtil.getAllGameModes();
		case "biomedictionary":
			return ElementUtil.loadBiomeDictionaryTypes();
		case "damagesource":
			return ElementUtil.getAllDamageSources();
		case "achievement":
			return ElementUtil.loadAllAchievements(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "potion":
			return ElementUtil.loadAllPotionEffects(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
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
			retval = workspace.getModElements().stream().filter(mu -> mu.getType().getBaseType() == BaseType.DIMENSION)
					.map(mu -> "CUSTOM:" + mu.getName()).collect(Collectors.toList());
			break;
		case "material":
			retval = ElementUtil.loadMaterials().stream().map(DataListEntry::getName).collect(Collectors.toList());
			break;
		case "rangeditem":
			retval = ListUtils.merge(Collections.singleton("Arrow"),
					workspace.getModElements().stream().filter(var -> var.getType() == ModElementType.RANGEDITEM)
							.map(ModElement::getName).collect(Collectors.toList()));
			break;
		case "planttype":
			return ElementUtil.getAllPlantTypes();
		default:
			retval = new ArrayList<>();
		}

		//We finish by checking if type is a call procedure with return value
		if(type.contains("procedure_retval_")) {
			retval = workspace.getModElements().stream().filter(mod -> {
				if (mod.getType() == ModElementType.PROCEDURE) {
					VariableElementType returnTypeCurrent = mod.getMetadata("return_type") != null ?
							VariableElementTypeLoader.getVariableFromType((String) mod.getMetadata("return_type")) :
							null;
					return returnTypeCurrent == VariableElementTypeLoader.getVariableFromType(StringUtils.removeStart(type, "procedure_retval_"));
				}
				return false;
			}).map(ModElement::getName).collect(Collectors.toList());
		}

		if (retval.size() <= 0)
			return new String[] { "" };

		return retval.toArray(new String[0]);
	}

	public void setJavaScriptEventListener(JavaScriptEventListener listener) {
		this.listener = listener;
	}

}
