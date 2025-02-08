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

package net.mcreator.minecraft.resourcepack;

import net.mcreator.io.zip.ZipIO;
import net.mcreator.workspace.Workspace;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class ResourcePackInfo {

	private static final Logger LOG = LogManager.getLogger(ResourcePackInfo.class);

	@Nullable private final File packFile;
	private final String namespace;

	public ResourcePackInfo(@Nullable File packFile, String namespace) {
		this.packFile = packFile;
		this.namespace = namespace;
	}

	@Nullable public File packFile() {
		return packFile;
	}

	public String namespace() {
		return namespace;
	}

	@Override public final boolean equals(Object o) {
		if (!(o instanceof ResourcePackInfo that))
			return false;

		return Objects.equals(packFile, that.packFile) && Objects.equals(namespace, that.namespace);
	}

	@Override public int hashCode() {
		int result = Objects.hashCode(packFile);
		result = 31 * result + Objects.hashCode(namespace);
		return result;
	}

	@Override public String toString() {
		return "ResourcePackInfo [packFile=" + packFile + ", namespace=" + namespace + "]";
	}

	public static List<ResourcePackInfo> findModResourcePacks(Workspace workspace) {
		List<ResourcePackInfo> retval = new ArrayList<>();
		for (File file : Objects.requireNonNullElse(workspace.getFolderManager().getModsDir().listFiles(),
				new File[0])) {
			try {
				if (ZipIO.checkIfZip(file)) {
					Set<String> namespaces = new HashSet<>();
					ZipIO.iterateZip(file, entry -> {
						String name = entry.getName();
						if (name.startsWith("assets/") && StringUtils.countMatches(name, "/") == 2) {
							namespaces.add(name.split("/")[1]);
						}
					}, false);
					for (String namespace : namespaces) {
						if (!namespace.equals("minecraft")) {
							retval.add(new ResourcePackInfo(file, namespace));
						}
					}
				}
			} catch (Exception e) {
				LOG.warn("Failed to read mod info from {} due to: {}", file, e.getMessage());
			}
		}
		return retval;
	}

	public static class Vanilla extends ResourcePackInfo {

		private final Workspace workspace;

		public Vanilla(Workspace workspace) {
			super(null, "minecraft");
			this.workspace = workspace;
		}

		@Nullable @Override public File packFile() {
			String vanillaResourcesJar = workspace.getGeneratorConfiguration().getSpecificRoot("vanilla_resources_jar");
			if (vanillaResourcesJar != null) {
				List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager() != null ?
						workspace.getGenerator().getProjectJarManager().getClassFileSources() :
						List.of();
				for (LibraryInfo libraryInfo : libraryInfos) {
					File libraryFile = new File(libraryInfo.getLocationAsString());
					if (libraryFile.isFile() && Pattern.compile(vanillaResourcesJar).matcher(libraryFile.getName())
							.find()) {
						return libraryFile;
					}
				}
			}
			return null;
		}

	}

}
