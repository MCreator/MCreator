/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class DynamicURLClassLoader extends URLClassLoader {

	public DynamicURLClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public DynamicURLClassLoader(URL[] urls) {
		super(urls);
	}

	public DynamicURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public DynamicURLClassLoader(String name, URL[] urls, ClassLoader parent) {
		super(name, urls, parent);
	}

	public DynamicURLClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(name, urls, parent, factory);
	}

	@Override public void addURL(URL url) {
		super.addURL(url);
	}
}
