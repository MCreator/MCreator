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

import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.gradle.GradleDaemonUtils;
import net.mcreator.gradle.GradleErrorCodes;
import net.mcreator.integration.TestSetup;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GeneratorsTest {

	private static Logger LOG;

	@BeforeAll public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));
		LOG = LogManager.getLogger("Generator Test");

		TestSetup.setupIntegrationTestEnvironment();

		// enable logging of HTML panes (gradle console)
		ConsolePane.DEBUG_CONTENTS_TO_LOG = true;

		// reduce autosave interval for tests
		PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval = 2000;
	}

	public @TestFactory Stream<DynamicTest> testGenerators() {
		Set<String> fileNames = PluginLoader.INSTANCE.getResources(Pattern.compile("generator\\.yaml"));
		LOG.info("Generators found: " + fileNames);

		long rgenseed = System.currentTimeMillis();
		Random random = new Random(rgenseed);
		LOG.info("Random number generator seed: " + rgenseed);

		return fileNames.stream().map(generatorFile -> {
			final String generator = generatorFile.replace("/generator.yaml", "");
			return DynamicTest.dynamicTest("Test generator: " + generator, () -> {
				LOG.info("================");
				LOG.info("TESTING GENERATOR " + generator);

				// create temporary directory
				Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");

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
				Workspace workspace = Workspace
						.createWorkspace(new File(tempDirWithPrefix.toFile(), "test_mod.mcreator"), workspaceSettings);

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
								if (taskResult.getStatusByMCreator() == GradleErrorCodes.STATUS_OK) {
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

				LOG.info("[" + generator + "] ----- Testing empty workspace build");
				GTBuild.runTest(LOG, generator, workspace);

				LOG.info("[" + generator + "] ----- Testing procedure triggers");
				GTProcedureTriggers.runTest(LOG, generator, workspace);

				LOG.info("[" + generator + "] ----- Testing procedure blocks");
				GTProcedureBlocks.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Testing building after procedure tests");
				GTBuild.runTest(LOG, generator, workspace);

				LOG.info("[" + generator + "] ----- Preparing and generating sample mod elements");
				GTSampleElements.provideAndGenerateSampleElements(random, workspace);

				LOG.info("[" + generator + "] ----- Testing mod elements generation");
				GTModElements.runTest(LOG, generator, random, workspace);

				LOG.info("[" + generator + "] ----- Testing workspace build with mod elements");
				GTBuild.runTest(LOG, generator, workspace);
			});
		});
	}

}
