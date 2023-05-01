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

package net.mcreator.ui.component;

import net.mcreator.ui.dialogs.ListEditorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JStringListField extends JPanel {

	private final List<String> textList = new ArrayList<>();
	private int index = -1;

	private final JButton edit = new JButton(UIRES.get("16px.edit.gif")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};
	private final JButton back = new JButton(UIRES.get("previous")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};
	private final JButton forward = new JButton(UIRES.get("next")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};
	private boolean joinEntries = false, uniqueEntries = false;

	private final JLabel label = new JLabel() {
		@Override public String getToolTipText() {
			return joinEntries || index < 0 ? null : L10N.t("components.string_list.item", index + 1);
		}
	};

	public JStringListField(Window parent, @Nullable Function<VTextField, Validator> validator) {
		super(new FlowLayout(FlowLayout.CENTER, 7, 5));
		setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		label.setOpaque(false);
		label.setFont(label.getFont().deriveFont(16f));
		ToolTipManager.sharedInstance().registerComponent(label);

		back.setMargin(new Insets(0, 0, 0, 0));
		back.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		back.setFocusPainted(false);
		back.setContentAreaFilled(false);
		back.setCursor(new Cursor(Cursor.HAND_CURSOR));
		back.addActionListener(e -> {
			if (!joinEntries) {
				if (index <= 0)
					index = textList.size();
				index--;
				refreshVisibleText();
			}
		});

		forward.setMargin(new Insets(0, 0, 0, 0));
		forward.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		forward.setFocusPainted(false);
		forward.setContentAreaFilled(false);
		forward.setCursor(new Cursor(Cursor.HAND_CURSOR));
		forward.addActionListener(e -> {
			if (!joinEntries) {
				index++;
				if (index >= textList.size())
					index = 0;
				refreshVisibleText();
			}
		});

		JScrollPane scrollPane = new JScrollPane(label);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setPreferredSize(new Dimension(200, 30));

		JPanel listPane = new JPanel(new BorderLayout(5, 5));
		listPane.setOpaque(false);
		listPane.add("West", back);
		listPane.add("Center", scrollPane);
		listPane.add("East", forward);
		add(listPane);

		edit.setOpaque(false);
		edit.setMargin(new Insets(0, 0, 0, 0));
		edit.setBorder(BorderFactory.createEmptyBorder());
		edit.setContentAreaFilled(false);
		edit.setToolTipText(L10N.t("components.string_list.edit"));
		edit.addActionListener(e -> {
			List<String> newList = ListEditorDialog.open(parent, textList, validator, uniqueEntries);
			if (newList != null)
				setTextList(newList);
		});

		JButton copy = new JButton(UIRES.get("16px.copyclipboard")) {
			@Override public String getName() {
				return "TechnicalButton";
			}
		};
		copy.setOpaque(false);
		copy.setMargin(new Insets(0, 0, 0, 0));
		copy.setBorder(BorderFactory.createEmptyBorder());
		copy.setContentAreaFilled(false);
		copy.setToolTipText(L10N.t("components.string_list.copy"));
		copy.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(String.join(System.lineSeparator(), textList)), null));

		JPanel controls = new JPanel();
		controls.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		controls.add(edit);
		controls.add(copy);
		add(controls);
	}

	@Override public void setEnabled(boolean b) {
		super.setEnabled(b);
		edit.setEnabled(b);
	}

	public JStringListField setJoinEntries(boolean joinEntries) {
		this.joinEntries = joinEntries;
		back.setVisible(!joinEntries);
		forward.setVisible(!joinEntries);
		return this;
	}

	public JStringListField setUniqueEntries(boolean uniqueEntries) {
		this.uniqueEntries = uniqueEntries;
		return this;
	}

	public List<String> getTextList() {
		return textList;
	}

	public void setTextList(List<String> textList) {
		if (this.textList.isEmpty() != textList.isEmpty())
			index = textList.isEmpty() ? -1 : 0;
		else if (!textList.isEmpty())
			index = Math.min(index, textList.size() - 1);
		this.textList.clear();
		this.textList.addAll(textList);
		refreshVisibleText();
	}

	private void refreshVisibleText() {
		if (joinEntries)
			label.setText(String.join(", ", textList));
		else
			label.setText(index >= 0 && index < textList.size() ? textList.get(index) : "");
	}
}
