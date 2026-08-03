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

package net.mcreator.ui.component;

import javax.swing.SwingUtilities;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MonacoEditorPool {

	private static final Queue<MonacoEditorPanel> POOL = new ConcurrentLinkedQueue<>();
	private static final int MAX_POOL_SIZE = 3;

	static {
		// Asynchronously pre-warm pool on Swing thread
		SwingUtilities.invokeLater(() -> {
			for (int i = 0; i < MAX_POOL_SIZE; i++) {
				try {
					MonacoEditorPanel prewarmed = new MonacoEditorPanel("", "java", false);
					POOL.offer(prewarmed);
				} catch (Exception ignored) {
				}
			}
		});
	}

	public static MonacoEditorPanel getOrCreate(String code, String langOrExtension, boolean readOnly) {
		MonacoEditorPanel panel;
		while ((panel = POOL.poll()) != null) {
			if (panel.isLoaded()) {
				panel.initForReuse(code, langOrExtension, readOnly);
				replenishPool();
				return panel;
			} else {
				try {
					panel.close();
				} catch (Exception ignored) {
				}
			}
		}
		MonacoEditorPanel fresh = new MonacoEditorPanel(code, langOrExtension, readOnly);
		replenishPool();
		return fresh;
	}

	private static void replenishPool() {
		SwingUtilities.invokeLater(() -> {
			if (POOL.size() < MAX_POOL_SIZE) {
				try {
					POOL.offer(new MonacoEditorPanel("", "java", false));
				} catch (Exception ignored) {
				}
			}
		});
	}

	public static void recycle(MonacoEditorPanel panel) {
		if (panel != null) {
			if (panel.isLoaded() && POOL.size() < MAX_POOL_SIZE) {
				panel.resetForPool();
				POOL.offer(panel);
			} else {
				try {
					panel.close();
				} catch (Exception ignored) {
				}
			}
		}
	}
}