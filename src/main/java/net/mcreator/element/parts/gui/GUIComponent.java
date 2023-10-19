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
import net.mcreator.element.parts.procedure.RetvalProcedure;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonAdapter(GUIComponent.GSONAdapter.class) public abstract class GUIComponent implements Comparable<GUIComponent> {

	@Nullable public AnchorPoint anchorPoint;
	public int x;
	public int y;

	public transient UUID uuid;

	private static final Map<String, Class<? extends GUIComponent>> typeMappings = new HashMap<>() {{
		put("tooltip", Tooltip.class); // weight -15
		put("entitymodel", EntityModel.class); //weight -10
		put("textfield", TextField.class); // weight 0
		put("label", Label.class); // weight 10
		put("checkbox", Checkbox.class); //weight 20
		put("imagebutton", ImageButton.class); //weight 25
		put("button", Button.class);// weight 30
		put("image", Image.class);// weight 40
		put("inputslot", InputSlot.class); // weight 50
		put("outputslot", OutputSlot.class); // weight 50
	}};

	private static final Map<Class<? extends GUIComponent>, String> typeMappingsReverse = typeMappings.entrySet()
			.stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	GUIComponent() {
		uuid = UUID.randomUUID();
	}

	GUIComponent(int x, int y) {
		this();
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the name of the component. Name should be Java and registry name compatible.
	 * <p>
	 * The name should be unique for the components that need it.
	 *
	 * @return Component name
	 */
	public abstract String getName();

	public abstract void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g);

	public abstract int getWidth(Workspace workspace);

	public abstract int getHeight(Workspace workspace);

	/**
	 * Returns the priority for when this component should be drawn in the UI, to represent how Minecraft draws components in the game.
	 *
	 * @return The priority of the component (lower means it will be rendered closer to the screen and higher means it will "sink" more behind other components)
	 */
	public abstract int getWeight();

	public boolean isSizeKnown() {
		return true;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final int gx(int width) {
		double mx = (WYSIWYG.W - width) / 2.0;
		return (int) Math.floor(x - mx);
	}

	public final int gy(int height) {
		double my = (WYSIWYG.H - height) / 2.0;
		return (int) Math.floor(y - my);
	}

	@Nullable public AnchorPoint getAnchorPoint() {
		return anchorPoint;
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
		return getName();
	}

	public static class GSONAdapter implements JsonSerializer<GUIComponent>, JsonDeserializer<GUIComponent> {

		private static final Gson gson;

		static {
			GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient();

			RetvalProcedure.GSON_ADAPTERS.forEach(gsonBuilder::registerTypeAdapter);

			gson = gsonBuilder.create();
		}

		@Override
		public GUIComponent deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			String elementType = jsonElement.getAsJsonObject().get("type").getAsString();

			Class<? extends GUIComponent> typeMapping = typeMappings.get(elementType);
			if (typeMapping == null)
				typeMapping = Unknown.class; // fallback to Unknown (e.g. plugin component that no longer exists)

			GUIComponent component = jsonDeserializationContext.deserialize(jsonElement.getAsJsonObject().get("data"),
					typeMapping);
			component.uuid = UUID.randomUUID(); // init UUID for deserialized component
			return component;
		}

		@Override
		public JsonElement serialize(GUIComponent element, Type type,
				JsonSerializationContext jsonSerializationContext) {
			JsonObject root = new JsonObject();

			String typeMapping = typeMappingsReverse.get(element.getClass());
			if (typeMapping == null)
				typeMapping = "unknown";

			root.add("type", new JsonPrimitive(typeMapping));
			root.add("data", gson.toJsonTree(element));
			return root;
		}

	}

	public static final class Unknown extends GUIComponent {

		@Override public String getName() {
			return "unknown_element";
		}

		@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {

		}

		@Override public int getWidth(Workspace workspace) {
			return 0;
		}

		@Override public int getHeight(Workspace workspace) {
			return 0;
		}

		@Override public int getWeight() {
			return 0;
		}
	}

	public enum AnchorPoint {
		TOP_LEFT("top_left"), TOP_CENTER("top_center"), TOP_RIGHT("top_right"), CENTER_LEFT("center_left"), CENTER(
				"center"), CENTER_RIGHT("center_right"), BOTTOM_LEFT("bottom_left"), BOTTOM_CENTER(
				"bottom_center"), BOTTOM_RIGHT("bottom_right");

		private final String id;

		AnchorPoint(String id) {
			this.id = id;
		}

		@Override public String toString() {
			return L10N.t("dialog.gui.anchor." + id);
		}

		public Point getAnchorPoint(int width, int height) {
			return switch (this) {
				case TOP_LEFT -> new Point(0, 0);
				case TOP_CENTER -> new Point(width / 2, 0);
				case TOP_RIGHT -> new Point(width, 0);
				case CENTER_LEFT -> new Point(0, height / 2);
				case CENTER -> new Point(width / 2, height / 2);
				case CENTER_RIGHT -> new Point(width, height / 2);
				case BOTTOM_LEFT -> new Point(0, height);
				case BOTTOM_CENTER -> new Point(width / 2, height);
				case BOTTOM_RIGHT -> new Point(width, height);
			};
		}

	}

}
