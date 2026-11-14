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

import javax.annotation.Nullable;
import java.util.List;

public class BedrockSoundElement extends SoundElement {
	private String beCategory;
	private float minAttenuationDistance;
	private float maxAttenuationDistance;

	public BedrockSoundElement(String name, List<Sound> files, @Nullable String subtitle) {
		this(name, "neutral", files, 0, 0, subtitle);
	}

	public BedrockSoundElement(String name, String beCategory, List<Sound> files, float minAttenuationDistance,
			float maxAttenuationDistance, @Nullable String subtitle) {
		super(name, files, subtitle);
		this.beCategory = beCategory;
		this.minAttenuationDistance = minAttenuationDistance;
		this.maxAttenuationDistance = maxAttenuationDistance;
	}

	public String getBECategory() {
		return beCategory;
	}

	public float getMinAttenuationDistance() {
		return minAttenuationDistance;
	}

	public float getMaxAttenuationDistance() {
		return maxAttenuationDistance;
	}

	public void setBECategory(String beCategory) {
		this.beCategory = beCategory;
	}

	public void setMinAttenuationDistance(float minAttenuationDistance) {
		this.minAttenuationDistance = minAttenuationDistance;
	}

	public void setMaxAttenuationDistance(float maxAttenuationDistance) {
		this.maxAttenuationDistance = maxAttenuationDistance;
	}
}