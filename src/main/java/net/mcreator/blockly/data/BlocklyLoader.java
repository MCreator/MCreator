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

package net.mcreator.blockly.data;

public class BlocklyLoader {

	public static BlocklyLoader INSTANCE;

	public static void init() {
		INSTANCE = new BlocklyLoader();
	}

	private final ExternalBlockLoader procedureBlockLoader;
	private final ExternalBlockLoader jsonTriggerLoader;
	private final ExternalBlockLoader aitaskBlockLoader;
	private final ExternalBlockLoader cmdargsBlockLoader;
	private final ExternalTriggerLoader externalTriggerLoader;

	private BlocklyLoader() {
		procedureBlockLoader = new ExternalBlockLoader("procedures");
		aitaskBlockLoader = new ExternalBlockLoader("aitasks");
		cmdargsBlockLoader = new ExternalBlockLoader("cmdargs");
		externalTriggerLoader = new ExternalTriggerLoader("triggers");
		jsonTriggerLoader = new ExternalBlockLoader("jsontriggers");
	}

	public ExternalBlockLoader getProcedureBlockLoader() {
		return procedureBlockLoader;
	}

	public ExternalBlockLoader getAITaskBlockLoader() {
		return aitaskBlockLoader;
	}

	public ExternalBlockLoader getCmdArgsBlockLoader() { return cmdargsBlockLoader;}

	public ExternalTriggerLoader getExternalTriggerLoader() {
		return externalTriggerLoader;
	}

	public ExternalBlockLoader getJSONTriggerLoader() {
		return jsonTriggerLoader;
	}

}
