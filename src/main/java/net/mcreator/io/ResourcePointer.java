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

package net.mcreator.io;

import net.mcreator.plugin.PluginLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourcePointer {
	public Object identifier;
	private final boolean inClasspath;

	public ResourcePointer(File file) {
		this.identifier = file;
		this.inClasspath = false;
	}

	public ResourcePointer(String resource) {
		this.identifier = resource;
		this.inClasspath = true;
	}

	public InputStream getStream() throws FileNotFoundException {
		if (inClasspath)
			return PluginLoader.INSTANCE.getResourceAsStream((String) identifier);
		else
			return new FileInputStream((File) identifier);
	}

	public boolean isInClasspath() {
		return inClasspath;
	}

	@Override public String toString() {
		if (inClasspath)
			return FilenameUtils
					.removeExtension(((String) identifier).substring(((String) identifier).lastIndexOf("/") + 1));
		else
			return FilenameUtils.removeExtension(((File) identifier).getName());
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof ResourcePointer) {
			ResourcePointer cmpObj = (ResourcePointer) obj;
			return cmpObj.identifier.equals(identifier);
		}
		return false;
	}

	@Override public int hashCode() {
		return identifier.hashCode();
	}
}
