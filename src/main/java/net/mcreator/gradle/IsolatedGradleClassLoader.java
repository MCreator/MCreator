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

package net.mcreator.gradle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.ProjectConnection;
import org.jboss.forge.roaster._shade.org.eclipse.core.runtime.FileLocator;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class IsolatedGradleClassLoader {

	private static final Logger LOG = LogManager.getLogger(IsolatedGradleClassLoader.class);

	private static URLClassLoader classLoader;

	private static void ensureLoaded() {
		if (classLoader == null) { // lazy-load classloader
			ClassLoader compatClassloader = GradleSyncBuildAction.class.getClassLoader();
			ClassLoader tapiClassloader = ProjectConnection.class.getClassLoader();
			URL[] urls;
			try {
				URL actionRootUrl = FileLocator.resolve(compatClassloader.getResource(""));
				urls = new URL[] { actionRootUrl };
			} catch (IOException e) {
				LOG.warn("Failed to resolve classloader root URL", e);
				urls = new URL[] {};
			}
			classLoader = new URLClassLoader(urls, tapiClassloader);
		}
	}

	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		ensureLoaded();
		return classLoader.loadClass(name);
	}

}
