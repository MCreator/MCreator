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

package net.mcreator.ui.views.editor.image.tool.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class JSlidingSpinner extends JPanel {
	boolean spinning = false, sliding = false;
	private transient ChangeEvent changeEvent;
	private double value = 0;
	private final JSpinner spinner;
	private final JLabel propertyLabel;

	public JSlidingSpinner(String propertyName) {
		this(propertyName, 100, 0, 100, 0.1);
	}

	public JSlidingSpinner(String propertyName, int def, int min, int max, double step) {
		super(new BorderLayout());
		this.propertyLabel = new JLabel(propertyName);
		this.value = def;

		spinner = new JSpinner(new SpinnerNumberModel(def, min, max, step));
		JSlider slider = new JSlider();

		slider.setOpaque(false);
		spinner.setOpaque(false);

		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setValue(def);

		slider.setPreferredSize(new Dimension(0, 0));

		spinner.addChangeListener(changeEvent -> {
			spinning = true;
			if (!sliding) {
				value = (Double) spinner.getValue();
				slider.setValue((int) Math.round(value));
				fireStateChanged();
			}
			spinning = false;
		});

		slider.addChangeListener(changeEvent -> {
			sliding = true;
			if (!spinning) {
				value = slider.getValue();
				spinner.setValue(value);
				fireStateChanged();
			}
			sliding = false;
		});

		propertyLabel.setBorder(new EmptyBorder(3, 0, 3, 0));
		slider.setBorder(new EmptyBorder(0, 0, 0, 5));

		add(propertyLabel, BorderLayout.NORTH);
		add(slider, BorderLayout.CENTER);
		add(spinner, BorderLayout.EAST);

		setOpaque(false);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		spinner.setValue(value);
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	public JLabel getLabel() {
		return propertyLabel;
	}
}
