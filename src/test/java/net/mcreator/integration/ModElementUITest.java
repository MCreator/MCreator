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
import net.mcreator.integration.javafx.JavaFXThreadingRule;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ModElementUITest {

	private static Logger LOG;

	// blockly editors use javafx, we need this fix for javafx to work in unittests
	@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	private static Workspace workspace;
	private static MCreator mcreator;

	@BeforeClass public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));
		LOG = LogManager.getLogger("Mod Element Test");

		TestSetup.setupIntegrationTestEnvironment();

		// create temporary directory
		Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");

		GeneratorConfiguration generatorConfiguration = Generator.GENERATOR_CACHE.values().stream()
				.filter(e -> e.getGeneratorFlavor() == GeneratorFlavor.FORGE).findFirst().orElse(null);

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

		// reduce autosave interval for tests
		PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval = 2000;
	}

	@Test public void testModElements() throws Exception {
		LOG.info("Test workspace folder: " + workspace.getFolderManager().getWorkspaceFolder());

		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		for (Map.Entry<ModElementType, ModElementTypeRegistry.ModTypeRegistration<?>> modElementRegistration : ModElementTypeRegistry.REGISTRY
				.entrySet()) {

			ModElement modElement = new ModElement(workspace, "Example" + modElementRegistration.getKey().name(),
					modElementRegistration.getKey());

			List<GeneratableElement> generatableElements = TestWorkspaceDataProvider
					.getModElementExamplesFor(modElement, random);

			LOG.info("Testing mod element type UI " + modElement.getType().getReadableName() + " with "
					+ generatableElements.size() + " variants");

			for (GeneratableElement generatableElementOrig : generatableElements) {
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

				Method method = modElementGUI.getClass()
						.getDeclaredMethod("openInEditingMode", GeneratableElement.class);
				method.setAccessible(true);
				method.invoke(modElementGUI, generatableElement);

				generatableElement = modElementGUI.getElementFromGUI();

				// compare GeneratableElements, no fields should change in the process
				String exportedJSON2 = workspace.getModElementManager().generatableElementToJSON(generatableElement);

				assertEquals(exportedJSON, exportedJSON2);
			}
		}
	}

}
