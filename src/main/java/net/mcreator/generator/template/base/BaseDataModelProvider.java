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

	private final Map<String, Object> providedData;

	@SuppressWarnings("InstantiationOfUtilityClass") public BaseDataModelProvider(Generator generator) {
		Map<String, Object> providedDataBuilder = new HashMap<>();
		providedDataBuilder.put("generator", new GeneratorWrapper(generator));
		providedDataBuilder.put("w", generator.getWorkspace().getWorkspaceInfo());
		providedDataBuilder.put("modid", generator.getWorkspaceSettings().getModID());
		providedDataBuilder.put("JavaModName", generator.getWorkspaceSettings().getJavaModName());
		providedDataBuilder.put("package", generator.getWorkspaceSettings().getModElementsPackage());
		providedDataBuilder.put("settings", generator.getWorkspaceSettings());
		providedDataBuilder.put("fp", new FileProvider(generator));
		providedDataBuilder.put("mcc", generator.getMinecraftCodeProvider());
		providedDataBuilder.put("JavaConventions", new JavaConventions());
		providedDataBuilder.put("thelper", new TemplateHelper());
		providedDataBuilder.put("Log", TEMPLATE_LOG);
		providedDataBuilder.put("opt", new ProcedureCodeOptimizer());
		this.providedData = Collections.unmodifiableMap(providedDataBuilder);
	}

	public Map<String, Object> provide() {
		return providedData;
	}

}
