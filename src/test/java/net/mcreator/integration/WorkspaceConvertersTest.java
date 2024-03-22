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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.ui.UITestUtil;
import net.mcreator.io.FileIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(IntegrationTestSetup.class) public class WorkspaceConvertersTest {

	public @TestFactory Stream<DynamicTest> testWorkspaceConversions() {
		Set<String> testWorkspaces = new Reflections(
				new ConfigurationBuilder().forPackages("workspaces").setScanners(Scanners.Resources)
						.setExpandSuperTypes(false)).getResources(Pattern.compile("test-.*\\.zip"));

		return testWorkspaces.stream().map(testWorkspace -> {
			final String testWorkspaceName = FilenameUtils.getBaseName(testWorkspace);
			return DynamicTest.dynamicTest("Test workspace: " + testWorkspaceName, () -> {
				File workspaceZip = File.createTempFile(testWorkspaceName, ".zip");
				FileUtils.copyURLToFile(Objects.requireNonNull(getClass().getResource("/" + testWorkspace)),
						workspaceZip);

				File workspaceDir = Files.createTempDirectory("mcreator_test_workspace").toFile();

				ZipIO.unzip(workspaceZip.getAbsolutePath(), workspaceDir.getAbsolutePath());

				File workspaceFile = WorkspaceUtils.getWorkspaceFileForWorkspaceFolder(workspaceDir);

				GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(
						Generator.GENERATOR_CACHE.values(), GeneratorFlavor.BaseLanguage.JAVA);

				assertNotNull(generatorConfiguration);

				try (Workspace workspace = Workspace.readFromFSUnsafe(workspaceFile, generatorConfiguration)) {
					// Conversions
					for (ModElement mod : workspace.getModElements()) {
						mod.getGeneratableElement();
					}

					MCreator mcreator = new MCreator(null, workspace);

					// Check if all MEs have valid GE definition
					for (ModElement mod : workspace.getModElements()) {
						GeneratableElement ge = mod.getGeneratableElement();

						assertNotNull(ge);

						// Check if all workspace fields are not null
						IWorkspaceDependent.processWorkspaceDependentObjects(ge,
								workspaceDependent -> assertNotNull(workspaceDependent.getWorkspace()));

						// test if methods below work and no exceptions are thrown

						// save custom mod element picture if it has one
						workspace.getModElementManager().storeModElementPicture(ge);

						// preload/update MCItem cache and MCItem icons
						ge.getModElement().getMCItems().forEach(mcItem -> mcItem.icon.getImage().flush());

						// we reinit the mod to load new icons etc.
						ge.getModElement().reinit(workspace);

						// test if GE definition is valid enough to be generated
						assertTrue(workspace.getGenerator().generateElement(ge));

						// test if the converted GE can be opened in the UI
						ModElementGUI<?> modElementGUI = UITestUtil.openModElementGUIFor(mcreator, ge);

						// test if UI validation is error free
						UITestUtil.testIfValidationPasses(modElementGUI, false);
					}
				}

				FileIO.deleteDir(workspaceDir);
			});
		});
	}

}
