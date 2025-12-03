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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.modgui.IBlocklyPanelHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class UITestUtil {

	public static ModElementGUI<?> openModElementGUIFor(MCreator mcreator, GeneratableElement generatableElement)
			throws Exception {
		// Store GeneratableElement to workspace so ModElementGUI can load GE from ME correctly
		mcreator.getWorkspace().getModElementManager().storeModElement(generatableElement);

		ModElementGUI<?> modElementGUI = generatableElement.getModElement().getType()
				.getModElementGUI(mcreator, generatableElement.getModElement(), true);

		// Verify that BlocklyPanels are fully loaded
		if (modElementGUI instanceof IBlocklyPanelHolder panelHolder) {
			CountDownLatch latch = new CountDownLatch(1);

			// Prepare a listener to detect if BlocklyPanel(s) are responding
			Set<BlocklyPanel> blocklyPanels = new HashSet<>();
			panelHolder.addBlocklyChangedListener((blocklyPanel, jsEventTriggeredChange) -> {
				if (jsEventTriggeredChange) {
					blocklyPanels.add(blocklyPanel);
					if (blocklyPanels.equals(panelHolder.getBlocklyPanels()))
						latch.countDown();
				}
			});

			// Give it time for panel to load and propagate the event
			assertTrue(latch.await(5, TimeUnit.SECONDS));
		}

		// Reload data lists to test this functionality
		modElementGUI.reloadDataLists();

		return modElementGUI;
	}

	public static void testIfValidationPasses(ModElementGUI<?> modElementGUI,
			boolean skipInitialXMLValidationIfAllowed) {
		AggregatedValidationResult validationResult = modElementGUI.validateAllPages();

		boolean hasErrors = false;
		for (ValidationResult result : validationResult.getGroupedValidationResults()) {
			if (result.type() == ValidationResult.Type.ERROR) {
				if (modElementGUI instanceof IBlocklyPanelHolder panelHolder) {
					if (result.isBlocklyResult()) {
						// skip Blockly validation in case it is marked that initial XML in the editor is not valid
						// and skipInitialXMLValidationIfAllowed flag is set to true
						if (skipInitialXMLValidationIfAllowed && !panelHolder.isInitialXMLValid())
							continue;
					}
				}

				hasErrors = true;
				break;
			}
		}

		if (hasErrors)
			fail(String.join(",", validationResult.getValidationProblemMessages()));
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
			Thread.sleep(50);

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
