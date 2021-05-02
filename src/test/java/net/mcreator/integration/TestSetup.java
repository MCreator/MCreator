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

package net.mcreator.integration;

import net.mcreator.Launcher;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.api.ModAPIManager;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.init.EntityAnimationsLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.laf.MCreatorLookAndFeel;
import net.mcreator.util.MCreatorVersionNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

public class TestSetup {

	private static final Logger LOG = LogManager.getLogger(TestSetup.class);

	private static boolean already = false;

	public static void setupIntegrationTestEnvironment() throws IOException {
		if (already)
			return;

		// print version of Java
		String java_spec_version = System.getProperty("java.specification.version");
		LOG.info("Java version: " + System.getProperty("java.version") + ", specification: " + java_spec_version
				+ ", VM name: " + System.getProperty("java.vm.name"));
		LOG.info("Current JAVA_HOME for running instance: " + System.getProperty("java.home"));

		Properties conf = new Properties();
		conf.load(Launcher.class.getResourceAsStream("/mcreator.conf"));
		Launcher.version = new MCreatorVersionNumber(conf);

		// init theme
		try {
			UIManager.setLookAndFeel(new MCreatorLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			LOG.error("Failed to set look and feel: " + e.getMessage());
		}

		// load plugins
		PluginLoader.initInstance();

		DataListLoader.preloadCache();

		// load translations after plugins are loaded
		L10N.initTranslations();
		L10N.enterTestingMode();

		// some mod element guis use icons
		TiledImageCache.loadAndTileImages();

		// load apis defined by plugins after plugins are loaded
		ModAPIManager.initAPIs();

		// blockly mod elements need blockly blocks loaded
		BlocklyLoader.init();

		// load entity animations for the Java Model animation editor
		EntityAnimationsLoader.loadEntityAnimations();

		// load generator configurations
		Set<String> fileNames = PluginLoader.INSTANCE.getResources(Pattern.compile("generator\\.yaml"));
		for (String generator : fileNames) {
			LOG.info("Loading generator: " + generator);
			generator = generator.replace("/generator.yaml", "");
			Generator.GENERATOR_CACHE.put(generator, new GeneratorConfiguration(generator));
		}

		already = true;
	}

}
