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
import net.mcreator.util.TraceUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NameMapper {

	private static final Logger LOG = LogManager.getLogger("Name Mapper");

	public static final String UNKNOWN_ELEMENT = "deleted_mod_element";

	public static final String MCREATOR_PREFIX = "CUSTOM:";

	public static final String EXTERNAL_PREFIX = "EXTERNAL:";

	private final String mappingSource;
	private Workspace workspace;

	public NameMapper(Workspace workspace, String mappingSource) {
		this.mappingSource = mappingSource;
		this.workspace = workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public String getMappingSource() {
		return mappingSource;
	}

	public String getMapping(String origName) {
		return this.getMapping(origName, 0);
	}

	public String getMapping(String origName, int mappingTable) {
		if (origName == null || origName.isEmpty())
			return origName;

		Map<?, ?> mapping = workspace.getGenerator().getMappings().getMapping(mappingSource);

		if (mapping == null)
			return origName;

		if (origName.startsWith(EXTERNAL_PREFIX)) {
			return origName.replace(EXTERNAL_PREFIX, "");
		}

		Object skip_prefixes = mapping.get("_bypass_prefix");
		if (skip_prefixes instanceof String skipPrefix && origName.startsWith(skipPrefix)) {
			return origName.replace(skipPrefix + "mod:",
					skipPrefix + workspace.getWorkspaceSettings().getModID() + ":");
		} else if (skip_prefixes instanceof List<?> skipPrefixesList) {
			for (Object skip_prefix : skipPrefixesList) {
				if (skip_prefix instanceof String skipPrefix && origName.startsWith(skipPrefix))
					return origName.replace(skipPrefix + "mod:",
							skipPrefix + workspace.getWorkspaceSettings().getModID() + ":");
			}
		}

		if (origName.startsWith(MCREATOR_PREFIX)) {
			Object mcreator_map_template = mapping.get("_mcreator_map_template");
			String toMapTemplate = null;
			String suffix = null;
			if (mcreator_map_template instanceof String) {
				toMapTemplate = (String) mcreator_map_template;
			} else if (mcreator_map_template instanceof List<?> mappingValuesList
					&& mappingTable < mappingValuesList.size()) {
				toMapTemplate = (String) mappingValuesList.get(mappingTable);
			} else if (mcreator_map_template instanceof Map<?, ?> mappingValuesMap) {
				String suffixSeparator = ".";
				if (mapping.get("_suffix_separator") != null) {
					suffixSeparator = (String) mapping.get("_suffix_separator");
				}
				String suffixLookup = StringUtils.substringAfterLast(origName.replace(MCREATOR_PREFIX, ""),
						suffixSeparator);
				if (suffixLookup.isEmpty()) { // If the entry has no suffix, use the "_default" mapping entry
					suffixLookup = "_default";
				} else {
					suffix = suffixSeparator + suffixLookup;
				}
				toMapTemplate = switch (mappingValuesMap.get(suffixLookup)) {
					case String stringEntry -> stringEntry;
					case List<?> listEntry when mappingTable < listEntry.size() -> (String) listEntry.get(mappingTable);
					default -> null;
				};
			}

			if (toMapTemplate != null) {
				// Remove prefix and possibly the suffix
				origName = StringUtils.removeEnd(origName.replace(MCREATOR_PREFIX, ""), suffix);
				String retval = GeneratorTokens.replaceTokens(workspace, toMapTemplate.replace("@NAME", origName)
						.replace("@UPPERNAME", origName.toUpperCase(Locale.ENGLISH))
						.replace("@name", origName.toLowerCase(Locale.ENGLISH)));
				if (toMapTemplate.contains("@registryname") || toMapTemplate.contains("@REGISTRYNAME")) {
					ModElement element = workspace.getModElementByName(origName);
					if (element != null) {
						retval = retval.replace("@registryname", element.getRegistryName())
								.replace("@REGISTRYNAME", element.getRegistryNameUpper());
					} else {
						LOG.warn("({}) Failed to determine registry name for: {}", TraceUtil.tryToFindMCreatorInvoker(),
								origName);
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
		return switch (mapping.get(origName)) {
			case String mappingString when mappingTable == 0 -> mappingString;
			case List<?> mappingValuesList when mappingTable < mappingValuesList.size() ->
					(String) mappingValuesList.get(mappingTable);
			case null, default -> null;
		};
	}

}
