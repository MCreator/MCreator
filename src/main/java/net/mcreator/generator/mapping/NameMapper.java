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

package net.mcreator.generator.mapping;

import net.mcreator.generator.GeneratorTokens;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NameMapper {

	private static final Logger LOG = LogManager.getLogger("Name Mapper");

	public static final String UNKNOWN_ELEMENT = "deleted_mod_element";

	String mappingSource;
	public Workspace workspace;

	public NameMapper(Workspace workspace, String mappingSource) {
		this.mappingSource = mappingSource;
		this.workspace = workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public String getMapping(String origName) {
		return this.getMapping(origName, 0);
	}

	public String getMapping(String origName, int mappingTable) {
		if (origName == null || origName.equals(""))
			return origName;

		Map<?, ?> mapping = workspace.getGenerator().getMappings().getMapping(mappingSource);

		if (mapping == null)
			return origName;

		Object skip_prefixes = mapping.get("_bypass_prefix");
		if (skip_prefixes instanceof String && origName.startsWith((String) skip_prefixes)) {
			return origName;
		} else if (skip_prefixes instanceof List) {
			for (Object skip_prefix : (List<?>) skip_prefixes) {
				if (skip_prefix instanceof String && origName.startsWith((String) skip_prefix))
					return origName;
			}
		}

		String mcreator_prefix = (String) mapping.get("_mcreator_prefix");
		if (mcreator_prefix != null && origName.startsWith(mcreator_prefix)) {
			String mcreator_map_template = (String) mapping.get("_mcreator_map_template");
			if (mcreator_map_template != null) {
				origName = origName.replace(mcreator_prefix, "");
				String retval = GeneratorTokens.replaceTokens(workspace,
						mcreator_map_template.replace("@NAME", origName)
								.replace("@UPPERNAME", origName.toUpperCase(Locale.ENGLISH))
								.replace("@name", origName.toLowerCase(Locale.ENGLISH)).replace("@NAME", origName));
				if (mcreator_map_template.contains("@registryname") || mcreator_map_template.contains(
						"@REGISTRYNAME")) {
					ModElement element = workspace.getModElementByName(origName);
					if (element != null) {
						retval = retval.replace("@registryname", element.getRegistryName())
								.replace("@REGISTRYNAME", element.getRegistryNameUpper());
					} else {
						LOG.warn("Failed to determine registry name for: " + origName);
						retval = retval.replace("@registryname", UNKNOWN_ELEMENT)
								.replace("@REGISTRYNAME", UNKNOWN_ELEMENT.toUpperCase(Locale.ENGLISH));
					}
				}
				return retval;
			} else {
				return origName;
			}
		}

		String mappedName = processMapping(mapping, origName, mappingTable);

		if (mappedName == null) {
			mappedName = processMapping(mapping, "_default", mappingTable);
			if (mappedName == null) {
				return origName;
			}
			return mappedName;
		}

		return mappedName;
	}

	@Nullable private String processMapping(Map<?, ?> mapping, String origName, int mappingTable) {
		String mappedName = null;

		Object mappedObject = mapping.get(origName);

		if (mappedObject instanceof String) {
			if (mappingTable == 0)
				mappedName = (String) mappedObject;
		} else if (mappedObject instanceof List<?> mappingValuesList) {
			if (mappingTable < mappingValuesList.size())
				mappedName = (String) mappingValuesList.get(mappingTable);
		}

		return mappedName;
	}

}
