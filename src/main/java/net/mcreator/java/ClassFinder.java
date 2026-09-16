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
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ClassFinder {

	private static final Logger LOG = LogManager.getLogger("Class Finder");

	public static String getCurrentFQDN(JavaParser parser) {
		Iterator<TypeDeclaration> i = parser.getCompilationUnit().getTypeDeclarationIterator();
		while (i.hasNext()) {
			TypeDeclaration td = i.next();
			if (td instanceof NormalClassDeclaration normalClassDeclaration) {
				return normalClassDeclaration.getPackage() + "." + normalClassDeclaration.getName();
			}
		}
		return null;
	}

	public static DeclarationFinder.InClassPosition fqdnToInClassPosition(Workspace workspace, String classIn,
			String packagefqdn, @Nullable ProjectJarManager jarManager) {
		DeclarationFinder.InClassPosition position = new DeclarationFinder.InClassPosition();
		String classFQDN;

		// if there is no package, it is a class in the current package
		if (!classIn.contains(".")) {
			if (new File(workspace.getGenerator().getSourceRoot(),
					packagefqdn.replace(".", "/") + "/" + classIn + ".java").isFile()) {
				position.classFileNode = new File(workspace.getGenerator().getSourceRoot(),
						packagefqdn.replace(".", "/") + "/" + classIn + ".java");
				position.openInReadOnly = false;
				position.virtualFile = position.classFileNode;
				return position;
			}

			// if there was no package, but the class was not found in SRCROOT, add package declaration to it
			classFQDN = packagefqdn + "." + classIn;
		} else
			classFQDN = classIn;

		// next we check if the class might be located in the src directory of the project under the given fqdn
		if (new File(workspace.getGenerator().getSourceRoot(), classFQDN.replace(".", "/") + ".java").isFile()) {
			position.classFileNode = new File(workspace.getGenerator().getSourceRoot(),
					classFQDN.replace(".", "/") + ".java");
			position.openInReadOnly = false;
			position.virtualFile = position.classFileNode;
			return position;
		}

		if (jarManager == null)
			return null;

		// next we try to find the declaration using the jar manager to check
		// if the class we are looking for is loaded with source
		DeclarationFinder.InClassPosition position1 = sourceCodeToInClassPosition(
				jarManager.getSourceCodeForClass(classFQDN), classFQDN);
		if (position1 != null)
			return position1;

		// next we try to find the declaration using the jar manager to check
		// if the class we are looking for is loaded with source
		// this time in default java lang package
		position1 = sourceCodeToInClassPosition(jarManager.getSourceCodeForClass("java.lang." + classIn),
				"java.lang." + classIn);

		return position1; // position1 can be null if position was not found
	}

	private static DeclarationFinder.InClassPosition sourceCodeToInClassPosition(@Nullable String code,
			String classfqdn) {
		if (code == null)
			return null;

		DeclarationFinder.InClassPosition position = new DeclarationFinder.InClassPosition();
		position.classFileNode = tmpFileFromCode(classfqdn, code);
		position.openInReadOnly = true;
		position.virtualFile = new File(classfqdn.replaceAll("\\.", "/") + ".java");
		return position;
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
