/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.blockly;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class PartLinker implements TemplateDirectiveModel {

	private String newHead;
	private String newTail;

	@Override public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		if (params.get("type") instanceof TemplateScalarModel templateScalarModel) {
			if ("head".equals(templateScalarModel.getAsString())) {
				var writer = new StringWriter();
				body.render(writer);
				newHead = writer.toString();
			}
			if ("tail".equals(templateScalarModel.getAsString())) {
				var writer = new StringWriter();
				body.toString();
				newTail = writer.toString();
			}
			//may be more
		}
	}

	public String getNewHead() {
		return newHead;
	}

	public String getNewTail() {
		return newTail;
	}
}
