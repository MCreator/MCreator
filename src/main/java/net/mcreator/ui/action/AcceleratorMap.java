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

package net.mcreator.ui.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class AcceleratorMap {

	private final Map<BasicAction, KeyStroke> actionKeyStrokeMap = new HashMap<>();

	public AcceleratorMap(ActionRegistry actionRegistry) {
		// Init defaults
		actionKeyStrokeMap.put(actionRegistry.preferences, KeyStroke.getKeyStroke(KeyEvent.VK_P,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
		actionKeyStrokeMap.put(actionRegistry.showFindBar,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.showReplaceBar,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.reformatCodeAndImports,
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.reformatCodeOnly, KeyStroke.getKeyStroke(KeyEvent.VK_W,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
		actionKeyStrokeMap.put(actionRegistry.saveCode,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.exportToJAR,
				KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.workspaceSettings, KeyStroke.getKeyStroke(KeyEvent.VK_P,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.ALT_DOWN_MASK));
		actionKeyStrokeMap.put(actionRegistry.buildWorkspace,
				KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.buildGradleOnly, KeyStroke.getKeyStroke(KeyEvent.VK_B,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
		actionKeyStrokeMap.put(actionRegistry.runServer, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.ALT_DOWN_MASK));
		actionKeyStrokeMap.put(actionRegistry.runClient, KeyStroke.getKeyStroke(KeyEvent.VK_C,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.ALT_DOWN_MASK));

		actionKeyStrokeMap.put(actionRegistry.showConsoleTab, KeyStroke.getKeyStroke(KeyEvent.VK_C,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.showWorkspaceTab, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		actionKeyStrokeMap.put(actionRegistry.showWorkspaceBrowser, KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.hideWorkspaceBrowser, KeyStroke.getKeyStroke(KeyEvent.VK_A,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		actionKeyStrokeMap.put(actionRegistry.syncToRemote,
				KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.syncFromRemote,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		actionKeyStrokeMap.put(actionRegistry.newWorkspace, KeyStroke.getKeyStroke(KeyEvent.VK_N,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		actionKeyStrokeMap.put(actionRegistry.createMCItemTexture, KeyStroke.getKeyStroke(KeyEvent.VK_9,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.createAnimatedTexture, KeyStroke.getKeyStroke(KeyEvent.VK_8,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.importOtherTexture, KeyStroke.getKeyStroke(KeyEvent.VK_7,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.importSound, KeyStroke.getKeyStroke(KeyEvent.VK_6,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.importStructure, KeyStroke.getKeyStroke(KeyEvent.VK_5,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.importStructure,
				KeyStroke.getKeyStroke(KeyEvent.VK_F2, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		actionKeyStrokeMap.put(actionRegistry.closeCurrentTab, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		actionKeyStrokeMap.put(actionRegistry.closeAllTabs, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));

		//Image Editor accelerators
		actionKeyStrokeMap.put(actionRegistry.imageEditorUndo,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorRedo,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorSave,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorSaveAs, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		//IE Tools
		actionKeyStrokeMap.put(actionRegistry.imageEditorResizeCanvas, KeyStroke.getKeyStroke(KeyEvent.VK_R,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorPencil,
				KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorShape,
				KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorEraser,
				KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorStamp,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorFloodFill,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorColorPicker, KeyStroke.getKeyStroke(KeyEvent.VK_C,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorColorize,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorDesaturate, KeyStroke.getKeyStroke(KeyEvent.VK_D,
				KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorHSVNoise,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorMoveLayer,
				KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		actionKeyStrokeMap.put(actionRegistry.imageEditorResizeLayer,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}

	void registerAll() {
		for (Map.Entry<BasicAction, KeyStroke> entry : actionKeyStrokeMap.entrySet())
			entry.getKey().setAccelerator(entry.getValue());
	}

	public Map<BasicAction, KeyStroke> getActionKeyStrokeMap() {
		return actionKeyStrokeMap;
	}
}
