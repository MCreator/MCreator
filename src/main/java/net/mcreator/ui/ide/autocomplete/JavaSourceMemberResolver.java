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
import net.mcreator.io.FileIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.*;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaSourceMemberResolver {

	private static final Logger LOG = LogManager.getLogger(JavaSourceMemberResolver.class);

	@Nullable private final Workspace workspace;

	// Maps source code hashCode -> map of simple class names to FQDNs parsed from imports
	@SuppressWarnings("NullableProblems")
	private final Cache<Integer, Map<String, String>> importsCache = CacheBuilder.newBuilder().maximumSize(50).build();

	// Maps source code hashCode -> map of method signatures to Javadoc documentation
	@SuppressWarnings("NullableProblems")
	private final Cache<Integer, Map<String, String>> docsCache = CacheBuilder.newBuilder().maximumSize(100).build();

	// Maps class FQDN -> loaded Java source code string
	@SuppressWarnings("NullableProblems") private final Cache<String, String> sourceCache = CacheBuilder.newBuilder()
			.maximumSize(100).build();

	public JavaSourceMemberResolver(@Nullable Workspace workspace) {
		this.workspace = workspace;
	}

	public void invalidateCaches() {
		importsCache.invalidateAll();
		docsCache.invalidateAll();
		sourceCache.invalidateAll();
	}

	public String loadSourceCodeForFQDN(String fqdn) {
		if (workspace == null || workspace.getGenerator() == null || fqdn == null)
			return null;
		String cached = sourceCache.getIfPresent(fqdn);
		if (cached != null)
			return cached.isEmpty() ? null : cached;

		String srcCode = loadSourceCodeForFQDNImpl(fqdn);
		sourceCache.put(fqdn, srcCode != null ? srcCode : "");
		return srcCode;
	}

	private String loadSourceCodeForFQDNImpl(String fqdn) {
		if (workspace == null || workspace.getGenerator() == null)
			return null;
		File srcFile = new File(workspace.getGenerator().getSourceRoot(), fqdn.replace('.', '/') + ".java");
		if (srcFile.isFile()) {
			return FileIO.readFileToString(srcFile);
		}
		// TODO PR #6542: Replace manual source loading below with jarManager.getSourceCodeForClass(fqdn)
		// once the updated ProjectJarManager is merged. The new PJM method handles both zip and directory
		// source locations, and also covers workspace sources via WorkspaceLibraryInfo.
		ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
		SourceLocation sourceLocation = jarManager.getSourceLocForClass(fqdn);
		if (sourceLocation instanceof ZipSourceLocation) {
			try (ZipFile zipFile = ZipIO.openZipFile(new File(sourceLocation.getLocationAsString()))) {
				String relativePath = fqdn.replace('.', '/') + ".java";
				ZipEntry entry = zipFile.getEntry(relativePath);
				if (entry == null) {
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry ze = entries.nextElement();
						String name = ze.getName();
						if (name.endsWith("/" + relativePath) || name.equals(relativePath)) {
							entry = ze;
							break;
						}
					}
				}
				if (entry != null) {
					return ZipIO.entryToString(zipFile, entry);
				}
			} catch (Exception e) {
				LOG.debug("could not read source from jar", e);
			}
		}
		return null;
	}

	public Map<String, String> getMethodDocsFromSource(String srcCode) {
		if (srcCode == null || srcCode.isEmpty())
			return Collections.emptyMap();
		int hash = srcCode.hashCode();
		Map<String, String> cached = docsCache.getIfPresent(hash);
		if (cached != null)
			return cached;

		Map<String, String> docs = new HashMap<>();
		try {
			JavaType<?> source = Roaster.parse(srcCode);
			if (source instanceof MethodHolderSource<?> mhs) {
				Map<String, String> imports = parseImports(srcCode);
				for (MethodSource<?> m : mhs.getMethods()) {
					if (m.getJavaDoc() != null) {
						String text = m.getJavaDoc().getFullText();
						if (text != null && !text.trim().isEmpty()) {
							List<? extends ParameterSource<?>> params = m.getParameters();
							String[] pTypes = params.stream().map(p -> {
								String name = p.getType().getName();
								String resolved = imports.get(name);
								if (resolved != null)
									return resolved;
								return name.length() == 1 ? "java.lang.Object" : name;
							}).toArray(String[]::new);
							docs.put(m.getName() + "(" + String.join(",", pTypes) + ")", text.trim());
							docs.putIfAbsent(m.getName() + "/" + params.size(), text.trim());
							docs.putIfAbsent(m.getName(), text.trim());
						}
					}
				}
			}
		} catch (Throwable e) {
			LOG.debug("Failed to parse method docs from source code", e);
		}
		Map<String, String> unmodifiable = Collections.unmodifiableMap(docs);
		docsCache.put(hash, unmodifiable);
		return unmodifiable;
	}

	public Map<String, String> parseImports(String code) {
		if (code == null || code.isEmpty())
			return Collections.emptyMap();
		int hash = code.hashCode();
		Map<String, String> cached = importsCache.getIfPresent(hash);
		if (cached != null)
			return cached;

		Map<String, String> imports = new HashMap<>();
		try {
			JavaType<?> source = Roaster.parse(code);
			if (source instanceof Importer<?> importer) {
				for (Import imp : importer.getImports()) {
					String fqdn = imp.getQualifiedName();
					String simple = imp.getSimpleName();
					imports.put(simple, fqdn);
				}
			}
		} catch (Throwable e) {
			LOG.debug("Failed to parse imports from source code", e);
		}
		Map<String, String> unmodifiable = Collections.unmodifiableMap(imports);
		importsCache.put(hash, unmodifiable);
		return unmodifiable;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parseSourceCodeCompletions(String srcCode, String declaringClass,
			List<JavaTypeResolver.CompletionItem> result, Set<String> added, boolean includePrivate) {
		if (srcCode == null || srcCode.isEmpty())
			return;

		try {
			JavaType<?> source = Roaster.parse(srcCode);

			List<FieldSource<?>> fields = source instanceof FieldHolderSource<?> fhs ?
					(List) fhs.getFields() :
					Collections.emptyList();
			List<MethodSource<?>> methods = source instanceof MethodHolderSource<?> mhs ?
					(List) mhs.getMethods() :
					Collections.emptyList();

			for (FieldSource<?> f : fields) {
				if (!includePrivate && f.isPrivate())
					continue;
				String fName = f.getName();
				if (fName.equals("class") || fName.equals("interface") || fName.equals("enum"))
					continue;

				String fType = f.getType().getSimpleName();
				String vis = f.isPublic() ?
						"public" :
						(f.isProtected() ? "protected" : (f.isPrivate() ? "private" : "package"));
				JavaTypeResolver.addFieldCompletion(fName, fType, f.isStatic(), f.isFinal(),
						f.hasAnnotation(Deprecated.class), vis, declaringClass, result, added);
			}

			Map<String, String> imports = parseImports(srcCode);
			for (MethodSource<?> m : methods) {
				if ((!includePrivate && m.isPrivate()) || m.isConstructor() || m.getName().startsWith("<"))
					continue;
				String mName = m.getName();
				if (mName.equals("if") || mName.equals("for") || mName.equals("while") || mName.equals("switch")
						|| mName.equals("catch") || mName.equals("class"))
					continue;

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

				String vis = m.isPublic() ?
						"public" :
						(m.isProtected() ? "protected" : (m.isPrivate() ? "private" : "package"));
				String docSummary = m.getJavaDoc() != null ? m.getJavaDoc().getFullText() : null;
				JavaTypeResolver.addMethodCompletion(mName, returnType, pTypes, pNames, fqdnPTypes, m.isStatic(),
						m.isAbstract(), m.hasAnnotation(Deprecated.class), vis, declaringClass, docSummary, result,
						added);
			}
		} catch (Throwable e) {
			LOG.debug("Roaster failed to parse source code completions", e);
		}
	}
}