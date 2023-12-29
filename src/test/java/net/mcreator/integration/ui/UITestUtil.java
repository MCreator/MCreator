/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.integration.ui;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.fail;

public class UITestUtil {

	public static ModElementGUI<?> openModElementGUIFor(MCreator mcreator, GeneratableElement generatableElement)
			throws Exception {
		ModElementGUI<?> modElementGUI = generatableElement.getModElement().getType()
				.getModElementGUI(mcreator, generatableElement.getModElement(), false);
		modElementGUI.reloadDataLists();

		Field field = modElementGUI.getClass().getSuperclass().getDeclaredField("editingMode");
		field.setAccessible(true);
		field.set(modElementGUI, true);

		// test opening generatable element
		Method method = modElementGUI.getClass().getDeclaredMethod("openInEditingMode", GeneratableElement.class);
		method.setAccessible(true);
		method.invoke(modElementGUI, generatableElement);

		// If ModElementGUI<?> contains BlocklyPanel, give it time to fully load
		if (Arrays.stream(modElementGUI.getClass().getDeclaredFields())
				.anyMatch(f -> f.getType() == BlocklyPanel.class)) {
			Thread.sleep(3500);
		}

		return modElementGUI;
	}

	public static void testIfValidationPasses(ModElementGUI<?> modElementGUI) {
		// test if UI validation is error free (skip advancement and feature as provider provides empty Blockly setup)
		AggregatedValidationResult validationResult = modElementGUI.validateAllPages();
		if ((modElementGUI.getModElement().getType() != ModElementType.ADVANCEMENT
				&& modElementGUI.getModElement().getType() != ModElementType.FEATURE)
				&& !validationResult.validateIsErrorFree()) {
			fail(String.join(",", validationResult.getValidationProblemMessages()));
		}
	}

	public static void waitUntilWindowIsOpen(Window master, Runnable openTask) throws Throwable {
		int frames_start = Window.getWindows().length;

		AtomicReference<Throwable> throwableAtomic = new AtomicReference<>(null);

		SwingUtilities.invokeLater(() -> {
			try {
				openTask.run();
			} catch (Throwable t) {
				throwableAtomic.set(t);
			}
		});

		long start = System.currentTimeMillis();
		while (Window.getWindows().length == frames_start) {
			//noinspection BusyWait
			Thread.sleep(100);

			if (System.currentTimeMillis() - start > 6000)
				throw new TimeoutException();

			if (throwableAtomic.get() != null)
				throw throwableAtomic.get();
		}

		Arrays.stream(Window.getWindows()).filter(w -> w != master).forEach(Window::dispose);

		if (throwableAtomic.get() != null)
			throw throwableAtomic.get();
	}
}
