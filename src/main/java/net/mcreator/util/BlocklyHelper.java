/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlocklyHelper {
	private final Document doc;

	public BlocklyHelper(Document doc) {
		this.doc = doc;
	}

	/**
	 * Creates a block of the given block type and with the given children
	 * @param blockType The type of this block
	 * @param children The children (fields, values...) that should be appended to this block
	 * @return An {@link Element} representing this block
	 */
	public Element createBlock(String blockType, Element... children) {
		Element block = doc.createElement("block");
		block.setAttribute("type", blockType);
		for (Element child : children) {
			if (child != null)
				block.appendChild(child);
		}

		return block;
	}

	/**
	 * Creates a field with the given name and the given value
	 * @param fieldName The name of this field
	 * @param fieldValue If not null, the value of this field
	 * @return An {@link Element} representing this field
	 */
	public Element createField(String fieldName, String fieldValue) {
		Element field = doc.createElement("field");
		field.setAttribute("name", fieldName);
		if (fieldValue != null)
			field.setTextContent(fieldValue);

		return field;
	}

	/**
	 * Creates a value input with the given name and the given block
	 * @param valueName The name of this value input
	 * @param inputBlock If present, the block attached to this input
	 * @return An {@link Element} representing this value input
	 */
	public Element createValue(String valueName, Element inputBlock) {
		Element value = doc.createElement("value");
		value.setAttribute("name", valueName);
		if (inputBlock != null)
			value.appendChild(inputBlock);

		return value;
	}
}
