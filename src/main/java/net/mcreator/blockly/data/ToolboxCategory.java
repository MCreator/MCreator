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

package net.mcreator.blockly.data;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.ui.init.L10N;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ToolboxCategory {
	private static final Logger LOG = LogManager.getLogger("Toolbox category");

	String id, name, description, color;
	@Nullable String parent_category;

	boolean api;
	boolean is_expanded;

	@Nullable transient ToolboxCategory parent = null;

	public String getName() {
		String localized_name = L10N.t("blockly.category." + id);
		if (localized_name != null)
			return localized_name;

		return name;
	}

	public String getDescription() {
		String localized_desc = L10N.t("blockly.category." + id + ".description");
		if (localized_desc != null)
			return localized_desc;

		return description;
	}

	@Nullable public ToolboxCategory getParent() {
		return parent;
	}

	/**
	 * Returns the color of this toolbox category. If the field is a valid hex color code, it's returned as-is.
	 * If it's a valid integer, it's treated as a hue to get the color with the correct saturation and value.
	 *
	 * @return The color of this toolbox category, or black if it's badly formatted.
	 */
	public Color getColor() {
		try {
			if (!color.startsWith("#"))
				return BlocklyBlockUtil.getBlockColorFromHUE(Integer.parseInt(color));
			else
				return Color.decode(color);
		} catch (Exception e) {
			LOG.warn("The color for toolbox category {} isn't formatted correctly. Using black color for it",
					getName());
			return Color.BLACK;
		}
	}

	private static ToolboxCategory builtin(String id, String color) {
		ToolboxCategory retval = new ToolboxCategory();
		retval.id = id;
		retval.name = id;
		retval.color = color;
		return retval;
	}

	private static ToolboxCategory builtin(String id, String color, String name) {
		ToolboxCategory retval = new ToolboxCategory();
		retval.id = id;
		retval.name = name;
		retval.color = color;
		return retval;
	}

	private static final Map<String, ToolboxCategory> BUILTIN = new HashMap<>() {{
		// toolbox_feature.xml
		put("features", builtin("features", "0"));
		put("orefeatures", builtin("orefeatures", "80"));
		put("treefeatures", builtin("treefeatures", "110"));
		put("trees", builtin("trees", "110"));
		put("treedecorators", builtin("treedecorators", "110"));
		put("advancedfeatures", builtin("advancedfeatures", "340"));
		put("blocks", builtin("blocks.feature", "60"));
		put("intproviders", builtin("intproviders", "230"));
		put("placements", builtin("placements", "130"));
		put("heightplacements", builtin("heightplacements", "190"));
		put("blockpredicates", builtin("blockpredicates", "210"));

		// toolbox_advancement.xml
		put("components", builtin("components", "300"));

		// toolbox_ai_builder.xml
		put("aiadvanced", builtin("advanced", "250"));

		// toolbox_procedure.xml
		put("mcelements", builtin("minecraft_components", "360"));
		put("customvariables", builtin("custom_variables", "150"));
		put("logicloops", builtin("logic_and_loops", "120"));
		put("logicoperations", builtin("logic", "210"));
		put("math", builtin("math", "230"));
		put("text", builtin("text", "160"));
		put("time", builtin("time", "#628c94"));
		put("advanced", builtin("advanced", "250"));

		// toolbox_command.xml
		put("actions", builtin("actions", "250"));
	}};

	/**
	 * Call this to get ToolboxCategory instance for builtin categories.
	 * This is only meant for visual use, e.g. in procedure search.
	 *
	 * @param id the id of the builtin category
	 * @return the ToolboxCategory instance, or a generic one with gray color if the id is unknown
	 */
	public static ToolboxCategory tryGetBuiltin(String id) {
		if (BUILTIN.containsKey(id)) {
			return BUILTIN.get(id);
		} else {
			return builtin(id, "#555555", WordUtils.capitalizeFully(id));
		}
	}

}
