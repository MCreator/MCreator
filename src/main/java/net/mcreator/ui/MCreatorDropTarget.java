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

package net.mcreator.ui;

import net.mcreator.ui.action.impl.workspace.resources.ModelImportActions;
import net.mcreator.ui.action.impl.workspace.resources.StructureImportActions;
import net.mcreator.ui.dialogs.SoundElementDialog;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

record MCreatorDropTarget(MCreator mcreator) implements DropTargetListener {

	private static final Logger LOG = LogManager.getLogger("DND");

	MCreatorDropTarget {
		new DropTarget(mcreator, DnDConstants.ACTION_MOVE, this, true, null);
	}

	@Override public void dragEnter(DropTargetDragEvent dtde) {
		processDrag(dtde);
	}

	@Override public void dragOver(DropTargetDragEvent dtde) {
		processDrag(dtde);
	}

	@Override public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override public void dragExit(DropTargetEvent dtde) {
	}

	@Override public void drop(DropTargetDropEvent dtde) {
		Transferable transferable = dtde.getTransferable();
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrop(dtde.getDropAction());
			try {
				List<?> transferData = (List<?>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if (transferData.size() > 0) {
					Object transfObj = transferData.get(0);
					if (transfObj instanceof File file) {
						if (file.getName().endsWith(".ogg")) {
							SoundElementDialog.importSound(mcreator, new File[] { file });
						} else if (file.getName().endsWith(".java")) {
							ModelImportActions.importJavaModel(mcreator, file);
						} else if (file.getName().endsWith(".json")) {
							ModelImportActions.importJSONModel(mcreator, file);
						} else if (file.getName().endsWith(".obj")) {
							ModelImportActions.importOBJModel(mcreator, file, null);
						} else if (file.getName().endsWith(".mtl")) {
							ModelImportActions.importOBJModel(mcreator, null, file);
						} else if (file.getName().endsWith(".png")) {
							TextureImportDialogs.importSingleTexture(mcreator, file,
									L10N.t("workspace.textures.select_texture_type"));
						} else if (file.getName().endsWith(".nbt")) {
							StructureImportActions.importStructure(mcreator, new File[] { file });
						} else {
							Toolkit.getDefaultToolkit().beep();
						}
					}
				}
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		} else {
			dtde.rejectDrop();
		}
	}

	private void processDrag(DropTargetDragEvent dtde) {
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			dtde.rejectDrag();
		}
	}

}
