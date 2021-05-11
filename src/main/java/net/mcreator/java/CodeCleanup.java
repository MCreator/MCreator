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

package net.mcreator.java;

import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.Roaster;

import javax.annotation.Nullable;
import java.util.Properties;

public class CodeCleanup {

	private static final Logger LOG = LogManager.getLogger(CodeCleanup.class);

	private final Properties formatPorperties;
	private final ImportFormat importFormat;

	public CodeCleanup() {
		formatPorperties = new Properties();
		formatPorperties.setProperty("org.eclipse.jdt.core.compiler.source", "1.8");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.lineSplit", "150");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_before_imports", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_after_imports", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_after_package", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_between_type_declarations", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_before_method", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_after_method", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.blank_lines_before_member_type", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.number_of_empty_lines_to_preserve", "1");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.tabulation.char", "tab");

		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.comment.format_block_comments", "false");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.comment.format_javadoc_comments", "false");
		formatPorperties.setProperty("org.eclipse.jdt.core.formatter.comment.format_line_comments", "false");

		importFormat = new ImportFormat();
	}

	public String reformatTheCodeAndOrganiseImports(@Nullable Workspace workspace, String code) {
		return this.reformatTheCodeAndOrganiseImports(workspace, code, false);
	}

	public String reformatTheCodeAndOrganiseImports(@Nullable Workspace workspace, String code,
			boolean skipModClassReloading) {
		try {
			return Roaster
					.format(formatPorperties, importFormat.arrangeImports(workspace, code, skipModClassReloading));
		} catch (Exception e) {
			LOG.error("Failed to format code and organize imports", e);
			return code;
		}
	}

	public String reformatTheCodeOnly(String code) {
		try {
			return Roaster.format(formatPorperties, code);
		} catch (Exception e) {
			LOG.error("Failed to format code", e);
			return code;
		}
	}

}
