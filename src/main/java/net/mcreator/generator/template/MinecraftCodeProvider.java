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

/*
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with JBoss Forge (or a modified version of that library), containing
 * parts covered by the terms of Eclipse Public License, the licensors of
 * this Program grant you additional permission to convey the resulting work.
 */

package net.mcreator.generator.template;

import net.mcreator.io.zip.ZipIO;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") public class MinecraftCodeProvider {

	private static final Logger LOG = LogManager.getLogger(MinecraftCodeProvider.class);

	private final Workspace workspace;

	private final Map<String, String> CACHE = new HashMap<>();

	public MinecraftCodeProvider(@NotNull Workspace workspace) {
		this.workspace = workspace;
	}

	private String readCode(@NotNull String template) {
		try {
			if (!CACHE.containsKey(template)) { // cache miss, add to cache
				ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
				if (jarManager != null) {
					SourceLocation sourceLocation = jarManager.getSourceLocForClass(template);
					CACHE.put(template, ZipIO.readCodeInZip(new File(sourceLocation.getLocationAsString()),
							template.replace(".", "/") + ".java"));
				}
			}

			return CACHE.get(template);
		} catch (Exception e) {
			LOG.error("Failed to load code provider for " + template, e);
			return null;
		}
	}

	public String getCodeFor(@NotNull String template, int lineFrom, int lineTo) {
		String code = readCode(template);
		if (code != null) {
			String[] lines = code.split("\\r?\\n");
			String[] usedLines = Arrays.copyOfRange(lines, lineFrom - 1, lineTo);
			return String.join(System.lineSeparator(), usedLines);
		} else {
			return "/* failed to load code for " + template + " */";
		}
	}

	public String getMethod(@NotNull String template, String method, String... params) {
		String code = readCode(template);
		if (code != null) {
			JavaClassSource classJavaSource = (JavaClassSource) Roaster.parse(code);
			return classJavaSource.getMethod(method, params).toString();
		} else {
			return "/* failed to load code for " + template + " */";
		}
	}

	public String getInnerClassBody(@NotNull String template, String innerClass) {
		String code = readCode(template);
		if (code != null) {
			CompilationUnit cu = new ASTFactory().getCompilationUnit(template, new Scanner(new StringReader(code)));

			TypeDeclaration inner = null;

			TypeDeclaration mainClass = cu.getTypeDeclaration(0);
			for (int i = 0; i < mainClass.getChildTypeCount(); i++) {
				if (mainClass.getChildType(i).getName().equals(innerClass)) {
					inner = mainClass.getChildType(i);
					break;
				}
			}

			if (inner != null)
				return code.substring(inner.getBodyStartOffset(), inner.getBodyEndOffset() + 1);
		}

		return "/* failed to load code for " + template + " */";
	}

}
