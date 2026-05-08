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
import net.mcreator.element.types.Biome;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SoundElement implements IElement {
	private static final Biome.ClimatePoint DefaultBEAttenuationDistance = new Biome.ClimatePoint(0, 0);

	private final String name;
	private List<Sound> files;
	@Nullable private String subtitle;

	private String beCategory;
	private Biome.ClimatePoint beAttenuationDistance;

	public SoundElement(String name, List<Sound> files, @Nullable String subtitle) {
		this(name, "neutral", DefaultBEAttenuationDistance, files, subtitle);
	}

	public SoundElement(String name, String beCategory, Biome.ClimatePoint beAttenuationDistance, List<Sound> files,
			@Nullable String subtitle) {
		this.name = name;
		this.files = files;
		this.subtitle = subtitle;
		this.beCategory = beCategory;
		this.beAttenuationDistance = beAttenuationDistance;
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

	@Override public String getName() {
		return name;
	}

	public String getJavaName() {
		return name.toUpperCase(Locale.ENGLISH).replace(".", "_").replace("/", "_").replace(":", "_").replace("-", "_");
	}

	public List<Sound> getFiles() {
		return files;
	}

	public void setFiles(List<Sound> files) {
		this.files = files;
	}

	public @Nullable String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(@Nullable String subtitle) {
		this.subtitle = subtitle;
	}

	public String getBECategory() {
		return beCategory;
	}

	public void setBECategory(String beCategory) {
		this.beCategory = beCategory;
	}

	public Biome.ClimatePoint getBEAttenuationDistance() {
		return beAttenuationDistance;
	}

	public void setBEAttenuationDistance(Biome.ClimatePoint beAttenuationDistance) {
		this.beAttenuationDistance = beAttenuationDistance;
	}

	public static class Sound {
		private String name;
		private String category;
		private float volume;
		private float pitch;
		private int weight;
		private boolean preload;
		private int attenuationDistance;

		private boolean beIs3D;
		private boolean beInterruptible;

		public Sound(String name) {
			this(name, 1f, 1f, 1, false, 16, "neutral", true, true);
		}

		public Sound(String name, float volume, float pitch, int weight, boolean preload, int attenuationDistance,
				String category, boolean beIs3D, boolean beInterruptible) {
			this.name = name;
			this.volume = volume;
			this.pitch = pitch;
			this.weight = weight;
			this.preload = preload;
			this.attenuationDistance = attenuationDistance;
			this.category = category;
			this.beIs3D = beIs3D;
			this.beInterruptible = beInterruptible;
		}

		public Sound(String name, float volume, float pitch, int weight, String category) {
			this(name, volume, pitch, weight, false, 16, category, true, true);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public float getVolume() {
			return volume;
		}

		public void setVolume(double volume) {
			this.volume = (float) volume;
		}

		public float getPitch() {
			return pitch;
		}

		public void setPitch(double pitch) {
			this.pitch = (float) pitch;
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

		public int getAttenuationDistance() {
			return attenuationDistance;
		}

		public void setAttenuationDistance(int attenuationDistance) {
			this.attenuationDistance = attenuationDistance;
		}

		public boolean isPreload() {
			return preload;
		}

		public void setPreload(boolean preload) {
			this.preload = preload;
		}

		public boolean isBEIs3D() {
			return beIs3D;
		}

		public void setBEIs3D(boolean beIs3D) {
			this.beIs3D = beIs3D;
		}

		public boolean isBEInterruptible() {
			return beInterruptible;
		}

		public void setBEInterruptible(boolean beInterruptible) {
			this.beInterruptible = beInterruptible;
		}

		public boolean isInline() {
			return volume == 1 && pitch == 1 && !preload && attenuationDistance == 16 && weight == 1
					&& !category.equals("record") && !category.equals("music") && beIs3D && beInterruptible;
		}

		@Override public String toString() {
			return name;
		}
	}

	public static class SoundElementDeserializer implements JsonDeserializer<SoundElement> {
		@Override public SoundElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			List<Sound> files;
			try {
				files = context.deserialize(jsonObject.get("files").getAsJsonArray(),
						new TypeToken<List<Sound>>() {}.getType());
			} catch (Exception e) {
				files = new ArrayList<>();
			}

			// MCreator 2026.2 converter
			String category;
			if (jsonObject.has("category") && jsonObject.get("category").getAsString() instanceof String str) {
				List<String> oldFiles;
				if (jsonObject.get("file") != null) {
					oldFiles = Collections.singletonList(jsonObject.get("file").getAsString());
				} else {
					try {
						oldFiles = context.deserialize(jsonObject.get("files").getAsJsonArray(),
								new TypeToken<List<String>>() {}.getType());
					} catch (Exception e) {
						oldFiles = new ArrayList<>();
					}
				}
				List<Sound> finalFiles = files;
				oldFiles.forEach(oldFile -> finalFiles.add(new Sound(oldFile)));
				files.forEach(file -> file.setCategory(str));
				category = getObjectName(jsonObject, "category", "neutral");
			} else {
				category = getObjectName(jsonObject, "beCategory", "neutral");
			}

			return new SoundElement(jsonObject.getAsJsonPrimitive("name").getAsString(), category,
					jsonObject.getAsJsonObject("beAttenuationDistance") == null ?
							DefaultBEAttenuationDistance :
							new Biome.ClimatePoint(
									getFloatValue(jsonObject.getAsJsonObject("beAttenuationDistance"), "min"),

									getFloatValue(jsonObject.getAsJsonObject("beAttenuationDistance"), "max")), files,
					getObjectName(jsonObject, "subtitle"));
		}

		private Float getFloatValue(JsonObject jsonObject, String name) {
			return jsonObject.getAsJsonPrimitive(name) != null ? jsonObject.getAsJsonPrimitive(name).getAsFloat() : 0;
		}

		private String getObjectName(JsonObject jsonObject, String name) {
			return getObjectName(jsonObject, name, null);
		}

		private String getObjectName(JsonObject jsonObject, String name, String defaultValue) {
			return jsonObject.getAsJsonPrimitive(name) != null ?
					jsonObject.getAsJsonPrimitive(name).getAsString() :
					defaultValue;
		}
	}

}
