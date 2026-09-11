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
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.util.regex.Pattern;

public class CustomClassCompletion extends BasicCompletion {
	public static final Pattern IMPORT_OR_PACKAGE_LINE = Pattern.compile("^(import(?!\\s+static\\b)|package)\\s+.*");

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

	public void insert(RSyntaxTextArea te, String alreadyEntered) {
		int dot = te.getCaretPosition();
		int start = dot - (alreadyEntered != null ? alreadyEntered.length() : 0);

		String lineText = getCurrentLineText(te);
		boolean isPrecededByDot = false;
		if (start > 0) {
			try {
				isPrecededByDot = ".".equals(te.getText(start - 1, 1));
			} catch (BadLocationException ignored) {
				return;
			}
		}

		if (isPrecededByDot || IMPORT_OR_PACKAGE_LINE.matcher(lineText).matches()) {
			String toInsert = isPrecededByDot ? className : getClassName(true);
			te.replaceRange(toInsert, start, dot);
			return;
		}

		ImportResult result = getShouldAddImport(te);
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

	private ImportResult getShouldAddImport(RSyntaxTextArea te) {
		if (pkg == null || pkg.isEmpty() || "java.lang".equals(pkg)) {
			return ImportResult.NONE;
		}

		String outerClassName = className.contains(".") ? className.substring(0, className.indexOf('.')) : className;
		String fqOuterClassName = pkg + "." + outerClassName;

		int offset = 0;
		int pkgEndOffset = -1;
		boolean alreadyImported = false;

		Element root = te.getDocument().getDefaultRootElement();
		int lineCount = root.getElementCount();

		for (int i = 0; i < lineCount; i++) {
			Element elem = root.getElement(i);
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();
			String line;
			try {
				line = te.getText(start, end - start).trim();
			} catch (BadLocationException e) {
				continue;
			}

			if (IMPORT_OR_PACKAGE_LINE.matcher(line).matches()) {
				int semi = line.indexOf(';');
				if (semi == -1)
					continue;

				if (line.startsWith("package")) {
					String pkgName = line.substring(line.indexOf('e') + 1, semi).trim();
					if (pkg.equals(pkgName)) {
						return ImportResult.NONE;
					}
					pkgEndOffset = end - 1;
				} else if (line.startsWith("import")) {
					offset = end - 1;
					String imported = line.substring(line.indexOf('t') + 1, semi).trim();

					if (imported.endsWith(".*")) {
						String importedPkg = imported.substring(0, imported.length() - 2).trim();
						if (pkg.equals(importedPkg)) {
							alreadyImported = true;
							break;
						}
					} else {
						int dot = imported.lastIndexOf('.');
						String importedClassName = dot > -1 ? imported.substring(dot + 1) : imported;
						if (outerClassName.equals(importedClassName)) {
							offset = -1;
							if (fqOuterClassName.equals(imported)) {
								alreadyImported = true;
							}
							break;
						}
					}
				}
			} else if (line.startsWith("public ") || line.startsWith("class ") || line.startsWith("interface ")
					|| line.startsWith("enum ") || line.startsWith("@")) {
				break;
			}
		}

		if (!alreadyImported) {
			if (offset > -1) {
				StringBuilder importToAdd = new StringBuilder();
				if (offset == 0 && pkgEndOffset > -1) {
					offset = pkgEndOffset;
					importToAdd.append('\n');
				}

				if (offset > 0) {
					importToAdd.append("\nimport ").append(fqOuterClassName).append(';');
				} else {
					importToAdd.append("import ").append(fqOuterClassName).append(";\n");
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