/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.preferences.data;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.ColorEntry;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

import java.awt.*;

public class ImageEditorSection extends PreferencesSection {

	public final BooleanEntry storeMetadata;
	public final ColorEntry selectedFramesColor;
	public final IntegerEntry defaultPercentageTimeline;
	public final StringEntry selectedFrameAtOpening;
	public final StringEntry singleFrameDeletionBehaviour;

	ImageEditorSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		storeMetadata = addEntry(new BooleanEntry("storeMetadata", true));
		selectedFramesColor = addEntry(new ColorEntry("selectedFramesColor", new Color(75, 85, 197)));
		defaultPercentageTimeline = addEntry(new IntegerEntry("defaultPercentageTimeline", 24, 0, 50));
		selectedFrameAtOpening = addEntry(new StringEntry("selectedFrameAtOpening", "First frame", "First frame", "Last frame"));
		singleFrameDeletionBehaviour = addEntry(new StringEntry("singleFrameDeletionBehaviour", "Empty frame", "Empty frame", "Keep existing frame"));
	}

	@Override public String getSectionKey() {
		return "imageEditor";
	}

}
