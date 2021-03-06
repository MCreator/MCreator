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

package net.mcreator.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.io.net.WebIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.MCreatorApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PluginLoader extends URLClassLoader {

	private static final Logger LOG = LogManager.getLogger("Plugin Loader");

	public static PluginLoader INSTANCE;
	private final List<Plugin> plugins;
	private final Reflections reflections;

	private List<Plugin> pluginsToUpdate = new ArrayList<>();
	private List<String> versions = new ArrayList<>();

	public PluginLoader() {
		super(new URL[] {}, null);

		this.plugins = new ArrayList<>();

		UserFolderManager.getFileFromUserFolder("plugins").mkdirs();

		List<Plugin> pluginsLoadList = new ArrayList<>();
		pluginsLoadList.addAll(listPluginsFromFolder(new File("./plugins/"), true));
		pluginsLoadList.addAll(listPluginsFromFolder(UserFolderManager.getFileFromUserFolder("plugins"), false));

		Collections.sort(pluginsLoadList);

		List<String> idList = pluginsLoadList.stream().map(Plugin::getID).collect(Collectors.toList());

		for (Plugin plugin : pluginsLoadList) {
			if (plugin.getInfo().getDependencies() != null) {
				if (!idList.containsAll(plugin.getInfo().getDependencies())) {
					LOG.warn(plugin.getInfo().getName() + " can not be loaded. The plugin needs " + plugin.getInfo()
							.getDependencies());
					plugin.loaded = false;
					continue;
				}
			}

			try {
				LOG.info("Loading plugin: " + plugin.getID() + " from " + plugin.getFile() + ", weight: " + plugin
						.getWeight());
				if (plugin.getFile().isDirectory()) {
					addURL(plugin.getFile().toURI().toURL());
				} else {
					addURL(new URL("jar:file:" + plugin.getFile().getAbsolutePath() + "!/"));
				}

				plugin.loaded = true;

				if(MCreatorApplication.isInternet) {
					if (plugin.getInfo().getUpdateJsonUrl() != null) {
						if(!plugin.getInfo().getVersion().equals("not specified")) {
							JsonObject version = new GsonBuilder().create()
									.fromJson(WebIO.readURLToString(plugin.getInfo().getUpdateJsonUrl()),
											JsonObject.class);
							if (!version.get(plugin.getID()).getAsJsonObject().get("latest").getAsString()
									.equals(plugin.getPluginVersion())) {
								pluginsToUpdate.add(plugin);
								versions.add(version.get(plugin.getID()).getAsJsonObject().get("latest").getAsString());
							}
						}
					}
				}
			} catch (MalformedURLException e) {
				LOG.error("Failed to add plugin to the loader", e);
			}
		}

		this.reflections = new Reflections(new ResourcesScanner(), this);
	}

	public static void initInstance() {
		INSTANCE = new PluginLoader();
	}

	public Set<String> getResources(Pattern pattern) {
		return this.getResources(null, pattern);
	}

	public Set<String> getResourcesInPackage(String pkg) {
		return this.getResources(pkg, null);
	}

	public Set<String> getResources(@Nullable String pkg, @Nullable Pattern pattern) {
		Set<String> reflectionsRetval =
				pattern != null ? this.reflections.getResources(pattern) : this.reflections.getResources(e -> true);
		if (pkg == null)
			return reflectionsRetval;
		return reflectionsRetval.stream().filter(e -> e.replace("/", ".").startsWith(pkg)).collect(Collectors.toSet());
	}

	public List<Plugin> getPlugins() {
		return plugins;
	}

	synchronized private List<Plugin> listPluginsFromFolder(File folder, boolean builtin) {
		List<Plugin> loadList = new ArrayList<>();

		File[] pluginFiles = folder.listFiles();
		for (File pluginFile : pluginFiles != null ? pluginFiles : new File[0]) {
			Plugin plugin = loadPlugin(pluginFile, builtin);
			if (plugin != null) {
				if (plugins.contains(plugin)) {
					LOG.warn("Trying to load duplicate plugin: " + plugin.getID() + " from: " + plugin.getFile());
					continue;
				}
				plugins.add(plugin);
				loadList.add(plugin);
			}
		}

		return loadList;
	}

	@Nullable synchronized private Plugin loadPlugin(File pluginFile, boolean builtin) {
		if (pluginFile.isDirectory()) {
			File pluginInfoFile = new File(pluginFile, "plugin.json");
			if (pluginInfoFile.isFile()) {
				try {
					String pluginInfo = FileIO.readFileToString(pluginInfoFile);
					Plugin plugin = new Gson().fromJson(pluginInfo, Plugin.class);
					plugin.builtin = builtin;
					plugin.file = pluginFile;
					return validatePlugin(plugin);
				} catch (Exception e) {
					LOG.error("Failed to load plugin from " + pluginFile, e);
				}
			} else {
				File[] pluginFiles = pluginFile.listFiles();
				for (File innerFile : pluginFiles != null ? pluginFiles : new File[0]) {
					if (innerFile.isDirectory())
						listPluginsFromFolder(innerFile, builtin);
				}
			}
		} else if (ZipIO.checkIfZip(pluginFile)) {
			try {
				String pluginInfo = ZipIO.readCodeInZip(pluginFile, "plugin.json");
				Plugin plugin = new Gson().fromJson(pluginInfo, Plugin.class);
				plugin.builtin = builtin;
				plugin.file = pluginFile;
				return validatePlugin(plugin);
			} catch (Exception e) {
				LOG.error("Failed to load plugin from " + pluginFile, e);
			}
		}
		return null;
	}

	@Nullable private Plugin validatePlugin(Plugin plugin) {
		if (!plugin.isCompatible()) {
			LOG.warn("Plugin " + plugin.getID()
					+ " is not compatible with this MCreator version! Skipping this plugin.");
			return null;
		}

		if (plugin.getMinVersion() < 0) {
			LOG.warn("Plugin " + plugin.getID() + " does not specify minversion. Skipping this plugin.");
			return null;
		}

		return plugin;
	}

	public List<Plugin> getPluginsToUpdate() {
		return pluginsToUpdate;
	}

	public List<String> getNewPluginVersions() {
		return versions;
	}
}
