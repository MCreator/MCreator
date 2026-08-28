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
import net.mcreator.ui.dialogs.PluginApprovalDialog;
import net.mcreator.util.TestUtil;
import net.mcreator.util.Tuple;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.Nullable;
import java.beans.Introspector;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

	private final Map<Tuple<String, String>, Set<String>> resourcesCache;

	/**
	 * <p>The core of the detection and loading</p>
	 */
	private PluginLoader() {
		super(new URL[] {}, null);

		this.plugins = new LinkedHashSet<>();
		this.javaPlugins = new LinkedHashSet<>();
		this.failedPlugins = new LinkedHashSet<>();
		this.pluginUpdates = new LinkedHashSet<>();
		this.pluginsModules = new LinkedHashSet<>();

		this.resourcesCache = new ConcurrentHashMap<>();

		UserFolderManager.getFileFromUserFolder("plugins").mkdirs();

		List<Plugin> pluginsLoadList = new ArrayList<>();
		pluginsLoadList.addAll(listPluginsFromFolder(new File("./plugins/"), true));
		pluginsLoadList.addAll(listPluginsFromFolder(UserFolderManager.getFileFromUserFolder("plugins"), false));

		if (System.getenv("MCREATOR_PLUGINS_FOLDER") != null)
			pluginsLoadList.addAll(listPluginsFromFolder(new File(System.getenv("MCREATOR_PLUGINS_FOLDER")), false));

		Collections.sort(pluginsLoadList);

		// Plugins that are not approved by the user are not loaded and thus also can't be used as plugin dependencies
		pluginsLoadList.removeIf(plugin -> !verifyPluginApproval(plugin));

		Set<String> idList = pluginsLoadList.stream().map(Plugin::getID).collect(Collectors.toSet());

		for (Plugin plugin : pluginsLoadList) {
			if (plugin.getInfo().getDependencies() != null) {
				if (!idList.containsAll(plugin.getInfo().getDependencies())) {
					LOG.warn("{} can not be loaded. The plugin needs {}", plugin.getInfo().getName(),
							plugin.getInfo().getDependencies());
					plugin.loaded_failure = "missing plugin dependencies";
					continue;
				}
			}

			try {
				LOG.info("Loading plugin: {} from {}, weight: {}", plugin.getID(), plugin.getFile(),
						plugin.getWeight());
				addURL(plugin.toURL());

				if (plugin.isJavaPlugin()) {
					@SuppressWarnings("resource") DynamicURLClassLoader javaPluginCL = new DynamicURLClassLoader(
							"PluginClassLoader-" + plugin.getID(), new URL[] {},
							Thread.currentThread().getContextClassLoader()) {
						@Override protected Class<?> findClass(String name) throws ClassNotFoundException {
							try {
								return super.findClass(name);
							} catch (Exception e) {
								for (StackTraceElement element : e.getStackTrace()) {
									if (element.getClassName().equals(Introspector.class.getName())) {
										// If the class not found was triggered due to Introspector looking for
										// XXXBeanInfo class or XXXCustomizer class, we can ignore this and
										// not log error or mark plugin as failed by setting loaded_failure
										throw e;
									}
								}

								plugin.loaded_failure =
										"internal error: " + e.getClass().getSimpleName() + ": " + e.getMessage();
								LOG.error("Failed to load class {} for plugin {}", name, plugin.getID(), e);
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
				}
			} catch (Exception e) {
				plugin.loaded_failure = "Load error: " + e.getMessage();
				LOG.error("Failed to load plugin {}", plugin.getID(), e);
			}
		}

		this.reflections = new Reflections(
				new ConfigurationBuilder().setClassLoaders(new ClassLoader[] { this }).setUrls(getURLs())
						.setScanners(Scanners.Resources).setExpandSuperTypes(false));

		checkForPluginUpdates();

		// Sort regular plugin list
		List<Plugin> sortedPlugins = new ArrayList<>(plugins);
		Collections.sort(sortedPlugins);
		plugins.clear();
		plugins.addAll(sortedPlugins);

		// Sort Java plugin list
		List<JavaPlugin> sortedJavaPlugins = new ArrayList<>(javaPlugins);
		Collections.sort(sortedJavaPlugins);
		javaPlugins.clear();
		javaPlugins.addAll(sortedJavaPlugins);
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
		return resourcesCache.computeIfAbsent(new Tuple<>(pkg, pattern == null ? null : pattern.pattern()), tuple -> {
			Set<String> reflectionsRetval =
					pattern != null ? this.reflections.getResources(pattern) : this.reflections.getResources(".*");
			if (pkg == null)
				return reflectionsRetval;
			return reflectionsRetval.stream().filter(e -> e.replace("/", ".").startsWith(pkg))
					.collect(Collectors.toSet());
		});
	}

	/**
	 * @return <p> A {@link List} of all loaded plugins. Sorted by plugin weight.</p>
	 */
	public Collection<Plugin> getPlugins() {
		return Collections.unmodifiableCollection(plugins);
	}

	/**
	 * @return <p> A {@link List} of all loaded Java plugins. Sorted by plugin weight.</p>
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
		LOG.debug("Searching for plugins in: {}", folder);

		List<Plugin> loadList = new ArrayList<>();

		File[] pluginFiles = folder.listFiles();
		for (File pluginFile : pluginFiles != null ? pluginFiles : new File[0]) {
			Plugin plugin = loadPlugin(pluginFile, builtin);
			if (plugin != null) {
				if (plugins.contains(plugin)) {
					LOG.warn("Trying to load duplicate plugin: {} from: {}", plugin.getID(), plugin.getFile());
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
				if (!builtin && !directoryPluginsAllowed()) {
					LOG.warn("Directory plugin {} can only be loaded in development environments", pluginFile);
					failedPlugins.add(new PluginLoadFailure(FilenameUtils.getBaseName(pluginFile.getName()), pluginFile,
							"directory plugins are only loaded in development environments"));
					return null;
				}
				try {
					String pluginInfo = FileIO.readFileToString(pluginInfoFile);
					Plugin plugin = new Gson().fromJson(pluginInfo, Plugin.class);
					plugin.builtin = builtin;
					plugin.file = pluginFile;
					return validatePlugin(plugin);
				} catch (Exception e) {
					LOG.error("Failed to load plugin from {}", pluginFile, e);
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
				LOG.error("Failed to load plugin from {}", pluginFile, e);
				failedPlugins.add(new PluginLoadFailure(FilenameUtils.getBaseName(pluginFile.getName()), pluginFile,
						"IO error: " + e.getMessage()));
			}
		}
		return null;
	}

	private static boolean directoryPluginsAllowed() {
		return (Launcher.version != null && Launcher.version.isDevelopment())
				|| System.getenv("MCREATOR_PLUGINS_DEV") != null;
	}

	/**
	 * <p>Checks if the given plugin is approved to be loaded by the user. Non-builtin file plugins are identified
	 * by the SHA-256 hash of their file. If the hash is not known yet, the user is asked whether to load the
	 * plugin or not and the decision is remembered in the preferences. This check can be disabled in preferences.</p>
	 *
	 * @return <p>True if the plugin can be loaded, false otherwise. In the latter case, load failure is recorded.</p>
	 */
	private boolean verifyPluginApproval(Plugin plugin) {
		if (plugin.isBuiltin())
			return true;

		// Directory plugins are only loaded in development environments (see loadPlugin) where they are trusted
		if (plugin.getFile().isDirectory())
			return true;

		// Hash is computed even if verification is disabled, so the plugin management UI can show and alter approvals
		try {
			plugin.sha256 = FileIO.sha256file(plugin.getFile());
		} catch (IOException | NoSuchAlgorithmException e) {
			LOG.error("Failed to compute SHA-256 hash of plugin {}", plugin.getFile(), e);
		}

		if (!PreferencesManager.PREFERENCES.hidden.verifyPluginHashes.get())
			return true;

		if (TestUtil.isTestingEnvironment())
			return true; // run tests with plugins without user approval, so tests can run in CI environments

		if (plugin.sha256 == null) {
			plugin.loaded_failure = "failed to verify plugin file";
			return false;
		}

		Map<String, Boolean> approvals = PreferencesManager.PREFERENCES.hidden.pluginHashApprovals.get();
		Boolean approved = approvals.get(plugin.sha256);
		if (approved == null) {
			approved = PluginApprovalDialog.promptPluginApproval(plugin);
			approvals.put(plugin.sha256, approved);
		}

		if (!approved) {
			LOG.warn("Plugin {} was not approved by the user and will not be loaded", plugin.getID());
			plugin.loaded_failure = "plugin was not approved for loading";
			return false;
		}

		return true;
	}

	@Nullable private Plugin validatePlugin(Plugin plugin) {
		if (!plugin.isBuiltin() && plugin.getSupportedVersions() == null) {
			LOG.warn("Plugin {} does not specify supportedversions.", plugin.getID());
			failedPlugins.add(new PluginLoadFailure(plugin, "plugin is missing supportedversions"));
			return null;
		}

		if (!plugin.isCompatible()) {
			LOG.warn("Plugin {} is not compatible with this MCreator version!", plugin.getID());
			if (System.getenv("MCREATOR_PLUGINS_DEV")
					== null) { // Only prevent the loading of incompatible plugins if MCREATOR_PLUGINS_DEV is not set
				failedPlugins.add(new PluginLoadFailure(plugin, "incompatible MCreator version"));
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
							LOG.warn("Failed to parse update info for plugin: {}", plugin.getID(), e);
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
