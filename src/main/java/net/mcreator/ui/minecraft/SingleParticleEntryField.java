/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.Particle;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JSingleEntrySelector;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

public class SingleParticleEntryField extends JSingleEntrySelector<Particle> {

	public SingleParticleEntryField(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected Particle openEntrySelector() {
		var entry = DataListSelectorDialog.openSelectorDialog(mcreator, ElementUtil::loadAllParticles,
				L10N.t("dialog.selector.title"), L10N.t("dialog.selector.particles.message"));
		return entry == null ? null : new Particle(mcreator.getWorkspace(), entry);
	}
}
