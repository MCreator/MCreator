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
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaTypeResolver {

	private static final Logger LOG = LogManager.getLogger(JavaTypeResolver.class);
	private static final Pattern METHOD_PATTERN = Pattern.compile("^[ \\t]*((?:(?:public|protected|private|default|static|final|native|synchronized|abstract)\\s+)*)\\b([A-Za-z0-9_<>?\\[\\]]+)\\s+([a-zA-Z0-9_]+)\\s*\\(([^)]*)\\)", Pattern.MULTILINE);
	private static final Pattern FIELD_PATTERN = Pattern.compile("^[ \\t]*((?:(?:public|protected|private|default|static|final|volatile|transient)\\s+)+)\\b([A-Za-z0-9_<>?\\[\\]]+)\\s+([a-zA-Z0-9_]+)\\s*(?:=|[;=])", Pattern.MULTILINE);
	private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([a-zA-Z0-9_.]+);");
	private static final Pattern EXTENDS_PATTERN = Pattern.compile("class\\s+[A-Za-z0-9_]+\\s+extends\\s+([A-Za-z0-9_.]+)");

	public static class CompletionItem {
		public String label;
		public String insertText;
		public String kind; // "method" or "field"
		public String detail;
		public String declaringClass;
		public String visibility = "public"; // "public", "protected", "private", "package"
		public boolean isSnippet;
		public boolean isStatic;
		public boolean isFinal;
		public boolean isAbstract;
		public boolean isDeprecated;
		public List<String> paramTypes;
		public List<String> paramNames;
		public List<String> fqdnParamTypes;
	}

	public static class ResolutionResult {
		public String fqdn;
		public boolean isStaticContext;
		public ResolutionResult(String fqdn, boolean isStaticContext) {
			this.fqdn = fqdn;
			this.isStaticContext = isStaticContext;
		}
	}

	private static void addMethodCompletion(String mName, String returnType, String[] paramTypes, String[] paramNames, String[] fqdnParamTypes, boolean isStatic, boolean isAbstract, boolean isDeprecated, String visibility, String declaringClass, List<CompletionItem> result, Set<String> added) {
		StringBuilder label = new StringBuilder(mName).append("(");
		StringBuilder insert = new StringBuilder(mName).append("(");

		for (int i = 0; i < paramNames.length; i++) {
			if (i > 0) {
				label.append(", ");
				insert.append(", ");
			}
			String pType = paramTypes[i];
			String pName = paramNames[i];
			label.append(pType).append(" ").append(pName);
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
			item.declaringClass = declaringClass;
			item.visibility = visibility;
			item.isSnippet = paramNames.length > 0;
			item.isStatic = isStatic;
			item.isAbstract = isAbstract;
			item.isDeprecated = isDeprecated;
			item.paramTypes = Arrays.asList(paramTypes);
			item.paramNames = Arrays.asList(paramNames);
			item.fqdnParamTypes = fqdnParamTypes != null ? Arrays.asList(fqdnParamTypes) : Arrays.asList(paramTypes);
			result.add(item);
		}
	}

	private static void addFieldCompletion(String fName, String fType, boolean isStatic, boolean isFinal, boolean isDeprecated, String visibility, String declaringClass, List<CompletionItem> result, Set<String> added) {
		if (added.add(fName)) {
			CompletionItem item = new CompletionItem();
			item.label = fName;
			item.insertText = fName;
			item.kind = "field";
			item.detail = fType;
			item.declaringClass = declaringClass;
			item.visibility = visibility;
			item.isSnippet = false;
			item.isStatic = isStatic;
			item.isFinal = isFinal;
			item.isDeprecated = isDeprecated;
			result.add(item);
		}
	}

	private static final Map<String, List<CompletionItem>> MEMBER_CACHE = new ConcurrentHashMap<>();

	public static List<CompletionItem> getCompletionsFor(String targetName, String code, String codeBeforeCursor, Workspace workspace) {
		List<CompletionItem> result = new ArrayList<>();
		if (targetName == null || targetName.trim().isEmpty()) return result;
		targetName = targetName.trim();

		ResolutionResult res = resolveTargetFQDN(targetName, code, codeBeforeCursor, workspace);
		if (res == null || res.fqdn == null) return result;

		List<CompletionItem> allMembers = getMembersOfFQDN(res.fqdn, workspace);
		for (CompletionItem item : allMembers) {
			if (!res.isStaticContext || item.isStatic) {
				result.add(item);
			}
		}
		return result;
	}

	public static List<CompletionItem> getMembersOfFQDN(String fqdn, Workspace workspace) {
		if (fqdn == null || fqdn.isEmpty()) return new ArrayList<>();
		List<CompletionItem> cached = MEMBER_CACHE.get(fqdn);
		if (cached != null) {
			return new ArrayList<>(cached);
		}
		List<CompletionItem> result = new ArrayList<>();
		Set<String> added = new HashSet<>();
		Set<String> visited = new HashSet<>();
		populateMembersOfFQDN(fqdn, workspace, result, added, visited);
		if (!result.isEmpty()) {
			MEMBER_CACHE.put(fqdn, Collections.unmodifiableList(new ArrayList<>(result)));
		}
		return result;
	}

	private static void populateMembersOfFQDN(String fqdn, Workspace workspace, List<CompletionItem> result, Set<String> added, Set<String> visited) {
		if (fqdn == null || !visited.add(fqdn)) return;

		String declaringClass = fqdn.contains(".") ? fqdn.substring(fqdn.lastIndexOf('.') + 1) : fqdn;

		try {
			Class<?> clazz = Class.forName(fqdn);
			for (Method m : clazz.getMethods()) {
				int mods = m.getModifiers();
				if (!Modifier.isPublic(mods) && !Modifier.isProtected(mods)) continue;

				String vis = Modifier.isPublic(mods) ? "public" : (Modifier.isProtected(mods) ? "protected" : (Modifier.isPrivate(mods) ? "private" : "package"));
				Class<?>[] params = m.getParameterTypes();
				String[] pTypes = new String[params.length];
				String[] pNames = new String[params.length];
				String[] fqdnPTypes = new String[params.length];
				for (int i = 0; i < params.length; i++) {
					pTypes[i] = params[i].getSimpleName();
					pNames[i] = "param" + i;
					fqdnPTypes[i] = params[i].getName();
				}
				addMethodCompletion(m.getName(), m.getReturnType().getSimpleName(), pTypes, pNames, fqdnPTypes, Modifier.isStatic(mods), Modifier.isAbstract(mods), m.isAnnotationPresent(Deprecated.class), vis, clazz.getSimpleName(), result, added);
			}

			for (Field f : clazz.getFields()) {
				int mods = f.getModifiers();
				if (!Modifier.isPublic(mods) && !Modifier.isProtected(mods)) continue;

				String vis = Modifier.isPublic(mods) ? "public" : (Modifier.isProtected(mods) ? "protected" : (Modifier.isPrivate(mods) ? "private" : "package"));
				addFieldCompletion(f.getName(), f.getType().getSimpleName(), Modifier.isStatic(mods), Modifier.isFinal(mods), f.isAnnotationPresent(Deprecated.class), vis, clazz.getSimpleName(), result, added);
			}

			return;
		} catch (ClassNotFoundException ignored) {
		}

		if (workspace != null && workspace.getGenerator() != null) {
			String srcCode = null;
			File srcFile = new File(workspace.getGenerator().getSourceRoot(), fqdn.replace('.', '/') + ".java");
			if (srcFile.isFile()) {
				srcCode = FileIO.readFileToString(srcFile);
			} else {
				ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
				if (jarManager != null) {
					SourceLocation sourceLocation = jarManager.getSourceLocForClass(fqdn);
					if (sourceLocation instanceof ZipSourceLocation) {
						try (ZipFile zipFile = ZipIO.openZipFile(new File(sourceLocation.getLocationAsString()))) {
							String entryName = fqdn.replace('.', '/') + ".java";
							ZipEntry entry = zipFile.getEntry(entryName);
							if (entry != null) {
								srcCode = ZipIO.entryToString(zipFile, entry);
							} else {
								Enumeration<? extends ZipEntry> entries = zipFile.entries();
								while (entries.hasMoreElements()) {
									ZipEntry e = entries.nextElement();
									if (e.getName().endsWith(entryName)) {
										srcCode = ZipIO.entryToString(zipFile, e);
										break;
									}
								}
							}
						} catch (Exception e) {
							LOG.debug("could not read source from jar", e);
						}
					}
				}
			}

			if (srcCode != null) {
				parseSourceCodeCompletions(srcCode, declaringClass, result, added);
				Matcher matcher = EXTENDS_PATTERN.matcher(srcCode);
				if (matcher.find()) {
					String parentName = matcher.group(1);
					Map<String, String> imports = parseImports(srcCode);
					String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
					String parentFQDN = resolveSimpleTypeName(parentName, imports, workspace, pkg);
					if (parentFQDN != null) {
						populateMembersOfFQDN(parentFQDN, workspace, result, added, visited);
					}
				}
			}
		}
	}

	private static void parseSourceCodeCompletions(String srcCode, String declaringClass, List<CompletionItem> result, Set<String> added) {
		if (srcCode == null || srcCode.isEmpty()) return;

		Map<String, String> imports = parseImports(srcCode);

		Matcher matcher = METHOD_PATTERN.matcher(srcCode);
		while (matcher.find()) {
			String modsStr = matcher.group(1);
			boolean isStatic = modsStr.contains("static");
			boolean isAbstract = modsStr.contains("abstract");
			String vis = modsStr.contains("protected") ? "protected" : (modsStr.contains("private") ? "private" : (modsStr.contains("public") ? "public" : "package"));
			String returnType = matcher.group(2);
			String mName = matcher.group(3);
			String paramsRaw = matcher.group(4);

			if (mName.equals("if") || mName.equals("for") || mName.equals("while") || mName.equals("switch") || mName.equals("catch") || mName.equals("class")) continue;
			if (returnType.equals("new") || returnType.equals("return") || returnType.equals("throw") || returnType.equals("else")) continue;

			String[] pTypes;
			String[] pNames;
			String[] fqdnPTypes;
			if (!paramsRaw.trim().isEmpty()) {
				String[] params = paramsRaw.split(",");
				pTypes = new String[params.length];
				pNames = new String[params.length];
				fqdnPTypes = new String[params.length];
				for (int p = 0; p < params.length; p++) {
					String pToken = params[p].trim();
					int lastSpace = pToken.lastIndexOf(' ');
					if (lastSpace != -1) {
						pTypes[p] = pToken.substring(0, lastSpace).trim();
						pNames[p] = pToken.substring(lastSpace + 1).trim();
					} else {
						pTypes[p] = pToken;
						pNames[p] = pToken;
					}
					String resolvedFQDN = imports.get(pTypes[p]);
					fqdnPTypes[p] = resolvedFQDN != null ? resolvedFQDN : pTypes[p];
				}
			} else {
				pTypes = new String[0];
				pNames = new String[0];
				fqdnPTypes = new String[0];
			}
			addMethodCompletion(mName, returnType, pTypes, pNames, fqdnPTypes, isStatic, isAbstract, false, vis, declaringClass, result, added);
		}

		Matcher fieldMatcher = FIELD_PATTERN.matcher(srcCode);
		while (fieldMatcher.find()) {
			String modsStr = fieldMatcher.group(1);
			boolean isStatic = modsStr.contains("static");
			boolean isFinal = modsStr.contains("final");
			String vis = modsStr.contains("protected") ? "protected" : (modsStr.contains("private") ? "private" : (modsStr.contains("public") ? "public" : "package"));
			String fType = fieldMatcher.group(2);
			String fName = fieldMatcher.group(3);
			if (fName.equals("class") || fName.equals("interface") || fName.equals("enum")) continue;
			addFieldCompletion(fName, fType, isStatic, isFinal, false, vis, declaringClass, result, added);
		}
	}

	private static Map<String, String> parseImports(String code) {
		Map<String, String> imports = new HashMap<>();
		Matcher importMatcher = IMPORT_PATTERN.matcher(code);
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
		return imports;
	}

	private static String resolveSimpleTypeName(String typeName, Map<String, String> imports, Workspace workspace, String currentPkg) {
		if (typeName == null) return null;
		if (typeName.contains(".")) return typeName;

		if (imports.containsKey(typeName)) {
			return imports.get(typeName);
		}

		String[] defaultPkgs = {"java.lang", "java.io", "java.util"};
		for (String pkg : defaultPkgs) {
			try {
				Class<?> clazz = Class.forName(pkg + "." + typeName);
				return clazz.getName();
			} catch (ClassNotFoundException ignored) {
			}
		}

		if (currentPkg != null && !currentPkg.isEmpty()) {
			String possibleFQDN = currentPkg + "." + typeName;
			if (workspace != null && workspace.getGenerator() != null) {
				File f = new File(workspace.getGenerator().getSourceRoot(), possibleFQDN.replace('.', '/') + ".java");
				if (f.exists()) return possibleFQDN;
			}
		}

		if (workspace != null && workspace.getGenerator() != null) {
			Map<String, List<String>> tree = null;
			if (workspace.getGenerator().getGradleCache() != null) {
				tree = workspace.getGenerator().getGradleCache().getImportTree();
			} else {
				ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
				if (jarManager != null) {
					tree = ImportTreeBuilder.generateImportTree(jarManager);
				}
			}

			if (tree != null && tree.containsKey(typeName)) {
				List<String> fqdns = tree.get(typeName);
				if (fqdns != null && !fqdns.isEmpty()) {
					return fqdns.getFirst();
				}
			}

			Map<String, List<String>> workspaceTree = new HashMap<>();
			ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), workspaceTree);
			if (workspaceTree.containsKey(typeName)) {
				List<String> fqdns = workspaceTree.get(typeName);
				if (fqdns != null && !fqdns.isEmpty()) {
					return fqdns.getFirst();
				}
			}
		}

		return null;
	}

	private static List<String> splitChains(String expression) {
		List<String> result = new ArrayList<>();
		int depth = 0;
		StringBuilder current = new StringBuilder();
		for (char c : expression.toCharArray()) {
			if (c == '(') depth++;
			else if (c == ')') depth--;
			else if (c == '.' && depth == 0) {
				result.add(current.toString().trim());
				current.setLength(0);
				continue;
			}
			current.append(c);
		}
		if (!current.isEmpty()) {
			result.add(current.toString().trim());
		}
		return result;
	}

	private static String getReturnTypeOfMember(String fqdn, String member, Workspace workspace) {
		if (fqdn == null || fqdn.isEmpty()) return null;
		String memberName = member;
		if (memberName.contains("(")) {
			memberName = memberName.substring(0, memberName.indexOf('('));
		}

		List<CompletionItem> members = getMembersOfFQDN(fqdn, workspace);
		for (CompletionItem item : members) {
			if (item.kind.equals("method") && item.label.startsWith(memberName + "(")) {
				return item.detail;
			} else if (item.kind.equals("field") && item.label.equals(memberName)) {
				return item.detail;
			}
		}
		return null;
	}

	public static ResolutionResult resolveTargetFQDN(String targetName, String code, String codeBeforeCursor, Workspace workspace) {
		if (code == null) code = "";
		if (codeBeforeCursor == null) codeBeforeCursor = code;

		Map<String, String> imports = parseImports(code);

		List<String> chain = splitChains(targetName);
		if (chain.isEmpty()) return null;

		boolean isStaticContext = false;
		String currentFQDN = null;
		String base = chain.getFirst();

		String currentClassFQDN = ClassFinder.getCurrentFQDN(code);
		String currentPkg = currentClassFQDN != null && currentClassFQDN.contains(".") ? currentClassFQDN.substring(0, currentClassFQDN.lastIndexOf('.')) : "";

		if (base.equals("this") || base.equals("super")) {
			currentFQDN = currentClassFQDN;
			if (base.equals("super") && currentFQDN != null) {
				Matcher matcher = EXTENDS_PATTERN.matcher(code);
				if (matcher.find()) {
					String parentName = matcher.group(1);
					currentFQDN = resolveSimpleTypeName(parentName, imports, workspace, currentPkg);
				} else {
					currentFQDN = "java.lang.Object";
				}
			}
		} else if (base.contains("(")) {
			String returnTypeSimple = getReturnTypeOfMember(currentClassFQDN, base, workspace);
			if (returnTypeSimple != null) {
				currentFQDN = resolveSimpleTypeName(returnTypeSimple, imports, workspace, currentPkg);
			}
		} else {
			Pattern varPattern = Pattern.compile("\\b([A-Z][a-zA-Z0-9_<>]*)\\s+(?:[a-zA-Z0-9_]+\\s*,\\s*)*" + Pattern.quote(base) + "(?:\\s*[,;=)]|\\b)");
			Matcher varMatcher = varPattern.matcher(codeBeforeCursor);
			String typeName = null;
			while (varMatcher.find()) {
				typeName = varMatcher.group(1);
				if (typeName.contains("<") && typeName.contains(">")) {
					typeName = typeName.substring(typeName.indexOf('<') + 1, typeName.indexOf('>'));
				} else if (typeName.contains("<")) {
					typeName = typeName.substring(0, typeName.indexOf('<'));
				}
			}

			if (typeName == null) {
				if (!base.isEmpty() && Character.isUpperCase(base.charAt(0))) {
					typeName = base;
					isStaticContext = true;
				} else {
					String fieldTypeSimple = getReturnTypeOfMember(currentClassFQDN, base, workspace);
					if (fieldTypeSimple != null) {
						typeName = fieldTypeSimple;
					}
				}
			}

			if (typeName != null) {
				currentFQDN = resolveSimpleTypeName(typeName, imports, workspace, currentPkg);
			}
		}

		for (int i = 1; i < chain.size(); i++) {
			if (currentFQDN == null) return null;
			String member = chain.get(i);
			String returnTypeSimple = getReturnTypeOfMember(currentFQDN, member, workspace);
			if (returnTypeSimple != null) {
				currentFQDN = resolveSimpleTypeName(returnTypeSimple, imports, workspace, currentPkg);
				isStaticContext = false;
			} else {
				return null;
			}
		}

		return new ResolutionResult(currentFQDN, isStaticContext);
	}
}