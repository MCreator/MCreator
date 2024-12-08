/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.variants.resourcepackmaker;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.recourcepack.ResourcePackEditor;
import net.mcreator.util.ColorUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ResourcePackMakerWorkspacePanel extends JPanel {

	public final JTextField search;

	public final ResourcePackEditor resourcePackEditor;

	ResourcePackMakerWorkspacePanel(MCreator mcreator) {
		super(new BorderLayout(3, 3));
		setOpaque(false);

		search = new JTextField(34) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getText().isEmpty()) {
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(new Color(120, 120, 120));
					g.drawString(L10N.t("workspace.elements.list.search_list"), 8, 19);
				}
			}
		};
		search.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) {
				super.focusGained(e);
				if (e.getCause() == FocusEvent.Cause.MOUSE_EVENT) {
					search.setText(null);
				}
			}
		});

		search.setToolTipText(L10N.t("workspace.elements.list.search.tooltip"));

		ComponentUtils.deriveFont(search, 14);
		search.setOpaque(false);

		search.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void removeUpdate(DocumentEvent arg0) {
				resourcePackEditor.refilterElements();
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				resourcePackEditor.refilterElements();
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				resourcePackEditor.refilterElements();
			}
		});

		search.setBackground(ColorUtils.applyAlpha(search.getBackground(), 150));
		search.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

		JPanel leftPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftPan.setOpaque(false);
		leftPan.add(search);

		add("North", leftPan);

		resourcePackEditor = new ResourcePackEditor(mcreator, () -> search.getText().trim());

		add("Center", resourcePackEditor);
	}

	public void reloadElements() {
		resourcePackEditor.reloadElements();
	}

	public ResourcePackEditor getResourcePackEditor() {
		return resourcePackEditor;
	}

}

