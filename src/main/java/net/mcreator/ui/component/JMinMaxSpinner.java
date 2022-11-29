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

	public JMinMaxSpinner(double minVal, double maxVal, double smin, double smax, double step) {
		super(new GridLayout(1, 2, 5, 0));
		setOpaque(false);

		min = new JSpinner(new SpinnerNumberModel(minVal, smin, smax, step));
		max = new JSpinner(new SpinnerNumberModel(maxVal, smin, smax, step));

		min.addChangeListener(e -> {
			if ((double) min.getValue() > (double) max.getValue())
				max.setValue(min.getValue());
		});

		max.addChangeListener(e -> {
			if ((double) max.getValue() < (double) min.getValue())
				min.setValue(max.getValue());
		});

		add(PanelUtils.westAndCenterElement(L10N.label("minmaxspinner.min"), min, 5, 0));
		add(PanelUtils.westAndCenterElement(L10N.label("minmaxspinner.max"), max, 5, 0));
	}

	@Override public void setEnabled(boolean enabled) {
		min.setEnabled(enabled);
		max.setEnabled(enabled);
	}

	public void addChangeListener(ChangeListener listener) {
		min.addChangeListener(listener);
		max.addChangeListener(listener);
	}

	public double getMinValue() {
		return (double) min.getValue();
	}

	public double getMaxValue() {
		return (double) max.getValue();
	}

	public void setMinValue(double val) {
		min.setValue(val);
	}

	public void setMaxValue(double val) {
		max.setValue(val);
	}

}
