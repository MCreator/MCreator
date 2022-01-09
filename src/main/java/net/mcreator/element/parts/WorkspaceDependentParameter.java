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

package net.mcreator.element.parts;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.mcreator.element.types.Item;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

public abstract class WorkspaceDependentParameter {

	protected transient Workspace workspace;

	protected WorkspaceDependentParameter(Workspace workspace) {
		this.workspace = workspace;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public static class GSONAdapter implements JsonDeserializer<WorkspaceDependentParameter> {

		@Nonnull private final Workspace workspace;

		public GSONAdapter(@Nonnull Workspace workspace) {
			this.workspace = workspace;
		}

		@Override
		public WorkspaceDependentParameter deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			WorkspaceDependentParameter entry = jsonDeserializationContext.deserialize(jsonElement.getAsJsonObject(),
					Item.ModelEntry.class);
			entry.workspace = this.workspace;
			return entry;
		}
	}
}
