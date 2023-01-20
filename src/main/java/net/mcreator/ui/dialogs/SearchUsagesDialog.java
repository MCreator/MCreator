/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.interfaces.IDataListEntriesDependent;
import net.mcreator.element.types.interfaces.IResourcesDependent;
import net.mcreator.element.types.interfaces.IXMLProvider;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SearchUsagesDialog {

	public static void searchModElementUsages(MCreator mcreator, ModElement modElement) {
		String searchQuery = new DataListEntry.Custom(modElement).getName();
		show(mcreator, modElement.getName(), L10N.t("dialog.search_usages.types.mod_element"),
				e -> e instanceof IDataListEntriesDependent dle && dle.getUsedDataListEntries().stream()
						.anyMatch(d -> d.getUnmappedValue().equals(searchQuery))
						|| e instanceof IXMLProvider provider && provider.getXML().contains(searchQuery));
	}

	public static void searchTextureUsages(MCreator mcreator, File texture) {
		show(mcreator, mcreator.getFolderManager().getPathInWorkspace(texture),
				L10N.t("dialog.search_usages.types.resource.texture"),
				e -> e instanceof IResourcesDependent res && Stream.of(TextureType.getTypes(true)).anyMatch(type -> {
					for (String t : res.getTextures(type)) {
						if (e.getModElement().getFolderManager()
								.getTextureFile(FilenameUtilsPatched.removeExtension(t), type).equals(texture))
							return true;
					}
					return false;
				}));
	}

	public static void searchModelUsages(MCreator mcreator, Model model) {
		show(mcreator, model.getReadableName(), L10N.t("dialog.search_usages.types.resource.model"),
				e -> e instanceof IResourcesDependent res && res.getModels().contains(model));
	}

	public static void searchSoundUsages(MCreator mcreator, SoundElement sound) {
		show(mcreator, sound.getName(), L10N.t("dialog.search_usages.types.resource.sound"),
				e -> e instanceof IResourcesDependent res && res.getSounds().stream()
						.anyMatch(s -> s.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(sound.getName())));
	}

	public static void searchStructureUsages(MCreator mcreator, String structure) {
		show(mcreator, structure, L10N.t("dialog.search_usages.types.resource.structure"),
				e -> e instanceof IResourcesDependent res && res.getStructures().contains(structure));
	}

	public static void searchGlobalVariableUsages(MCreator mcreator, String variableName) {
		String searchQuery = "<field name=\"VAR\">global:" + variableName + "</field>";
		show(mcreator, variableName, L10N.t("dialog.search_usages.types.global_variable"),
				e -> e instanceof IXMLProvider provider && provider.getXML().contains(searchQuery));
	}

	public static void searchTranslationKeyUsages(MCreator mcreator, String translationKey) {
		show(mcreator, translationKey, L10N.t("dialog.search_usages.types.translation_key"),
				e -> e instanceof IXMLProvider provider && provider.getXML().contains(translationKey)/*
						|| mcreator.getGenerator().getLocalizationKeys(e).contains(translationKey)*/); // TODO
	}

	public static void show(MCreator mcreator, String query, String targetType, Predicate<GeneratableElement> matcher) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.search_usages.title", query), true);
		JButton close = L10N.button("dialog.search_usages.close");
		close.addActionListener(e -> dialog.setVisible(false));

		mcreator.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		Set<ModElement> referencingMods = new HashSet<>();
		for (ModElement modEl : mcreator.getWorkspace().getModElements()) {
			if (!modEl.isCodeLocked() && mcreator.getModElementManager().hasModElementGeneratableElement(modEl)) {
				if (matcher.test(modEl.getGeneratableElement()))
					referencingMods.add(modEl);
			}
		}

		mcreator.setCursor(Cursor.getDefaultCursor());
		if (referencingMods.isEmpty()) {
			JOptionPane.showOptionDialog(mcreator,
					L10N.t("dialog.search_usages.list.empty", targetType, query),
					L10N.t("dialog.search_usages.title", query), JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, new Object[] { close.getText() }, close.getText());
			return;
		}

		JList<ModElement> referencingElements = new JList<>(referencingMods.toArray(ModElement[]::new));
		referencingElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		referencingElements.setSelectedIndex(0);
		referencingElements.setFixedCellHeight(40);
		referencingElements.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		referencingElements.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
			JLabel label = L10N.label("dialog.search_usages.list.item", value.getName(),
					value.getType().getReadableName());
			label.setOpaque(true);
			label.setIcon(ModElementManager.getModElementIcon(value/*.getType().getIcon()*/));
			label.setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.GRAY_COLOR") :
					(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			label.setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return label;
		});

		referencingElements.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editSelected(mcreator, referencingElements.getSelectedValue(), dialog);
			}
		});
		referencingElements.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					editSelected(mcreator, referencingElements.getSelectedValue(), dialog);
			}
		});

		JScrollPane sp = new JScrollPane(referencingElements);
		sp.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		sp.setPreferredSize(new Dimension(150, 140));

		JButton edit = L10N.button("dialog.search_usages.open_selected");
		edit.addActionListener(e -> {
			if (edit.isEnabled() && !referencingElements.isSelectionEmpty())
				editSelected(mcreator, referencingElements.getSelectedValue(), dialog);
		});

		JPanel list = new JPanel(new BorderLayout(10, 10));
		list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		list.add("North", L10N.label("dialog.search_usages.list", targetType, query));
		list.add("Center", sp);
		list.add("South", PanelUtils.join(edit, close));

		dialog.getContentPane().add(list);
		dialog.pack();
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private static void editSelected(MCreator mcreator, ModElement element, MCreatorDialog dialog) {
		ModElementGUI<?> gui = element.getType().getModElementGUI(mcreator, element, true);
		if (gui != null) {
			gui.showView();
			dialog.setVisible(false);
		}
	}
}
