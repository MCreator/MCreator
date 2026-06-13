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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReadConsoleTool extends MCreatorMcpTool<ReadConsoleTool.Args> {

	public static class Args {
		public int lastNCharsToRead = 3000;
	}

	public ReadConsoleTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, ReadConsoleTool.Args.class);
	}

	@Override public String getName() {
		return "read_console";
	}

	@Override public String getDescription() {
		return "Reads current contents of the Gradle console for given workspace";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ReadConsoleTool.Args input) {
		String consoleContent = mcreator.getGradleConsole().getConsoleText();
		int startIndex = Math.max(0, consoleContent.length() - input.lastNCharsToRead);
		consoleContent = consoleContent.substring(startIndex);
		return CompletableFuture.completedFuture(ToolResult.text(consoleContent));
	}

}

