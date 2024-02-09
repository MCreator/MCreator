/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.integration;

import javafx.embed.swing.JFXPanel;
import net.mcreator.Launcher;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.LoggingSystem;
import net.mcreator.io.net.analytics.GoogleAnalytics;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.WebConsoleListener;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.help.HelpLoader;
import net.mcreator.ui.init.*;
import net.mcreator.ui.laf.themes.ThemeManager;
import net.mcreator.util.MCreatorVersionNumber;
import net.mcreator.util.TerribleModuleHacks;
import net.mcreator.util.UTF8Forcer;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class IntegrationTestSetup implements BeforeAllCallback {

	private static final String STORE_KEY = IntegrationTestSetup.class.getName();

	@Override public synchronized void beforeAll(ExtensionContext context) throws Exception {
		Object value = context.getRoot().getStore(GLOBAL).get(STORE_KEY);
		if (value == null) {
			context.getRoot().getStore(GLOBAL).put(STORE_KEY, this);
			setup();
		}
	}

	public void setup() throws Exception {
		/* ******************************
		 * START: Launcher.java emulation
		 * ******************************/
		LoggingSystem.init();

		TerribleModuleHacks.openAllFor(ClassLoader.getSystemClassLoader().getUnnamedModule());
		TerribleModuleHacks.openMCreatorRequirements();

		UTF8Forcer.forceGlobalUTF8();

		Logger LOG = LogManager.getLogger("Test setup");

		Properties conf = new Properties();
		conf.load(Launcher.class.getResourceAsStream("/mcreator.conf"));
		Launcher.version = new MCreatorVersionNumber(conf);

		// print version of Java
		LOG.info("Java version: " + System.getProperty("java.version") + ", VM: " + System.getProperty("java.vm.name")
				+ ", vendor: " + System.getProperty("java.vendor"));
		LOG.info("Current JAVA_HOME for running instance: " + System.getProperty("java.home"));

		// load preferences
		PreferencesManager.init();

		// Init JFX Toolkit
		ThreadUtil.runOnSwingThreadAndWait(JFXPanel::new);
		WebConsoleListener.registerLogger(LOG);
		/* ****************************
		 * END: Launcher.java emulation
		 * ****************************/

		// Reduce autosave interval for tests
		PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval.set(2000);

		// Gradle's builds are RAM intensive, so we may need more RAM
		PreferencesManager.PREFERENCES.gradle.xmx.set(3072); // 3G

		// Disable native file choosers for tests due to threading issues
		PreferencesManager.PREFERENCES.ui.useNativeFileChooser.set(false);

		// Do not track unit tests
		GoogleAnalytics.ANALYTICS_ENABLED = false;

		// Enable logging of HTML panes (gradle console)
		ConsolePane.DEBUG_CONTENTS_TO_LOG = true;

		/* *****************************************
		 * START: MCreatorApplication.java emulation
		 * *****************************************/
		MCreatorApplication.isInternet = MCreatorApplication.WEB_API.initAPI();

		// load plugins
		// We begin by loading plugins, so every image can be changed
		PluginLoader.initInstance();

		// We load UI theme now as theme plugins are loaded at this point
		ThemeManager.loadThemes();

		UIRES.preloadImages();

		ThemeManager.applySelectedTheme();

		// preload help entries cache
		HelpLoader.preloadCache();

		BlockItemIcons.init();
		DataListLoader.preloadCache();

		// load translations after plugins are loaded
		L10N.initTranslations();
		L10N.enterTestingMode();

		// may be needed to generate icons for MCItems (e.g. generation of potion icons)
		ImageMakerTexturesCache.init();
		ArmorMakerTexturesCache.init();

		// load apis defined by plugins after plugins are loaded
		ModAPIManager.initAPIs();

		// load variable elements
		VariableTypeLoader.loadVariableTypes();

		// load JS files for Blockly
		BlocklyJavaScriptsLoader.init();
		BlocklyToolboxesLoader.init();

		// blockly mod elements need blockly blocks loaded
		BlocklyLoader.init();

		// load entity animations for the Java Model animation editor
		EntityAnimationsLoader.init();

		// register mod element types
		ModElementTypeLoader.loadModElements();

		// load generator configurations
		Set<String> fileNames = PluginLoader.INSTANCE.getResources(Pattern.compile("generator\\.yaml"));
		for (String generator : fileNames) {
			LOG.info("Loading generator: " + generator);
			generator = generator.replace("/generator.yaml", "");
			Generator.GENERATOR_CACHE.put(generator, new GeneratorConfiguration(generator));
		}
		/* ***************************************
		 * END: MCreatorApplication.java emulation
		 * ***************************************/
	}

}
