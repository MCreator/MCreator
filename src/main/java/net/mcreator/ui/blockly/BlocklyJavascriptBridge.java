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

import net.mcreator.blockly.BlocklyVariables;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.element.types.Procedure;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.*;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.dialogs.AIConditionEditor;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BlocklyJavascriptBridge {

	private static final Logger LOG = LogManager.getLogger("Blockly JS Bridge");

	private final Runnable blocklyEvent;
	private final MCreator mcreator;

	BlocklyJavascriptBridge(@Nonnull MCreator mcreator, @Nonnull Runnable blocklyEvent) {
		this.blocklyEvent = blocklyEvent;
		this.mcreator = mcreator;
	}

	// these methods are called from JavaScript so we suppress warnings
	@SuppressWarnings("unused") public void triggerEvent() {
		blocklyEvent.run();
	}

	@SuppressWarnings("unused") public String startBlockForEditor(String editorName) {
		BlocklyEditorType bet = BlocklyEditorType.fromName(editorName);
		return bet == null ? null : bet.startBlockName();
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

	@SuppressWarnings("unused") public void openColorSelector(String color, Consumer<String> callback) {
		SwingUtilities.invokeLater(() -> {
			Color newColor = JColor.openDialog(mcreator,
					L10N.t("dialog.image_maker.tools.component.colorselector_select_foreground"), Color.decode(color));
			AtomicReference<String> colorString = new AtomicReference<>(newColor == null ?
					null :
					String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue()));
			callback.accept(colorString.get());
		});
	}

	@SuppressWarnings("unused") public void openMCItemSelector(String type, Consumer<String> callback) {
		SwingUtilities.invokeLater(() -> {
			MCItem selected = MCItemSelectorDialog.openSelectorDialog(mcreator,
					"allblocks".equals(type) ? ElementUtil::loadBlocks : ElementUtil::loadBlocksAndItems);
			callback.accept(selected == null ? null : selected.getName());
		});
	}

	@SuppressWarnings("unused") public void openAIConditionEditor(String data, Consumer<String> callback) {
		SwingUtilities.invokeLater(() -> {
			List<String> retval = AIConditionEditor.open(mcreator, data.split(","));
			callback.accept(StringUtils.join(retval, ','));
		});
	}

	/**
	 * Opens a data list selector window for the searchable Blockly selectors
	 *
	 * @param entryProvider The function that provides the entries from a given workspace
	 * @param type          The type of the data list, used for the selector title and message
	 * @return A {"value", "readable name"} pair, or the default entry if no entry was selected
	 */
	private String[] openDataListEntrySelector(Function<Workspace, Collection<DataListEntry>> entryProvider,
			String type) {
		String[] retval = new String[] { "", L10N.t("blockly.extension.data_list_selector.no_entry") };
		DataListEntry selected = DataListSelectorDialog.openSelectorDialog(mcreator, entryProvider,
				L10N.t("dialog.selector.title"), L10N.t("dialog.selector." + type + ".message"));
		if (selected != null) {
			retval[0] = selected.getName();
			retval[1] = selected.getReadableName();
		}
		return retval;
	}

	/**
	 * Opens a string selector window for the searchable Blockly selectors
	 *
	 * @param entryProvider The function that provides the strings from a given workspace
	 * @param type          The type of the data list, used for the selector title and message
	 * @return A {"value", "value"} pair (strings don't have readable names!), or the default entry if no string was selected
	 */
	private String[] openStringEntrySelector(Function<Workspace, String[]> entryProvider, String type) {
		String[] retval = new String[] { "", L10N.t("blockly.extension.data_list_selector.no_entry") };
		String selected = StringSelectorDialog.openSelectorDialog(mcreator, entryProvider,
				L10N.t("dialog.selector.title"), L10N.t("dialog.selector." + type + ".message"));
		if (selected != null) {
			retval[0] = selected;
			retval[1] = selected;
		}
		return retval;
	}

	private String getEntrySelectorDialogLangKeyType(String type) {
		return switch (type) {
			case "entity", "spawnableEntity", "customEntity" -> "entity";
			case "fluid" -> "fluids";
			case "structure" -> "structures";
			case "gamerulesboolean", "gamerulesnumber" -> "gamerules";
			case "eventparametersnumber", "eventparametersboolean" -> "eventparameters";
			case "arrowProjectile" -> "projectiles";
			case "configuredfeature" -> "configured_features";
			case "entitydata_logic", "entitydata_integer", "entitydata_string" -> "entity_data";
			case "dimensionCustomWithPortal", "dimensionCustom" -> "dimension";
			default -> type;
		};
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
			@Nullable String customEntryProviders, Consumer<String[]> callback) {
		SwingUtilities.invokeLater(() -> {
			String[] retval;
			if ("global_triggers".equals(type)) {
				String[] selectedEntry = openDataListEntrySelector(w -> ext_triggers.entrySet().stream()
						.map(entry -> (DataListEntry) new DataListEntry.Dummy(entry.getKey()) {{
							setReadableName(entry.getValue());
						}}).toList(), "global_trigger");

				// Legacy: for global triggers, "no_ext_trigger" is used to indicate no selected value, whereas normally it is ""
				if (selectedEntry[0].isEmpty()) {
					selectedEntry = new String[] { "no_ext_trigger", L10N.t("trigger.no_ext_trigger") };
				}
				retval = selectedEntry;
			} else if (type.startsWith("procedure_retval_")) {
				var variableType = VariableTypeLoader.INSTANCE.fromName(
						Strings.CS.removeStart(type, "procedure_retval_"));
				retval = openStringEntrySelector(w -> ElementUtil.getProceduresOfType(w, variableType), "procedure");
			} else {
				String[] arrayList = BlocklyElementUtil.getStringArrayForEntrySelector(mcreator.getWorkspace(), type,
						customEntryProviders);
				if (arrayList != null) {
					retval = openStringEntrySelector(w -> arrayList, getEntrySelectorDialogLangKeyType(type));
				} else {
					retval = openDataListEntrySelector(
							w -> BlocklyElementUtil.getDataListEntriesForEntrySelector(w, type, typeFilter,
									customEntryProviders), getEntrySelectorDialogLangKeyType(type));
				}
			}
			callback.accept(retval);
		});
	}

	private final Map<String, String> ext_triggers = new LinkedHashMap<>() {{
		put("no_ext_trigger", L10N.t("trigger.no_ext_trigger"));
	}};

	void addExternalTrigger(ExternalTrigger external_trigger) {
		ext_triggers.put(external_trigger.getID(), external_trigger.getName());
	}

	@SuppressWarnings("unused") public Dependency.BlocklyDependency[] getDependencies(String procedureName) {
		ModElement me = mcreator.getWorkspace().getModElementByName(procedureName);
		return me != null && me.getGeneratableElement() instanceof Procedure procedure ?
				procedure.getDependencies().stream().map(Dependency.BlocklyDependency::new)
						.toArray(Dependency.BlocklyDependency[]::new) :
				new Dependency.BlocklyDependency[0];
	}

	@SuppressWarnings("unused") public String t(String key) {
		return L10N.t(key);
	}

	@SuppressWarnings("unused") public String[] getListOf(String type) {
		Workspace workspace = mcreator.getWorkspace();

		// Legacy mapping
		if (type.equals("direction"))
			type = "directions";

		// check if the data list exists and returns it if true
		if (!DataListLoader.loadDataList(type).isEmpty())
			return ElementUtil.getDataListAsStringArray(type);

		return new String[] { "" };
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
		if (value.startsWith(NameMapper.MCREATOR_PREFIX))
			return value.substring(7);

		String datalist;
		switch (type) {
		case "entity", "spawnableEntity" -> datalist = "entities";
		case "biome" -> datalist = "biomes";
		case "arrowProjectile", "projectiles" -> datalist = "projectiles";
		case "eventparametersnumber", "eventparametersboolean" -> datalist = "eventparameters";
		case "sound" -> datalist = "sounds";
		case "direction" -> datalist = "directions";
		case "global_triggers" -> {
			return ext_triggers.get(value);
		}
		default -> datalist = type;
		}

		var map = DataListLoader.loadDataMap(datalist);
		var entry = map.get(value);
		return entry != null ? entry.getReadableName() : "";
	}

}
