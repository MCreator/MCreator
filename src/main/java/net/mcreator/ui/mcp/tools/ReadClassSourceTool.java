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

package net.mcreator.ui.mcp.tools;

import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReadClassSourceTool extends MCreatorMcpTool<ReadClassSourceTool.Args> {

	public static class Args {
		public String className;
	}

	public ReadClassSourceTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "read_class_source";
	}

	@Override public String getDescription() {
		return "Reads Java source code for a class by its FQDN (org.package.ClassName) from the workspace classpath."
				+ "One use case is to read source of Minecraft classes to confirm behavior.";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.className == null || input.className.isBlank()) {
			return CompletableFuture.completedFuture(ToolResult.error("className is required"));
		}

		String code = null;
		ProjectJarManager jarManager = mcreator.getGenerator().getProjectJarManager();
		if (jarManager != null)
			code = jarManager.getSourceCodeForClass(input.className.trim());

		if (code == null) {
			return CompletableFuture.completedFuture(ToolResult.error(
					"Could not read source for class: " + input.className
							+ ". If this is a workspace class, make sure the workspace is built."));
		}

		return CompletableFuture.completedFuture(ToolResult.text(code));
	}

}
