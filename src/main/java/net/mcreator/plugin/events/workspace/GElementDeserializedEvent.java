/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.plugin.events.workspace;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import net.mcreator.plugin.MCREvent;
import net.mcreator.workspace.Workspace;

public class GElementDeserializedEvent extends MCREvent {

	private final Workspace workspace;
	private final JsonElement jsonElement;
	private final JsonDeserializationContext deserializationContext;

	/**
	 * <p>This event is triggered when a generatable element is deserialized.
	 * This event can be used to create a custom mod element converter or other plugin features.</p>
	 *
	 * @param workspace		<p>The {@link Workspace} where the generatable element is stored.</p>
	 * @param jsonElement			<p>The {@link JsonElement} that is deserialized.</p>
	 * @param deserializationContext	<p>The {@link JsonDeserializationContext} used to deserialize json parameters.</p>
	 */

	public GElementDeserializedEvent(Workspace workspace, JsonElement jsonElement, JsonDeserializationContext deserializationContext) {
		this.workspace = workspace;
		this.jsonElement = jsonElement;
		this.deserializationContext = deserializationContext;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public JsonElement getJsonElement() {
		return jsonElement;
	}

	public JsonDeserializationContext getDeserializationContext() {
		return deserializationContext;
	}

}