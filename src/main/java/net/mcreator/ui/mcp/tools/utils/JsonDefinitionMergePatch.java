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

package net.mcreator.ui.mcp.tools.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.io.mcp.McpJson;

import java.util.Map;

public final class JsonDefinitionMergePatch {

	// serializeNulls is required, so null values in the patch map survive toJsonTree and can remove keys
	private static final Gson gson = McpJson.lenientGson().newBuilder().serializeNulls().create();

	private JsonDefinitionMergePatch() {}

	public static class MergePatchException extends Exception {

		public MergePatchException(String message) {
			super(message);
		}

	}

	public static JsonElement apply(JsonElement document, Map<String, Object> patch) throws MergePatchException {
		return apply(document, gson.toJsonTree(patch));
	}

	public static JsonElement apply(JsonElement document, JsonElement patch) throws MergePatchException {
		if (!patch.isJsonObject()) {
			throw new MergePatchException("Patch must be a JSON object");
		}
		if (!document.isJsonObject()) {
			throw new MergePatchException("Document must be a JSON object");
		}

		JsonObject target = document.getAsJsonObject().deepCopy();
		mergePatch(target, patch.getAsJsonObject());
		return target;
	}

	private static void mergePatch(JsonObject target, JsonObject patch) {
		for (Map.Entry<String, JsonElement> entry : patch.entrySet()) {
			String key = entry.getKey();
			JsonElement patchValue = entry.getValue();
			if (patchValue.isJsonNull()) {
				target.remove(key);
			} else if (patchValue.isJsonObject() && target.has(key) && target.get(key).isJsonObject()) {
				mergePatch(target.getAsJsonObject(key), patchValue.getAsJsonObject());
			} else {
				target.add(key, patchValue.deepCopy());
			}
		}
	}

}
