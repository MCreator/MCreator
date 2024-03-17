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

package net.mcreator.ui.blockly;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompileNotesPanel extends JPanel {

	private final JLabel compileNotesLabel = L10N.label("blockly.compile_notes", 0);
	private final DefaultListModel<BlocklyCompileNote> compileNotes = new DefaultListModel<>();

	private boolean everUpdated = false;

	public CompileNotesPanel() {
		super(new BorderLayout());
		setOpaque(false);

		JList<BlocklyCompileNote> compileNotesList = new JList<>(compileNotes);
		compileNotesList.setOpaque(false);
		compileNotesList.setCellRenderer(new CompileNotesListRenderer());
		compileNotesList.setBorder(BorderFactory.createEmptyBorder());
		compileNotesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPaneCompileNotes = new JScrollPane(compileNotesList);
		scrollPaneCompileNotes.setOpaque(false);
		scrollPaneCompileNotes.getViewport().setOpaque(false);
		scrollPaneCompileNotes.getVerticalScrollBar().setUnitIncrement(11);
		scrollPaneCompileNotes.getHorizontalScrollBar().setUnitIncrement(11);
		scrollPaneCompileNotes.setBorder(null);
		add("Center", scrollPaneCompileNotes);
		JPanel varHeader2 = new JPanel(new GridLayout(1, 1));
		varHeader2.setOpaque(false);
		varHeader2.setBorder(BorderFactory.createEmptyBorder(1, 0, 3, 0));

		varHeader2.add(ComponentUtils.deriveFont(compileNotesLabel, 12.0f));
		add("North", varHeader2);
		setPreferredSize(new Dimension(0, 50));
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	}

	@Override public void paintComponent(Graphics g) {
		g.setColor(ColorUtils.applyAlpha(Theme.current().getAltBackgroundColor(), 160));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	public void updateCompileNotes(List<BlocklyCompileNote> compileNotesArrayList) {
		synchronized (compileNotes) {
			compileNotes.clear();
			compileNotesArrayList.forEach(compileNotes::addElement);
		}
		compileNotesLabel.setText(L10N.t("blockly.compile_notes", compileNotesArrayList.size()));
		everUpdated = true;
	}

	public List<BlocklyCompileNote> getCompileNotes() {
		List<BlocklyCompileNote> retval = new ArrayList<>();
		if (!everUpdated) {
			retval.add(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.editor_not_ready")));
		} else {
			synchronized (compileNotes) {
				for (int i = 0; i < compileNotes.size(); i++)
					retval.add(compileNotes.get(i));
			}
		}
		return retval;
	}

	static class CompileNotesListRenderer extends JLabel implements ListCellRenderer<BlocklyCompileNote> {
		@Override
		public Component getListCellRendererComponent(JList<? extends BlocklyCompileNote> list,
				BlocklyCompileNote value, int index, boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(Theme.current().getBackgroundColor());
			setForeground(Theme.current().getForegroundColor());
			ComponentUtils.deriveFont(this, 12);
			if (value.type() == BlocklyCompileNote.Type.ERROR) {
				setIcon(UIRES.get("18px.remove"));
			} else if (value.type() == BlocklyCompileNote.Type.WARNING) {
				setIcon(UIRES.get("18px.warning"));
			} else {
				setIcon(UIRES.get("18px.info"));
			}
			setText(value.message());
			return this;
		}
	}

}
