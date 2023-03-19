/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.blockly;

public record BlocklyEditorType(String registryName, String extension, String startBlockName) {

	public static final BlocklyEditorType PROCEDURE = new BlocklyEditorType("procedures", "ptpl", "event_trigger");
	public static final BlocklyEditorType AI_TASK = new BlocklyEditorType("aitasks", "aitpl", "aitasks_container");
	public static final BlocklyEditorType COMMAND_ARG = new BlocklyEditorType("cmdargs", "cmdtpl", "args_start");
	public static final BlocklyEditorType FEATURE = new BlocklyEditorType("features", "ftpl", "feature_container");
	public static final BlocklyEditorType JSON_TRIGGER = new BlocklyEditorType("jsontriggers", null,
			"advancement_trigger");

}
