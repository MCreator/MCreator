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

package net.mcreator.util.locale;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class MultiResourceBundle extends ResourceBundle {

	private final List<ResourceBundle> delegates;

	public MultiResourceBundle(List<ResourceBundle> resourceBundles) {
		this.delegates = resourceBundles == null ? new ArrayList<>() : resourceBundles;
	}

	@Override protected Object handleGetObject(@Nonnull String key) {
		return this.delegates.stream().filter(delegate -> delegate != null && delegate.containsKey(key))
				.map(delegate -> delegate.getObject(key)).findFirst().orElse(null);
	}

	@Override @Nonnull public Enumeration<String> getKeys() {
		return Collections.enumeration(this.delegates.stream().filter(Objects::nonNull)
				.flatMap(delegate -> Collections.list(delegate.getKeys()).stream()).collect(Collectors.toList()));
	}
}
