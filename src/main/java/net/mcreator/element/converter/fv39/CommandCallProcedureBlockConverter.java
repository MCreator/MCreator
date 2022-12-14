/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.element.converter.fv39;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Command;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandCallProcedureBlockConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger("CommandCallProcedureBlockConverter");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Command command = (Command) input;
		try {
			command.argsxml = fixXML(command.argsxml);
		} catch (Exception e) {
			LOG.warn("Failed to convert procedure " + input.getModElement().getName());
		}
		return command;
	}

	@Override public int getVersionConvertingTo() {
		return 39;
	}

	protected String fixXML(String xml) {
		return xml.replace("type=\"call_procedure\"", "type=\"call_procedure_no_args\"");
	}
}