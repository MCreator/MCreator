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

import com.google.gson.Gson;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.gradle.GradleDaemonUtils;
import net.mcreator.gradle.GradleErrorCodes;
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.io.FileIO;
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(IntegrationTestSetup.class) public class GeneratorsTest {

	private static final Logger LOG = LogManager.getLogger("Generator Test");

	public @TestFactory Stream<DynamicTest> testGenerators() {
		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

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

		LOG.info("Generators found: " + fileNamesSorted);

		return fileNamesSorted.stream().map(generatorFile -> {
			final String generator = generatorFile.replace("/generator.yaml", "");
			return DynamicTest.dynamicTest("Test generator: " + generator, () -> {
				LOG.info("================");
				LOG.info("TESTING GENERATOR " + generator);

				// create temporary directory
				File workspaceDir = Files.createTempDirectory("mcreator_test_workspace").toFile();

				// we create a new workspace
				WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
				workspaceSettings.setVersion("1.0.0");
				workspaceSettings.setDescription("Test mod");
				workspaceSettings.setAuthor("Unit tests");
				workspaceSettings.setLicense("GPL 3.0");
				workspaceSettings.setWebsiteURL("https://mcreator.net/");
				workspaceSettings.setUpdateURL("https://mcreator.net/");
				workspaceSettings.setModPicture("example");
				workspaceSettings.setModName("Test mod");
				workspaceSettings.setCurrentGenerator(generator);
				Workspace workspace = Workspace.createWorkspace(new File(workspaceDir, "test_mod.mcreator"),
						workspaceSettings);

				LOG.info("[" + generator + "] ----- Test workspace folder: " + workspace.getFolderManager()
						.getWorkspaceFolder());

				TestWorkspaceDataProvider.fillWorkspaceWithTestData(workspace);

				LOG.info("[" + generator + "] ----- Setting up workspace base for selected generator");
				WorkspaceGeneratorSetup.setupWorkspaceBase(workspace);

				if (workspace.getGeneratorConfiguration().getGradleTaskFor("setup_task") != null) {
					CountDownLatch latch = new CountDownLatch(1);

					GradleDaemonUtils.stopAllDaemons(workspace);

					new MCreator(null, workspace).getGradleConsole()
							.exec(workspace.getGeneratorConfiguration().getGradleTaskFor("setup_task"), taskResult -> {
								if (taskResult.statusByMCreator() == GradleErrorCodes.STATUS_OK) {
									workspace.getGenerator().reloadGradleCaches();
								} else {
									fail("Gradle MDK setup failed!");
								}
								latch.countDown();
							});
					latch.await();
				}

				LOG.info("[" + generator + "] ----- Testing base generation");
				assertTrue(workspace.getGenerator().generateBase());

				LOG.info("[" + generator + "] ----- Testing resource setup tasks");
				workspace.getGenerator().runResourceSetupTasks();

				LOG.info("[" + generator + "] ----- Preparing and generating sample mod elements");
				GTSampleElements.provideAndGenerateSampleElements(random, workspace);

				LOG.info("[" + generator + "] ----- Testing mod elements generation");
				GTModElements.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Testing procedure triggers");
				GTProcedureTriggers.runTest(LOG, generator, workspace);

				LOG.info("[" + generator + "] ----- Testing procedure blocks");
				GTProcedureBlocks.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Testing command argument blocks");
				GTCommandArgBlocks.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Testing feature blocks");
				GTFeatureBlocks.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Testing AI task blocks");
				GTAITaskBlocks.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Re-generating base to include generated mod elements");
				assertTrue(workspace.getGenerator().generateBase());

				LOG.info("[" + generator + "] ----- Reformatting the code and organising the imports");
				try (Stream<Path> entries = Files.walk(workspace.getWorkspaceFolder().toPath())) {
					ClassWriter.formatAndOrganiseImportsForFiles(workspace,
							entries.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList()), null);
				}

				LOG.info("[" + generator + "] ----- Testing workspace build with mod elements");
				GTBuild.runTest(LOG, generator, workspace); // This will verify Java files

				// We also need to verify JSON files
				LOG.info("[" + generator + "] ----- Verifying workspace JSON files");
				verifyGeneratedJSON(workspace);

				LOG.info("[" + generator + "] ----- Attempting to stop all Gradle daemons");
				GradleDaemonUtils.stopAllDaemons(workspace);

				workspace.close();

				FileIO.deleteDir(workspaceDir);
			});
		});
	}

	private void verifyGeneratedJSON(Workspace workspace) throws IOException {
		try (Stream<Path> entries = Files.walk(workspace.getWorkspaceFolder().toPath())) {
			entries.filter(Files::isRegularFile).map(Path::toFile)
					.filter(file -> FilenameUtils.isExtension(file.getName(), "json")).forEach(file -> {
						try {
							new Gson().fromJson(FileIO.readFileToString(file), Object.class); // try to parse JSON
						} catch (Exception e) {
							fail("Invalid JSON in file: " + file);
						}
					});
		}
	}

}
