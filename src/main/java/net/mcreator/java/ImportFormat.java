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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ImportFormat {

	private final Map<String, List<String>> cache = new HashMap<>();

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
				cache.clear();
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

			// get list of import candidates
			Set<String> importsToAdd = getUsedWildcardImports(memberList, cache);
			importsToAdd.addAll(getUsedImports(memberList, imports));

			// then clean up imports that are not needed and resolve duplicates
			cleanupAndResolveDuplicates(workspace, importsToAdd, imports, cu.getPackageName());

			StringBuilder importscode = new StringBuilder();
			importsToAdd.stream().sorted(Collections.reverseOrder())
					.forEach(i -> importscode.append("import ").append(i).append(";\n"));

			return formatImportGroups(before + "\n" + importscode + "\n" + after);
		}

		return code;
	}

	private static final Pattern DEFAULT_PACKAGE_IMPORT = Pattern.compile("^java\\.lang\\.[A-Z]\\w+");

	/**
	 * Cleanup redundant and duplicate imports (imports of the same class from multiple packages). When possible, we prioritize in this order:
	 * <p>
	 * 1. If there are existing imports for given class, use the package of the existing import
	 * 2. If class exists in project package, it is imported from there
	 * 3. Otherwise, we consider duplicates_whitelist and priority_imports configuration of the current generator
	 * <p>
	 * This method also removes redundant imports:
	 * <p>
	 * 1. It removes default package imports (java.lang.*)
	 * 2. It removes imports from the package the current class is in
	 *
	 * @param workspace       Workspace instance to use for project package detection
	 * @param importsToAdd    Set of all imports that will be reduced
	 * @param existingImports Existing imports in the java file
	 * @param currentPackage  Package name of the current class
	 */
	private void cleanupAndResolveDuplicates(@Nullable Workspace workspace, Set<String> importsToAdd,
			Set<String> existingImports, @Nullable String currentPackage) {
		String workspacePackageRoot;
		if (workspace != null) {
			workspacePackageRoot = workspace.getWorkspaceSettings().getModElementsPackage();
		} else {
			workspacePackageRoot = null;
		}

		Set<String> classesFromProjectPackage = new HashSet<>();

		// list of imports to remove, so we don't modify the set during iteration
		Set<String> importsToRemove = new HashSet<>();

		// first remove current class package and default package imports
		// in the same iteration, we also generate a map of classes to packages
		Map<String, List<String>> classToPackageMap = new HashMap<>();
		for (String _import : importsToAdd) {
			String originalImport = _import;
			String importPackage = null;

			int lastDotIndex = _import.lastIndexOf('.');

			if (lastDotIndex != -1) {
				importPackage = _import.substring(0, lastDotIndex);
				_import = _import.substring(lastDotIndex);
			}

			// collect list of classes from workspace package
			if (workspacePackageRoot != null) {
				if (originalImport.startsWith(workspacePackageRoot + ".")) {
					classesFromProjectPackage.add(_import);
				}
			}

			// add to map of classes to packages (same class name can be present in multiple packages
			if (!classToPackageMap.containsKey(_import)) {
				classToPackageMap.put(_import, new ArrayList<>());
			}
			classToPackageMap.get(_import).add(originalImport);

			// remove imports from default package at this step
			if (DEFAULT_PACKAGE_IMPORT.matcher(originalImport).matches()) {
				importsToRemove.add(originalImport);
			}

			// remove imports from the current package
			if (importPackage != null && currentPackage != null) {
				if (importPackage.equals(currentPackage)) {
					importsToRemove.add(originalImport);
				}
			}
		}

		// Remove classes that map to single package from the map
		classToPackageMap.entrySet().removeIf(entry -> entry.getValue().size() <= 1);

		List<String> duplicatesWhitelist = workspace != null ?
				workspace.getGeneratorConfiguration().getImportFormatterDuplicatesWhitelist() :
				List.of();
		Map<String, String> priorityImports = workspace != null ?
				workspace.getGeneratorConfiguration().getImportFormatterPriorityImports() :
				Map.of();

		for (String duplicateClass : classToPackageMap.keySet()) {
			List<String> packageCandidates = classToPackageMap.get(duplicateClass);

			// Priority 1: check if one of existing imports defines which package to use for given class with multiple packages
			String existingImportForClass = existingImports.stream()
					.filter(existingImport -> existingImport.endsWith(duplicateClass)).findFirst().orElse(null);
			if (existingImportForClass != null) {
				// Remove all imports for this class except the existing one
				importsToRemove.addAll(
						packageCandidates.stream().filter(_import -> !_import.equals(existingImportForClass)).toList());
				continue; // We use existing import, no further processing needed
			}

			// Priority 2: if class is defined in project package root, prioritize it over other imports
			if (workspacePackageRoot != null) {
				if (classesFromProjectPackage.contains(duplicateClass)) {
					importsToRemove.addAll(packageCandidates.stream()
							.filter(_import -> !_import.startsWith(workspacePackageRoot + ".")).toList());
					continue; // We use import from package root, no further processing needed
				}
			}

			// Priority 3: if class is defined in priority imports, prioritize it over other imports
			if (priorityImports.containsKey(duplicateClass)) {
				importsToRemove.addAll(packageCandidates.stream()
						.filter(_import -> !_import.equals(priorityImports.get(duplicateClass))).toList());
				continue; // We use import from priority imports, no further processing needed
			}

			// Priority 4: if class is defined in duplicates whitelist, prioritize it over other imports
			importsToRemove.addAll(packageCandidates.stream()
					.filter(_import -> duplicatesWhitelist.stream().noneMatch(_import::startsWith)).toList());
		}

		// finally, remove duplicates and imports from default or current package
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
