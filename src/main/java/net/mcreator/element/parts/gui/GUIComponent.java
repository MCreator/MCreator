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

package net.mcreator.element.parts.gui;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonAdapter(GUIComponent.GSONAdapter.class) public abstract class GUIComponent implements Comparable<GUIComponent> {

	public String name;
	public int x;
	public int y;

	public transient UUID uuid;

	private static transient final Map<String, Class<? extends GUIComponent>> typeMappings = new HashMap<String, Class<? extends GUIComponent>>() {{
		put("button", Button.class);
		put("image", Image.class);
		put("inputslot", InputSlot.class);
		put("outputslot", OutputSlot.class);
		put("label", Label.class);
		put("textfield", TextField.class);
		put("checkbox", Checkbox.class);
	}};

	private static transient final Map<Class<? extends GUIComponent>, String> typeMappingsReverse = typeMappings
			.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	GUIComponent() {
		uuid = UUID.randomUUID();
	}

	GUIComponent(String name, int x, int y) {
		this();
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public abstract void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g);

	public abstract int getWidth(Workspace workspace);

	public abstract int getHeight(Workspace workspace);

	public abstract int getWeight();

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	@Override public final int compareTo(@Nonnull GUIComponent o) {
		return o.getWeight() - getWeight();
	}

	@Override public final int hashCode() {
		return uuid.hashCode();
	}

	@Override public final boolean equals(Object obj) {
		return (obj instanceof GUIComponent) && ((GUIComponent) obj).uuid.equals(this.uuid);
	}

	@Override public String toString() {
		return name;
	}

	public static class GSONAdapter implements JsonSerializer<GUIComponent>, JsonDeserializer<GUIComponent> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@Override
		public GUIComponent deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			String elementType = jsonElement.getAsJsonObject().get("type").getAsString();

			GUIComponent component = jsonDeserializationContext
					.deserialize(jsonElement.getAsJsonObject().get("data"), typeMappings.get(elementType));
			component.uuid = UUID.randomUUID(); // init UUID for deserialized component
			return component;
		}

		@Override
		public JsonElement serialize(GUIComponent element, Type type,
				JsonSerializationContext jsonSerializationContext) {
			JsonObject root = new JsonObject();
			root.add("type", new JsonPrimitive(typeMappingsReverse.get(element.getClass())));
			root.add("data", gson.toJsonTree(element));
			return root;
		}

	}

}
