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

package net.mcreator.minecraft;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class DataListLoader {

	private static final Logger LOG = LogManager.getLogger("Data List Loader");

	private static final Map<String, LinkedHashMap<String, DataListEntry>> cache = new HashMap<>();

	public static void preloadCache() {
		Set<String> fileNames = PluginLoader.INSTANCE.getResources("datalists", Pattern.compile(".*\\.yaml"));
		for (String res : fileNames) {
			String datalistname = res.split("datalists/")[1].replace(".yaml", "");
			loadDataList(datalistname);
		}
	}

	public static Map<String, LinkedHashMap<String, DataListEntry>> getCache() {
		return cache;
	}

	public static List<DataListEntry> loadDataList(String listName) {
		return loadDataMap(listName).values().stream().toList();
	}

	public static Map<String, DataListEntry> loadDataMap(String listName) {
		if (!cache.containsKey(listName)) {
			LinkedHashMap<String, DataListEntry> list = new LinkedHashMap<>();

			try {
				Enumeration<URL> res = PluginLoader.INSTANCE.getResources("datalists/" + listName + ".yaml");
				Collections.list(res).forEach(resource -> {
					YamlReader reader = new YamlReader(FileIO.readResourceToString(resource));
					try {
						List<?> objects = (List<?>) reader.read();
						for (Object elementObj : objects) {
							if (elementObj instanceof String stringObj) {
								if (list.containsKey(stringObj))
									LOG.warn("Duplicate datalist key: " + elementObj);
								list.put(stringObj, new DataListEntry(stringObj));
							} else if (elementObj instanceof Map<?, ?> element) {
								for (Map.Entry<?, ?> mapEntry : element.entrySet()) {
									if (mapEntry.getValue() == null) {
										String elementName = (String) mapEntry.getKey();

										DataListEntry entry = new DataListEntry(elementName);
										entry.setReadableName((String) element.get("readable_name"));
										entry.setType((String) element.get("type"));
										entry.setDescription((String) element.get("description"));
										entry.setOther(element.get("other"));
										entry.setTexture((String) element.get("texture"));

										if (element.get("required_apis") instanceof List<?> apis)
											entry.setRequiredAPIs(apis.stream().map(Object::toString).toList());

										if (list.containsKey(elementName))
											LOG.warn("Duplicate datalist key: " + elementName);
										if (listName.equals("blocksitems")) {
											MCItem mcitem = new MCItem(entry);
											if (element.get("subtypes") != null) {
												mcitem.setSubtypes(
														Boolean.parseBoolean((String) element.get("subtypes")));
											}
											list.put(elementName, mcitem);
										} else {
											list.put(elementName, entry);
										}
									}
								}
							}
						}
					} catch (YamlException e) {
						LOG.error(e.getMessage(), e);
					}
				});
			} catch (IOException e) {
				LOG.error("Failed to load datalist resource", e);
			}

			cache.put(listName, list);
			LOG.debug("Added " + listName + " datamap to cache");
		}

		return cache.get(listName);
	}
}
