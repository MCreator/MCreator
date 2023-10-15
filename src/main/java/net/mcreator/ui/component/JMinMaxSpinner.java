/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class JMinMaxSpinner extends JPanel {

	private final JSpinner min;
	private final JSpinner max;
	private boolean allowEqualValues = false;

	private boolean directValueSet = false;

	public JMinMaxSpinner(double minVal, double maxVal, double smin, double smax, double step) {
		this(minVal, maxVal, smin, smax, step, L10N.t("minmaxspinner.min"), L10N.t("minmaxspinner.max"));
	}

	public JMinMaxSpinner(int minVal, int maxVal, int smin, int smax, int step) {
		this(minVal, maxVal, smin, smax, step, L10N.t("minmaxspinner.min"), L10N.t("minmaxspinner.max"));
	}

	public JMinMaxSpinner(double minVal, double maxVal, double smin, double smax, double step, String stringMin,
			String stringMax) {
		min = new JSpinner(new SpinnerNumberModel(minVal, smin, smax, step));
		max = new JSpinner(new SpinnerNumberModel(maxVal, smin, smax, step));

		init(stringMin, stringMax);

		// increase visible fraction part if needed
		if (step < 0.00001) {
			((JSpinner.NumberEditor) min.getEditor()).getFormat().setMaximumFractionDigits(6);
			((JSpinner.NumberEditor) max.getEditor()).getFormat().setMaximumFractionDigits(6);
		} else if (step < 0.0001) {
			((JSpinner.NumberEditor) min.getEditor()).getFormat().setMaximumFractionDigits(5);
			((JSpinner.NumberEditor) max.getEditor()).getFormat().setMaximumFractionDigits(5);
		} else if (step < 0.001) {
			((JSpinner.NumberEditor) min.getEditor()).getFormat().setMaximumFractionDigits(4);
			((JSpinner.NumberEditor) max.getEditor()).getFormat().setMaximumFractionDigits(4);
		}
	}

	public JMinMaxSpinner(int minVal, int maxVal, int smin, int smax, int step, String stringMin, String stringMax) {
		min = new JSpinner(new SpinnerNumberModel(minVal, smin, smax, step));
		max = new JSpinner(new SpinnerNumberModel(maxVal, smin, smax, step));

		init(stringMin, stringMax);
	}

	private void init(String stringMin, String stringMax) {
		setLayout(new GridLayout(1, 2, 5, 0));
		setOpaque(false);

		min.addChangeListener(e -> {
			if (directValueSet)
				return;

			Number minVal = getMinNumber(), maxVal = getMaxNumber();
			if (minVal.doubleValue() > maxVal.doubleValue()) {
				max.setValue(minVal); // update maximum to not be lower than minimum
			} else if (!allowEqualValues && (minVal.doubleValue() == maxVal.doubleValue())) {
				min.setValue(min.getPreviousValue()); // if fails, cancel minimum value update
			}
		});
		max.addChangeListener(e -> {
			if (directValueSet)
				return;

			Number minVal = getMinNumber(), maxVal = getMaxNumber();
			if (maxVal.doubleValue() < minVal.doubleValue()) {
				min.setValue(maxVal); // update minimum to not be higher than maximum
			} else if (!allowEqualValues && (maxVal.doubleValue() == minVal.doubleValue())) {
				max.setValue(max.getNextValue()); // if fails, cancel maximum value update
			}
		});

		add(PanelUtils.westAndCenterElement(new JLabel(stringMin), min, 5, 0));
		add(PanelUtils.westAndCenterElement(new JLabel(stringMax), max, 5, 0));
	}

	@Override public void setEnabled(boolean enabled) {
		min.setEnabled(enabled);
		max.setEnabled(enabled);
	}

	public void addChangeListener(ChangeListener listener) {
		min.addChangeListener(listener);
		max.addChangeListener(listener);
	}

	private Number getMinNumber() {
		return ((SpinnerNumberModel) min.getModel()).getNumber();
	}

	private Number getMaxNumber() {
		return ((SpinnerNumberModel) max.getModel()).getNumber();
	}

	public double getMinValue() {
		return (double) min.getValue();
	}

	public double getMaxValue() {
		return (double) max.getValue();
	}

	public int getIntMinValue() {
		return getMinNumber().intValue();
	}

	public int getIntMaxValue() {
		return getMaxNumber().intValue();
	}

	public void setMinValue(Number val) {
		directValueSet = true;
		min.setValue(val);
		directValueSet = false;
	}

	public void setMaxValue(Number val) {
		directValueSet = true;
		max.setValue(val);
		directValueSet = false;
	}

	public void setAllowEqualValues(boolean allowEqualValues) {
		this.allowEqualValues = allowEqualValues;
	}

}
