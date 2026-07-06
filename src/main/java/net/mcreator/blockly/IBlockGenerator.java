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

package net.mcreator.blockly;

import net.mcreator.generator.template.TemplateGeneratorException;
import org.w3c.dom.Element;

import javax.annotation.Nullable;

public interface IBlockGenerator {

	void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException;

	String[] getSupportedBlocks();

	BlockType getBlockType();

	enum BlockType {
		PROCEDURAL, OUTPUT
	}

	/**
	 * @return Blockly JSON definitions for each block returned by {@link #getSupportedBlocks()}, in the same order.
	 */
	default @Nullable String[] getBlockJSONDefinitions() {
		return null;
	}

	default @Nullable String getToolboxCategory() {
		return null;
	}

	/**
	 * Retrieves the initialization XML for the toolbox for each block returned by {@link #getSupportedBlocks()}, in the same order.
	 */
	default @Nullable String[][] getToolboxInit() {
		return null;
	}

}
