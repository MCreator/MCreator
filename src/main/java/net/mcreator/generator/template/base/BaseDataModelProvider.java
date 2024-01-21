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

package net.mcreator.generator.template.base;

import net.mcreator.blockly.java.ProcedureCodeOptimizer;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.template.TemplateHelper;
import net.mcreator.java.JavaConventions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseDataModelProvider {

	private static final Logger TEMPLATE_LOG = LogManager.getLogger("Template Generator LOG");

	private final Map<String, Object> providedData = new HashMap<>();

	private final Generator generator;

	@SuppressWarnings("InstantiationOfUtilityClass") public BaseDataModelProvider(Generator generator) {
		this.generator = generator;

		// Static helpers
		providedData.put("Log", TEMPLATE_LOG);
		providedData.put("thelper", new TemplateHelper());
		providedData.put("opt", new ProcedureCodeOptimizer());
		providedData.put("JavaConventions", new JavaConventions());

		// Data that does not change for the current generator (BaseDataModelProvider is generator specific)
		providedData.put("generator", new GeneratorWrapper(generator));
		providedData.put("w", generator.getWorkspace().getWorkspaceInfo());
		providedData.put("fp", new FileProvider(generator));
		providedData.put("mcc", generator.getMinecraftCodeProvider());
	}

	public Map<String, Object> provide() {
		providedData.put("settings", generator.getWorkspaceSettings()); // workspaceSettings is not final!
		providedData.put("modid", generator.getWorkspaceSettings().getModID());
		providedData.put("JavaModName", generator.getWorkspaceSettings().getJavaModName());
		providedData.put("package", generator.getWorkspaceSettings().getModElementsPackage());
		return Collections.unmodifiableMap(providedData);
	}

}
