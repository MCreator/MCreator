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
import net.mcreator.java.JavaConventions;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.classreader.AccessFlags;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.classreader.Util;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import javax.annotation.Nullable;
import java.util.*;

public class JavaMemberResolver {

	private static final Logger LOG = LogManager.getLogger(JavaMemberResolver.class);

	@Nullable private final Workspace workspace;
	private final JavaSourceResolver sourceResolver;
	private final JavaTypeResolver typeResolver;

	// Maps class FQDN -> cached list of field and method completion items
	@SuppressWarnings("NullableProblems")
	private final Cache<String, List<JavaTypeResolver.CompletionItem>> memberCache = CacheBuilder.newBuilder().maximumSize(500).build();

	public JavaMemberResolver(@Nullable Workspace workspace, JavaSourceResolver sourceResolver,
			JavaTypeResolver typeResolver) {
		this.workspace = workspace;
		this.sourceResolver = sourceResolver;
		this.typeResolver = typeResolver;
	}

	public void invalidateCaches() {
		memberCache.invalidateAll();
	}

	public List<JavaTypeResolver.CompletionItem> getMembersOfFQDN(String fqdn, @Nullable String currentClassFQDN,
			@Nullable String currentCode) {
		if (fqdn == null || fqdn.isEmpty())
			return new ArrayList<>();
		boolean isCurrentClass = fqdn.equals(currentClassFQDN);

		if (!isCurrentClass) {
			List<JavaTypeResolver.CompletionItem> cached = memberCache.getIfPresent(fqdn);
			if (cached != null) {
				return new ArrayList<>(cached);
			}
		}

		List<JavaTypeResolver.CompletionItem> result = new ArrayList<>();
		Set<String> added = new HashSet<>();
		Set<String> visited = new HashSet<>();

		if (isCurrentClass && currentCode != null && !currentCode.isEmpty()) {
			String declaringClass = fqdn.contains(".") ? fqdn.substring(fqdn.lastIndexOf('.') + 1) : fqdn;
			sourceResolver.parseSourceCodeCompletions(currentCode, declaringClass, result, added, true, false);
			populateSuperAndInterfaces(currentCode, fqdn, result, added, visited, false);
		} else {
			populateMembersOfFQDN(fqdn, currentClassFQDN, currentCode, result, added, visited, false);
		}

		if (!isCurrentClass) {
			memberCache.put(fqdn, List.copyOf(result));
		}
		return result;
	}

	private void populateSuperAndInterfaces(String srcCode, String fqdn, List<JavaTypeResolver.CompletionItem> result,
			Set<String> added, Set<String> visited, boolean defaultOnly) {
		try {
			JavaType<?> source = Roaster.parse(srcCode);
			String declaringClass = fqdn.contains(".") ? fqdn.substring(fqdn.lastIndexOf('.') + 1) : fqdn;
			source = JavaSourceResolver.findType(source, declaringClass);
			Map<String, String> imports = sourceResolver.parseImports(srcCode);
			String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
			if (source instanceof JavaClassSource javaClass) {
				String parentName = javaClass.getSuperType();
				if (parentName != null && !parentName.isEmpty() && !parentName.equals("java.lang.Object")) {
					String parentFQDN = typeResolver.resolveSimpleTypeName(parentName, imports, pkg);
					if (parentFQDN != null) {
						populateMembersOfFQDN(parentFQDN, null, null, result, added, visited, false);
					}
				}
				for (String ifName : javaClass.getInterfaces()) {
					String ifFQDN = typeResolver.resolveSimpleTypeName(ifName, imports, pkg);
					if (ifFQDN != null) {
						populateMembersOfFQDN(ifFQDN, null, null, result, added, visited, true);
					}
				}
			} else if (source instanceof JavaInterfaceSource javaInterface) {
				for (String ifName : javaInterface.getInterfaces()) {
					String ifFQDN = typeResolver.resolveSimpleTypeName(ifName, imports, pkg);
					if (ifFQDN != null) {
						populateMembersOfFQDN(ifFQDN, null, null, result, added, visited, defaultOnly);
					}
				}
			}
		} catch (Throwable e) {
			LOG.debug("Failed to parse super type / interfaces for {}", fqdn, e);
		}
	}

	private void populateMembersOfFQDN(String fqdn, @Nullable String currentClassFQDN, @Nullable String currentCode,
			List<JavaTypeResolver.CompletionItem> result, Set<String> added, Set<String> visited, boolean defaultOnly) {
		if (fqdn == null || fqdn.isEmpty() || !visited.add(fqdn + (defaultOnly ? "#default" : "")))
			return;

		String declaringClass = fqdn.contains(".") ? fqdn.substring(fqdn.lastIndexOf('.') + 1) : fqdn;
		ProjectJarManager jarManager = workspace != null ? workspace.getGenerator().getProjectJarManager() : null;

		if (jarManager != null) {
			try {
				ClassFile cf = getClassFile(jarManager, fqdn);
				if (cf != null) {
					addMembersFromClassFile(cf, declaringClass, result, added, defaultOnly);
					recurseHierarchy(cf, result, added, visited, defaultOnly);
					return;
				}
			} catch (Throwable e) {
				LOG.debug("Failed to read class file from ProjectJarManager for {}", fqdn, e);
			}
		}

		// Fallback to source code parsing when no ClassFile is available
		String srcCode = (currentClassFQDN != null && fqdn.equals(currentClassFQDN) && currentCode != null) ?
				currentCode :
				(workspace != null ? sourceResolver.loadSourceCodeForFQDN(fqdn) : null);
		if (srcCode != null) {
			sourceResolver.parseSourceCodeCompletions(srcCode, declaringClass, result, added, false, defaultOnly);
			populateSuperAndInterfaces(srcCode, fqdn, result, added, visited, defaultOnly);
		}
	}

	private String toSimpleType(String fqdnType) {
		return fqdnType == null ? null : fqdnType.replaceAll("([a-zA-Z_$][a-zA-Z0-9_$]*\\.)+", "");
	}

	private void addMembersFromClassFile(ClassFile cf, String declaringClass,
			List<JavaTypeResolver.CompletionItem> result, Set<String> added, boolean defaultOnly) {
		if (cf.getParamTypes() != null) {
			Map<String, String> identity = new HashMap<>();
			for (String p : cf.getParamTypes())
				identity.put(p, p);
			cf.setTypeParamsToTypeArgs(identity);
		}

		String fqdn = cf.getClassName(true);
		String srcCode = sourceResolver.loadSourceCodeForFQDN(fqdn);
		Map<String, String> docs = sourceResolver.getMethodDocsFromSource(srcCode);

		for (int i = 0; i < cf.getMethodCount(); i++) {
			MethodInfo mi = cf.getMethodInfo(i);
			int flags = mi.getAccessFlags();

			if (Util.isPrivate(flags) || (flags & 0x0040) != 0 || (flags & AccessFlags.ACC_SYNTHETIC) != 0)
				continue;
			if (defaultOnly && (mi.isAbstract() || mi.isStatic()))
				continue;

			String mName = mi.getName();
			if (mi.isConstructor() || mName.startsWith("<") || JavaConventions.JAVA_RESERVED_WORDS.contains(mName))
				continue;

			String vis = Util.isPublic(flags) ? "public" : (Util.isProtected(flags) ? "protected" : "package");

			int pCount = mi.getParameterCount();
			String[] pTypes = new String[pCount];
			String[] pNames = new String[pCount];
			String[] fqdnPTypes = new String[pCount];
			for (int j = 0; j < pCount; j++) {
				pNames[j] = mi.getParameterName(j);
				if (pNames[j] == null || pNames[j].isEmpty())
					pNames[j] = "arg" + j;
				fqdnPTypes[j] = mi.getParameterType(j, true);
				pTypes[j] = toSimpleType(fqdnPTypes[j]);
			}

			String doc = lookupDoc(docs, mName, pTypes, pCount);
			JavaTypeResolver.addMethodCompletion(mName, toSimpleType(mi.getReturnTypeString(true)), pTypes, pNames,
					fqdnPTypes, mi.isStatic(), mi.isAbstract(), mi.isDeprecated(), vis, declaringClass, doc, result,
					added);
		}

		if (!defaultOnly) {
			for (int i = 0; i < cf.getFieldCount(); i++) {
				FieldInfo fi = cf.getFieldInfo(i);
				int flags = fi.getAccessFlags();
				if (Util.isPrivate(flags))
					continue;

				String fName = fi.getName();
				if (JavaConventions.JAVA_RESERVED_WORDS.contains(fName))
					continue;

				String vis = Util.isPublic(flags) ? "public" : (Util.isProtected(flags) ? "protected" : "package");
				JavaTypeResolver.addFieldCompletion(fName, toSimpleType(fi.getTypeString(true)), fi.isStatic(),
						fi.isFinal(), fi.isDeprecated(), vis, declaringClass, result, added);
			}
		}
	}

	private void recurseHierarchy(ClassFile cf, List<JavaTypeResolver.CompletionItem> result, Set<String> added,
			Set<String> visited, boolean defaultOnly) {
		boolean isInterface = (cf.getAccessFlags() & AccessFlags.ACC_INTERFACE) != 0;

		if (isInterface) {
			for (int j = 0; j < cf.getImplementedInterfaceCount(); j++) {
				String superIf = cf.getImplementedInterfaceName(j, true);
				if (superIf != null && !superIf.isEmpty()) {
					populateMembersOfFQDN(superIf, null, null, result, added, visited, defaultOnly);
				}
			}
		} else {
			String superClassName = cf.getSuperClassName(true);
			if (superClassName != null && !superClassName.isEmpty() && !superClassName.equals("java.lang.Object")) {
				populateMembersOfFQDN(superClassName, null, null, result, added, visited, false);
			}
			for (int j = 0; j < cf.getImplementedInterfaceCount(); j++) {
				String ifName = cf.getImplementedInterfaceName(j, true);
				if (ifName != null && !ifName.isEmpty()) {
					populateMembersOfFQDN(ifName, null, null, result, added, visited, true);
				}
			}
		}
	}

	@Nullable public ClassFile getClassFile(ProjectJarManager jarManager, String fqdn) {
		ClassFile cf = jarManager.getClassEntry(fqdn);
		if (cf != null)
			return cf;
		String temp = fqdn;
		while (temp.contains(".")) {
			int lastDot = temp.lastIndexOf('.');
			temp = temp.substring(0, lastDot) + "$" + temp.substring(lastDot + 1);
			cf = jarManager.getClassEntry(temp);
			if (cf != null)
				return cf;
		}
		return null;
	}

	private String lookupDoc(Map<String, String> docs, String mName, String[] pTypes, int pCount) {
		String doc = docs.get(mName + "(" + String.join(",", pTypes) + ")");
		if (doc == null)
			doc = docs.get(mName + "/" + pCount);
		if (doc == null)
			doc = docs.get(mName);
		return doc;
	}

}