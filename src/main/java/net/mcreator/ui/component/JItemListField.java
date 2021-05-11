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

package net.mcreator.ui.component;

import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import org.apache.commons.io.FilenameUtils;
import javax.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class JItemListField<T> extends JPanel implements IValidable {

	private final JButton bt = new JButton(UIRES.get("18px.add"));
	private final JButton bt2 = new JButton(UIRES.get("18px.remove"));
	private final JButton bt3 = new JButton(UIRES.get("18px.removeall"));

	private Validator validator = null;
	private Validator.ValidationResult currentValidationResult = null;

	private final DefaultListModel<T> elementsListModel = new DefaultListModel<>();

	protected JItemListField() {
		JList<T> elementsList = new JList<>(elementsListModel);
		elementsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		elementsList.setVisibleRowCount(1);
		elementsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		elementsList.setCellRenderer(new CustomListCellRenderer());

		bt.setOpaque(false);
		bt.setMargin(new Insets(0, 0, 0, 0));
		bt.setBorder(BorderFactory.createEmptyBorder());
		bt.setContentAreaFilled(false);

		bt2.setOpaque(false);
		bt2.setMargin(new Insets(0, 0, 0, 0));
		bt2.setBorder(BorderFactory.createEmptyBorder());
		bt2.setContentAreaFilled(false);

		bt3.setOpaque(false);
		bt3.setMargin(new Insets(0, 0, 0, 0));
		bt3.setBorder(BorderFactory.createEmptyBorder());
		bt3.setContentAreaFilled(false);

		bt.addActionListener(e -> {
			List<T> list = getElementsToAdd();
			for (T el : list)
				elementsListModel.addElement(el);

		});

		bt2.addActionListener(e -> {
			T element = elementsList.getSelectedValue();
			if (element != null) {
				elementsListModel.removeElement(element);
			}
		});

		bt3.addActionListener(e -> elementsListModel.removeAllElements());

		JScrollPane pane = new JScrollPane(PanelUtils.totalCenterInPanel(elementsList));
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane.setWheelScrollingEnabled(false);
		pane.addMouseWheelListener(new MouseAdapter() {
			@Override public void mouseWheelMoved(MouseWheelEvent evt) {
				int amount = evt.getScrollAmount();
				if (evt.getWheelRotation() == 1) {
					int value = pane.getHorizontalScrollBar().getValue()
							+ pane.getHorizontalScrollBar().getBlockIncrement() * amount;
					if (value > pane.getHorizontalScrollBar().getMaximum()) {
						value = pane.getHorizontalScrollBar().getMaximum();
					}
					pane.getHorizontalScrollBar().setValue(value);
				} else if (evt.getWheelRotation() == -1) {
					int value = pane.getHorizontalScrollBar().getValue()
							- pane.getHorizontalScrollBar().getBlockIncrement() * amount;
					if (value < 0) {
						value = 0;
					}
					pane.getHorizontalScrollBar().setValue(value);
				}
			}
		});

		pane.setPreferredSize(getPreferredSize());

		JComponent buttons = PanelUtils.totalCenterInPanel(PanelUtils.join(bt, bt2, bt3));
		buttons.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		buttons.setOpaque(true);
		buttons.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		add(buttons, BorderLayout.EAST);
	}

	protected abstract List<T> getElementsToAdd();

	@Override public void setEnabled(boolean enabled) {
		bt.setEnabled(enabled);
		bt2.setEnabled(enabled);
		bt3.setEnabled(enabled);
	}

	public List<T> getListElements() {
		List<T> retval = new ArrayList<>();
		for (int i = 0; i < elementsListModel.size(); i++) {
			T element = elementsListModel.get(i);
			if (element instanceof MappableElement)
				if (!((MappableElement) element).canProperlyMap())
					continue;
			retval.add(elementsListModel.get(i));
		}
		return retval;
	}

	public void setListElements(@Nullable List<T> elements) {
		if (elements == null)
			return;

		elementsListModel.removeAllElements();
		for (T el : elements)
			elementsListModel.addElement(el);
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		if (currentValidationResult != null) {
			if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.WARNING) {
				g.drawImage(UIRES.get("18px.warning").getImage(), 0, 0, 13, 13, null);
				g.setColor(new Color(238, 229, 113));
			} else if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.ERROR) {
				g.drawImage(UIRES.get("18px.remove").getImage(), 0, 0, 13, 13, null);
				g.setColor(new Color(204, 108, 108));
			} else if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.PASSED) {
				g.drawImage(UIRES.get("18px.ok").getImage(), 0, 0, 13, 13, null);
				g.setColor(new Color(79, 192, 121));
			}

			if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.ERROR
					|| currentValidationResult.getValidationResultType() == Validator.ValidationResultType.WARNING) {
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		}
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		Validator.ValidationResult validationResult = validator == null ? null : validator.validateIfEnabled(this);

		this.currentValidationResult = validationResult;

		//repaint as new validation status might have to be rendered
		repaint();

		return validationResult;
	}

	@Override public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override public Validator getValidator() {
		return validator;
	}

	class CustomListCellRenderer extends JLabel implements ListCellRenderer<T> {

		@Override
		public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
				boolean cellHasFocus) {
			setOpaque(true);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") :
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 5, 0, 5, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")),
					BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);

			setIcon(null);

			if (value instanceof MappableElement) {
				setText(((MappableElement) value).getMappedValueOrFallbackToUnmapped());
				if (!((MappableElement) value).canProperlyMap()) {
					setIcon(UIRES.get("18px.warning"));
				}
			} else if (value instanceof File) {
				setText(FilenameUtils.removeExtension(((File) value).getName()));
			} else {
				setText(value.toString());
			}

			return this;
		}
	}

}
