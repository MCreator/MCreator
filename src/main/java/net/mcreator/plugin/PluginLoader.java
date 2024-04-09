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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.io.net.WebIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.Nullable;
import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>This class detects and then try to load all builtin or custom {@link Plugin}s. </p>
 */
public class PluginLoader extends URLClassLoader {

	private static final Logger LOG = LogManager.getLogger("Plugin Loader");

	public static PluginLoader INSTANCE;

	/**
	 * <p>Set the value to the INSTANCE variable, so we can access values everywhere in the code.</p>
	 */
	public static void initInstance() {
		INSTANCE = new PluginLoader();
	}

	private final Set<Plugin> plugins;

	private final Set<JavaPlugin> javaPlugins;

	// list of plugins that failed to load and are thus not present on plugins list
	private final Set<PluginLoadFailure> failedPlugins;

	private final Set<PluginUpdateInfo> pluginUpdates;

	private final Reflections reflections;

	private final Set<Module> pluginsModules;

	/**
	 * <p>The core of the detection and loading</p>
	 */
	public PluginLoader() {
		super(new URL[] {}, null);

		this.plugins = new HashSet<>();
		this.failedPlugins = new HashSet<>();
		this.javaPlugins = new HashSet<>();
		this.pluginUpdates = new HashSet<>();
		this.pluginsModules = new HashSet<>();

		UserFolderManager.getFileFromUserFolder("plugins").mkdirs();

		List<Plugin> pluginsLoadList = new ArrayList<>();
		pluginsLoadList.addAll(listPluginsFromFolder(new File("./plugins/"), true));
		pluginsLoadList.addAll(listPluginsFromFolder(UserFolderManager.getFileFromUserFolder("plugins"), false));

		if (System.getenv("MCREATOR_PLUGINS_FOLDER") != null)
			pluginsLoadList.addAll(listPluginsFromFolder(new File(System.getenv("MCREATOR_PLUGINS_FOLDER")), false));

		Collections.sort(pluginsLoadList);

		Set<String> idList = pluginsLoadList.stream().map(Plugin::getID).collect(Collectors.toSet());

		for (Plugin plugin : pluginsLoadList) {
			if (plugin.getInfo().getDependencies() != null) {
				if (!idList.containsAll(plugin.getInfo().getDependencies())) {
					LOG.warn(plugin.getInfo().getName() + " can not be loaded. The plugin needs " + plugin.getInfo()
							.getDependencies());
					plugin.loaded_failure = "missing dependencies";
					continue;
				}
			}

			try {
				LOG.info("Loading plugin: " + plugin.getID() + " from " + plugin.getFile() + ", weight: "
						+ plugin.getWeight());
				addURL(plugin.toURL());

				if (PreferencesManager.PREFERENCES.hidden.enableJavaPlugins.get() && plugin.isJavaPlugin()) {
					@SuppressWarnings("resource") DynamicURLClassLoader javaPluginCL = new DynamicURLClassLoader(
							"PluginClassLoader-" + plugin.getID(), new URL[] {},
							Thread.currentThread().getContextClassLoader()) {
						@Override protected Class<?> findClass(String name) throws ClassNotFoundException {
							try {
								return super.findClass(name);
							} catch (Exception e) {
								for (StackTraceElement element : e.getStackTrace()) {
									if (element.getClassName().equals(Introspector.class.getName())) {
										// If class not found was triggered due to Introspector looking for
										// XXXBeanInfo class or XXXCustomizer class, we can ignore this and
										// not log error or mark plugin as failed by setting loaded_failure
										throw e;
									}
								}

								plugin.loaded_failure =
										"internal error: " + e.getClass().getSimpleName() + ": " + e.getMessage();
								LOG.error("Failed to load class " + name + " for plugin " + plugin.getID(), e);
								throw e;
							}
						}
					};

					javaPluginCL.addURL(plugin.toURL());

					pluginsModules.add(javaPluginCL.getUnnamedModule());

					Class<?> clazz = javaPluginCL.loadClass(plugin.getJavaPlugin());
					Constructor<?> ctor = clazz.getConstructor(Plugin.class);
					JavaPlugin javaPlugin = (JavaPlugin) ctor.newInstance(plugin);
					javaPlugins.add(javaPlugin);
				} else if (plugin.isJavaPlugin()) {
					LOG.warn(plugin.getID() + " is Java plugin, but Java plugins are disabled in preferences");

					plugin.loaded_failure = "Java plugins disabled";
				}
			} catch (Exception e) {
				plugin.loaded_failure = "Load error: " + e.getMessage();
				LOG.error("Failed to load plugin " + plugin.getID(), e);
			}
		}

		this.reflections = new Reflections(
				new ConfigurationBuilder().setClassLoaders(new ClassLoader[] { this }).setUrls(getURLs())
						.setScanners(Scanners.Resources).setExpandSuperTypes(false));

		checkForPluginUpdates();
	}

	/**
	 * @param pattern <p>Returned file names will need to follow this {@link Pattern}.</p>
	 * @return <p>The path into a {@link Plugin} of all files following the provided {@link Pattern}.</p>
	 */
	public Set<String> getResources(Pattern pattern) {
		return this.getResources(null, pattern);
	}

	/**
	 * @param pkg <p>The path of directories the method will use to access wanted files. Sub folders need to be split with a dot.</p>
	 * @return <p>The path into a {@link Plugin} of all files inside the provided folder.</p>
	 */
	public Set<String> getResourcesInPackage(String pkg) {
		return this.getResources(pkg, null);
	}

	/**
	 * @param pkg     <p>The path of directories the method will use to access wanted files. Sub folders need to be split with a dot.</p>
	 * @param pattern <p>Returned file names will need to follow this {@link Pattern}.</p>
	 * @return <p>The path into a {@link Plugin} of all files inside the provided folder following the provided {@link Pattern} .</p>
	 */
	public Set<String> getResources(@Nullable String pkg, @Nullable Pattern pattern) {
		Set<String> reflectionsRetval =
				pattern != null ? this.reflections.getResources(pattern) : this.reflections.getResources(".*");
		if (pkg == null)
			return reflectionsRetval;
		return reflectionsRetval.stream().filter(e -> e.replace("/", ".").startsWith(pkg)).collect(Collectors.toSet());
	}

	/**
	 * @return <p> A {@link List} of all loaded plugins.</p>
	 */
	public Collection<Plugin> getPlugins() {
		return Collections.unmodifiableCollection(plugins);
	}

	/**
	 * @return <p> A {@link List} of all loaded Java plugins.</p>
	 */
	protected Collection<JavaPlugin> getJavaPlugins() {
		return Collections.unmodifiableCollection(javaPlugins);
	}

	/**
	 * @return <p>A list of all plugin updates detected.</p>
	 */
	public Collection<PluginUpdateInfo> getPluginUpdates() {
		return Collections.unmodifiableCollection(pluginUpdates);
	}

	/**
	 * @return <p>A list of all plugin modules.</p>
	 */
	public Collection<Module> getPluginModules() {
		return Collections.unmodifiableCollection(pluginsModules);
	}

	synchronized private List<Plugin> listPluginsFromFolder(File folder, boolean builtin) {
		LOG.debug("Searching for plugins in: " + folder);

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
					failedPlugins.add(new PluginLoadFailure(FilenameUtils.getBaseName(pluginFile.getName()), pluginFile,
							"IO error: " + e.getMessage()));
				}
			} else if (!builtin) { // we don't load builtin plugins recursively
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
				failedPlugins.add(new PluginLoadFailure(FilenameUtils.getBaseName(pluginFile.getName()), pluginFile,
						"IO error: " + e.getMessage()));
			}
		}
		return null;
	}

	@Nullable private Plugin validatePlugin(Plugin plugin) {
		if (!plugin.isBuiltin() && plugin.getSupportedVersions() == null) {
			LOG.warn("Plugin " + plugin.getID() + " does not specify supportedversions.");
			failedPlugins.add(new PluginLoadFailure(plugin, "missing supportedversions"));
			return null;
		}

		if (!plugin.isCompatible()) {
			LOG.warn("Plugin " + plugin.getID() + " is not compatible with this MCreator version!");
			if (!Launcher.version.isDevelopment() && System.getenv("MCREATOR_PLUGINS_DEV")
					== null) { // We allow loading of incompatible plugins in dev
				failedPlugins.add(new PluginLoadFailure(plugin, "incompatible version"));
				return null;
			}
		}

		return plugin;
	}

	private void checkForPluginUpdates() {
		if (MCreatorApplication.isInternet
				&& PreferencesManager.PREFERENCES.notifications.checkAndNotifyForPluginUpdates.get()) {
			pluginUpdates.addAll(plugins.parallelStream().map(plugin -> {
				if (plugin.getInfo().getUpdateJSONURL() != null) {
					if (!plugin.getInfo().getVersion().equals(PluginInfo.VERSION_NOT_SPECIFIED)) {
						try {
							String updateJSON = WebIO.readURLToString(plugin.getInfo().getUpdateJSONURL());
							JsonObject updateData = JsonParser.parseString(updateJSON).getAsJsonObject()
									.get(plugin.getID()).getAsJsonObject();
							String version = updateData.get("latest").getAsString();
							if (!version.equals(plugin.getPluginVersion())) {
								return new PluginUpdateInfo(plugin, version, updateData.has("changes") ?
										updateData.get("changes").getAsJsonArray().asList().stream()
												.map(JsonElement::getAsString).toList() :
										null);
							}
						} catch (Exception e) {
							LOG.warn("Failed to parse update info for plugin: " + plugin.getID(), e);
						}
					}
				}
				return null;
			}).filter(Objects::nonNull).toList());
		}
	}

	public Collection<PluginLoadFailure> getFailedPlugins() {
		Set<PluginLoadFailure> failedPluginsAggregated = new HashSet<>(this.failedPlugins);

		for (Plugin plugin : plugins) {
			if (!plugin.isLoaded())
				failedPluginsAggregated.add(new PluginLoadFailure(plugin, plugin.getLoadFailure()));
		}

		return Collections.unmodifiableCollection(failedPluginsAggregated);
	}

}
