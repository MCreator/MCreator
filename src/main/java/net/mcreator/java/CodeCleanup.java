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

import com.google.googlejavaformat.java.Formatter;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class CodeCleanup {

	private static final Logger LOG = LogManager.getLogger(CodeCleanup.class);

	private final ImportFormat importFormat;
	private final Formatter formatter;

	public CodeCleanup() {
		importFormat = new ImportFormat();
		formatter = new Formatter();
	}

	public String reformatTheCodeAndOrganiseImports(@Nullable Workspace workspace, String code) {
		return this.reformatTheCodeAndOrganiseImports(workspace, code, false);
	}

	public String reformatTheCodeAndOrganiseImports(@Nullable Workspace workspace, String code,
			boolean skipModClassReloading) {
		try {
			return tabify(formatter.formatSource(importFormat.arrangeImports(workspace, code, skipModClassReloading)));
		} catch (Exception e) {
			LOG.error("Failed to format code and organize imports", e);
			return code;
		}
	}

	public String reformatTheCodeOnly(String code) {
		try {
			return tabify(formatter.formatSource(code));
		} catch (Exception e) {
			LOG.error("Failed to format code", e);
			return code;
		}
	}

	private static String tabify(String code) {
		var betterCode = new StringBuilder();
		boolean start = true, second = false;
		char c;
		for (int i = 0, n = code.length(); i < n; i++) {
			c = code.charAt(i);
			if (start && c == ' ') {
				if (second)
					betterCode.append("\t");
				second = !second;
			} else {
				if (second) {
					betterCode.append(' ');
					second = false;
				}
				betterCode.append(c);
				start = c == '\n';
			}
		}
		return betterCode.toString();
	}

}
