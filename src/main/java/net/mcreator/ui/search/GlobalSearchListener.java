/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.search;

import net.mcreator.ui.component.util.ThreadUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class GlobalSearchListener {

	// Weak hash map is used to not hold reference to windows after they are disposed
	private static final Map<Window, Supplier<JComponent>> searchWindows = new WeakHashMap<>();

	public static void install(Window window, Supplier<JComponent> currentTab) {
		ThreadUtil.runOnSwingThread(() -> {
			installKeyEventDispatcher();
			searchWindows.put(window, currentTab);
		});
	}

	private static boolean keyEventDispatcherInstalled = false;

	private static void installKeyEventDispatcher() {
		if (!keyEventDispatcherInstalled) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

				private static long lastShiftTime = 0;
				private static final int DOUBLE_SHIFT_THRESHOLD_MS = 400;

				@Override public boolean dispatchKeyEvent(KeyEvent e) {
					if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_SHIFT
							&& e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
						long currentTime = System.currentTimeMillis();
						if (currentTime - lastShiftTime < DOUBLE_SHIFT_THRESHOLD_MS) {
							Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager()
									.getFocusedWindow();
							if (focusedWindow != null && searchWindows.containsKey(focusedWindow)) {
								Supplier<JComponent> searchWindowSupplier = searchWindows.get(focusedWindow);
								if (searchWindowSupplier != null) {
									JComponent currentTab = searchWindowSupplier.get();
									if (currentTab instanceof ISearchable searchable) {
										searchable.search(null);
										return true;
									}
								}
							}
						}
						lastShiftTime = currentTime;
					}
					return false;
				}
			});
			keyEventDispatcherInstalled = true;
		}
	}

}
