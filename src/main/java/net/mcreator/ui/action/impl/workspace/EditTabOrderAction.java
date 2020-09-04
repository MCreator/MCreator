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

package net.mcreator.ui.action.impl.workspace;

import net.mcreator.element.ModElementType;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.ElementOrderEditor;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

public class EditTabOrderAction extends BasicAction {

	public EditTabOrderAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.edit_creative_tab_order"),
				e -> ElementOrderEditor.openElementOrderEditor(actionRegistry.getMCreator()));
		setIcon(UIRES.get("16px.editorder"));
	}

	@Override public boolean isEnabled() {
		GeneratorConfiguration gc = actionRegistry.getMCreator().getWorkspace().getGenerator()
				.getGeneratorConfiguration();
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TAB)
				!= GeneratorStats.CoverageStatus.NONE;
	}
}
