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
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Type;

import java.util.List;

class DeclarationChecker {

	static DeclarationFinder.InClassPosition checkForThisDeclaration(String code, String clickedWord,
			TypeDeclaration classNameInWhichWeAre) {
		if ("this".equals(clickedWord)) {
			int startPos = code.indexOf("class " + classNameInWhichWeAre.getName());
			if (startPos != -1) {
				DeclarationFinder.InClassPosition position = new DeclarationFinder.InClassPosition();
				position.classFileNode = null;
				position.carret = startPos + 6;
				return position;
			}

		}
		return null;
	}

	static DeclarationFinder.InClassPosition checkForSuperDeclaration(Workspace workspace, String clickedWord,
			TypeDeclaration classNameInWhichWeAre, CompilationUnit compilationUnit, JarManager jarManager) {
		if ("super".equals(clickedWord) && classNameInWhichWeAre instanceof NormalClassDeclaration) {
			Type superClassName = ((NormalClassDeclaration) classNameInWhichWeAre).getExtendedType();
			String fqdnSuperClassName = ClassFinder
					.tryToFQDNClass(superClassName.getName(true, false), compilationUnit);
			DeclarationFinder.InClassPosition position = ClassFinder
					.fqdnToInClassPosition(workspace, fqdnSuperClassName, compilationUnit.getPackageName(), jarManager);
			if (position != null) {
				String codeFromParent = FileIO.readFileToString(position.classFileNode);
				int startPos = codeFromParent.indexOf("class " + superClassName.getName(false, false));
				position.carret = startPos + 6;
				return position;
			}
		}
		return null;
	}

	static DeclarationFinder.InClassPosition checkForClassDeclaration(Workspace workspace, String clickedWord,
			CompilationUnit compilationUnit, JarManager jarManager) {
		List<ImportDeclaration> imports = compilationUnit.getImports();

		// first we check if the word could be found in imports, to get the fqdn
		for (ImportDeclaration singleImport : imports) {
			String[] path = singleImport.getName().split("\\.");
			if (path.length > 0) {
				String last = path[path.length - 1];
				if (last.equals(clickedWord)) {
					DeclarationFinder.InClassPosition position = ClassFinder
							.fqdnToInClassPosition(workspace, singleImport.getName(), compilationUnit.getPackageName(),
									jarManager);
					return inClassPositionCarretFix(position, clickedWord);
				}
			}
		}

		// if it is not in the imports, it could be from the same package
		DeclarationFinder.InClassPosition position = ClassFinder
				.fqdnToInClassPosition(workspace, clickedWord, compilationUnit.getPackageName(), jarManager);
		return inClassPositionCarretFix(position, clickedWord);
	}

	private static DeclarationFinder.InClassPosition inClassPositionCarretFix(
			DeclarationFinder.InClassPosition original, String className) {
		if (original == null)
			return null;
		String codeFromParent = FileIO.readFileToString(original.classFileNode);
		int startPos = codeFromParent.indexOf("class " + className);
		original.carret = startPos + 6;
		return original;
	}

}
