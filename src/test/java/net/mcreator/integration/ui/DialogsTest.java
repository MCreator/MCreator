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

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.TestSetup;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.dialogs.ElementOrderEditor;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.workspace.GeneratorSelector;
import net.mcreator.ui.dialogs.workspace.NewWorkspaceDialog;
import net.mcreator.ui.workspace.selector.WorkspaceSelector;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.fail;

public class DialogsTest {

	private static MCreator mcreator;

	@BeforeAll public static void initTest() throws IOException {
		TestSetup.setupIntegrationTestEnvironment();

		// create temporary directory
		Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration
				.getRecommendedGeneratorForFlavor(Generator.GENERATOR_CACHE.values(), GeneratorFlavor.FORGE);

		if (generatorConfiguration == null)
			fail("Failed to load any Forge flavored generator for this unit test");

		// we create a new workspace
		WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
		workspaceSettings.setModName("Test mod");
		workspaceSettings.setCurrentGenerator(generatorConfiguration.getGeneratorName());
		Workspace workspace = Workspace
				.createWorkspace(new File(tempDirWithPrefix.toFile(), "test_mod.mcreator"), workspaceSettings);

		mcreator = new MCreator(null, workspace);
	}

	@Test public void testWorkspaceSelector() throws Throwable {
		waitUntilWindowIsOpen(() -> new WorkspaceSelector(null, f -> {}));
	}

	@Test public void testNewWorkspaceDialog() throws Throwable {
		waitUntilWindowIsOpen(() -> new NewWorkspaceDialog(mcreator));
	}

	@Test public void testGeneratorSelectorDialog() throws Throwable {
		waitUntilWindowIsOpen(() -> GeneratorSelector
				.getGeneratorSelector(mcreator, mcreator.getGeneratorConfiguration(), GeneratorFlavor.FORGE));
	}

	@Test public void testAboutDialog() throws Throwable {
		waitUntilWindowIsOpen(() -> AboutAction.showDialog(mcreator));
	}

	@Test public void testPreferencesDialog() throws Throwable {
		waitUntilWindowIsOpen(() -> new PreferencesDialog(mcreator, null));
	}

	@Test public void testMCItemSelector() throws Throwable {
		waitUntilWindowIsOpen(() -> MCItemSelectorDialog.openSelectorDialog(mcreator, ElementUtil::loadBlocksAndItems));
	}

	@Test public void testElementOrderEditor() throws Throwable {
		waitUntilWindowIsOpen(() -> ElementOrderEditor.openElementOrderEditor(mcreator));
	}

	@Test public void testTextureDialogs() throws Throwable {
		waitUntilWindowIsOpen(() -> TextureImportDialogs.importArmor(mcreator));
		waitUntilWindowIsOpen(() -> TextureImportDialogs
				.importTexturesBlockOrItem(mcreator, BlockItemTextureSelector.TextureType.BLOCK));
		waitUntilWindowIsOpen(() -> TextureImportDialogs
				.importTexturesBlockOrItem(mcreator, BlockItemTextureSelector.TextureType.ITEM));
		waitUntilWindowIsOpen(() -> TextureImportDialogs.importOtherTextures(mcreator));
	}

	private void waitUntilWindowIsOpen(Runnable openTask) throws Throwable {
		int frames_start = Window.getWindows().length;

		AtomicReference<Throwable> throwableAtomic = new AtomicReference<>(null);

		SwingUtilities.invokeLater(() -> {
			try {
				openTask.run();
			} catch (Throwable t) {
				throwableAtomic.set(t);
			}
		});

		long start = System.currentTimeMillis();
		while (Window.getWindows().length == frames_start) {
			//noinspection BusyWait
			Thread.sleep(5);

			if (System.currentTimeMillis() - start > 3000)
				throw new TimeoutException();

			if (throwableAtomic.get() != null)
				throw throwableAtomic.get();
		}

		if (throwableAtomic.get() != null)
			throw throwableAtomic.get();

		Arrays.stream(Window.getWindows()).filter(w -> w != mcreator).forEach(Window::dispose);
	}

}
