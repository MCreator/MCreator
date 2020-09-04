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

package net.mcreator.ui.component.util;

import foxtrot.Job;
import foxtrot.Worker;
import javafx.application.Platform;

import javax.swing.*;

public class EDTUtils {

	public static void requestNonBlockingUIRefresh() {
		if (SwingUtilities.isEventDispatchThread() && !Platform
				.isFxApplicationThread()) { // we can only run this on EDT
			Worker.post(new Job() {
				@Override public Object run() {
					try {
						Thread.sleep(10);
					} catch (Exception ignored) {
					}
					return null;
				}
			});
		}
	}

}
