/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.blockly;

import net.mcreator.generator.blockly.SectionMarker;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public interface IBlockGeneratorWithSections extends IBlockGenerator {

	static Sections addSectionsToDataModel(Map<String, Object> dataModel) {
		AtomicReference<String> head = new AtomicReference<>("");
		AtomicReference<String> tail = new AtomicReference<>("");
		dataModel.put("head", new SectionMarker(head));
		dataModel.put("tail", new SectionMarker(tail));
		return new Sections(head, tail);
	}

	static void handleSections(BlocklyToCode master, Sections sections) {
		if (!Objects.equals(master.getHeadSection(), sections.head().get()) || !Objects.equals(master.getTailSection(),
				sections.tail().get())) {
			master.append(master.getTailSection());
			master.setTailSection(sections.tail().get());
			master.setHeadSection(sections.head().get());
			master.append(master.getHeadSection());
		}
	}

	static void terminateSections(BlocklyToCode master) {
		master.append(master.getTailSection());
		master.clearSections();
	}

	@Override default BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}

	record Sections(AtomicReference<String> head, AtomicReference<String> tail) {}

}
