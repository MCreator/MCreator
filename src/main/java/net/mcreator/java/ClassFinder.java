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

package net.mcreator.java;

import net.mcreator.io.FileIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassFinder {

	private static final Logger LOG = LogManager.getLogger("Class Finder");

	public static DeclarationFinder.InClassPosition fqdnToInClassPosition(Workspace workspace, String classfqdn,
			String packagefqdn, JarManager jarManager) {
		DeclarationFinder.InClassPosition position = new DeclarationFinder.InClassPosition();

		// if there is no package, it is a class in the current package
		if (!classfqdn.contains(".")) {
			if (new File(workspace.getGenerator().getSourceRoot(),
					packagefqdn.replace(".", "/") + "/" + classfqdn + ".java").isFile()) {
				position.classFileNode = new File(workspace.getGenerator().getSourceRoot(),
						packagefqdn.replace(".", "/") + "/" + classfqdn + ".java");
				position.openInReadOnly = false;
				position.virtualFile = position.classFileNode;
				return position;
			}

			// if there was no package, but the class was not found in SRCROOT, add package declatation to it
			classfqdn = packagefqdn + "." + classfqdn;
		}

		// next we check if the class might be located in the src direcotry of the project under the given fqdn
		if (new File(workspace.getGenerator().getSourceRoot(), classfqdn.replace(".", "/") + ".java").isFile()) {
			position.classFileNode = new File(workspace.getGenerator().getSourceRoot(),
					classfqdn.replace(".", "/") + ".java");
			position.openInReadOnly = false;
			position.virtualFile = position.classFileNode;
			return position;
		}

		// next we try to find the declaration using JarManager to check
		// if the class we are looking for is loaded with source
		SourceLocation sourceLocation = jarManager.getSourceLocForClass(classfqdn);
		DeclarationFinder.InClassPosition position1 = sourceLocationToInClassPosition(sourceLocation, classfqdn);
		if (position1 != null)
			return position1;

		// next we try to find the declaration using JarManager to check
		// if the class we are looking for is loaded with source
		// this time in default java lang package
		sourceLocation = jarManager.getSourceLocForClass("java.lang." + classfqdn);
		position1 = sourceLocationToInClassPosition(sourceLocation, "java.lang." + classfqdn);

		return position1; // position1 can be null if position was not found
	}

	private static DeclarationFinder.InClassPosition sourceLocationToInClassPosition(SourceLocation sourceLocation,
			String classfqdn) {
		if (sourceLocation != null) {
			if (sourceLocation instanceof ZipSourceLocation) {
				try (ZipFile zipFile = new ZipFile(new File(sourceLocation.getLocationAsString()))) {
					String entryName = classfqdn.replaceAll("\\.", "/");
					entryName = entryName + ".java";
					ZipEntry entry = zipFile.getEntry(entryName);
					if (entry == null)
						entry = zipFile.getEntry("src/" + entryName);
					if (entry != null) {
						String code = ZipIO.entryToString(zipFile, entry);
						DeclarationFinder.InClassPosition position = new DeclarationFinder.InClassPosition();
						position.classFileNode = tmpFileFromCode(classfqdn, code);
						position.openInReadOnly = true;
						position.virtualFile = new File(classfqdn.replaceAll("\\.", "/") + ".java");
						return position;
					}
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	private static File tmpFileFromCode(String classfqdn, String code) {
		File tmp = null;
		try {
			tmp = File.createTempFile(classfqdn, ".java");
			tmp.deleteOnExit();
			FileIO.writeStringToFile(code, tmp);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return tmp;
	}

	static String tryToFQDNClass(String name, CompilationUnit foundIn) {
		if (name.contains("."))
			return name;

		List<ImportDeclaration> imports = foundIn.getImports();

		for (ImportDeclaration singleImport : imports)
			if (singleImport.getName().contains(name))
				return singleImport.getName();

		return name;
	}
}
