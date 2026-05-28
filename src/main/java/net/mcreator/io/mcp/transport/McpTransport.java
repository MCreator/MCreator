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

package net.mcreator.io.mcp.transport;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for MCP communication transports.
 * Transports are responsible for receiving and sending raw JSON-RPC messages.
 */
public interface McpTransport {

    /**
     * Starts the transport.
     *
     * @param handler The handler to call when a new message is received.
     * @throws IOException If the transport fails to start.
     */
    void start(McpHandler handler) throws IOException;

    /**
     * Stops the transport and releases any resources.
     */
    void stop();

    /**
     * Sends a message to a specific session.
     *
     * @param sessionId The ID of the session to send the message to.
     * @param message   The raw JSON-RPC message string.
     */
    void sendMessage(String sessionId, String message);

    /**
     * Functional interface for handling incoming MCP messages.
     */
    interface McpHandler {
        /**
         * Processes an incoming message and returns a future for the response.
         *
         * @param sessionId The ID of the session that sent the message.
         * @param message   The raw JSON-RPC message string.
         * @return A CompletableFuture that will be completed with the JSON-RPC response,
         *         or null if no immediate response is required (e.g. for notifications).
         */
        CompletableFuture<String> handleMessage(String sessionId, String message);
    }

}
