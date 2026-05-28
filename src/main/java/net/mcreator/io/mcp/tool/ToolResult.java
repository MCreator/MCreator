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

package net.mcreator.io.mcp.tool;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.mcreator.io.mcp.protocol.model.McpSchema;

import java.util.List;

public final class ToolResult {

    private static final Gson gson = new Gson();

    private final McpSchema.CallToolResponse response;

    private ToolResult(McpSchema.CallToolResponse response) {
        this.response = response;
    }

    public static ToolResult text(String text) {
        return new ToolResult(new McpSchema.CallToolResponse(List.of(McpSchema.Content.text(text))));
    }

    public static ToolResult text(String text, McpSchema.Annotations annotations) {
        return new ToolResult(new McpSchema.CallToolResponse(
                List.of(McpSchema.Content.text(text, annotations))));
    }

    /**
     * Returns a structured JSON object result per MCP 2025-11-25.
     * Includes serialized JSON in a text content block for backwards compatibility.
     */
    public static ToolResult object(Object structured) {
        JsonElement structuredContent = gson.toJsonTree(structured);
        if (!structuredContent.isJsonObject()) {
            throw new IllegalArgumentException("structuredContent must be a JSON object");
        }
        String json = gson.toJson(structured);
        return new ToolResult(new McpSchema.CallToolResponse(
                List.of(McpSchema.Content.text(json)),
                false,
                structuredContent));
    }

    public static ToolResult error(String message) {
        return new ToolResult(new McpSchema.CallToolResponse(List.of(McpSchema.Content.text(message)), true));
    }

    McpSchema.CallToolResponse toResponse() {
        return response;
    }
}
