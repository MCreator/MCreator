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
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchUsagesDialog {

	/**
	 * Opens a dialog that shows usages of the elements selected by the user before all across the current workspace.
	 *
	 * @param mcreator          Workspace window calling this method.
	 * @param queryType         Localized string representing type of elements used by mod elements in the given list.
	 * @param references        List of referencing/dependent mod elements.
	 * @param deletionRequested Whether user wants to delete selected elements or just view their usages.
	 * @return Whether elements deletion was requested and the user confirmed deletion.
	 */
	public static boolean show(MCreator mcreator, String queryType, List<ModElement> references,
			boolean deletionRequested) {
		if (references.isEmpty()) { // skip custom dialog if there are no references to show
			if (deletionRequested) {
				int n = JOptionPane.showConfirmDialog(mcreator,
						L10N.t("dialog.search_usages.deletion_safe.confirm_msg", queryType),
						L10N.t("common.confirmation", queryType), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
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
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.search_usages.title", queryType), true);

		JList<ModElement> refList = new JList<>(references.toArray(ModElement[]::new));
		refList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		refList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		refList.setSelectedIndex(0);
		refList.setFixedCellHeight(40);
		refList.setFixedCellWidth(200);
		refList.setVisibleRowCount(-1);
		refList.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		refList.setCellRenderer(new CompactModElementListCellRenderer());
		refList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					edit(mcreator, refList.getModel().getElementAt(refList.locationToIndex(e.getPoint())), dialog);
			}
		});

		JScrollPane sp = new JScrollPane(refList);
		sp.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		sp.setPreferredSize(new Dimension(150, 140));

		JButton edit = L10N.button("dialog.search_usages.open_selected");
		JButton close = deletionRequested ?
				new JButton(UIManager.getString("OptionPane.cancelButtonText")) :
				L10N.button("common.close");

		edit.addActionListener(e -> {
			if (!refList.isSelectionEmpty())
				edit(mcreator, refList.getSelectedValue(), dialog);
		});
		close.addActionListener(e -> dialog.setVisible(false));

		if (deletionRequested) { // if deletion is pending, put focus on the close button
			dialog.getRootPane().setDefaultButton(close);
		} else { // otherwise focus the references list
			refList.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
						edit(mcreator, refList.getSelectedValue(), dialog);
				}
			});
		}

		JPanel list = new JPanel(new BorderLayout(10, 10));
		list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		list.add("North", deletionRequested ?
				L10N.label("dialog.search_usages.deletion.confirm_msg", queryType) :
				L10N.label("dialog.search_usages.list", queryType));
		list.add("Center", sp);

		if (deletionRequested) { // don't forget the delete button
			JButton delete = L10N.button("dialog.search_usages.deletion.confirm");
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

	private static void edit(MCreator mcreator, ModElement element, MCreatorDialog dialog) {
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
			label.setIcon(ImageUtils.fit(ModElementManager.getModElementIcon(value).getImage(), 32));
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
