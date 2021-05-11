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
import java.net.URISyntaxException;

public interface IHelpContext {

	IHelpContext NONE = new IHelpContext() {
		@Override public @Nullable String getContextName() {
			return null;
		}

		@Override public @Nullable URI getContextURL() {
			return null;
		}
	};

	default IHelpContext withEntry(String entry) {
		try {
			return new HelpContextWithEntry(this.getContextName(), this.getContextURL(), entry);
		} catch (URISyntaxException e) {
			return new HelpContextWithEntry(this.getContextName(), null, entry);
		}
	}

	default String getEntry() {
		return null;
	}

	@Nullable String getContextName();

	@Nullable default URI getContextURL() throws URISyntaxException {
		return null;
	}

}
