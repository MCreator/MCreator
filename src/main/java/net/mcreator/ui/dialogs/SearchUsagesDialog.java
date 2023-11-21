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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.elementlist.special.CompactModElementListCellRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Opens a dialog that shows usages of the elements selected by the user before all across the current workspace.
 */
public class SearchUsagesDialog {

	/**
	 * @param mcreator   Workspace window calling this method.
	 * @param queryType  Localized string representing type of elements used by mod elements in the given list.
	 * @param references List of referencing/dependent mod elements.
	 */
	public static void showUsagesDialog(MCreator mcreator, String queryType, Collection<ModElement> references) {
		showDialog(mcreator, queryType, references, false, null);
	}

	/**
	 * @param mcreator   Workspace window calling this method.
	 * @param queryType  Localized string representing type of elements used by mod elements in the given list.
	 * @param references List of referencing/dependent mod elements.
	 * @return Whether the user confirmed deletion of selected elements.
	 */
	public static boolean showDeleteDialog(MCreator mcreator, String queryType, Collection<ModElement> references) {
		return showDialog(mcreator, queryType, references, true, null);
	}

	/**
	 * @param mcreator      Workspace window calling this method.
	 * @param queryType     Localized string representing type of elements used by mod elements in the given list.
	 * @param references    List of referencing/dependent mod elements.
	 * @param messageSuffix Additional information to be displayed by deletion dialog.
	 * @return Whether the user confirmed deletion of selected elements.
	 */
	public static boolean showDeleteDialog(MCreator mcreator, String queryType, Collection<ModElement> references,
			@Nullable String messageSuffix) {
		return showDialog(mcreator, queryType, references, true, messageSuffix);
	}

	/**
	 * @param mcreator          Workspace window calling this method.
	 * @param queryType         Localized string representing type of elements used by mod elements in the given list.
	 * @param references        List of referencing/dependent mod elements.
	 * @param deletionRequested Whether user wants to delete selected elements or just view their usages.
	 * @param messageSuffix     Additional information to be displayed by deletion dialog.
	 * @return Whether elements deletion was requested and the user confirmed deletion.
	 */
	public static boolean showDialog(MCreator mcreator, String queryType, Collection<ModElement> references,
			boolean deletionRequested, @Nullable String messageSuffix) {
		if (references.isEmpty()) { // skip custom dialog if there are no references to show
			if (deletionRequested) {
				String msg = L10N.t("dialog.search_usages.deletion_safe.confirm_msg", queryType);
				int n = JOptionPane.showConfirmDialog(mcreator,
						messageSuffix != null ? msg + "<br><br><small>" + messageSuffix : msg,
						L10N.t("common.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				return n == JOptionPane.YES_OPTION;
			} else {
				JOptionPane.showOptionDialog(mcreator, L10N.t("dialog.search_usages.list.empty", queryType),
						L10N.t("dialog.search_usages.title"), JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, new Object[] { L10N.t("common.close") },
						L10N.t("common.close"));
				return false;
			}
		}

		AtomicBoolean retVal = new AtomicBoolean(false);
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.search_usages.title"), true);

		JList<ModElement> refList = new JList<>(
				references.stream().sorted(Comparator.comparing(ModElement::getName)).toArray(ModElement[]::new));
		refList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		refList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		refList.setSelectedIndex(0);
		refList.setFixedCellHeight(40);
		refList.setFixedCellWidth(200);
		refList.setVisibleRowCount(-1);
		refList.setBackground(Theme.current().getSecondAltBackgroundColor());
		refList.setCellRenderer(new CompactModElementListCellRenderer());
		refList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					edit(mcreator, refList.getModel().getElementAt(refList.locationToIndex(e.getPoint())), dialog);
			}
		});

		JScrollPane sp = new JScrollPane(refList);
		sp.setBackground(Theme.current().getSecondAltBackgroundColor());
		sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JButton edit = L10N.button("dialog.search_usages.open_selected");
		JButton close = deletionRequested ?
				new JButton(UIManager.getString("OptionPane.cancelButtonText")) :
				L10N.button("common.close");

		edit.addActionListener(e -> {
			if (!refList.isSelectionEmpty())
				edit(mcreator, refList.getSelectedValue(), dialog);
		});
		close.addActionListener(e -> dialog.setVisible(false));

		if (deletionRequested) { // if deletion is pending, focus the close button
			dialog.getRootPane().setDefaultButton(close);
		}

		JLabel msgLabel = new JLabel();
		msgLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		dialog.add("North", msgLabel);

		dialog.add("Center", sp);

		if (deletionRequested) {
			String msg = L10N.t("dialog.search_usages.deletion.confirm_msg", queryType);
			if (messageSuffix != null)
				msg += "<br><br><small>" + messageSuffix;
			msgLabel.setText(msg);

			JButton delete = L10N.button("dialog.search_usages.deletion.confirm");
			delete.addActionListener(e -> {
				retVal.set(true);
				dialog.setVisible(false);
			});
			dialog.add("South", PanelUtils.join(edit, delete, close));
		} else {
			msgLabel.setText(L10N.t("dialog.search_usages.list", queryType));

			dialog.add("South", PanelUtils.join(edit, close));
		}

		dialog.setSize(640, 350);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static void edit(MCreator mcreator, ModElement modElement, MCreatorDialog dialog) {
		if (modElement.getGeneratableElement() != null && !modElement.isCodeLocked()) {
			ModElementGUI<?> gui = modElement.getType().getModElementGUI(mcreator, modElement, true);
			if (gui != null) {
				gui.showView();
				dialog.setVisible(false);
			}
		}
	}

}
