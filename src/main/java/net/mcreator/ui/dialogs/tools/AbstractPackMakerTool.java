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

package net.mcreator.ui.dialogs.tools;

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.TagElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public abstract class AbstractPackMakerTool extends MCreatorDialog {

	protected ValidationGroup validableElements = new ValidationGroup();

	public AbstractPackMakerTool(MCreator mcreator, String localizationKey, Image icon) {
		super(mcreator, L10N.t("dialog.tools." + localizationKey + "_title"), true);
		this.setLayout(new BorderLayout(10, 10));
		this.setIconImage(icon);
		this.add("North", PanelUtils.centerInPanel(L10N.label("dialog.tools." + localizationKey + "_info")));

		JButton ok = L10N.button("dialog.tools." + localizationKey + "_create");
		ok.addActionListener(e -> {
			if (validableElements.validateIsErrorFree()) {
				this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				generatePack(mcreator);
				mcreator.reloadWorkspaceTabContents();
				this.setCursor(Cursor.getDefaultCursor());
				this.dispose();
			}
		});
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> this.dispose());
		this.add("South", PanelUtils.join(ok, cancel));

		this.getRootPane().setDefaultButton(ok);
	}

	protected abstract void generatePack(MCreator mcreator);

	public static boolean checkIfNamesAvailable(Workspace workspace, String... names) {
		for (String name : names) {
			if (workspace.containsModElement(name)) {
				return false;
			}
		}

		return true;
	}

	public static void addGeneratableElementToWorkspace(Workspace workspace, FolderElement folder,
			GeneratableElement generatableElement) {
		if (!workspace.containsModElement(generatableElement.getModElement().getName())) {
			generatableElement.getModElement().setParentFolder(folder);
			workspace.getModElementManager().storeModElementPicture(generatableElement);
			workspace.getWorkspace().addModElement(generatableElement.getModElement());
			workspace.getGenerator().generateElement(generatableElement);
			workspace.getModElementManager().storeModElement(generatableElement);
		}
	}

	public static void addTagEntries(Workspace workspace, TagType tagType, String tagName, String... entries) {
		if (workspace.getGeneratorStats().getBaseCoverageInfo().get("tags") == GeneratorStats.CoverageStatus.FULL) {
			// Create tag if it doesn't exist yet
			TagElement tag = new TagElement(tagType, tagName);
			if (!workspace.getTagElements().containsKey(tag)) {
				workspace.addTagElement(tag);
			}

			// Add entries if they're not already contained in the tag (in normal or managed form)
			ArrayList<String> tagEntries = workspace.getTagElements().get(tag);
			for (String entry : entries) {
				if (!tagEntries.contains(entry) && !tagEntries.contains(TagElement.makeEntryManaged(entry))) {
					tagEntries.add(entry);
				}
			}
		}
	}
}
