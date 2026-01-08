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
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(IntegrationTestSetup.class) public class ModElementUITest {

	@Nested @TestInstance(TestInstance.Lifecycle.PER_CLASS) class JavaGeneratorTests {

		private Collection<ModElementType<?>> modElementTypesToTest;

		@BeforeAll void setup(@TempDir File tempDir) {
			initMCreator(tempDir,
					GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(Generator.GENERATOR_CACHE.values(),
							GeneratorFlavor.BaseLanguage.JAVA));

			modElementTypesToTest = TestWorkspaceDataProvider.getOrderedModElementTypesForTests(
					mcreator.getGeneratorConfiguration());
		}

		@Test void testModElementsDefaultLocale() throws Exception {
			testDefaultLocale(modElementTypesToTest);
		}

		@Test void testModElementsNonDefaultLocale() throws Exception {
			testNonDefaultLocale(modElementTypesToTest);
		}
	}

	@Nested @TestInstance(TestInstance.Lifecycle.PER_CLASS) class AddonGeneratorTests {

		private Collection<ModElementType<?>> modElementTypesToTest;

		@BeforeAll void setup(@TempDir File tempDir) {
			initMCreator(tempDir,
					GeneratorConfiguration.getRecommendedGeneratorForFlavor(Generator.GENERATOR_CACHE.values(),
							GeneratorFlavor.ADDON));

			modElementTypesToTest = TestWorkspaceDataProvider.getOrderedModElementTypesForTests(
					mcreator.getGeneratorConfiguration()).stream().filter(type -> {
				List<GeneratorFlavor> coveredFlavors = type.getCoveredFlavors();
				return coveredFlavors.size() == 1 && coveredFlavors.getFirst() == GeneratorFlavor.ADDON;
			}).toList();
		}

		@Test void testModElementsDefaultLocale() throws Exception {
			testDefaultLocale(modElementTypesToTest);
		}

		@Test void testModElementsNonDefaultLocale() throws Exception {
			testNonDefaultLocale(modElementTypesToTest);
		}
	}

	private static final Logger LOG = LogManager.getLogger("Mod Element Test");

	private Random random;
	private MCreator mcreator;

	private void initMCreator(File tempDir, @Nullable GeneratorConfiguration generatorConfiguration) {
		if (generatorConfiguration == null)
			fail("Failed to load generator configuration");

		long rgenseed = System.currentTimeMillis();
		random = new Random(rgenseed);
		LOG.info("Random number generator seed ({}): {}", generatorConfiguration.getGeneratorFlavor(), rgenseed);

		mcreator = MCreator.create(null,
				TestWorkspaceDataProvider.createTestWorkspace(tempDir, generatorConfiguration, true, true, random));
	}

	private void testDefaultLocale(Collection<ModElementType<?>> modElementTypesToTest) throws Exception {
		PreferencesManager.PREFERENCES.ui.language.set(L10N.DEFAULT_LOCALE);
		L10N.initTranslations();
		testModElementLoading(modElementTypesToTest, random);
	}

	private void testNonDefaultLocale(Collection<ModElementType<?>> modElementTypesToTest) throws Exception {
		PreferencesManager.PREFERENCES.ui.language.set(
				L10N.getSupportedLocales().stream().filter(locale -> locale != L10N.DEFAULT_LOCALE)
						.max(Comparator.comparingInt(L10N::getUITextsLocaleSupport)).orElse(null));
		L10N.initTranslations();

		LOG.info("Testing mod element GUI for locale {}", PreferencesManager.PREFERENCES.ui.language.get());

		testModElementLoading(modElementTypesToTest, random);
	}

	private void testModElementLoading(Collection<ModElementType<?>> modElementTypesToTest, Random random)
			throws Exception {
		for (ModElementType<?> modElementType : modElementTypesToTest) {

			if (modElementType == ModElementType.CODE)
				continue; // does not have regular handling, so skip it

			List<GeneratableElement> generatableElements = TestWorkspaceDataProvider.getModElementExamplesFor(
					mcreator.getWorkspace(), modElementType, true, random);

			LOG.info("Testing mod element type UI {} with {} variants", modElementType.getReadableName(),
					generatableElements.size());

			for (GeneratableElement generatableElementOrig : generatableElements) {
				ModElement modElement = generatableElementOrig.getModElement();

				// convert mod element to json
				String exportedJSON = mcreator.getWorkspace().getModElementManager()
						.generatableElementToJSON(generatableElementOrig);

				// back to GeneratableElement
				GeneratableElement generatableElement = mcreator.getWorkspace().getModElementManager()
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

				// close mod element GUI
				modElementGUI.onViewClosed();

				// Check if all workspace fields are not null after reading from GUI
				IWorkspaceDependent.processWorkspaceDependentObjects(generatableElement,
						workspaceDependent -> assertNotNull(workspaceDependent.getWorkspace()));

				// compare GeneratableElements, no fields should change in the process
				String exportedJSON2 = mcreator.getWorkspace().getModElementManager()
						.generatableElementToJSON(generatableElement);

				assertEquals(exportedJSON, exportedJSON2);
			}
		}
	}
}
