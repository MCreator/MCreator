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

import java.io.File;
import java.util.List;

public class DeclarationChecker {

	public static InClassPosition checkForThisDeclaration(String code, String clickedWord,
			TypeDeclaration classNameInWhichWeAre) {
		if ("this".equals(clickedWord)) {
			int startPos = code.indexOf("class " + classNameInWhichWeAre.getName());
			if (startPos != -1) {
				InClassPosition position = new InClassPosition();
				position.classFileNode = null;
				position.caret = startPos + 6;
				return position;
			}

		}
		return null;
	}

	public static InClassPosition checkForSuperDeclaration(Workspace workspace, String clickedWord,
			TypeDeclaration classNameInWhichWeAre, CompilationUnit compilationUnit, JarManager jarManager) {
		if ("super".equals(clickedWord) && classNameInWhichWeAre instanceof NormalClassDeclaration) {
			Type superClassName = ((NormalClassDeclaration) classNameInWhichWeAre).getExtendedType();
			String fqdnSuperClassName = ClassFinder.tryToFQDNClass(superClassName.getName(true, false),
					compilationUnit);
			InClassPosition position = ClassFinder.fqdnToInClassPosition(workspace,
					fqdnSuperClassName, compilationUnit.getPackageName(), jarManager);
			if (position != null) {
				String codeFromParent = FileIO.readFileToString(position.classFileNode);
				int startPos = codeFromParent.indexOf("class " + superClassName.getName(false, false));
				position.caret = startPos + 6;
				return position;
			}
		}
		return null;
	}

	public static InClassPosition checkForClassDeclaration(Workspace workspace, String clickedWord,
			CompilationUnit compilationUnit, JarManager jarManager) {
		List<ImportDeclaration> imports = compilationUnit.getImports();

		if (clickedWord.contains(".")) {
			InClassPosition position = ClassFinder.fqdnToInClassPosition(workspace, clickedWord,
					compilationUnit.getPackageName(), jarManager);
			if (position != null)
				return inClassPositionCaretFix(position, clickedWord);
		}

		// first we check if the word could be found in imports, to get the fqdn
		for (ImportDeclaration singleImport : imports) {
			String[] path = singleImport.getName().split("\\.");
			if (path.length > 0) {
				String last = path[path.length - 1];
				if (last.equals(clickedWord)) {
					InClassPosition position = ClassFinder.fqdnToInClassPosition(workspace,
							singleImport.getName(), compilationUnit.getPackageName(), jarManager);
					return inClassPositionCaretFix(position, clickedWord);
				} else if (singleImport.isWildcard()) { // if it is wildcard import, check if that package contains this class
					String packageName = singleImport.getName().substring(0, singleImport.getName().lastIndexOf('.'));
					InClassPosition position = ClassFinder.fqdnToInClassPosition(workspace,
							packageName + "." + clickedWord, compilationUnit.getPackageName(), jarManager);
					if (position != null)
						return inClassPositionCaretFix(position, clickedWord);
				}
			}
		}

		// if it is not in the imports, it could be from the same package
		InClassPosition position = ClassFinder.fqdnToInClassPosition(workspace, clickedWord,
				compilationUnit.getPackageName(), jarManager);
		return inClassPositionCaretFix(position, clickedWord);
	}

	private static InClassPosition inClassPositionCaretFix(InClassPosition original,
			String className) {
		if (original == null)
			return null;
		if (original.classFileNode != null) {
			String codeFromParent = FileIO.readFileToString(original.classFileNode);
			if (codeFromParent != null) {
				int startPos = codeFromParent.indexOf("class " + className);
				if (startPos != -1) {
					original.caret = startPos + 6;
				}
			}
		}
		return original;
	}

	public static class InClassPosition {
		public int caret;
		public boolean openInReadOnly = true;
		public File virtualFile;
		public File classFileNode; //null if current class
	}
}