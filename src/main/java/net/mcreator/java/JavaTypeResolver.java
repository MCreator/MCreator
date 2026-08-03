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
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaTypeResolver {

	public static class CompletionItem {
		public String label;
		public String insertText;
		public String kind; // "method" or "field"
		public String detail;
		public boolean isSnippet;
	}

	private static void addMethodCompletion(String mName, String returnType, String[] paramNames, List<CompletionItem> result, Set<String> added) {
		StringBuilder label = new StringBuilder(mName).append("(");
		StringBuilder insert = new StringBuilder(mName).append("(");
		
		for (int i = 0; i < paramNames.length; i++) {
			if (i > 0) {
				label.append(", ");
				insert.append(", ");
			}
			String pName = paramNames[i];
			label.append(pName);
			insert.append("${").append(i + 1).append(":").append(pName).append("}");
		}
		label.append(")");
		insert.append(")");

		String key = label.toString();
		if (added.add(key)) {
			CompletionItem item = new CompletionItem();
			item.label = key;
			item.insertText = paramNames.length > 0 ? insert.toString() : mName + "()";
			item.kind = "method";
			item.detail = returnType;
			item.isSnippet = paramNames.length > 0;
			result.add(item);
		}
	}

	private static void addFieldCompletion(String fName, String fType, List<CompletionItem> result, Set<String> added) {
		if (added.add(fName)) {
			CompletionItem item = new CompletionItem();
			item.label = fName;
			item.insertText = fName;
			item.kind = "field";
			item.detail = fType;
			item.isSnippet = false;
			result.add(item);
		}
	}

	public static List<CompletionItem> getCompletionsFor(String targetName, String code, Workspace workspace) {
		List<CompletionItem> result = new ArrayList<>();
		if (targetName == null || targetName.trim().isEmpty()) return result;
		targetName = targetName.trim();

		String fqdn = resolveTargetFQDN(targetName, code, workspace);
		if (fqdn == null) return result;

		Set<String> added = new HashSet<>();

		// JDK runtime classes
		try {
			Class<?> clazz = Class.forName(fqdn);
			for (Method m : clazz.getMethods()) {
				if (!Modifier.isPublic(m.getModifiers())) continue;

				Class<?>[] params = m.getParameterTypes();
				String[] pNames = new String[params.length];
				for (int i = 0; i < params.length; i++) {
					pNames[i] = params[i].getSimpleName();
				}
				addMethodCompletion(m.getName(), m.getReturnType().getSimpleName(), pNames, result, added);
			}

			for (Field f : clazz.getFields()) {
				if (!Modifier.isPublic(f.getModifiers())) continue;
				addFieldCompletion(f.getName(), f.getType().getSimpleName(), result, added);
			}

			if (!result.isEmpty()) return result;
		} catch (ClassNotFoundException ignored) {
		}

		// Workspace source
		if (workspace != null && workspace.getGenerator() != null) {
			File srcFile = new File(workspace.getGenerator().getSourceRoot(), fqdn.replace('.', '/') + ".java");
			if (srcFile.isFile()) {
				String srcCode = FileIO.readFileToString(srcFile);
				parseSourceCodeCompletions(srcCode, result, added);
				if (!result.isEmpty()) return result;
			}

			// JarManager for minecraft classes
			ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
			if (jarManager != null) {
				SourceLocation sourceLocation = jarManager.getSourceLocForClass(fqdn);
				if (sourceLocation instanceof ZipSourceLocation) {
					try (ZipFile zipFile = ZipIO.openZipFile(new File(sourceLocation.getLocationAsString()))) {
						String entryName = fqdn.replace('.', '/') + ".java";
						Enumeration<? extends ZipEntry> entries = zipFile.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							if (entry.getName().endsWith(entryName)) {
								String srcCode = ZipIO.entryToString(zipFile, entry);
								parseSourceCodeCompletions(srcCode, result, added);
								break;
							}
						}
					} catch (Exception ignored) {
					}
				}
			}
		}

		return result;
	}

	private static void parseSourceCodeCompletions(String srcCode, List<CompletionItem> result, Set<String> added) {
		if (srcCode == null || srcCode.isEmpty()) return;

		Pattern methodPattern = Pattern.compile("(?:public|protected|static|final|native|synchronized|\\s)+\\b([A-Za-z0-9_<>]+)\\s+([a-zA-Z0-9_]+)\\s*\\(([^)]*)\\)");
		Matcher matcher = methodPattern.matcher(srcCode);
		while (matcher.find()) {
			String returnType = matcher.group(1);
			String mName = matcher.group(2);
			String paramsRaw = matcher.group(3);

			if (mName.equals("if") || mName.equals("for") || mName.equals("while") || mName.equals("switch") || mName.equals("catch") || mName.equals("class")) continue;

			String[] pNames;
			if (!paramsRaw.trim().isEmpty()) {
				String[] params = paramsRaw.split(",");
				pNames = new String[params.length];
				for (int p = 0; p < params.length; p++) {
					String pToken = params[p].trim();
					pNames[p] = pToken.contains(" ") ? pToken.substring(pToken.lastIndexOf(' ') + 1) : pToken;
				}
			} else {
				pNames = new String[0];
			}
			addMethodCompletion(mName, returnType, pNames, result, added);
		}

		Pattern fieldPattern = Pattern.compile("(?:public|protected|static|final|\\s)+\\b([A-Za-z0-9_<>]+)\\s+([a-zA-Z0-9_]+)\\s*(?:=|[;=])");
		Matcher fieldMatcher = fieldPattern.matcher(srcCode);
		while (fieldMatcher.find()) {
			String fType = fieldMatcher.group(1);
			String fName = fieldMatcher.group(2);
			if (fName.equals("class") || fName.equals("interface") || fName.equals("enum")) continue;
			addFieldCompletion(fName, fType, result, added);
		}
	}

	public static String resolveTargetFQDN(String targetName, String code, Workspace workspace) {
		if (code == null) code = "";

		Map<String, String> imports = new HashMap<>();
		Pattern importPattern = Pattern.compile("import\\s+([a-zA-Z0-9_.]+);");
		Matcher importMatcher = importPattern.matcher(code);
		while (importMatcher.find()) {
			String imp = importMatcher.group(1);
			if (imp.contains(".")) {
				String simple = imp.substring(imp.lastIndexOf('.') + 1);
				imports.put(simple, imp);
			}
		}

		String[] javaLangTypes = {"String", "Math", "System", "Object", "Integer", "Double", "Float", "Boolean", "Long", "Short", "Byte", "Character", "Thread", "Throwable", "Exception"};
		for (String t : javaLangTypes) {
			imports.putIfAbsent(t, "java.lang." + t);
		}

		Pattern varPattern = Pattern.compile("\\b([A-Z][a-zA-Z0-9_<>]*)\\s+(?:[a-zA-Z0-9_]+\\s*,\\s*)*" + Pattern.quote(targetName) + "(?:\\s*[,;=)]|\\b)");
		Matcher varMatcher = varPattern.matcher(code);
		String typeName = null;
		if (varMatcher.find()) {
			typeName = varMatcher.group(1);
			if (typeName.contains("<") && typeName.contains(">")) {
				typeName = typeName.substring(typeName.indexOf('<') + 1, typeName.indexOf('>'));
			} else if (typeName.contains("<")) {
				typeName = typeName.substring(0, typeName.indexOf('<'));
			}
		} else {
			if (!targetName.isEmpty() && Character.isUpperCase(targetName.charAt(0))) {
				typeName = targetName;
			}
		}

		if (typeName == null) return null;

		if (imports.containsKey(typeName)) {
			return imports.get(typeName);
		}

		if (workspace != null && workspace.getGenerator() != null) {
			ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
			if (jarManager != null) {
				Map<String, List<String>> tree = ImportTreeBuilder.generateImportTree(jarManager);
				if (tree != null && tree.containsKey(typeName)) {
					List<String> fqdns = tree.get(typeName);
					if (fqdns != null && !fqdns.isEmpty()) {
						return fqdns.getFirst();
					}
				}
			}
		}

		return null;
	}
}