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

package net.mcreator.integration.workspace;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceUtils;
import net.mcreator.workspace.elements.ModElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(IntegrationTestSetup.class) public class OrphanedModElementsTest {

	@Test void testOrphanedModElementsRecovery(@TempDir File tempDir) {
		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(
				Generator.GENERATOR_CACHE.values(), GeneratorFlavor.BaseLanguage.JAVA);
		assertNotNull(generatorConfiguration);

		try (Workspace workspace = TestWorkspaceDataProvider.createTestWorkspace(tempDir, generatorConfiguration,
				true, false, null)) {
			List<GeneratableElement> blocks = TestWorkspaceDataProvider.getModElementExamplesFor(workspace,
					ModElementType.BLOCK, false, new Random(0));

			// mod element added to the workspace and stored
			GeneratableElement listed = blocks.getFirst();
			workspace.addModElement(listed.getModElement());
			workspace.getModElementManager().storeModElement(listed);

			// mod element stored, but not added to the workspace (e.g. workspace file was not saved before a crash)
			GeneratableElement orphaned = blocks.get(1);
			workspace.getModElementManager().storeModElement(orphaned);

			List<ModElement> orphanedModElements = WorkspaceUtils.findOrphanedModElements(workspace);
			assertEquals(List.of(orphaned.getModElement()), orphanedModElements);

			orphanedModElements.forEach(workspace::addModElement);
			assertTrue(WorkspaceUtils.findOrphanedModElements(workspace).isEmpty());

			// load the recovered mod element from its definition file and check that it can be generated
			workspace.getModElementManager().invalidateCache();
			ModElement recovered = workspace.getModElementByName(orphaned.getModElement().getName());
			assertNotNull(recovered);
			assertEquals(ModElementType.BLOCK, recovered.getType());
			GeneratableElement generatableElement = recovered.getGeneratableElement();
			assertNotNull(generatableElement);
			assertTrue(workspace.getGenerator().generateElement(generatableElement));
		}
	}

}
