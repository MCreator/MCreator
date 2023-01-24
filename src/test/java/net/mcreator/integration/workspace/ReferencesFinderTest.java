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

package net.mcreator.integration.workspace;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.TestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.integration.generator.GTModElements;
import net.mcreator.integration.generator.GTSampleElements;
import net.mcreator.integration.ui.UITestUtil;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.SearchUsagesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.ReferencesFinder;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

public class ReferencesFinderTest {

	private static Logger LOG;
	private static Random random;

	private static Workspace workspace;
	private static MCreator mcreator;

	@BeforeAll public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));
		LOG = LogManager.getLogger("References Finder Test");

		long rgenseed = System.currentTimeMillis();
		random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		TestSetup.setupIntegrationTestEnvironment();

		// create temporary directory
		Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForFlavor(
				Generator.GENERATOR_CACHE.values(), GeneratorFlavor.FORGE);

		if (generatorConfiguration == null)
			fail("Failed to load any Forge flavored generator for this unit test");

		// we create a new workspace
		WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
		workspaceSettings.setModName("Test mod");
		workspaceSettings.setCurrentGenerator(generatorConfiguration.getGeneratorName());
		workspace = Workspace.createWorkspace(new File(tempDirWithPrefix.toFile(), "test_mod.mcreator"),
				workspaceSettings);

		mcreator = new MCreator(null, workspace);

		LOG.info("Generating sample elements");
		TestWorkspaceDataProvider.fillWorkspaceWithTestData(workspace);
		GTSampleElements.provideAndGenerateSampleElements(random, workspace);
		GTModElements.runTest(null, generatorConfiguration.getGeneratorName(), random, workspace);
	}

	@BeforeEach void printName(TestInfo testInfo) {
		LOG.info("Running " + testInfo.getDisplayName());
	}

	@Test void testModElementUsagesSearch() throws Throwable {
		ModElement modElement = ListUtils.getRandomItem(workspace.getModElements().toArray(ModElement[]::new));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.mod_element"),
						ReferencesFinder.searchModElementUsages(workspace, modElement), false));
	}

	@Test void testTextureUsagesSearch() throws Throwable {
		TextureType section = ListUtils.getRandomItem(TextureType.getTypes(true));
		File texture = ListUtils.getRandomItem(workspace.getFolderManager().getTexturesList(section));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.resource.texture"),
						ReferencesFinder.searchTextureUsages(workspace, texture, section), false));
	}

	@Test void testModelUsagesSearch() throws Throwable {
		Model model = random.nextBoolean() ? new Model.BuiltInModel("Normal") : new Model.BuiltInModel("Default");
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.resource.model"),
						ReferencesFinder.searchModelUsages(workspace, model), false));
	}

	@Test void testSoundUsagesSearch() throws Throwable {
		SoundElement sound = new SoundElement(ListUtils.getRandomItem(ElementUtil.getAllSounds(workspace)), List.of(),
				"neutral", null);
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.resource.sound"),
						ReferencesFinder.searchSoundUsages(workspace, sound), false));
	}

	@Test void testStructureUsagesSearch() throws Throwable {
		String structure = ListUtils.getRandomItem(workspace.getFolderManager().getStructureList());
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.resource.structure"),
						ReferencesFinder.searchStructureUsages(workspace, structure), false));
	}

	@Test void testGlobalVariableUsagesSearch() throws Throwable {
		String variableName = ListUtils.getRandomItem(workspace.getVariableElements().toArray(VariableElement[]::new))
				.getName();
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.global_variable"),
						ReferencesFinder.searchGlobalVariableUsages(workspace, variableName), false));
	}

	@Test void testLocalizationKeyUsagesSearch() throws Throwable {
		String localizationKey = ListUtils.getRandomItem(
				workspace.getLanguageMap().get("en_us").values().toArray(String[]::new));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> SearchUsagesDialog.show(mcreator, L10N.t("dialog.search_usages.type.localization_key"),
						ReferencesFinder.searchLocalizationKeyUsages(workspace, localizationKey), false));
	}
}