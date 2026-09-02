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
import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Named;
import org.jboss.forge.roaster.model.source.*;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class JavaTypeResolver {

	private static final Logger LOG = LogManager.getLogger(JavaTypeResolver.class);

	public record CompletionItem(String label, String insertText, String kind, String detail, String declaringClass,
								 String visibility, String docSummary, boolean isSnippet, boolean isStatic,
								 boolean isFinal, boolean isAbstract, boolean isDeprecated, List<String> paramTypes,
								 List<String> paramNames, List<String> fqdnParamTypes) {}

	private record ResolutionResult(String fqdn, boolean isStaticContext) {}

	public static void addMethodCompletion(String mName, String returnType, String[] paramTypes, String[] paramNames,
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
		String methodSig = mName + "(" + String.join(",", paramTypes) + ")";
		if (added.add(methodSig)) {
			result.add(new CompletionItem(label.toString(), paramNames.length > 0 ? insert.toString() : mName + "()",
					"method", returnType, declaringClass, visibility, docSummary, paramNames.length > 0, isStatic,
					false, isAbstract, isDeprecated, Arrays.asList(paramTypes), Arrays.asList(paramNames),
					fqdnParamTypes != null ? Arrays.asList(fqdnParamTypes) : Arrays.asList(paramTypes)));
		}
	}

	public static void addFieldCompletion(String fName, String fType, boolean isStatic, boolean isFinal,
			boolean isDeprecated, String visibility, String declaringClass, List<CompletionItem> result,
			Set<String> added) {
		if (added.add(fName)) {
			result.add(
					new CompletionItem(fName, fName, "field", fType, declaringClass, visibility, null, false, isStatic,
							isFinal, false, isDeprecated, null, null, null));
		}
	}

	@Nullable private final Workspace workspace;
	private final JavaSourceResolver sourceResolver;
	private final JavaMemberResolver memberResolver;

	// Maps "currentPkg:typeName" -> resolved FQDN for simple type name lookup
	@SuppressWarnings("NullableProblems")
	private final Cache<String, String> simpleTypeCache = CacheBuilder.newBuilder().maximumSize(500).build();

	// Maps class FQDN -> list of inner class simple names
	@SuppressWarnings("NullableProblems")
	private final Cache<String, List<String>> innerClassesCache = CacheBuilder.newBuilder().maximumSize(200).build();

	private Map<String, List<String>> cachedModClasses = null;
	private long lastModClassesUpdate = 0;

	public JavaTypeResolver(@Nullable Workspace workspace) {
		this.workspace = workspace;
		this.sourceResolver = new JavaSourceResolver(workspace);
		this.memberResolver = new JavaMemberResolver(workspace, sourceResolver, this);
	}

	public synchronized void invalidateCaches() {
		memberResolver.invalidateCaches();
		simpleTypeCache.invalidateAll();
		innerClassesCache.invalidateAll();
		sourceResolver.invalidateCaches();
		cachedModClasses = null;
		lastModClassesUpdate = 0;
	}

	public JavaSourceResolver getSourceResolver() {
		return sourceResolver;
	}

	public synchronized Map<String, List<String>> getModClasses() {
		if (workspace == null)
			return Collections.emptyMap();
		long now = System.currentTimeMillis();
		if (cachedModClasses == null || (now - lastModClassesUpdate > 5000)) {
			Map<String, List<String>> modClasses = new HashMap<>();
			ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), modClasses);
			cachedModClasses = modClasses;
			lastModClassesUpdate = now;
		}
		return cachedModClasses;
	}

	public List<String> getInnerClasses(String fqdn) {
		if (fqdn == null || fqdn.isEmpty())
			return Collections.emptyList();
		List<String> cached = innerClassesCache.getIfPresent(fqdn);
		if (cached != null)
			return cached;

		List<String> inners = new ArrayList<>();
		String src = sourceResolver.loadSourceCodeForFQDN(fqdn);
		if (src != null && !src.isEmpty()) {
			try {
				JavaType<?> source = Roaster.parse(src);
				if (fqdn.contains(".")) {
					String declaringClass = fqdn.substring(fqdn.lastIndexOf('.') + 1);
					source = JavaSourceResolver.findType(source, declaringClass);
				}
				List<?> nestedList = Collections.emptyList();
				if (source instanceof JavaClassSource javaClass) {
					nestedList = javaClass.getNestedTypes();
				} else if (source instanceof JavaInterfaceSource javaInterface) {
					nestedList = javaInterface.getNestedTypes();
				} else if (source instanceof JavaEnumSource javaEnum) {
					nestedList = javaEnum.getNestedTypes();
				}
				for (Object nested : nestedList) {
					if (nested instanceof JavaSource<?> js) {
						if (!js.isPrivate()) {
							inners.add(js.getName());
						}
					}
				}
			} catch (Throwable e) {
				LOG.debug("Failed to parse inner classes for {}", fqdn, e);
			}
		}
		inners = Collections.unmodifiableList(inners);
		innerClassesCache.put(fqdn, inners);
		return inners;
	}

	public List<CompletionItem> getCompletionsFor(String targetName, String code, String codeBeforeCursor,
			@Nullable String currentClassFQDN) {
		List<CompletionItem> result = new ArrayList<>();
		if (targetName == null || targetName.trim().isEmpty())
			return result;
		targetName = targetName.trim();

		ResolutionResult res = resolveTargetFQDN(targetName, code, codeBeforeCursor, currentClassFQDN);
		if (res == null || res.fqdn == null)
			return result;

		List<CompletionItem> allMembers = memberResolver.getMembersOfFQDN(res.fqdn, currentClassFQDN, code);
		for (CompletionItem item : allMembers) {
			if (!res.isStaticContext || item.isStatic()) {
				result.add(item);
			}
		}

		if (res.isStaticContext) {
			String declaringClass = res.fqdn.contains(".") ? res.fqdn.substring(res.fqdn.lastIndexOf('.') + 1) : res.fqdn;
			for (String inner : getInnerClasses(res.fqdn)) {
				result.add(new CompletionItem(inner, inner, "field", res.fqdn + "." + inner, declaringClass,
						"public", null, false, true, true, false, false, null, null, null));
			}
		}

		return result;
	}

	public String resolveSimpleTypeName(String typeName, Map<String, String> imports, String currentPkg) {
		if (typeName == null)
			return null;

		if (imports != null && imports.containsKey(typeName)) {
			return imports.get(typeName);
		}

		String cacheKey = (currentPkg != null ? currentPkg : "") + ":" + typeName;
		String cached = simpleTypeCache.getIfPresent(cacheKey);
		if (cached != null)
			return cached.isEmpty() ? null : cached;

		String resolved = resolveSimpleTypeNameImpl(typeName, imports, currentPkg);
		simpleTypeCache.put(cacheKey, resolved != null ? resolved : "");
		return resolved;
	}

	private String resolveSimpleTypeNameImpl(String typeName, Map<String, String> imports, String currentPkg) {
		if (imports != null && imports.containsKey(typeName)) {
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

		if (workspace != null && workspace.getGenerator().getGradleCache() != null) {
			Map<String, List<String>> tree = workspace.getGenerator().getGradleCache().getImportTree();
			if (tree != null) {
				List<String> fqdns = tree.get(typeName);
				if (fqdns != null && !fqdns.isEmpty()) {
					return fqdns.getFirst();
				}
			}
		}

		if (workspace != null) {
			Map<String, List<String>> modClasses = getModClasses();
			if (modClasses != null) {
				List<String> fqdns = modClasses.get(typeName);
				if (fqdns != null && !fqdns.isEmpty()) {
					return fqdns.getFirst();
				}
			}
		}

		if (typeName.contains(".")) {
			int dot = typeName.indexOf('.');
			String outer = typeName.substring(0, dot);
			String resolvedOuter = resolveSimpleTypeName(outer, imports, currentPkg);
			if (resolvedOuter != null) {
				return resolvedOuter + typeName.substring(dot);
			}
			return typeName;
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

		List<CompletionItem> members = memberResolver.getMembersOfFQDN(fqdn, currentClassFQDN, currentCode);
		for (CompletionItem item : members) {
			if (item.kind().equals("method") && item.label().startsWith(memberName + "(")) {
				return item.detail();
			} else if (item.kind().equals("field") && item.label().equals(memberName)) {
				return item.detail();
			}
		}
		return null;
	}


	private List<String> getTypeParameters(String fqdn, @Nullable String currentClassFQDN,
			@Nullable String currentCode) {
		if (fqdn == null || fqdn.isEmpty())
			return Collections.emptyList();
		ProjectJarManager jarManager = workspace != null ? workspace.getGenerator().getProjectJarManager() : null;
		ClassFile cf = jarManager != null ? memberResolver.getClassFile(jarManager, fqdn) : null;
		if (cf != null && cf.getParamTypes() != null)
			return cf.getParamTypes();
		String src = (currentClassFQDN != null && fqdn.equals(currentClassFQDN) && currentCode != null) ?
				currentCode :
				sourceResolver.loadSourceCodeForFQDN(fqdn);
		if (src != null) {
			try {
				JavaType<?> type = Roaster.parse(src);
				if (fqdn.contains(".")) {
					String declaringClass = fqdn.substring(fqdn.lastIndexOf('.') + 1);
					type = JavaSourceResolver.findType(type, declaringClass);
				}
				if (type instanceof JavaClassSource javaClass) {
					return javaClass.getTypeVariables().stream().map(Named::getName).toList();
				} else if (type instanceof JavaInterfaceSource javaInterface) {
					return javaInterface.getTypeVariables().stream().map(Named::getName).toList();
				}
			} catch (Throwable e) {
				LOG.debug("Failed to parse type parameters for {}", fqdn, e);
			}
		}
		return Collections.emptyList();
	}

	private ResolutionResult resolveTargetFQDN(String targetName, String code, String codeBeforeCursor,
			@Nullable String currentClassFQDN) {
		if (code == null)
			code = "";
		if (codeBeforeCursor == null)
			codeBeforeCursor = code;

		Map<String, String> imports = sourceResolver.parseImports(code);

		List<String> chain = splitChains(targetName);
		if (chain.isEmpty())
			return null;

		boolean isStaticContext = false;
		String currentFQDN = null;
		String typeName = null;
		List<String> currentGenericArgs = Collections.emptyList();
		String base = chain.getFirst();

		String currentPkg = currentClassFQDN != null && currentClassFQDN.contains(".") ?
				currentClassFQDN.substring(0, currentClassFQDN.lastIndexOf('.')) :
				"";

		if (targetName.contains(".")) {
			ProjectJarManager jarManager = workspace != null ? workspace.getGenerator().getProjectJarManager() : null;
			if (jarManager != null) {
				String[] segments = targetName.split("\\.");
				String fqdnCandidate = "";
				int classSegmentIndex = -1;
				for (int s = 0; s < segments.length; s++) {
					fqdnCandidate = fqdnCandidate.isEmpty() ? segments[s] : fqdnCandidate + "." + segments[s];
					if (jarManager.getClassEntry(fqdnCandidate) != null) {
						classSegmentIndex = s;
						currentFQDN = fqdnCandidate;
						isStaticContext = true;
						break;
					}
				}
				if (classSegmentIndex >= 0) {
					for (int i = classSegmentIndex + 1; i < segments.length; i++) {
						String member = segments[i];
						String returnTypeSimple = getReturnTypeOfMember(currentFQDN, member, currentClassFQDN, code);
						if (returnTypeSimple != null) {
							currentFQDN = resolveSimpleTypeName(returnTypeSimple, imports, currentPkg);
							isStaticContext = false;
						} else if (getInnerClasses(currentFQDN).contains(member)) {
							currentFQDN += "." + member;
							isStaticContext = true;
						} else {
							return null;
						}
					}
					return new ResolutionResult(currentFQDN, isStaticContext);
				}
			}
		}

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
				currentGenericArgs = varInfo.genericArgs();
			}

			if (typeName != null) {
				if (typeName.contains("<") && typeName.endsWith(">")) {
					String gen = typeName.substring(typeName.indexOf('<') + 1, typeName.length() - 1);
					typeName = typeName.substring(0, typeName.indexOf('<')).trim();
					if (!gen.isEmpty() && currentGenericArgs.isEmpty()) {
						currentGenericArgs = Arrays.stream(gen.split(","))
								.map(String::trim)
								.filter(s -> !s.isEmpty())
								.toList();
					}
				}
				currentFQDN = resolveSimpleTypeName(typeName, imports, currentPkg);
			}
		}

		for (int i = 1; i < chain.size(); i++) {
			if (currentFQDN == null)
				return null;
			String member = chain.get(i);
			String returnTypeSimple = getReturnTypeOfMember(currentFQDN, member, currentClassFQDN, code);

			List<String> typeParams = getTypeParameters(currentFQDN, currentClassFQDN, code);
			int paramIndex = typeParams.indexOf(returnTypeSimple);
			if (paramIndex >= 0 && paramIndex < currentGenericArgs.size()) {
				returnTypeSimple = currentGenericArgs.get(paramIndex);
			} else if (returnTypeSimple != null && returnTypeSimple.length() == 1 && currentGenericArgs.size() == 1) {
				returnTypeSimple = currentGenericArgs.getFirst();
			}

			if (returnTypeSimple != null) {
				String rawType = returnTypeSimple;
				currentGenericArgs = Collections.emptyList();
				if (rawType.contains("<") && rawType.endsWith(">")) {
					String gen = rawType.substring(rawType.indexOf('<') + 1, rawType.length() - 1);
					rawType = rawType.substring(0, rawType.indexOf('<')).trim();
					if (!gen.isEmpty()) {
						currentGenericArgs = Arrays.stream(gen.split(","))
								.map(String::trim)
								.filter(s -> !s.isEmpty())
								.toList();
					}
				}
				currentFQDN = resolveSimpleTypeName(rawType, imports, currentPkg);
				isStaticContext = false;
			} else if (getInnerClasses(currentFQDN).contains(member)) {
				currentFQDN += "." + member;
				isStaticContext = true;
				currentGenericArgs = Collections.emptyList();
			} else {
				return null;
			}
		}

		return new ResolutionResult(currentFQDN, isStaticContext);
	}
}