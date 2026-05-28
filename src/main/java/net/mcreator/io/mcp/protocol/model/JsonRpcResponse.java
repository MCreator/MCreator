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

package net.mcreator.io.mcp.protocol.model;

import com.google.gson.JsonElement;

public record JsonRpcResponse(String jsonrpc, JsonElement result, JsonRpcError error, JsonElement id) {
    public JsonRpcResponse {
        if (jsonrpc == null) jsonrpc = "2.0";
    }

    public JsonRpcResponse(JsonElement id, JsonElement result) {
        this("2.0", result, null, id);
    }

    public JsonRpcResponse(JsonElement id, JsonRpcError error) {
        this("2.0", null, error, id);
    }

    public record JsonRpcError(int code, String message, JsonElement data) {
        public JsonRpcError(int code, String message) {
            this(code, message, null);
        }
    }
}
