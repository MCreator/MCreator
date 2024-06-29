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

package net.mcreator.ui.help;

import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.modgui.ModElementGUI;

import javax.annotation.Nullable;
import javax.swing.*;
import java.net.URI;

public record ModElementHelpContext<GE extends GeneratableElement>(@Nullable String contextName,
																   @Nullable URI contextURL, @Nullable String entry,
																   ModElementGUI<GE> modElementGUI)
		implements IHelpContext {

	public GE getModElementFromGUI() {
		return modElementGUI.getElementFromGUI();
	}

	@Nullable @Override public JComponent owner() {
		return modElementGUI;
	}

}
