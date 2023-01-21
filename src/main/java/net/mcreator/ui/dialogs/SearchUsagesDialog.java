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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class SearchUsagesDialog {

	public static boolean showModElementUsages(MCreator mcreator, List<ModElement> modElements,
			boolean deletionRequested) {
		return show(mcreator, modElements, L10N.t("dialog.search_usages.type.mod_element"), deletionRequested,
				(e, q) -> {
					String query = new DataListEntry.Custom(q).getName();
					if (e instanceof IDataListEntriesDependent dle) {
						if (dle.getUsedDataListEntries().stream().anyMatch(d -> d.getUnmappedValue().equals(query)))
							return true;
					}
					if (e instanceof IXMLProvider provider)
						return provider.getXML().contains(query);
					return false;
				});
	}

	public static boolean showTextureUsages(MCreator mcreator, List<File> textures, TextureType type,
			boolean deletionRequested) {
		return show(mcreator, textures, L10N.t("dialog.search_usages.type.resource.texture"), deletionRequested,
				(e, q) -> e instanceof IResourcesDependent res && res.getTextures(type).stream().anyMatch(
						t -> e.getModElement().getFolderManager()
								.getTextureFile(FilenameUtilsPatched.removeExtension(t), type).equals(q)));
	}

	public static boolean showModelUsages(MCreator mcreator, List<Model> models, boolean deletionRequested) {
		return show(mcreator, models, L10N.t("dialog.search_usages.type.resource.model"), deletionRequested,
				(e, q) -> e instanceof IResourcesDependent res && res.getModels().contains(q));
	}

	public static boolean showSoundUsages(MCreator mcreator, List<SoundElement> sounds, boolean deletionRequested) {
		return show(mcreator, sounds, L10N.t("dialog.search_usages.type.resource.sound"), deletionRequested,
				(e, q) -> e instanceof IResourcesDependent res && res.getSounds().stream()
						.anyMatch(s -> s.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(q.getName())));
	}

	public static boolean showStructureUsages(MCreator mcreator, List<String> structures, boolean deletionRequested) {
		return show(mcreator, structures, L10N.t("dialog.search_usages.type.resource.structure"), deletionRequested,
				(e, q) -> e instanceof IResourcesDependent res && res.getStructures().contains(q));
	}

	public static boolean showGlobalVariableUsages(MCreator mcreator, String variableName, boolean deletionRequested) {
		return show(mcreator, List.of(variableName), L10N.t("dialog.search_usages.type.global_variable"), deletionRequested,
				(e, q) -> e instanceof IXMLProvider provider && provider.getXML()
						.contains("<field name=\"VAR\">global:" + q + "</field>"));
	}

	public static boolean showTranslationKeyUsages(MCreator mcreator, String translationKey,
			boolean deletionRequested) {
		return show(mcreator, List.of(translationKey), L10N.t("dialog.search_usages.type.translation_key"), deletionRequested,
				(e, q) -> e instanceof IXMLProvider provider && provider.getXML().contains(q)/*
						|| LocalizationUtils.getLocalizationKeys(e).contains(q)*/); // TODO
	}

	public static boolean show(MCreator mcreator, String searchQuery, List<ModElement> references, String queryType,
			boolean deletionRequested) {
		return false; // TODO
	}

	public static <T> boolean show(MCreator mcreator, List<T> elements, String targetType, boolean deletionRequested,
			BiPredicate<GeneratableElement, T> matcher) {
		AtomicBoolean retVal = new AtomicBoolean(false);
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.search_usages.title", targetType), true);

		JButton close = deletionRequested ?
				new JButton(UIManager.getString("OptionPane.cancelButtonText")) :
				L10N.button("dialog.search_usages.close");
		close.addActionListener(e -> dialog.setVisible(false));
		if (deletionRequested)
			dialog.getRootPane().setDefaultButton(close);

		mcreator.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		Set<ModElement> referencingMods = new LinkedHashSet<>();
		for (T t : elements) { // TODO: Move references acquirement BTS
			for (ModElement modEl : mcreator.getWorkspace().getModElements()) {
				if (!modEl.isCodeLocked() && mcreator.getModElementManager().hasModElementGeneratableElement(modEl)) {
					if (matcher.test(modEl.getGeneratableElement(), t))
						referencingMods.add(modEl);
				}
			}
		}

		mcreator.setCursor(Cursor.getDefaultCursor());
		if (referencingMods.isEmpty()) {
			if (deletionRequested) {
				int n = JOptionPane.showConfirmDialog(mcreator,
						L10N.t("dialog.search_usages.deletion_safe.confirm_msg", targetType),
						L10N.t("dialog.search_usages.deletion.title", targetType), JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				if (n == JOptionPane.YES_OPTION)
					return true;
			} else {
				JOptionPane.showOptionDialog(mcreator, L10N.t("dialog.search_usages.list.empty", targetType, ""),
						L10N.t("dialog.search_usages.title", targetType), JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, new Object[] { close.getText() }, close.getText());
				return false;
			}
		}

		JList<ModElement> referencingElements = new JList<>(referencingMods.toArray(ModElement[]::new));
		referencingElements.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		referencingElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		referencingElements.setSelectedIndex(0);
		referencingElements.setFixedCellHeight(40);
		referencingElements.setFixedCellWidth(200);
		referencingElements.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		referencingElements.setCellRenderer(new CompactModElementListCellRenderer());
		referencingElements.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editSelected(mcreator, referencingElements.getModel()
							.getElementAt(referencingElements.locationToIndex(e.getPoint())), dialog);
			}
		});
		if (!deletionRequested) {
			referencingElements.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
						editSelected(mcreator, referencingElements.getSelectedValue(), dialog);
				}
			});
		}

		JScrollPane sp = new JScrollPane(referencingElements);
		sp.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		sp.setPreferredSize(new Dimension(150, 140));

		JButton edit = L10N.button("dialog.search_usages.open_selected");
		edit.addActionListener(e -> {
			if (!referencingElements.isSelectionEmpty())
				editSelected(mcreator, referencingElements.getSelectedValue(), dialog);
		});

		JPanel list = new JPanel(new BorderLayout(10, 10));
		list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		list.add("North", deletionRequested ?
				L10N.label("dialog.search_usages.deletion.confirm_msg", targetType, "") :
				L10N.label("dialog.search_usages.list", targetType, ""));
		list.add("Center", sp);

		if (deletionRequested) {
			JButton delete = L10N.button("dialog.search_usages.deletion.confirm", "");
			delete.addActionListener(e -> {
				retVal.set(true);
				dialog.setVisible(false);
			});

			list.add("South", PanelUtils.join(edit, delete, close));
		} else {
			list.add("South", PanelUtils.join(edit, close));
		}

		dialog.getContentPane().add(list);
		dialog.pack();
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static void editSelected(MCreator mcreator, ModElement element, MCreatorDialog dialog) {
		ModElementGUI<?> gui = element.getType().getModElementGUI(mcreator, element, true);
		if (gui != null) {
			gui.showView();
			dialog.setVisible(false);
		}
	}

	private static class CompactModElementListCellRenderer implements ListCellRenderer<ModElement> {

		@Override
		public Component getListCellRendererComponent(JList<? extends ModElement> list, ModElement value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JLabel label = L10N.label("dialog.search_usages.list.item", value.getName(),
					value.getType().getReadableName());
			label.setOpaque(true);
			label.setIcon(ModElementManager.getModElementIcon(value));
			label.setIconTextGap(10);
			label.setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") :
					(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			label.setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return label;
		}
	}
}
