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

package net.mcreator.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class GeneratorTemplate {

	private final File file;
	private final Object templateData;

	private final String templateIdentificator;

	private final boolean isGlobal;

	private final Map<String, Object> dataModel = new HashMap<>();

	GeneratorTemplate(File file, String templateIdentificator, boolean global, Object templateData) {
		this.file = file;
		this.templateData = templateData;
		this.isGlobal = global;

		this.templateIdentificator = templateIdentificator;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public File getFile() {
		return file;
	}

	public Object getTemplateData() {
		return templateData;
	}

	public String getTemplateIdentificator() {
		return templateIdentificator;
	}

	public Map<String, Object> getDataModel() {
		return dataModel;
	}

	public void addDataModelEntry(String key, Object value) {
		dataModel.put(key, value);
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return file.equals(((GeneratorTemplate) o).file);
	}

	@Override public int hashCode() {
		return file.hashCode();
	}

}
