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
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.dialogs.*;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.tools.*;
import net.mcreator.ui.dialogs.workspace.GeneratorSelector;
import net.mcreator.ui.dialogs.workspace.NewWorkspaceDialog;
import net.mcreator.ui.dialogs.wysiwyg.*;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.workspace.resources.TextureType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;

public class DialogsTest {

	private static Logger LOG;

	private static MCreator mcreator;

	@BeforeAll public static void initTest() throws IOException {
		System.setProperty("log_directory", System.getProperty("java.io.tmpdir"));
		LOG = LogManager.getLogger("Dialogs Test");

		// disable native file choosers for tests due to threading issues
		FileDialogs.DISABLE_NATIVE_DIALOGS = true;

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
						GeneratorFlavor.FORGE, true));
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
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> TextureImportDialogs.importSingleTexture(mcreator, new File(""),
						L10N.t("workspace.textures.select_texture_type")));
		for (TextureType type : TextureType.values()) {
			if (type != TextureType.ARMOR) {
				UITestUtil.waitUntilWindowIsOpen(mcreator,
						() -> TextureImportDialogs.importMultipleTextures(mcreator, type));
			}
		}
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
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new EntityModelDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new ImageButtonDialog(editor, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new TooltipDialog(editor, null));
	}

	@Test public void testAIConditionEditor() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> AIConditionEditor.open(mcreator, null));
	}

	@Test public void testStateEditorDialog() throws Throwable {
		List<PropertyData<?>> testProps = new ArrayList<>();
		testProps.add(new PropertyData.LogicType("logic"));
		testProps.add(new PropertyData.IntegerType("integer"));
		testProps.add(new PropertyData.IntegerType("integer2", -100, 100));
		testProps.add(new PropertyData.NumberType("number"));
		testProps.add(new PropertyData.NumberType("number2", -0.0001, 1000000));
		testProps.add(new PropertyData.StringType("text", ElementUtil.loadDirections()));
		Random rng = new Random();
		StateMap testState = new StateMap();
		if (rng.nextBoolean())
			testState.put(testProps.get(0), rng.nextBoolean());
		if (rng.nextBoolean())
			testState.put(testProps.get(1), rng.nextInt());
		if (rng.nextBoolean())
			testState.put(testProps.get(2), rng.nextInt());
		if (rng.nextBoolean())
			testState.put(testProps.get(3), rng.nextDouble());
		if (rng.nextBoolean())
			testState.put(testProps.get(4), rng.nextDouble());
		if (rng.nextBoolean())
			testState.put(testProps.get(5), TestWorkspaceDataProvider.getRandomItem(rng, ElementUtil.loadDirections()));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> StateEditorDialog.open(mcreator, testProps, testState, JStateLabel.NumberMatchType.EQUAL));
	}

	@Test public void testListEditor() throws Throwable {
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> ListEditorDialog.open(mcreator,
				Collections.enumeration(Arrays.asList("info 1", "info 2", "test \\, is this", "another one")), null,
				false));
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

	@Test public void testGeneralTextureSelector() throws Throwable {
		for (TextureType type : TextureType.values()) {
			UITestUtil.waitUntilWindowIsOpen(mcreator,
					() -> new TypedTextureSelectorDialog(mcreator, type).setVisible(true));
		}
	}

}
