/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.integration.ui;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.integration.generator.GTSampleElements;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(IntegrationTestSetup.class) public class ModElementUITest {

	private static final Logger LOG = LogManager.getLogger("Mod Element Test");

	private static Workspace workspace;
	private static MCreator mcreator;

	@BeforeAll public static void initTest(@TempDir File tempDir) {
		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(
				Generator.GENERATOR_CACHE.values(), GeneratorFlavor.BaseLanguage.JAVA);

		if (generatorConfiguration == null)
			fail("Failed to load any Forge flavored generator for this unit test");

		// we create a new workspace
		WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
		workspaceSettings.setModName("Test mod");
		workspaceSettings.setCurrentGenerator(generatorConfiguration.getGeneratorName());
		workspace = Workspace.createWorkspace(new File(tempDir, "test_mod.mcreator"), workspaceSettings);

		mcreator = new MCreator(null, workspace);

		TestWorkspaceDataProvider.fillWorkspaceWithTestData(workspace);
		GTSampleElements.provideAndGenerateSampleElements(new Random(), workspace);

		// reduce autosave interval for tests
		PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval.set(2000);

		LOG.info("Test workspace folder: " + workspace.getWorkspaceFolder());
	}

	@Test public void testModElementsDefaultLocale() throws Exception {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		PreferencesManager.PREFERENCES.ui.language.set(L10N.DEFAULT_LOCALE);
		L10N.initTranslations();

		// test mod elements using default (en) translations
		testModElementLoading(random);
	}

	// use non-default translation to test translations at the same time
	@Test public void testModElementsNonDefaultLocale() throws Exception {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		PreferencesManager.PREFERENCES.ui.language.set(
				L10N.getSupportedLocales().stream().filter(locale -> locale != L10N.DEFAULT_LOCALE)
						.max(Comparator.comparingInt(L10N::getUITextsLocaleSupport)).orElse(null));
		L10N.initTranslations();

		LOG.info("Testing mod element GUI for locale " + PreferencesManager.PREFERENCES.ui.language.get());

		testModElementLoading(random);
	}

	private void testModElementLoading(Random random) throws Exception {
		for (ModElementType<?> modElementType : ModElementTypeLoader.REGISTRY) {

			if (modElementType == ModElementType.CODE)
				continue; // does not have regular handling so skip it

			List<GeneratableElement> generatableElements = TestWorkspaceDataProvider.getModElementExamplesFor(workspace,
					modElementType, true, random);

			LOG.info("Testing mod element type UI " + modElementType.getReadableName() + " with "
					+ generatableElements.size() + " variants");

			for (GeneratableElement generatableElementOrig : generatableElements) {
				ModElement modElement = generatableElementOrig.getModElement();

				GeneratableElement generatableElement;

				// convert mod element to json
				String exportedJSON = workspace.getModElementManager().generatableElementToJSON(generatableElementOrig);

				// back to GeneratableElement
				generatableElement = workspace.getModElementManager()
						.fromJSONtoGeneratableElement(exportedJSON, modElement);// from JSON to GeneratableElement

				// Check if all workspace fields are not null after re-import
				IWorkspaceDependent.processWorkspaceDependentObjects(generatableElement,
						workspaceDependent -> assertNotNull(workspaceDependent.getWorkspace()));

				assertNotNull(generatableElement);

				ModElementGUI<?> modElementGUI = UITestUtil.openModElementGUIFor(mcreator, generatableElement);

				// test if UI validation is error free
				UITestUtil.testIfValidationPasses(modElementGUI, true);

				// test if data remains the same after reloading the data lists
				modElementGUI.reloadDataLists();

				// test UI -> GeneratableElement
				generatableElement = modElementGUI.getElementFromGUI();

				// Check if all workspace fields are not null after reading from GUI
				IWorkspaceDependent.processWorkspaceDependentObjects(generatableElement,
						workspaceDependent -> assertNotNull(workspaceDependent.getWorkspace()));

				// compare GeneratableElements, no fields should change in the process
				String exportedJSON2 = workspace.getModElementManager().generatableElementToJSON(generatableElement);

				assertEquals(exportedJSON, exportedJSON2);
			}
		}
	}

}
