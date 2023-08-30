/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2021_3;

import net.mcreator.element.converter.ProcedureConverter;
import net.mcreator.element.types.Procedure;

public class LegacyProcedureBlockRemover extends ProcedureConverter {

	@Override public int getVersionConvertingTo() {
		return 25;
	}

	@Override protected String fixXML(Procedure procedure, String xml) {
		return xml.replace("type=\"compare_mcitems_exact\"", "type=\"compare_mcitems\"")
				.replace("type=\"compare_mcitems_oredictionary_exact\"", "type=\"compare_mcitems_oredictionary\"")
				.replace("type=\"compare_mcblocks_exact\"", "type=\"compare_mcblocks\"");
	}
}
