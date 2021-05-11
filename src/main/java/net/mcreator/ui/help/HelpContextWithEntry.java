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

package net.mcreator.ui.help;

import javax.annotation.Nullable;
import java.net.URI;

public class HelpContextWithEntry implements IHelpContext {

	@Nullable private final String name;
	@Nullable private final URI contextURL;

	@Nullable private final String entry;

	public HelpContextWithEntry(@Nullable String name, @Nullable URI contextURL, @Nullable String entry) {
		this.name = name;
		this.contextURL = contextURL;
		this.entry = entry;
	}

	@Nullable @Override public String getContextName() {
		return name;
	}

	@Nullable @Override public URI getContextURL() {
		return contextURL;
	}

	@Nullable @Override public String getEntry() {
		return entry;
	}
}
