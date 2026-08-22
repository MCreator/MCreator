/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import net.mcreator.element.types.Biome;

import javax.annotation.Nullable;
import java.util.List;

public class BedrockSoundElement extends SoundElement {
	static final Biome.ClimatePoint DefaultBEAttenuationDistance = new Biome.ClimatePoint(0, 0);

	private String beCategory;
	private Biome.ClimatePoint beAttenuationDistance;

	public BedrockSoundElement(String name, List<Sound> files, @Nullable String subtitle) {
		this(name, "neutral", DefaultBEAttenuationDistance, files, subtitle);
	}

	public BedrockSoundElement(String name, String beCategory, Biome.ClimatePoint beAttenuationDistance, List<Sound> files,
			@Nullable String subtitle) {
		super(name, files, subtitle);
		this.beCategory = beCategory;
		this.beAttenuationDistance = beAttenuationDistance;
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
}