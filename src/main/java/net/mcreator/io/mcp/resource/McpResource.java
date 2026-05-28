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

import java.util.concurrent.CompletableFuture;

/**
 * Base class for MCP resources. Subclass to add resource-specific behavior and APIs.
 */
public abstract class McpResource implements IMcpResource {

    private final String uri;
    private final String name;
    private final String description;
    private final String mimeType;
    private final McpSchema.Annotations annotations;

    protected McpResource(String uri, String name, String description, String mimeType) {
        this(uri, name, description, mimeType, null);
    }

    protected McpResource(String uri, String name, String description, String mimeType, McpSchema.Annotations annotations) {
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
        this.annotations = annotations;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public McpSchema.Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public final CompletableFuture<McpSchema.ResourceContent> read(String uri) {
        return readContent(uri);
    }

    protected abstract CompletableFuture<McpSchema.ResourceContent> readContent(String uri);

    protected static CompletableFuture<McpSchema.ResourceContent> completed(McpSchema.ResourceContent content) {
        return CompletableFuture.completedFuture(content);
    }
}
