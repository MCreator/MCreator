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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.mcreator.java.ClassFinder;
import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class JavaTypeResolver {

	private static final Logger LOG = LogManager.getLogger(JavaTypeResolver.class);

	public record CompletionItem(String label, String insertText, String kind, String detail, String declaringClass,
								 String visibility, String docSummary, boolean isSnippet, boolean isStatic,
								 boolean isFinal, boolean isAbstract, boolean isDeprecated, List<String> paramTypes,
								 List<String> paramNames, List<String> fqdnParamTypes) {}

	public record ResolutionResult(String fqdn, boolean isStaticContext) {}

	static void addMethodCompletion(String mName, String returnType, String[] paramTypes, String[] paramNames,
			String[] fqdnParamTypes, boolean isStatic, boolean isAbstract, boolean isDeprecated, String visibility,
			String declaringClass, String docSummary, List<CompletionItem> result, Set<String> added) {
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
			result.add(new CompletionItem(key, paramNames.length > 0 ? insert.toString() : mName + "()", "method",
					returnType, declaringClass, visibility, docSummary, paramNames.length > 0, isStatic, false,
					isAbstract, isDeprecated, Arrays.asList(paramTypes), Arrays.asList(paramNames),
					fqdnParamTypes != null ? Arrays.asList(fqdnParamTypes) : Arrays.asList(paramTypes)));
		}
	}

	static void addFieldCompletion(String fName, String fType, boolean isStatic, boolean isFinal, boolean isDeprecated,
			String visibility, String declaringClass, List<CompletionItem> result, Set<String> added) {
		if (added.add(fName)) {
			result.add(
					new CompletionItem(fName, fName, "field", fType, declaringClass, visibility, null, false, isStatic,
							isFinal, false, isDeprecated, null, null, null));
		}
	}

	@Nullable private final Workspace workspace;
	private final JavaSourceMemberResolver sourceMemberResolver;

	// Maps class FQDN -> cached list of field and method completion items
	@SuppressWarnings("NullableProblems")
	private final Cache<String, List<CompletionItem>> memberCache = CacheBuilder.newBuilder().maximumSize(500).build();

	// Maps "currentPkg:typeName" -> resolved FQDN for simple type name lookup
	@SuppressWarnings("NullableProblems")
	private final Cache<String, String> simpleTypeCache = CacheBuilder.newBuilder().maximumSize(500).build();

	public JavaTypeResolver(@Nullable Workspace workspace) {
		this.workspace = workspace;
		this.sourceMemberResolver = new JavaSourceMemberResolver(workspace);
	}

	public void invalidateCaches() {
		memberCache.invalidateAll();
		simpleTypeCache.invalidateAll();
		sourceMemberResolver.invalidateCaches();
	}

	public List<CompletionItem> getCompletionsFor(String targetName, String code, String codeBeforeCursor,
			JavaParser parser) {
		List<CompletionItem> result = new ArrayList<>();
		if (targetName == null || targetName.trim().isEmpty())
			return result;
		targetName = targetName.trim();

		String currentClassFQDN = ClassFinder.getCurrentFQDN(Objects.requireNonNull(parser));

		ResolutionResult res = resolveTargetFQDN(targetName, code, codeBeforeCursor, parser);
		if (res == null || res.fqdn == null)
			return result;

		List<CompletionItem> allMembers = getMembersOfFQDN(res.fqdn, currentClassFQDN, code);
		for (CompletionItem item : allMembers) {
			if (!res.isStaticContext || item.isStatic()) {
				result.add(item);
			}
		}
		return result;
	}

	public List<CompletionItem> getMembersOfFQDN(String fqdn, @Nullable String currentClassFQDN,
			@Nullable String currentCode) {
		if (fqdn == null || fqdn.isEmpty())
			return new ArrayList<>();
		boolean isCurrentClass = fqdn.equals(currentClassFQDN);

		if (!isCurrentClass) {
			List<CompletionItem> cached = memberCache.getIfPresent(fqdn);
			if (cached != null) {
				return new ArrayList<>(cached);
			}
		}

		List<CompletionItem> result = new ArrayList<>();
		Set<String> added = new HashSet<>();
		Set<String> visited = new HashSet<>();

		if (isCurrentClass && currentCode != null && !currentCode.isEmpty()) {
			String declaringClass = fqdn.contains(".") ? fqdn.substring(fqdn.lastIndexOf('.') + 1) : fqdn;
			sourceMemberResolver.parseSourceCodeCompletions(currentCode, declaringClass, result, added, true);
			try {
				JavaType<?> source = Roaster.parse(currentCode);
				if (source instanceof JavaClassSource javaClass) {
					String parentName = javaClass.getSuperType();
					if (parentName != null && !parentName.isEmpty() && !parentName.equals("java.lang.Object")) {
						Map<String, String> imports = sourceMemberResolver.parseImports(currentCode);
						String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
						String parentFQDN = resolveSimpleTypeName(parentName, imports, pkg);
						if (parentFQDN != null) {
							populateMembersOfFQDN(parentFQDN, result, added, visited);
						}
					}
				}
			} catch (Throwable e) {
				LOG.debug("Failed to parse super type from current code", e);
			}
		} else {
			populateMembersOfFQDN(fqdn, result, added, visited);
		}

		if (!isCurrentClass) {
			memberCache.put(fqdn, List.copyOf(result));
		}
		return result;
	}

	private void populateMembersOfFQDN(String fqdn, List<CompletionItem> result, Set<String> added,
			Set<String> visited) {
		if (fqdn == null || fqdn.isEmpty() || !visited.add(fqdn))
			return;

		String declaringClass = fqdn.contains(".") ? fqdn.substring(fqdn.lastIndexOf('.') + 1) : fqdn;

		ProjectJarManager jarManager = workspace != null ? workspace.getGenerator().getProjectJarManager() : null;

		if (jarManager != null) {
			try {
				ClassFile cf = jarManager.getClassEntry(fqdn);
				if (cf != null) {
					String srcCode = sourceMemberResolver.loadSourceCodeForFQDN(fqdn);
					Map<String, String> docs = sourceMemberResolver.getMethodDocsFromSource(srcCode);

					int mCount = cf.getMethodCount();
					for (int i = 0; i < mCount; i++) {
						MethodInfo mi = cf.getMethodInfo(i);
						int flags = mi.getAccessFlags();
						if ((flags & 0x0002) != 0)
							continue; // private

						String mName = mi.getName();
						if (mi.isConstructor() || mName.startsWith("<") || mName.equals("if") || mName.equals("for")
								|| mName.equals("while") || mName.equals("switch") || mName.equals("catch")
								|| mName.equals("class"))
							continue;

						boolean isPublic = (flags & 0x0001) != 0;
						boolean isProtected = (flags & 0x0004) != 0;
						String vis = isPublic ? "public" : (isProtected ? "protected" : "package");

						int pCount = mi.getParameterCount();
						String[] pTypes = new String[pCount];
						String[] pNames = new String[pCount];
						String[] fqdnPTypes = new String[pCount];
						for (int j = 0; j < pCount; j++) {
							pTypes[j] = mi.getParameterType(j, false);
							pNames[j] = mi.getParameterName(j);
							if (pNames[j] == null || pNames[j].isEmpty())
								pNames[j] = "arg" + j;
							fqdnPTypes[j] = mi.getParameterType(j, true);
						}

						String key = mName + "(" + String.join(",", fqdnPTypes) + ")";
						String doc = docs.get(key);
						if (doc == null)
							doc = docs.get(mName + "/" + pCount);
						if (doc == null)
							doc = docs.get(mName);

						addMethodCompletion(mName, mi.getReturnTypeString(false), pTypes, pNames, fqdnPTypes,
								mi.isStatic(), mi.isAbstract(), mi.isDeprecated(), vis, declaringClass, doc, result,
								added);
					}

					int fCount = cf.getFieldCount();
					for (int i = 0; i < fCount; i++) {
						FieldInfo fi = cf.getFieldInfo(i);
						int flags = fi.getAccessFlags();
						if ((flags & 0x0002) != 0)
							continue; // private

						String fName = fi.getName();
						if (fName.equals("class") || fName.equals("interface") || fName.equals("enum"))
							continue;

						boolean isPublic = (flags & 0x0001) != 0;
						boolean isProtected = (flags & 0x0004) != 0;
						String vis = isPublic ? "public" : (isProtected ? "protected" : "package");
						addFieldCompletion(fName, fi.getTypeString(false), fi.isStatic(), fi.isFinal(),
								fi.isDeprecated(), vis, declaringClass, result, added);
					}

					String superClassName = cf.getSuperClassName(true);
					if (superClassName != null && !superClassName.isEmpty() && !superClassName.equals(
							"java.lang.Object")) {
						populateMembersOfFQDN(superClassName, result, added, visited);
					}

					return;
				}
			} catch (Throwable e) {
				LOG.debug("Failed to read class file from ProjectJarManager for {}", fqdn, e);
			}
		}

		if (workspace != null) {
			String srcCode = sourceMemberResolver.loadSourceCodeForFQDN(fqdn);
			if (srcCode != null) {
				sourceMemberResolver.parseSourceCodeCompletions(srcCode, declaringClass, result, added, false);
				try {
					JavaType<?> source = Roaster.parse(srcCode);
					if (source instanceof JavaClassSource javaClass) {
						String parentName = javaClass.getSuperType();
						if (parentName != null && !parentName.isEmpty() && !parentName.equals("java.lang.Object")) {
							Map<String, String> imports = sourceMemberResolver.parseImports(srcCode);
							String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
							String parentFQDN = resolveSimpleTypeName(parentName, imports, pkg);
							if (parentFQDN != null) {
								populateMembersOfFQDN(parentFQDN, result, added, visited);
							}
						}
					}
				} catch (Throwable e) {
					LOG.debug("Failed to parse super type for {}", fqdn, e);
				}
			}
		}
	}

	private String resolveSimpleTypeName(String typeName, Map<String, String> imports, String currentPkg) {
		if (typeName == null)
			return null;
		if (typeName.contains("."))
			return typeName;

		String cacheKey = (currentPkg != null ? currentPkg : "") + ":" + typeName;
		String cached = simpleTypeCache.getIfPresent(cacheKey);
		if (cached != null)
			return cached.isEmpty() ? null : cached;

		String resolved = resolveSimpleTypeNameImpl(typeName, imports, currentPkg);
		simpleTypeCache.put(cacheKey, resolved != null ? resolved : "");
		return resolved;
	}

	private String resolveSimpleTypeNameImpl(String typeName, Map<String, String> imports, String currentPkg) {
		if (imports.containsKey(typeName)) {
			return imports.get(typeName);
		}

		if (currentPkg != null && !currentPkg.isEmpty()) {
			String possibleFQDN = currentPkg + "." + typeName;
			if (workspace != null) {
				File f = new File(workspace.getGenerator().getSourceRoot(), possibleFQDN.replace('.', '/') + ".java");
				if (f.exists())
					return possibleFQDN;
			}
		}

		// TODO PR #6542: Once the updated ProjectJarManager with WorkspaceLibraryInfo is merged,
		// workspace classes will be indexed directly in the PJM. The import tree from
		// GeneratorGradleCache will include them, and reloadClassesFromMod will no longer be needed.
		if (workspace != null && workspace.getGenerator().getGradleCache() != null) {
			Map<String, List<String>> tree = workspace.getGenerator().getGradleCache().getImportTree();
			if (tree != null) {
				// TODO PR #6542: Remove reloadClassesFromMod once WorkspaceLibraryInfo handles workspace classes
				ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), tree);
				List<String> fqdns = tree.get(typeName);
				if (fqdns != null && !fqdns.isEmpty()) {
					return fqdns.getFirst();
				}
			}
		}

		return null;
	}

	private List<String> splitChains(String expression) {
		List<String> result = new ArrayList<>();
		int depth = 0;
		StringBuilder current = new StringBuilder();
		for (char c : expression.toCharArray()) {
			if (c == '(')
				depth++;
			else if (c == ')')
				depth--;
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

	private String getReturnTypeOfMember(String fqdn, String member, @Nullable String currentClassFQDN,
			@Nullable String currentCode) {
		if (fqdn == null || fqdn.isEmpty())
			return null;
		String memberName = member.contains("(") ? member.substring(0, member.indexOf('(')) : member;

		List<CompletionItem> members = getMembersOfFQDN(fqdn, currentClassFQDN, currentCode);
		for (CompletionItem item : members) {
			if (item.kind().equals("method") && item.label().startsWith(memberName + "(")) {
				return item.detail();
			} else if (item.kind().equals("field") && item.label().equals(memberName)) {
				return item.detail();
			}
		}
		return null;
	}

	public ResolutionResult resolveTargetFQDN(String targetName, String code, String codeBeforeCursor,
			JavaParser parser) {
		if (code == null)
			code = "";
		if (codeBeforeCursor == null)
			codeBeforeCursor = code;

		Map<String, String> imports = sourceMemberResolver.parseImports(code);

		List<String> chain = splitChains(targetName);
		if (chain.isEmpty())
			return null;

		boolean isStaticContext = false;
		String currentFQDN = null;
		String typeName = null;
		String currentGenericArg = null;
		String base = chain.getFirst();

		String currentClassFQDN = ClassFinder.getCurrentFQDN(Objects.requireNonNull(parser));
		String currentPkg = currentClassFQDN != null && currentClassFQDN.contains(".") ?
				currentClassFQDN.substring(0, currentClassFQDN.lastIndexOf('.')) :
				"";

		if (base.equals("this") || base.equals("super")) {
			currentFQDN = currentClassFQDN;
			if (base.equals("super") && currentFQDN != null) {
				try {
					JavaType<?> source = Roaster.parse(code);
					if (source instanceof JavaClassSource javaClass) {
						String parentName = javaClass.getSuperType();
						if (parentName != null && !parentName.isEmpty()) {
							currentFQDN = resolveSimpleTypeName(parentName, imports, currentPkg);
						} else {
							currentFQDN = "java.lang.Object";
						}
					}
				} catch (Throwable e) {
					LOG.debug("Failed to resolve superclass for super keyword", e);
					currentFQDN = "java.lang.Object";
				}
			}
		} else {
			LocalVariableResolver.VarTypeInfo varInfo = LocalVariableResolver.findLocalVariableType(codeBeforeCursor,
					base);

			if (varInfo == null) {
				if (!base.isEmpty() && Character.isUpperCase(base.charAt(0))) {
					typeName = base;
					isStaticContext = true;
				} else {
					String fieldTypeSimple = getReturnTypeOfMember(currentClassFQDN, base, currentClassFQDN, code);
					if (fieldTypeSimple != null) {
						typeName = fieldTypeSimple;
					}
				}
			} else {
				typeName = varInfo.rawType();
				currentGenericArg = varInfo.genericArg();
			}

			if (typeName != null) {
				currentFQDN = resolveSimpleTypeName(typeName, imports, currentPkg);
			}
		}

		for (int i = 1; i < chain.size(); i++) {
			if (currentFQDN == null)
				return null;
			String member = chain.get(i);
			String returnTypeSimple = getReturnTypeOfMember(currentFQDN, member, currentClassFQDN, code);

			if ((returnTypeSimple == null || returnTypeSimple.equals("Object") || returnTypeSimple.equals("E")
					|| returnTypeSimple.equals("T") || returnTypeSimple.equals("V") || returnTypeSimple.equals("K"))
					&& currentGenericArg != null) {
				returnTypeSimple = currentGenericArg;
			}

			if (returnTypeSimple != null) {
				currentFQDN = resolveSimpleTypeName(returnTypeSimple, imports, currentPkg);
				isStaticContext = false;
			} else {
				return null;
			}
		}

		return new ResolutionResult(currentFQDN, isStaticContext);
	}
}