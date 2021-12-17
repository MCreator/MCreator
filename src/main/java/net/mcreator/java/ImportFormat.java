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

import net.mcreator.workspace.Workspace;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;

import javax.annotation.Nullable;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class ImportFormat {

	private Map<String, List<String>> cache = new HashMap<>();

	public static String removeImports(String code, String replacement) {
		CompilationUnit cu = new ASTFactory().getCompilationUnit("", new Scanner(new StringReader(code)));

		int spos = 0, epos = 0;
		List<ImportDeclaration> currentImports = cu.getImports();
		for (int i = 0; i < currentImports.size(); i++) {
			ImportDeclaration imp = currentImports.get(i);
			if (i == 0)
				spos = imp.getNameStartOffset();

			if (i == currentImports.size() - 1)
				epos = imp.getNameEndOffset();
		}

		String before, after;
		if (epos != 0) { // we found imports
			before = code.substring(0, spos);
			int last = before.lastIndexOf("import");
			before = before.substring(0, last);

			after = code.substring(epos);
			int first = after.indexOf(";");
			after = after.substring(first + 1);
		} else if (cu.getPackage() != null) {
			before = code.substring(0, cu.getPackage().getNameEndOffset()) + ";";

			after = code.substring(cu.getPackage().getNameEndOffset());
			int first = after.indexOf(";");
			after = after.substring(first + 1);
		} else {
			before = "";
			after = code;
		}

		return before.trim() + replacement + after.trim();
	}

	public String arrangeImports(@Nullable Workspace workspace, String code, boolean skipModClassReloading) {
		if (workspace != null && workspace.getGenerator().getGradleCache() != null) {
			if (!skipModClassReloading) {
				cache = new HashMap<>();
				cache.putAll(workspace.getGenerator().getGradleCache().getImportTree());
				ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), cache);
			}

			CompilationUnit cu = new ASTFactory().getCompilationUnit("", new Scanner(new StringReader(code)));

			Set<String> imports = new HashSet<>();

			int spos = 0, epos = 0;
			List<ImportDeclaration> currentImports = cu.getImports();
			for (int i = 0; i < currentImports.size(); i++) {
				ImportDeclaration imp = currentImports.get(i);

				if (i == 0)
					spos = imp.getNameStartOffset();

				if (i == currentImports.size() - 1)
					epos = imp.getNameEndOffset();

				if (!imp.isWildcard())
					imports.add(imp.getName());
			}

			String before, after;
			if (epos != 0) { // we found imports
				before = code.substring(0, spos);
				int last = before.lastIndexOf("import");
				before = before.substring(0, last);

				after = code.substring(epos);
				int first = after.indexOf(";");
				after = after.substring(first + 1);
			} else if (cu.getPackage() != null) {
				before = code.substring(0, cu.getPackage().getNameEndOffset()) + ";";

				after = code.substring(cu.getPackage().getNameEndOffset());
				int first = after.indexOf(";");
				after = after.substring(first + 1);
			} else {
				before = "";
				after = code;
			}

			Set<String> memberList = JavaMemberExtractor.getMemberList(before + after);

			// sorting out imports
			Set<String> importsToAdd = getUsedWildcardImports(memberList, cache);
			importsToAdd.addAll(getUsedImports(memberList, imports));

			resolveDuplicates(workspace, importsToAdd, imports, cu.getPackageName());

			StringBuilder importscode = new StringBuilder();
			importsToAdd.stream().sorted(Collections.reverseOrder())
					.forEach(i -> importscode.append("import ").append(i).append(";\n"));

			return formatImportGroups(before + "\n" + importscode + "\n" + after);
		}

		return code;
	}

	/**
	 * Remove duplicate imports (imports of the same class from multiple packages). When possible, we prioritize in this order:
	 * <p>
	 * 1. If there are existing imports for given class, use the package of the existing import
	 * 2. If class exists in project package, it is imported from there
	 * 3. If the class is in one of the prioritized packages (net.minecraft*, java.util*, java.io*)
	 * <p>
	 * This method does some other optimizations too:
	 * <p>
	 * 1. It removes default package imports (java.lang.*)
	 * 2. It removed imports from the package the current class is in
	 *
	 * @param workspace      Workspace instance to use for project package detection
	 * @param importsToAdd   Set of all imports that will be reduced
	 * @param imports        Existing imports in the java file
	 * @param currentPackage Package name of the current class
	 */
	private void resolveDuplicates(@Nullable Workspace workspace, Set<String> importsToAdd, Set<String> imports,
			@Nullable String currentPackage) {
		String workspacePackageRoot = null;
		if (workspace != null) {
			workspacePackageRoot = workspace.getWorkspaceSettings().getModElementsPackage();
		}

		Set<String> classesFromProjectPackage = new HashSet<>();

		// list of imports to remove, so we don't modify the set during iteration
		Set<String> importsToRemove = new HashSet<>();

		// first detect duplicates and eliminate duplicates of classes that also exist in project package
		Set<String> scannedClasses = new HashSet<>();
		Set<String> duplicateClasses = new HashSet<>();
		for (String _import : importsToAdd) {
			String originalImport = _import;
			String importPackage = null;

			int lastIndxDot = _import.lastIndexOf('.');

			if (lastIndxDot != -1) {
				importPackage = _import.substring(0, lastIndxDot);
				_import = _import.substring(lastIndxDot);
			}

			// if class is defined in project package root, prioritize if over other imports
			if (workspacePackageRoot != null) {
				if (originalImport.startsWith(workspacePackageRoot + ".")) {
					classesFromProjectPackage.add(_import);
				}
			}

			if (scannedClasses.contains(_import)) // if this class was scanned before, it is dulpicate
				duplicateClasses.add(_import);
			else
				scannedClasses.add(_import);

			// remove imports from default package at this step
			if (originalImport.matches("^java\\.lang\\.[A-Z]\\w+")) {
				importsToRemove.add(originalImport);
			}

			if (importPackage != null && currentPackage != null) {
				if (importPackage.equals(currentPackage)) {
					importsToRemove.add(originalImport); // remove imports from the current package
				}
			}
		}

		// then resolve the duplicates
		outer:
		for (String _import : importsToAdd) {
			for (String duplicateClass : duplicateClasses) {
				if (_import.endsWith(duplicateClass)) {

					// check if one of existing imports defines which package to use for given class with multiple packages
					for (String existingImport : imports) {
						if (existingImport.endsWith(duplicateClass)) {
							if (!_import.equals(existingImport)) {
								importsToRemove.add(_import);
							}
							continue outer;
						}
					}

					// if class is defined in project package root, prioritize if over other imports
					if (workspacePackageRoot != null) {
						if (classesFromProjectPackage.contains(duplicateClass)) {
							if (!_import.startsWith(workspacePackageRoot
									+ ".")) { // import for class from project from external package
								importsToRemove.add(_import);
							}
							continue outer; // this is import of this class for the project
						}
					}

					// if class with multiple packages is not included in one of the default packages, we do not import it
					if (!_import.startsWith("net.minecraft") && !_import.startsWith("java.util") && !_import.startsWith(
							"java.io") && !_import.startsWith("org.lwjgl") && !_import.startsWith("java.lang")
							&& !_import.startsWith("org.bukkit") && !_import.startsWith("net.fabricmc") &&
							!_import.startsWith("com.google.gson")) {
						importsToRemove.add(_import);
						continue outer;
					}
				}
			}
		}

		// finally, remove duplicates
		importsToAdd.removeAll(importsToRemove);
	}

	private String formatImportGroups(String code) {
		String[] lines = code.split("[\\r\\n]+");
		String prevGroup = null;
		StringBuilder retCode = new StringBuilder();
		for (String line : lines) {
			if (line.trim().startsWith("import ")) {
				String[] path = line.split("\\.");
				String currGroup = path[0] + path[1];
				if (!currGroup.equals(prevGroup)) {
					retCode.append("\n");
					prevGroup = currGroup;
				}
			}
			retCode.append(line).append("\n");
		}
		return retCode.toString();
	}

	private Set<String> getUsedWildcardImports(Set<String> memberList, Map<String, List<String>> loadFrom) {
		return memberList.parallelStream().filter(loadFrom::containsKey)
				.flatMap(member -> loadFrom.get(member).stream()).collect(Collectors.toSet());
	}

	private Set<String> getUsedImports(Set<String> memberList, Set<String> normalImports) {
		return normalImports.parallelStream().filter(normalImport -> {
			String[] stringArray = normalImport.split("\\.");
			return memberList.contains(stringArray[stringArray.length - 1]);
		}).collect(Collectors.toSet());
	}

}
