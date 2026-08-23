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

package net.mcreator.ui.chromium;

import javax.annotation.Nullable;
import java.io.InputStream;

public interface RequestHandler {

	/**
	 * Called for each request before resource resolution, allowing the handler to rewrite the request path.
	 * Rewrites of all registered handlers are chained in registration order.
	 *
	 * @param path The requested resource path (e.g. /blockly/blockly.html), potentially already
	 *             rewritten by previously registered handlers.
	 * @return The rewritten path, or the passed path unchanged if this handler does not rewrite it.
	 */
	default String rewritePath(String path) {
		return path;
	}

	/**
	 * Called for each request before the default class loader based resource lookup.
	 *
	 * @param path The requested resource path (e.g. /blockly/blockly.html).
	 * @return Stream with the resource contents, or null to pass processing back to the default handler.
	 * @throws Exception If the request handling fails. The request is then passed to the remaining handlers.
	 */
	@Nullable InputStream handleRequest(String path) throws Exception;
}
