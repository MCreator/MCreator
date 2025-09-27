/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.variants.modmaker;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MainMenuBar;
import net.mcreator.ui.init.L10N;

import javax.swing.*;

class ModMakerMenuBar extends MainMenuBar {

	protected ModMakerMenuBar(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected void assembleMenuBar(MCreator mcreator) {
		JMenu workspace = L10N.menu("menubar.workspace");
		workspace.setMnemonic('S');
		workspace.add(mcreator.getActionRegistry().setCreativeTabItemOrder);
		workspace.addSeparator();
		workspace.add(mcreator.getActionRegistry().openWorkspaceFolder);
		workspace.addSeparator();
		workspace.add(mcreator.getActionRegistry().workspaceSettings);
		workspace.addSeparator();
		workspace.add(mcreator.getActionRegistry().exportToJAR);
		add(workspace);

		JMenu resources = L10N.menu("menubar.resources");
		resources.setMnemonic('R');
		resources.add(mcreator.getActionRegistry().importBlockTexture);
		resources.add(mcreator.getActionRegistry().importItemTexture);
		resources.add(mcreator.getActionRegistry().importEntityTexture);
		resources.add(mcreator.getActionRegistry().importEffectTexture);
		resources.add(mcreator.getActionRegistry().importParticleTexture);
		resources.add(mcreator.getActionRegistry().importScreenTexture);
		resources.add(mcreator.getActionRegistry().importArmorTexture);
		resources.add(mcreator.getActionRegistry().importOtherTexture);
		resources.addSeparator();
		resources.add(mcreator.getActionRegistry().importSound);
		resources.addSeparator();
		resources.add(mcreator.getActionRegistry().importStructure);
		resources.add(mcreator.getActionRegistry().importStructureFromMinecraft);
		resources.addSeparator();
		resources.add(mcreator.getActionRegistry().importJavaModel);
		resources.add(mcreator.getActionRegistry().importJSONModel);
		resources.add(mcreator.getActionRegistry().importOBJModel);
		resources.addSeparator();
		resources.add(mcreator.getActionRegistry().importJavaModelAnimation);
		add(resources);

		JMenu build = L10N.menu("menubar.build_and_run");
		build.setMnemonic('B');
		build.add(mcreator.getActionRegistry().buildWorkspace);
		build.add(mcreator.getActionRegistry().buildGradleOnly);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().regenerateCode);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().reloadGradleProject);
		build.add(mcreator.getActionRegistry().clearAllGradleCaches);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().cancelGradleTaskAction);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().runGradleTask);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().runClient);
		build.add(mcreator.getActionRegistry().debugClient);
		build.add(mcreator.getActionRegistry().runServer);
		add(build);

		JMenu tools = L10N.menu("menubar.tools");
		tools.setMnemonic('T');
		tools.add(mcreator.getActionRegistry().createMCItemTexture);
		tools.add(mcreator.getActionRegistry().createArmorTexture);
		tools.add(mcreator.getActionRegistry().createAnimatedTexture);
		tools.addSeparator();
		tools.add(mcreator.getActionRegistry().openMaterialPackMaker);
		tools.add(mcreator.getActionRegistry().openOrePackMaker);
		tools.add(mcreator.getActionRegistry().openToolPackMaker);
		tools.add(mcreator.getActionRegistry().openArmorPackMaker);
		tools.add(mcreator.getActionRegistry().openWoodPackMaker);
		tools.addSeparator();
		tools.add(mcreator.getActionRegistry().openJavaEditionFolder);
		tools.add(mcreator.getActionRegistry().openBedrockEditionFolder);
		tools.addSeparator();
		JMenu dataLists = L10N.menu("menubar.tools.data_lists");
		dataLists.add(mcreator.getActionRegistry().showEntityIDList);
		dataLists.add(mcreator.getActionRegistry().showItemBlockList);
		dataLists.add(mcreator.getActionRegistry().showParticleIDList);
		dataLists.add(mcreator.getActionRegistry().showSoundsList);
		dataLists.add(mcreator.getActionRegistry().showFuelBurnTimes);
		dataLists.add(mcreator.getActionRegistry().showVanillaLootTables);
		tools.add(dataLists);
		add(tools);
	}

}
