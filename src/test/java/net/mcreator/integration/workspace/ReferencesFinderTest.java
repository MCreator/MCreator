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

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.generator.*;
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.integration.generator.GTSampleElements;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.references.ReferencesFinder;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(IntegrationTestSetup.class) public class ReferencesFinderTest {

	private static final Logger LOG = LogManager.getLogger("References Finder Test");

	private static Workspace workspace;

	@BeforeAll public static void initTest(@TempDir File tempDir) {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(
				Generator.GENERATOR_CACHE.values(), GeneratorFlavor.BaseLanguage.JAVA);

		if (generatorConfiguration == null)
			fail("Failed to load any Forge flavored generator for this unit test");

		// we create a new workspace
		WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
		workspaceSettings.setModName("Test mod");
		workspaceSettings.setCurrentGenerator(generatorConfiguration.getGeneratorName());
		workspace = Workspace.createWorkspace(new File(tempDir, "test_mod.mcreator"), workspaceSettings);

		LOG.info("Generating sample elements");
		TestWorkspaceDataProvider.fillWorkspaceWithTestData(workspace);
		GTSampleElements.provideAndGenerateSampleElements(random, workspace);
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(type)
					!= GeneratorStats.CoverageStatus.NONE) {
				TestWorkspaceDataProvider.getModElementExamplesFor(workspace, type, false, random).forEach(e -> {
					workspace.addModElement(e.getModElement());
					workspace.getModElementManager().storeModElement(e);

					// generate I18N keys for testLocalizationKeyUsagesSearch() to check later
					LocalizationUtils.generateLocalizationKeys(workspace.getGenerator(), e,
							(List<?>) generatorConfiguration.getDefinitionsProvider().getModElementDefinition(type)
									.get("localizationkeys"));
				});
			}
		}
	}

	@Test void testModElementUsagesSearch() {
		ModElement modElement = workspace.getModElementByName("Exampleblock3");
		ReferencesFinder.searchModElementUsages(workspace, modElement);

		modElement = workspace.getModElementByName("Examplelivingentity3");
		ReferencesFinder.searchModElementUsages(workspace, modElement);

		modElement = workspace.getModElementByName("condition1");
		Set<ModElement> references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Examplelivingentity")));
		modElement = workspace.getModElementByName("condition4");
		references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Exampleoverlay")));
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Examplegui")));
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Examplearmor")));

		modElement = workspace.getModElementByName("number1");
		references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Exampleitem")));

		modElement = workspace.getModElementByName("string1");
		references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Exampleitem")));
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Exampleblock")));

		modElement = workspace.getModElementByName("procedure1");
		references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Exampledimension")));

		modElement = workspace.getModElementByName("procedure10");
		references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Examplegui")));

		modElement = workspace.getModElementByName("ExampleLootTable1");
		references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		assertTrue(references.stream().map(ModElement::getName).anyMatch(e -> e.contains("Exampleachievement")));
	}

	@Test void testTextureUsagesSearch() {
		TextureType section = TextureType.PARTICLE;
		File texture = workspace.getFolderManager().getTextureFile("particle1", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleparticle")));

		section = TextureType.EFFECT;
		texture = workspace.getFolderManager().getTextureFile("effect1", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplepotioneffect")));

		section = TextureType.ENTITY;
		texture = workspace.getFolderManager().getTextureFile("entityTx1", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplelivingentity")));
		texture = workspace.getFolderManager().getTextureFile("entityTx2", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplelivingentity")));

		section = TextureType.BLOCK;
		texture = workspace.getFolderManager().getTextureFile("test6", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleblock")));

		section = TextureType.ITEM;
		texture = workspace.getFolderManager().getTextureFile("test", section);
		// "test" texture is not used as ITEM type in Block demo MEs, so this needs to be false
		assertFalse(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleblock")));
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleitem")));

		section = TextureType.SCREEN;
		texture = workspace.getFolderManager().getTextureFile("picture1", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplegui")));
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleoverlay")));

		section = TextureType.ARMOR;
		texture = workspace.getFolderManager().getTextureFile("armorTexture_layer_1", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplearmor")));
		texture = workspace.getFolderManager().getTextureFile("armorTexture_layer_2", section);
		assertTrue(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplearmor")));
		texture = workspace.getFolderManager().getTextureFile("armorTexture", section);
		assertFalse(ReferencesFinder.searchTextureUsages(workspace, texture, section).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplearmor")));
	}

	@Test void testModelUsagesSearch() {
		Model model = new Model.BuiltInModel("Normal");
		assertTrue(ReferencesFinder.searchModelUsages(workspace, model).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampletool")));
		model = new Model.BuiltInModel("Cross model");
		assertTrue(ReferencesFinder.searchModelUsages(workspace, model).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleplant")));
		model = new Model.BuiltInModel("Default");
		assertTrue(ReferencesFinder.searchModelUsages(workspace, model).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Exampleprojectile")));
	}

	@Test void testSoundUsagesSearch() {
		SoundElement sound = ListUtils.getRandomItem(List.copyOf(workspace.getSoundElements()));
		ReferencesFinder.searchSoundUsages(workspace, sound);
	}

	@Test void testStructureUsagesSearch() {
		String structure = "test";
		assertTrue(ReferencesFinder.searchStructureUsages(workspace, structure).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplestructure")));

		structure = "test2";
		assertTrue(ReferencesFinder.searchStructureUsages(workspace, structure).stream().map(ModElement::getName)
				.anyMatch(e -> e.contains("Examplestructure")));
	}

	@Test void testGlobalVariableUsagesSearch() {
		String variableName = "logic2";
		assertTrue(ReferencesFinder.searchGlobalVariableUsages(workspace, variableName).isEmpty());
		variableName = "direction4";
		assertTrue(ReferencesFinder.searchGlobalVariableUsages(workspace, variableName).isEmpty());
		variableName = "blockstate3";
		assertTrue(ReferencesFinder.searchGlobalVariableUsages(workspace, variableName).isEmpty());
	}

	@Test void testLocalizationKeyUsagesSearch() {
		String localizationKey = ListUtils.getRandomItem(
				workspace.getLanguageMap().get("en_us").keySet().toArray(String[]::new));
		assertFalse(ReferencesFinder.searchLocalizationKeyUsages(workspace, localizationKey).isEmpty());
	}
}