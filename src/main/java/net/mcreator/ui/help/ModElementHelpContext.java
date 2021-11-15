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

import javax.annotation.Nullable;
import java.net.URI;
import java.util.function.Supplier;

public record ModElementHelpContext(@Nullable String name, @Nullable URI contextURL, @Nullable String entry,
									Supplier<GeneratableElement> generatableElement) implements IHelpContext {

	@Nullable @Override public String getContextName() {
		return name;
	}

	@Nullable @Override public URI getContextURL() {
		return contextURL;
	}

	@Nullable @Override public String getEntry() {
		return entry;
	}

	public GeneratableElement getModElementFromGUI() {
		return generatableElement.get();
	}
}
