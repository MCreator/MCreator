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

package net.mcreator.workspace.resources;

import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Animation {

	private final String name;

	private final File file;

	private Animation(File file) {
		this.file = file;
		this.name = FilenameUtils.getBaseName(file.getName());
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	public static Collection<Animation> getAnimations(Workspace workspace) {
		Set<Animation> animations = new HashSet<>();

		File[] candidates = workspace.getFolderManager().getModelAnimationsDir().listFiles();
		for (File f : candidates != null ? candidates : new File[0]) {
			if (f.isDirectory())
				continue;

			// we load java models in a separate loop below
			if (f.getName().endsWith(".java")) {
				animations.add(new Animation(f));
			}
		}

		// only return valid models
		return animations;
	}

}
