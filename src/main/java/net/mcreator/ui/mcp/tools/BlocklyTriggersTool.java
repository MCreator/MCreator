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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BlocklyTriggersTool extends MCreatorMcpTool<BlocklyTriggersTool.Args> {

	public static class Args {
		@Nullable public String blocklyEditorType;
	}

	public BlocklyTriggersTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "blockly_triggers";
	}

	@Override public String getDescription() {
		return "Lists trigger info (ID, dependencies, ...) for blocklyEditorType (procedures or scripts).";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
		if (editorType == null) {
			return completedError("Invalid or missing blocklyEditorType (use procedures or scripts)");
		}

		List<ExternalTrigger> externalTriggersAll = BlocklyLoader.INSTANCE.getExternalTriggerLoader(editorType)
				.getExternalTriggers();
		Set<String> supportedTriggers = mcreator.getGeneratorStats().getBlocklyTriggers(editorType);

		return CompletableFuture.completedFuture(ToolResult.collection(
				externalTriggersAll.stream().filter(trigger -> supportedTriggers.contains(trigger.getID())).toList()));
	}

	@Nullable private static BlocklyEditorType parseEditorType(@Nullable String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		BlocklyEditorType editorType = BlocklyEditorType.fromName(raw.trim());
		if (editorType != BlocklyEditorType.PROCEDURE && editorType != BlocklyEditorType.SCRIPT) {
			return null;
		}
		return editorType;
	}

}
