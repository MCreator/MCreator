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

package net.mcreator.workspace.elements;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundElement {

	private String name;
	private List<String> files;
	private String category;
	@Nullable private String directory;

	@Nullable private String subtitle;

	public SoundElement(String name, List<String> files, String category, @Nullable String subtitle, @Nullable String directory) {
		this.name = name;
		this.files = files;
		this.category = category;
		this.subtitle = subtitle;
		this.directory = directory;
	}

	@Override public String toString() {
		return getName();
	}

	@Override public boolean equals(Object element) {
		return element instanceof SoundElement && name.equals(((SoundElement) element).getName());
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public @Nullable String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(@Nullable String subtitle) {
		this.subtitle = subtitle;
	}

	@Nullable public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public static class SoundElementDeserializer implements JsonDeserializer<SoundElement> {
		@Override public SoundElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			List<String> files;
			if (jsonObject.get("file") != null) {
				files = Collections.singletonList(jsonObject.get("file").getAsString());
			} else {
				try {
					files = context.deserialize(jsonObject.get("files").getAsJsonArray(),
							new TypeToken<List<String>>() {}.getType());
				} catch (Exception e) {
					files = new ArrayList<>();
				}
			}

			return new SoundElement(jsonObject.getAsJsonPrimitive("name").getAsString(), files,
					jsonObject.getAsJsonPrimitive("category").getAsString(),
					jsonObject.getAsJsonPrimitive("subtitle") != null ?
							jsonObject.getAsJsonPrimitive("subtitle").getAsString() : null,
					jsonObject.getAsJsonPrimitive("directory") != null ?
							jsonObject.getAsJsonPrimitive("directory").getAsString() : null);
		}
	}

}
