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
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JItemListField<T> extends JPanel implements IValidable {

	private final TechnicalButton add = new TechnicalButton(UIRES.get("18px.add"));
	private final TechnicalButton remove = new TechnicalButton(UIRES.get("18px.remove"));
	private final TechnicalButton removeall = new TechnicalButton(UIRES.get("18px.removeall"));
	private final TechnicalButton addtag = new TechnicalButton(UIRES.get("18px.addtag"));
	private final JToggleButton include = L10N.togglebutton("elementgui.common.include");
	private final JToggleButton exclude = L10N.togglebutton("elementgui.common.exclude");

	private Validator validator = null;
	private Validator.ValidationResult currentValidationResult = null;

	private final DefaultListModel<T> elementsListModel = new DefaultListModel<>();

	protected final JList<T> elementsList = new JList<>(elementsListModel);

	protected final MCreator mcreator;

	private final List<ChangeListener> listeners = new ArrayList<>();

	protected JItemListField(MCreator mcreator) {
		this(mcreator, false);
	}

	protected JItemListField(MCreator mcreator, boolean excludeButton) {
		this(mcreator, excludeButton, false);
	}

	protected JItemListField(MCreator mcreator, boolean excludeButton, boolean allowTags) {
		this.mcreator = mcreator;

		setLayout(new BorderLayout());

		elementsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		elementsList.setVisibleRowCount(1);
		elementsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		elementsList.setCellRenderer(new CustomListCellRenderer());

		add.setOpaque(false);
		add.setMargin(new Insets(0, 0, 0, 0));
		add.setBorder(BorderFactory.createEmptyBorder());
		add.setContentAreaFilled(false);

		remove.setOpaque(false);
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setBorder(BorderFactory.createEmptyBorder());
		remove.setContentAreaFilled(false);

		removeall.setOpaque(false);
		removeall.setMargin(new Insets(0, 0, 0, 0));
		removeall.setBorder(BorderFactory.createEmptyBorder());
		removeall.setContentAreaFilled(false);

		addtag.setOpaque(false);
		addtag.setMargin(new Insets(0, 0, 0, 0));
		addtag.setBorder(BorderFactory.createEmptyBorder());
		addtag.setContentAreaFilled(false);

		add.addActionListener(e -> {
			List<T> list = getElementsToAdd();
			for (T el : list)
				if (!elementsListModel.contains(el))
					elementsListModel.addElement(el);

			if (!list.isEmpty())
				this.listeners.forEach(l -> l.stateChanged(new ChangeEvent(e.getSource())));
		});

		remove.addActionListener(e -> {
			List<T> elements = elementsList.getSelectedValuesList();
			for (var element : elements) {
				if (element != null) {
					elementsListModel.removeElement(element);
					this.listeners.forEach(l -> l.stateChanged(new ChangeEvent(e.getSource())));
				}
			}
		});

		removeall.addActionListener(e -> {
			elementsListModel.removeAllElements();
			this.listeners.forEach(l -> l.stateChanged(new ChangeEvent(e.getSource())));
		});

		addtag.addActionListener(e -> {
			List<T> list = getTagsToAdd();
			for (T el : list)
				if (!elementsListModel.contains(el))
					elementsListModel.addElement(el);
			this.listeners.forEach(l -> l.stateChanged(new ChangeEvent(e.getSource())));
		});

		include.addActionListener(e -> this.listeners.forEach(l -> l.stateChanged(new ChangeEvent(e.getSource()))));
		exclude.addActionListener(e -> this.listeners.forEach(l -> l.stateChanged(new ChangeEvent(e.getSource()))));

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

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.add(add);
		if (allowTags)
			buttonsPanel.add(addtag);
		buttonsPanel.add(remove);
		buttonsPanel.add(removeall);

		JComponent buttons = PanelUtils.totalCenterInPanel(buttonsPanel);
		buttons.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.current().getInterfaceAccentColor()));
		buttons.setOpaque(true);
		buttons.setBackground(Theme.current().getSecondAltBackgroundColor());

		if (excludeButton) {
			include.setSelected(true);
			ButtonGroup group = new ButtonGroup();
			group.add(include);
			group.add(exclude);

			include.setMargin(new Insets(0, 1, 0, 1));
			exclude.setMargin(new Insets(0, 1, 0, 1));

			JComponent incexc = PanelUtils.totalCenterInPanel(PanelUtils.join(include, exclude));
			incexc.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current().getInterfaceAccentColor()));

			add(incexc, BorderLayout.WEST);
		}

		add(pane, BorderLayout.CENTER);
		add(buttons, BorderLayout.EAST);
	}

	protected abstract List<T> getElementsToAdd();

	protected List<T> getTagsToAdd() {
		return List.of();
	}

	@Override public void setEnabled(boolean enabled) {
		add.setEnabled(enabled);
		remove.setEnabled(enabled);
		removeall.setEnabled(enabled);
		addtag.setEnabled(enabled);
		include.setEnabled(enabled);
		exclude.setEnabled(enabled);
	}

	public void addChangeListener(ChangeListener changeListener) {
		this.listeners.add(changeListener);
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

	public boolean isExclusionMode() {
		return exclude.isSelected();
	}

	public void setExclusionMode(boolean isExcluded) {
		exclude.setSelected(isExcluded);
		include.setSelected(!isExcluded);
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
			setBackground(isSelected ? Theme.current().getForegroundColor() : Theme.current().getAltBackgroundColor());
			setForeground(
					isSelected ? Theme.current().getSecondAltBackgroundColor() : Theme.current().getForegroundColor());
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 5, 0, 5, Theme.current().getBackgroundColor()),
					BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);

			setIcon(null);

			if (value instanceof MappableElement mappableElement) {
				Optional<DataListEntry> dataListEntryOpt = mappableElement.getDataListEntry();
				if (dataListEntryOpt.isPresent()) {
					DataListEntry dataListEntry = dataListEntryOpt.get();
					setText(dataListEntry.getReadableName());
					if (dataListEntry.getTexture() != null) {
						setIcon(new ImageIcon(ImageUtils.resizeAA(
								BlockItemIcons.getIconForItem(dataListEntry.getTexture()).getImage(), 18)));
					}
				} else {
					String unmappedValue = mappableElement.getUnmappedValue();
					setText(unmappedValue.replace("CUSTOM:", "").replace("Blocks.", "").replace("Items.", "")
							.replace("#", ""));

					if (unmappedValue.startsWith("CUSTOM:"))
						setIcon(new ImageIcon(ImageUtils.resizeAA(
								MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), unmappedValue).getImage(),
								18)));
					else if (unmappedValue.startsWith("#"))
						setIcon(new ImageIcon(ImageUtils.resizeAA(MCItem.TAG_ICON.getImage(), 18)));
				}

				if (!(mappableElement).canProperlyMap())
					setIcon(UIRES.get("18px.warning"));
			} else if (value instanceof File) {
				setText(FilenameUtilsPatched.removeExtension(((File) value).getName()));
			} else {
				setText(StringUtils.machineToReadableName(value.toString().replace("CUSTOM:", "")));

				if (value.toString().contains("CUSTOM:"))
					setIcon(new ImageIcon(ImageUtils.resizeAA(
							MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), value.toString()).getImage(), 18)));
			}

			return this;
		}
	}

}
