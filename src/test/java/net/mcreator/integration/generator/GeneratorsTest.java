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

package net.mcreator.integration.generator;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.gradle.GradleDaemonUtils;
import net.mcreator.gradle.GradleErrorCodes;
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.io.FileIO;
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.resources.ExternalTexture;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(IntegrationTestSetup.class) public class GeneratorsTest {

	private static final Logger LOG = LogManager.getLogger("Generator Test");

	public @TestFactory Stream<DynamicContainer> testGenerators() {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: {}", rgenseed);

		Set<String> fileNames = PluginLoader.INSTANCE.getResources(Pattern.compile("generator\\.yaml"));

		// Sort generators, so they are tested in predictable order
		List<String> fileNamesSorted = fileNames.stream().sorted((a, b) -> {
			String[] ap = a.split("/")[0].split("-");
			String[] bp = b.split("/")[0].split("-");

			if (ap[0].equals(bp[0])) { // same type, sort by version
				return bp[1].compareTo(ap[1]);
			} else { // different status, sort by status
				return bp[0].compareTo(ap[0]);
			}
		}).toList();

		LOG.info("Generators found: {}", fileNamesSorted);

		return fileNamesSorted.stream()
				.map(generatorFile -> Generator.GENERATOR_CACHE.get(generatorFile.replace("/generator.yaml", "")))
				.filter(Objects::nonNull).map(generatorConfiguration -> {
					String generator = generatorConfiguration.getGeneratorName();

					List<DynamicTest> tests = new ArrayList<>();

					AtomicReference<Workspace> workspace = new AtomicReference<>();

					tests.add(DynamicTest.dynamicTest(generator + " - Workspace setup", () -> {
						// create temporary directory
						File workspaceDir = Files.createTempDirectory("mcreator_test_workspace").toFile();

						workspace.set(
								TestWorkspaceDataProvider.createTestWorkspace(workspaceDir, generatorConfiguration,
										true, false, random));

						WorkspaceGeneratorSetup.setupWorkspaceBase(workspace.get());

						GradleDaemonUtils.stopAllDaemons(workspace.get());

						CountDownLatch latch = new CountDownLatch(1);
						new MCreator(null, workspace.get()).getGradleConsole()
								.exec(GradleConsole.GRADLE_SYNC_TASK, taskResult -> {
									if (taskResult.statusByMCreator() == GradleErrorCodes.STATUS_OK) {
										workspace.get().getGenerator().reloadGradleCaches();
									} else {
										fail("Gradle MDK setup failed!");
									}
									latch.countDown();
								});
						latch.await();
					}));

					if (generatorConfiguration.getSpecificRoot("vanilla_block_textures_dir") != null) {
						tests.add(DynamicTest.dynamicTest(generator + " - Testing texture references system",
								() -> assertFalse(ExternalTexture.getTexturesOfType(workspace.get(), TextureType.BLOCK)
										.isEmpty())));
					}

					tests.add(DynamicTest.dynamicTest(generator + " - Base generation",
							() -> assertTrue(workspace.get().getGenerator().generateBase())));
					tests.add(DynamicTest.dynamicTest(generator + " - Resource setup tasks",
							() -> workspace.get().getGenerator().runResourceSetupTasks()));

					tests.add(DynamicTest.dynamicTest(generator + " - Preparing and generating sample mod elements",
							() -> TestWorkspaceDataProvider.provideAndGenerateSampleElements(random, workspace.get())));
					tests.add(DynamicTest.dynamicTest(generator + " - Testing mod elements generation",
							() -> GTModElements.runTest(LOG, generator, random, workspace.get())));

					if (generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo()
							.get(ModElementType.PROCEDURE) != GeneratorStats.CoverageStatus.NONE) {
						tests.add(DynamicTest.dynamicTest(generator + " - Testing procedure triggers",
								() -> GTProcedureTriggers.runTest(LOG, generator, workspace.get())));
						tests.add(DynamicTest.dynamicTest(generator + " - Testing procedure blocks",
								() -> GTProcedureBlocks.runTest(LOG, generator, random, workspace.get())));
					}

					if (generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo()
							.get(ModElementType.COMMAND) != GeneratorStats.CoverageStatus.NONE)
						tests.add(DynamicTest.dynamicTest(generator + " - Testing command argument blocks",
								() -> GTCommandArgBlocks.runTest(LOG, generator, random, workspace.get())));

					if (generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo()
							.get(ModElementType.FEATURE) != GeneratorStats.CoverageStatus.NONE)
						tests.add(DynamicTest.dynamicTest(generator + " - Testing feature blocks",
								() -> GTFeatureBlocks.runTest(LOG, generator, random, workspace.get())));

					if (generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo()
							.get(ModElementType.LIVINGENTITY) != GeneratorStats.CoverageStatus.NONE)
						tests.add(DynamicTest.dynamicTest(generator + " - Testing AI task blocks",
								() -> GTAITaskBlocks.runTest(LOG, generator, random, workspace.get())));

					tests.add(DynamicTest.dynamicTest(
							generator + " - Re-generating base to include generated mod elements",
							() -> assertTrue(workspace.get().getGenerator().generateBase())));

					if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage()
							== GeneratorFlavor.BaseLanguage.JAVA) {
						tests.add(DynamicTest.dynamicTest(generator + " - Reformatting the code and organising imports",
								() -> {
									try (Stream<Path> entries = Files.walk(
											workspace.get().getGenerator().getSourceRoot().toPath())) {
										ClassWriter.formatAndOrganiseImportsForFiles(workspace.get(),
												entries.filter(Files::isRegularFile).map(Path::toFile)
														.collect(Collectors.toList()), null);
									}
								}));

						// Verify if MinecraftCodeProvider failed to load any code
						tests.add(DynamicTest.dynamicTest(generator + " - Making sure code provider works",
								() -> assertFalse(workspace.get().checkFailingGradleDependenciesAndClear())));

						// Verify Java files
						tests.add(DynamicTest.dynamicTest(generator + " - Testing workspace build with mod elements",
								() -> GTBuild.runTest(LOG, generator, workspace.get())));
					}

					// Verify JSON files
					tests.add(DynamicTest.dynamicTest(generator + " - Verifying workspace JSON files",
							() -> verifyGeneratedJSON(workspace.get())));

					tests.add(DynamicTest.dynamicTest(generator + " - Stop Gradle and close workspace", () -> {
						GradleDaemonUtils.stopAllDaemons(workspace.get());
						workspace.get().close();
						FileIO.deleteDir(workspace.get().getWorkspaceFolder());
					}));

					return DynamicContainer.dynamicContainer("Test generator: " + generator, tests);
				});
	}

	private void verifyGeneratedJSON(Workspace workspace) throws IOException {
		try (Stream<Path> entries = Files.walk(workspace.getWorkspaceFolder().toPath())) {
			entries.filter(Files::isRegularFile).map(Path::toFile)
					.filter(file -> FilenameUtils.isExtension(file.getName(), "json")).forEach(file -> {
						String contents = FileIO.readFileToString(file);

						// If png extension is present twice, something is wrong with resource path handling somewhere
						assertFalse(contents.contains(".png.png"));

						// If there is any resource path containing more than one colon, it is invalid
						assertFalse(contents.contains("\"([^\":]*:){2,}[^\":]*\""));

						// If there is any resource path that is tag and contains invalid characters, it is invalid
						assertFalse(contents.contains("\"#([a-z0-9/._\\-:]*[^a-z0-9/._\\-:\"]+[a-z0-9/._\\-:]*)*\""));

						try {
							new GsonBuilder().setStrictness(Strictness.STRICT).create()
									.fromJson(contents, Object.class); // try to parse JSON
						} catch (Exception e) {
							fail("Invalid JSON in file: " + file);
						}
					});
		}
	}

}
