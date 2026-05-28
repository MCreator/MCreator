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

package net.mcreator.io.mcp.protocol;

import com.google.gson.JsonElement;

import java.util.List;

public class McpSchema {

    public record Implementation(String name, String version) {
    }

    public record InitializeResponse(String protocolVersion, ServerCapabilities capabilities,
                                     Implementation serverInfo, String instructions) {
        public InitializeResponse(String protocolVersion, ServerCapabilities capabilities,
                                  Implementation serverInfo) {
            this(protocolVersion, capabilities, serverInfo, null);
        }
    }

    public record ServerCapabilities(Object tools, ResourceCapability resources) {
    }

    public record ResourceCapability(boolean subscribe, boolean listChanged) {
    }

    public record SubscribeRequest(String uri) {
    }

    public record Annotations(List<String> audience, Double priority, String lastModified) {
    }

    public record ToolAnnotations(Double priorityHint) {
    }

    public record Tool(String name, String description, Object inputSchema, ToolAnnotations annotations,
                      Object outputSchema) {
        public Tool(String name, String description, Object inputSchema, ToolAnnotations annotations) {
            this(name, description, inputSchema, annotations, null);
        }
    }

    public record ToolListResponse(List<Tool> tools) {
    }

    public record CallToolRequest(String name, JsonElement arguments) {
    }

    public record CallToolResponse(List<Content> content, boolean isError, JsonElement structuredContent) {
        public CallToolResponse(List<Content> content) {
            this(content, false, null);
        }

        public CallToolResponse(List<Content> content, boolean isError) {
            this(content, isError, null);
        }
    }

    public record Content(String type, String text, Annotations annotations) {
        public Content(String type, String text) {
            this(type, text, null);
        }

        public static Content text(String text) {
            return new Content("text", text, null);
        }

        public static Content text(String text, Annotations annotations) {
            return new Content("text", text, annotations);
        }
    }

    public record Resource(String uri, String name, String description, String mimeType, Annotations annotations) {
    }

    public record ResourceListResponse(List<Resource> resources) {
    }

    public record ReadResourceRequest(String uri) {
    }

    public record ReadResourceResponse(List<ResourceContent> contents) {
    }

    public record ResourceContent(String uri, String mimeType, String text, String blob, Annotations annotations) {
        public static ResourceContent text(String uri, String mimeType, String text, Annotations annotations) {
            return new ResourceContent(uri, mimeType, text, null, annotations);
        }

        public static ResourceContent blob(String uri, String mimeType, String blobBase64, Annotations annotations) {
            return new ResourceContent(uri, mimeType, null, blobBase64, annotations);
        }
    }
}
