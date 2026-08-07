/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.ide.autocomplete;

import net.mcreator.io.FileIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.java.ClassFinder;
import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.*;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaTypeResolver {

	private static final Logger LOG = LogManager.getLogger(JavaTypeResolver.class);
	private static final Map<String, Map<String, String>> IMPORTS_CACHE = new ConcurrentHashMap<>();

	public static class CompletionItem {
		public String label, insertText, kind, detail, declaringClass, visibility;
		public boolean isSnippet, isStatic, isFinal, isAbstract, isDeprecated;
		public List<String> paramTypes, paramNames, fqdnParamTypes;

		public CompletionItem(String label, String insertText, String kind, String detail, String declaringClass, String visibility, boolean isSnippet, boolean isStatic, boolean isFinal, boolean isAbstract, boolean isDeprecated, List<String> paramTypes, List<String> paramNames, List<String> fqdnParamTypes) {
			this.label = label;
			this.insertText = insertText;
			this.kind = kind;
			this.detail = detail;
			this.declaringClass = declaringClass;
			this.visibility = visibility;
			this.isSnippet = isSnippet;
			this.isStatic = isStatic;
			this.isFinal = isFinal;
			this.isAbstract = isAbstract;
			this.isDeprecated = isDeprecated;
			this.paramTypes = paramTypes;
			this.paramNames = paramNames;
			this.fqdnParamTypes = fqdnParamTypes;
		}
	}

	public record ResolutionResult(String fqdn, boolean isStaticContext) {}

	private static void addMethodCompletion(String mName, String returnType, String[] paramTypes, String[] paramNames, String[] fqdnParamTypes, boolean isStatic, boolean isAbstract, boolean isDeprecated, String visibility, String declaringClass, List<CompletionItem> result, Set<String> added) {
		StringBuilder label = new StringBuilder(mName).append("(");
		StringBuilder insert = new StringBuilder(mName).append("(");
		for (int i = 0; i < paramNames.length; i++) {
			if (i > 0) {
				label.append(", ");
				insert.append(", ");
			}
			label.append(paramTypes[i]).append(" ").append(paramNames[i]);
			insert.append("${").append(i + 1).append(":").append(paramNames[i]).append("}");
		}
		label.append(")");
		insert.append(")");
		String key = label.toString();
		if (added.add(key)) {
			result.add(new CompletionItem(key, paramNames.length > 0 ? insert.toString() : mName + "()", "method", returnType, declaringClass, visibility, paramNames.length > 0, isStatic, false, isAbstract, isDeprecated, Arrays.asList(paramTypes), Arrays.asList(paramNames), fqdnParamTypes != null ? Arrays.asList(fqdnParamTypes) : Arrays.asList(paramTypes)));
		}
	}

	private static void addFieldCompletion(String fName, String fType, boolean isStatic, boolean isFinal, boolean isDeprecated, String visibility, String declaringClass, List<CompletionItem> result, Set<String> added) {
		if (added.add(fName)) {
			result.add(new CompletionItem(fName, fName, "field", fType, declaringClass, visibility, false, isStatic, isFinal, false, isDeprecated, null, null, null));
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
			MEMBER_CACHE.put(fqdn, List.copyOf(result));
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

			Set<String> processedFields = new HashSet<>();
			List<Field> allFields = new ArrayList<>();
			Collections.addAll(allFields, clazz.getFields());
			Collections.addAll(allFields, clazz.getDeclaredFields());
			for (Field f : allFields) {
				if (!processedFields.add(f.getName())) continue;
				int mods = f.getModifiers();
				if (Modifier.isPrivate(mods)) continue;

				String vis = Modifier.isPublic(mods) ? "public" : (Modifier.isProtected(mods) ? "protected" : "package");
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
							}
						} catch (Exception e) {
							LOG.debug("could not read source from jar", e);
						}
					}
				}
			}

			if (srcCode != null) {
				parseSourceCodeCompletions(srcCode, declaringClass, result, added);
				try {
					JavaType<?> source = Roaster.parse(srcCode);
					if (source instanceof JavaClassSource javaClass) {
						String parentName = javaClass.getSuperType();
						if (parentName != null && !parentName.isEmpty() && !parentName.equals("java.lang.Object")) {
							Map<String, String> imports = parseImports(srcCode);
							String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
							String parentFQDN = resolveSimpleTypeName(parentName, imports, workspace, pkg);
							if (parentFQDN != null) {
								populateMembersOfFQDN(parentFQDN, workspace, result, added, visited);
							}
						}
					}
				} catch (Throwable ignored) {
				}
			}
		}
	}

	private static void parseSourceCodeCompletions(String srcCode, String declaringClass, List<CompletionItem> result, Set<String> added) {
		if (srcCode == null || srcCode.isEmpty()) return;

		try {
			JavaType<?> source = Roaster.parse(srcCode);

			List<FieldSource<?>> fields = source instanceof FieldHolderSource<?> fhs ? (List) fhs.getFields() : Collections.emptyList();
			List<MethodSource<?>> methods = source instanceof MethodHolderSource<?> mhs ? (List) mhs.getMethods() : Collections.emptyList();

			for (FieldSource<?> f : fields) {
				if (f.isPrivate()) continue;
				String fName = f.getName();
				if (fName.equals("class") || fName.equals("interface") || fName.equals("enum")) continue;

				String fType = f.getType().getSimpleName();
				String vis = f.isPublic() ? "public" : (f.isProtected() ? "protected" : "package");
				addFieldCompletion(fName, fType, f.isStatic(), f.isFinal(), f.hasAnnotation(Deprecated.class), vis, declaringClass, result, added);
			}

			Map<String, String> imports = parseImports(srcCode);
			for (MethodSource<?> m : methods) {
				if (m.isPrivate() || m.isConstructor()) continue;
				String mName = m.getName();
				if (mName.equals("if") || mName.equals("for") || mName.equals("while") || mName.equals("switch") || mName.equals("catch") || mName.equals("class")) continue;

				String returnType = m.getReturnType().getSimpleName();
				List<? extends ParameterSource<?>> params = m.getParameters();
				String[] pTypes = new String[params.size()];
				String[] pNames = new String[params.size()];
				String[] fqdnPTypes = new String[params.size()];

				for (int p = 0; p < params.size(); p++) {
					ParameterSource<?> param = params.get(p);
					pTypes[p] = param.getType().getSimpleName();
					pNames[p] = param.getName();
					String rawType = param.getType().getName();
					String resolvedFQDN = imports.get(rawType);
					fqdnPTypes[p] = resolvedFQDN != null ? resolvedFQDN : rawType;
				}

				String vis = m.isPublic() ? "public" : (m.isProtected() ? "protected" : "package");
				addMethodCompletion(mName, returnType, pTypes, pNames, fqdnPTypes, m.isStatic(), m.isAbstract(), m.hasAnnotation(Deprecated.class), vis, declaringClass, result, added);
			}
		} catch (Throwable e) {
			LOG.debug("Roaster failed to parse source code completions", e);
		}
	}

	private static Map<String, String> parseImports(String code) {
		if (code == null || code.isEmpty()) return Collections.emptyMap();
		return IMPORTS_CACHE.computeIfAbsent(code, c -> {
			Map<String, String> imports = new HashMap<>();
			try {
				JavaType<?> source = Roaster.parse(c);
				if (source instanceof Importer<?> importer) {
					for (Import imp : importer.getImports()) {
						String fqdn = imp.getQualifiedName();
						String simple = imp.getSimpleName();
						imports.put(simple, fqdn);
					}
				}
			} catch (Throwable ignored) {
			}
			return imports;
		});
	}

	private static String resolveSimpleTypeName(String typeName, Map<String, String> imports, Workspace workspace, String currentPkg) {
		if (typeName == null) return null;
		if (typeName.contains(".")) return typeName;

		if (imports.containsKey(typeName)) {
			return imports.get(typeName);
		}

		if (currentPkg != null && !currentPkg.isEmpty()) {
			String possibleFQDN = currentPkg + "." + typeName;
			if (workspace != null && workspace.getGenerator() != null) {
				File f = new File(workspace.getGenerator().getSourceRoot(), possibleFQDN.replace('.', '/') + ".java");
				if (f.exists()) return possibleFQDN;
			}
		}

		if (workspace != null && workspace.getGenerator() != null) {
			Map<String, List<String>> tree = workspace.getGenerator().getGradleCache() != null ?
					workspace.getGenerator().getGradleCache().getImportTree() :
					(workspace.getGenerator().getProjectJarManager() != null ?
							ImportTreeBuilder.generateImportTree(workspace.getGenerator().getProjectJarManager()) : null);

			if (tree != null) {
				ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), tree);
				List<String> fqdns = tree.get(typeName);
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
		String memberName = member.contains("(") ? member.substring(0, member.indexOf('(')) : member;

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
				try {
					JavaType<?> source = Roaster.parse(code);
					if (source instanceof JavaClassSource javaClass) {
						String parentName = javaClass.getSuperType();
						if (parentName != null && !parentName.isEmpty()) {
							currentFQDN = resolveSimpleTypeName(parentName, imports, workspace, currentPkg);
						} else {
							currentFQDN = "java.lang.Object";
						}
					}
				} catch (Throwable ignored) {
					currentFQDN = "java.lang.Object";
				}
			}
		} else if (base.contains("(")) {
			String returnTypeSimple = getReturnTypeOfMember(currentClassFQDN, base, workspace);
			if (returnTypeSimple != null) {
				currentFQDN = resolveSimpleTypeName(returnTypeSimple, imports, workspace, currentPkg);
			}
		} else {
			String typeName = null;
			try {
				Scanner scanner = new Scanner(new StringReader(codeBeforeCursor));
				Token t;
				Token prevToken = null;
				while ((t = scanner.yylex()) != null) {
					if (t.isIdentifier()) {
						String lex = t.getLexeme();
						if (lex.equals(base) && prevToken != null && prevToken.isIdentifier() && !prevToken.getLexeme().equals(base) && Character.isUpperCase(prevToken.getLexeme().charAt(0))) {
							typeName = prevToken.getLexeme();
						}
					}
					prevToken = t;
				}
			} catch (Throwable ignored) {
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