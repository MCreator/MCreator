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
import net.mcreator.integration.IntegrationTestSetup;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.imageeditor.*;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(IntegrationTestSetup.class) public class ImageMakerTest {

	private static final Logger LOG = LogManager.getLogger("Image Maker Test");

	private static MCreator mcreator;

	@BeforeAll public static void initTest() throws IOException {
		// create temporary directory
		Path tempDirWithPrefix = Files.createTempDirectory("mcreator_test_workspace");

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForBaseLanguage(
				Generator.GENERATOR_CACHE.values(), GeneratorFlavor.BaseLanguage.JAVA);

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

	@Test public void testImageMaker() throws Throwable {
		ImageMakerView imv = new ImageMakerView(mcreator);
		imv.newImage(new Layer(100, 100, 0, 0, "Layer", Color.red));

		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new DesaturateDialog(mcreator, imv.getCanvas(), null, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new FromTemplateDialog(mcreator, imv.getCanvas(), null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new HSVNoiseDialog(mcreator, imv.getCanvas(), null, null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new NewImageDialog(mcreator));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new NewLayerDialog(mcreator, imv.getCanvas()));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> new RecolorDialog(mcreator, imv.getCanvas(), null, new ColorSelector(mcreator), null));
		UITestUtil.waitUntilWindowIsOpen(mcreator, () -> new ResizeCanvasDialog(mcreator, imv.getCanvas()));
		UITestUtil.waitUntilWindowIsOpen(mcreator,
				() -> new ResizeDialog(mcreator, imv.getCanvas(), imv.getToolPanel().getCurrentTool().getLayer(),
						null));
	}

}
