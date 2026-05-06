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

package net.mcreator.element.parts.procedure;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused") @JsonAdapter(Procedure.GSONAdapter.class) public class Procedure {

	private static final Logger LOG = LogManager.getLogger(Procedure.class);

	private final String name;

	public transient boolean exists = false;

	public Procedure(String name) {
		this.name = name;
	}

	@Nullable public String getName() {
		return name;
	}

	public List<Dependency> getDependencies(Workspace workspace) {
		ModElement modElement = workspace.getModElementByName(name);
		if (modElement != null) {
			// when deserializing, at this point, workspace may not be applied to the ME yet, so we do it now just in case
			modElement.setWorkspace(workspace);
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof net.mcreator.element.types.Procedure) {
				this.exists = true;
				return ((net.mcreator.element.types.Procedure) generatableElement).getDependencies();
			}
		} else {
			LOG.warn("Procedure {} not found while trying to extract dependencies!", name);
			TestUtil.failIfTestingEnvironment();
		}

		this.exists = false;
		return Collections.emptyList();
	}

	public String getReturnValueType(Workspace workspace) {
		ModElement modElement = workspace.getModElementByName(name);
		if (modElement != null) { // procedure ME may be removed and thus cause NPE here
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof net.mcreator.element.types.Procedure) {
				try {
					return ((net.mcreator.element.types.Procedure) generatableElement).getBlocklyToProcedure(
							new HashMap<>()).getReturnType().getName();
				} catch (Exception ignored) {
				}
			}
		}

		return "none";
	}

	public static class GSONAdapter implements JsonSerializer<Procedure>, JsonDeserializer<Procedure> {

		@Override public Procedure deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if (json.isJsonNull()) {
				return new Procedure(null);
			} else if (json.isJsonPrimitive()) {
				return new Procedure(json.getAsString());
			} else if (json.isJsonObject()) {
				return new Procedure(json.getAsJsonObject().get("name").getAsString());
			} else {
				throw new JsonParseException("Invalid JSON for Procedure: " + json);
			}
		}

		@Override
		public JsonElement serialize(Procedure procedure, Type typeOfSrc, JsonSerializationContext context) {
			return (procedure.name == null || procedure.name.isEmpty()) ? JsonNull.INSTANCE : new JsonPrimitive(procedure.name);
		}

	}

}
