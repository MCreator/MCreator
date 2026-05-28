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

package net.mcreator.io.mcp.resource;

import net.mcreator.io.mcp.protocol.model.McpSchema;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * Binary resource that loads raw bytes and exposes them as a base64 blob in MCP content.
 */
public abstract class BinaryMcpResource extends McpResource {

    protected BinaryMcpResource(String uri, String name, String description, String mimeType) {
        super(uri, name, description, mimeType);
    }

    protected BinaryMcpResource(String uri, String name, String description, String mimeType, McpSchema.Annotations annotations) {
        super(uri, name, description, mimeType, annotations);
    }

    @Override
    protected CompletableFuture<McpSchema.ResourceContent> readContent(String uri) {
        return loadBytes(uri).thenApply(bytes ->
                McpSchema.ResourceContent.blob(uri, getMimeType(), Base64.getEncoder().encodeToString(bytes), getAnnotations()));
    }

    protected abstract CompletableFuture<byte[]> loadBytes(String uri);

    protected CompletableFuture<byte[]> completedBytes(byte[] bytes) {
        return CompletableFuture.completedFuture(bytes);
    }
}
