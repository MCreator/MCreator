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
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceConvertersTest {

	@BeforeAll public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));

		TestSetup.setupIntegrationTestEnvironment();
	}

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

				Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");
				File workspaceDir = tempDirWithPrefix.toFile();

				ZipIO.unzip(workspaceZip.getAbsolutePath(), workspaceDir.getAbsolutePath());

				File workspaceFile = WorkspaceUtils.getWorkspaceFileForWorkspaceFolder(workspaceDir);

				GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForFlavor(
						Generator.GENERATOR_CACHE.values(), GeneratorFlavor.FORGE);

				assertNotNull(generatorConfiguration);

				try (Workspace workspace = Workspace.readFromFS(workspaceFile, generatorConfiguration)) {
					// Conversions
					for (ModElement mod : workspace.getModElements()) {
						mod.getGeneratableElement();
					}

					// Check if all MEs have valid GE definition
					for (ModElement mod : workspace.getModElements()) {
						assertTrue(workspace.getModElementManager().hasModElementGeneratableElement(mod));

						GeneratableElement ge = mod.getGeneratableElement();

						assertNotNull(ge);

						// test if methods below work and no exceptions are thrown

						// save custom mod element picture if it has one
						workspace.getModElementManager().storeModElementPicture(ge);

						// add mod element to workspace again (update ME action)
						workspace.addModElement(ge.getModElement());

						// we reinit the mod to load new icons etc.
						ge.getModElement().reinit(workspace);
					}
				}
			});
		});
	}

}
