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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ModElementUITest {

	private static Logger LOG;

	private static Workspace workspace;
	private static MCreator mcreator;

	@BeforeAll public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));
		LOG = LogManager.getLogger("Mod Element Test");

		// disable webview to avoid issues in headless test environments
		BlocklyPanel.DISABLE_WEBVIEW = true;

		TestSetup.setupIntegrationTestEnvironment();

		// create temporary directory
		Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration
				.getRecommendedGeneratorForFlavor(Generator.GENERATOR_CACHE.values(), GeneratorFlavor.FORGE);

		if (generatorConfiguration == null)
			fail("Failed to load any Forge flavored generator for this unit test");

		// we create a new workspace
		WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
		workspaceSettings.setModName("Test mod");
		workspaceSettings.setCurrentGenerator(generatorConfiguration.getGeneratorName());
		workspace = Workspace
				.createWorkspace(new File(tempDirWithPrefix.toFile(), "test_mod.mcreator"), workspaceSettings);

		mcreator = new MCreator(null, workspace);

		TestWorkspaceDataProvider.fillWorkspaceWithTestData(workspace);

		// generate some "dummy" procedures for dropdowns to work
		for (int i = 1; i <= 13; i++) {
			workspace.addModElement(new ModElement(workspace, "procedure" + i, ModElementType.PROCEDURE)
					.putMetadata("dependencies", new ArrayList<String>()));
		}

		for (int i = 1; i <= 4; i++) {
			workspace.addModElement(new ModElement(workspace, "condition" + i, ModElementType.PROCEDURE)
					.putMetadata("dependencies", new ArrayList<String>()).putMetadata("return_type", "LOGIC"));
		}

		// reduce autosave interval for tests
		PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval = 2000;

		LOG.info("Test workspace folder: " + workspace.getWorkspaceFolder());
	}

	@Test public void testModElementsDefaultLocale() throws Exception {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		PreferencesManager.PREFERENCES.ui.language = L10N.DEFAULT_LOCALE;
		L10N.initTranslations();

		// test mod elements using default (en) translations
		testModElementLoading(random);
	}

	// use non-default translation to test translations at the same time
	@Test public void testModElementsNonDefaultLocale() throws Exception {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		PreferencesManager.PREFERENCES.ui.language = L10N.getSupportedLocales().stream()
				.filter(locale -> locale != L10N.DEFAULT_LOCALE).max(Comparator.comparingInt(L10N::getLocaleSupport))
				.orElse(null);
		L10N.initTranslations();

		LOG.info("Testing mod element GUI for locale " + PreferencesManager.PREFERENCES.ui.language);

		testModElementLoading(random);
	}

	private void testModElementLoading(Random random)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
		for (Map.Entry<ModElementType, ModElementTypeRegistry.ModTypeRegistration<?>> modElementRegistration : ModElementTypeRegistry.REGISTRY
				.entrySet()) {

			List<GeneratableElement> generatableElements = TestWorkspaceDataProvider
					.getModElementExamplesFor(workspace, modElementRegistration.getKey(), random);

			LOG.info("Testing mod element type UI " + modElementRegistration.getKey().getReadableName() + " with "
					+ generatableElements.size() + " variants");

			for (GeneratableElement generatableElementOrig : generatableElements) {
				ModElement modElement = generatableElementOrig.getModElement();

				GeneratableElement generatableElement;

				// convert mod element to json
				String exportedJSON = workspace.getModElementManager().generatableElementToJSON(generatableElementOrig);

				// back to GeneratableElement
				generatableElement = workspace.getModElementManager()
						.fromJSONtoGeneratableElement(exportedJSON, modElement);// from JSON to generatableelement

				if (generatableElement == null) {
					LOG.warn("This mod element type does not support generatable elements: " + modElement.getType()
							.getReadableName());
					continue;
				}

				ModElementGUI<?> modElementGUI = modElementRegistration.getValue()
						.getModElement(mcreator, modElement, false);

				Field field = modElementGUI.getClass().getSuperclass().getDeclaredField("editingMode");
				field.setAccessible(true);
				field.set(modElementGUI, true);

				// test opening generatable element
				Method method = modElementGUI.getClass()
						.getDeclaredMethod("openInEditingMode", GeneratableElement.class);
				method.setAccessible(true);
				method.invoke(modElementGUI, generatableElement);

				// test if data remains the same after reloading the data lists
				modElementGUI.reloadDataLists();

				// test UI -> GeneratableElement
				generatableElement = modElementGUI.getElementFromGUI();

				// compare GeneratableElements, no fields should change in the process
				String exportedJSON2 = workspace.getModElementManager().generatableElementToJSON(generatableElement);

				assertEquals(exportedJSON, exportedJSON2);
			}
		}
	}

}
