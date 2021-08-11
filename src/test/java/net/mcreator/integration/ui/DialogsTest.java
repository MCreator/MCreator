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

import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.integration.TestSetup;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.dialogs.*;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.tools.*;
import net.mcreator.ui.dialogs.workspace.GeneratorSelector;
import net.mcreator.ui.dialogs.workspace.NewWorkspaceDialog;
import net.mcreator.ui.dialogs.wysiwyg.*;
import net.mcreator.ui.workspace.selector.WorkspaceSelector;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;
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

import static org.junit.jupiter.api.Assertions.fail;

public class DialogsTest {

	private static Logger LOG;

	private static MCreator mcreator;

	@BeforeAll public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));
		LOG = LogManager.getLogger("Dialogs Test");

		// disable webview to avoid issues in headless test environments
		BlocklyPanel.DISABLE_WEBVIEW = true;

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
		Workspace workspace = Workspace.createWorkspace(new File(tempDirWithPrefix.toFile(), "test_mod.mcreator"),
				workspaceSettings);

		mcreator = new MCreator(null, workspace);
	}

	@BeforeEach void printName(TestInfo testInfo) {
		LOG.info("Running " + testInfo.getDisplayName());
	}

	@Test public void testWorkspaceSelector() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new WorkspaceSelector(null, f -> {}));
	}

	@Test public void testNewWorkspaceDialog() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new NewWorkspaceDialog(mcreator));
	}

	@Test public void testGeneratorSelectorDialog() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> GeneratorSelector.getGeneratorSelector(mcreator, mcreator.getGeneratorConfiguration(),
						GeneratorFlavor.FORGE));
	}

	@Test public void testAboutDialog() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> AboutAction.showDialog(mcreator));
	}

	@Test public void testPreferencesDialog() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new PreferencesDialog(mcreator, null));
	}

	@Test public void testMCItemSelector() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> MCItemSelectorDialog.openSelectorDialog(mcreator, ElementUtil::loadBlocksAndItems));
	}

	@Test public void testElementOrderEditor() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> ElementOrderEditor.openElementOrderEditor(mcreator));
	}

	@Test public void testTextureDialogs() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> TextureImportDialogs.importArmor(mcreator));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> TextureImportDialogs.importTexturesBlockOrItem(mcreator,
				BlockItemTextureSelector.TextureType.BLOCK));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> TextureImportDialogs.importTexturesBlockOrItem(mcreator,
				BlockItemTextureSelector.TextureType.ITEM));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> TextureImportDialogs.importOtherTextures(mcreator));
	}

	@Test public void testToolsDialogs() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> ArmorPackMakerTool.getAction(mcreator.actionRegistry).doAction());
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> InjectTagsTool.getAction(mcreator.actionRegistry).doAction());
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> MaterialPackMakerTool.getAction(mcreator.actionRegistry).doAction());
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> OrePackMakerTool.getAction(mcreator.actionRegistry).doAction());
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> ToolPackMakerTool.getAction(mcreator.actionRegistry).doAction());
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> WoodPackMakerTool.getAction(mcreator.actionRegistry).doAction());
	}

	@Test public void testWYSIWYGDialogs() throws Throwable {
		WYSIWYGEditor editor = new WYSIWYGEditor(mcreator, true);
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new ButtonDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new CheckboxDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new ImageDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new InputSlotDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new LabelDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new OutputSlotDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new TextFieldDialog(editor, null));
	}

	@Test public void testAIConditionEditor() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> AIConditionEditor.open(mcreator, null));
	}

	@Test public void testFileDialogs() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> FileDialogs.getWorkspaceDirectorySelectDialog(mcreator,
				new File(System.getProperty("user.home"))));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> FileDialogs.getMultiOpenDialog(mcreator, new String[] { ".png" }));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> FileDialogs.getOpenDialog(mcreator, new String[] { ".png" }));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> FileDialogs.getSaveDialog(mcreator, new String[] { ".png" }));
	}

	@Test public void testSoundElementDialog() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> SoundElementDialog.soundDialog(mcreator, null, null));
	}

	@Test public void testNewModElementDialog() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> NewModElementDialog.showNameDialog(mcreator, ModElementType.BLOCK));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> NewModElementDialog.showNameDialog(mcreator, ModElementType.CODE));
	}

	@Test public void testBlockItemTextureSelector() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.BLOCK).setVisible(
						true));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM).setVisible(
						true));
	}

}
