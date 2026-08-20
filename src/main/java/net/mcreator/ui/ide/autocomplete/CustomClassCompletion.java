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

package net.mcreator.ui.ide.autocomplete;

import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.util.Iterator;

public class CustomClassCompletion extends BasicCompletion {
	private final String className;
	private final String pkg;
	private final boolean isInterface;
	private final boolean isEnum;

	private record ImportToAddInfo(int offset, String text) {}

	private record ImportResult(ImportToAddInfo importToAdd, boolean mustFullyQualify) {
		static final ImportResult NONE = new ImportResult(null, false);
		static final ImportResult QUALIFY = new ImportResult(null, true);
	}

	public CustomClassCompletion(CompletionProvider provider, String name, String pkg, boolean isInterface,
			boolean isEnum) {
		super(provider, name, pkg, buildShortDescription(name, pkg, isInterface, isEnum));
		this.className = name;
		this.pkg = pkg;
		this.isInterface = isInterface;
		this.isEnum = isEnum;
		setRelevance(2);
	}

	public String getClassName(boolean fullyQualified) {
		return fullyQualified && pkg != null && !pkg.isEmpty() ? pkg + "." + className : className;
	}

	private static String buildShortDescription(String name, String pkg, boolean isInterface, boolean isEnum) {
		String type;
		if (isInterface) {
			type = "interface ";
		} else if (isEnum) {
			type = "enum ";
		} else {
			type = "class ";
		}

		String packageInfo = (pkg != null && !pkg.isEmpty()) ? "<br>package " + pkg : "";
		return "<html>" + type + name + packageInfo + "</html>";
	}

	public void insert(RSyntaxTextArea te, JavaParser parser, String alreadyEntered) {
		int dot = te.getCaretPosition();
		int start = dot - (alreadyEntered != null ? alreadyEntered.length() : 0);

		String lineText = getCurrentLineText(te);
		if (lineText.startsWith("import ")) {
			String textBeforeCaret = lineText.substring(0,
					Math.max(0, lineText.length() - (alreadyEntered != null ? alreadyEntered.length() : 0))).trim();
			String toInsert = textBeforeCaret.endsWith(".") ? className : getClassName(true);
			te.replaceRange(toInsert, start, dot);
			return;
		}

		ImportResult result = getShouldAddImport(te, parser);
		String textToInsert = result.mustFullyQualify ? getClassName(true) : className;

		te.beginAtomicEdit();
		try {
			te.replaceRange(textToInsert, start, dot);
			if (result.importToAdd != null) {
				te.insert(result.importToAdd.text(), result.importToAdd.offset());
			}
		} finally {
			te.endAtomicEdit();
		}
	}

	private ImportResult getShouldAddImport(RSyntaxTextArea te, JavaParser parser) {
		CompilationUnit cu = parser.getCompilationUnit();

		if (pkg == null || pkg.isEmpty() || "java.lang".equals(pkg)) {
			return ImportResult.NONE;
		}

		String fqClassName = getClassName(true);
		int lastClassNameDot = fqClassName.lastIndexOf('.');
		boolean ccInPackage = lastClassNameDot > -1;
		Package pkgDecl = cu.getPackage();

		if (ccInPackage && pkgDecl != null) {
			String ccPkg = fqClassName.substring(0, lastClassNameDot);
			if (ccPkg.equals(pkgDecl.getName())) {
				return ImportResult.NONE;
			}
		} else if (!ccInPackage && pkgDecl == null) {
			return ImportResult.NONE;
		}

		int offset = 0;
		boolean alreadyImported = false;

		Iterator<ImportDeclaration> i = cu.getImportIterator();
		while (i.hasNext()) {
			ImportDeclaration id = i.next();
			offset = id.getNameEndOffset() + 1;

			if (!id.isStatic()) {
				if (id.isWildcard()) {
					if (lastClassNameDot > -1) {
						String imported = id.getName();
						int dot = imported.lastIndexOf('.');
						String importedPkg = dot > -1 ? imported.substring(0, dot) : imported;
						String classPkg = fqClassName.substring(0, lastClassNameDot);
						if (importedPkg.equals(classPkg)) {
							alreadyImported = true;
							break;
						}
					}
				} else {
					String fullyImportedClassName = id.getName();
					int dot = fullyImportedClassName.lastIndexOf('.');
					String importedClassName =
							dot > -1 ? fullyImportedClassName.substring(dot + 1) : fullyImportedClassName;
					if (className.equals(importedClassName)) {
						offset = -1;
						if (fqClassName.equals(fullyImportedClassName)) {
							alreadyImported = true;
						}
						break;
					}
				}
			}
		}

		if (!alreadyImported) {
			if (offset > -1) {
				StringBuilder importToAdd = new StringBuilder();
				if (offset == 0 && pkgDecl != null) {
					offset = pkgDecl.getNameEndOffset() + 1;
					importToAdd.append('\n');
				}

				if (offset > 0) {
					importToAdd.append("\nimport ").append(fqClassName).append(';');
				} else {
					importToAdd.append("import ").append(fqClassName).append(";\n");
				}

				return new ImportResult(new ImportToAddInfo(offset, importToAdd.toString()), false);
			} else {
				return ImportResult.QUALIFY;
			}
		}

		return ImportResult.NONE;
	}

	private String getCurrentLineText(RSyntaxTextArea te) {
		int caretPosition = te.getCaretPosition();
		Element root = te.getDocument().getDefaultRootElement();
		int line = root.getElementIndex(caretPosition);
		Element elem = root.getElement(line);
		int endOffset = elem.getEndOffset();
		int lineStart = elem.getStartOffset();

		try {
			return te.getText(lineStart, endOffset - lineStart).trim();
		} catch (BadLocationException e) {
			return "";
		}
	}

	@Override public Icon getIcon() {
		if (isInterface) {
			return IconFactory.get().getIcon(IconFactory.INTERFACE_ICON);
		} else if (isEnum) {
			return IconFactory.get().getIcon(IconFactory.ENUM_ICON);
		}
		return IconFactory.get().getIcon(IconFactory.CLASS_ICON);
	}

	@Override public String toString() {
		if (pkg != null && !pkg.isEmpty()) {
			return className + " - " + pkg;
		}
		return className;
	}
}