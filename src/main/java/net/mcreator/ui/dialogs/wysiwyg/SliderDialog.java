/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs.wysiwyg;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.Slider;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class SliderDialog extends AbstractWYSIWYGDialog<Slider> {

	public SliderDialog(WYSIWYGEditor editor, @Nullable Slider slider) {
		super(editor, slider);
		setModal(true);
		setSize(460, 360);
		setLocationRelativeTo(editor.mcreator);
		setTitle(L10N.t("dialog.gui.slider_add_title"));

		VTextField sliderMachineName = new VTextField(15);
		UniqueNameValidator validator = new UniqueNameValidator(L10N.t("dialog.gui.slider_name_validator"),
				sliderMachineName::getText, () -> editor.getComponentList().stream().map(GUIComponent::getName),
				new JavaMemberNameValidator(sliderMachineName, false));
		validator.setIsPresentOnList(slider != null);
		sliderMachineName.setValidator(validator);
		sliderMachineName.enableRealtimeValidation();

		JTextField sliderPrefix = new JTextField(8);
		JMinMaxSpinner rangeSpinner = new JMinMaxSpinner(0.0, 10.0, -Double.MAX_VALUE, Double.MAX_VALUE, 1.0);
		rangeSpinner.setPreferredSize(new Dimension(200, 20));
		JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(5, -10000.0, 10000.0, 1));
		valueSpinner.addChangeListener(e -> {
			double d = (double) valueSpinner.getValue();
			if (d < rangeSpinner.getMinValue())
				valueSpinner.setValue(rangeSpinner.getMinValue());
			else if (d > rangeSpinner.getMaxValue())
				valueSpinner.setValue(rangeSpinner.getMaxValue());
		});
		JSpinner stepSpinner = new JSpinner(new SpinnerNumberModel(1, -10000.0, 10000.0, 1));
		stepSpinner.addChangeListener(e -> {
			double d = (double) stepSpinner.getValue();
			if ((rangeSpinner.getMaxValue() - rangeSpinner.getMinValue()) < d)
				stepSpinner.setValue(rangeSpinner.getMaxValue() - rangeSpinner.getMinValue());
		});

		rangeSpinner.addChangeListener(e -> {
			double value = (double) valueSpinner.getValue();
			if (value < rangeSpinner.getMinValue())
				valueSpinner.setValue(rangeSpinner.getMinValue());
			else if (value > rangeSpinner.getMaxValue())
				valueSpinner.setValue(rangeSpinner.getMaxValue());

			double step = (double) stepSpinner.getValue();
			if ((rangeSpinner.getMaxValue() - rangeSpinner.getMinValue()) < step)
				stepSpinner.setValue(rangeSpinner.getMaxValue() - rangeSpinner.getMinValue());
		});
		JTextField sliderSuffix = new JTextField(8);

		JPanel grid = new JPanel(new GridLayout(-1, 2, 5, 2));

		if (slider == null)
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.slider_change_width")));
		else
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.slider_resize")));

		sliderMachineName.setPreferredSize(new Dimension(200, 28));

		grid.add(L10N.label("dialog.gui.slider_name"));
		grid.add(sliderMachineName);
		grid.add(L10N.label("dialog.gui.slider_range"));
		grid.add(rangeSpinner);
		grid.add(L10N.label("dialog.gui.slider_value"));
		grid.add(valueSpinner);
		grid.add(L10N.label("dialog.gui.slider_step"));
		grid.add(stepSpinner);
		grid.add(L10N.label("dialog.gui.slider_prefix"));
		grid.add(sliderPrefix);
		grid.add(L10N.label("dialog.gui.slider_suffix"));
		grid.add(sliderSuffix);

		AbstractProcedureSelector.ReloadContext context = AbstractProcedureSelector.ReloadContext.create(
				editor.mcreator.getWorkspace());

		ProcedureSelector whenSliderMoves = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/when_slider_moves"),
				editor.mcreator, L10N.t("dialog.gui.when_slider_moves"), ProcedureSelector.Side.CLIENT, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/value:number"));
		whenSliderMoves.refreshList(context);

		add("Center",
				PanelUtils.northAndCenterElement(PanelUtils.join(grid), PanelUtils.centerInPanel(whenSliderMoves), 5,
						5));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (slider != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			sliderMachineName.setText(slider.name);
			sliderPrefix.setText(slider.prefix);
			sliderSuffix.setText(slider.suffix);
			rangeSpinner.setMinValue(slider.min);
			rangeSpinner.setMaxValue(slider.max);
			valueSpinner.setValue(slider.value);
			stepSpinner.setValue(slider.step);

			whenSliderMoves.setSelectedProcedure(slider.whenSliderMoves);
		}

		cancel.addActionListener(arg01 -> dispose());
		ok.addActionListener(arg01 -> {
			if (sliderMachineName.getValidationStatus().type() != ValidationResult.Type.ERROR) {
				dispose();
				String sliderName = sliderMachineName.getText();
				if (!sliderName.isEmpty()) {
					if (slider == null) {
						String fullText = sliderPrefix.getText() + rangeSpinner.getMaxValue() + sliderSuffix.getText();
						int textWidth = (int) (WYSIWYG.fontMC.getStringBounds(fullText, WYSIWYG.frc).getWidth());

						Slider component = new Slider(0, 0, textWidth + 25, 20, sliderName, rangeSpinner.getMinValue(),
								rangeSpinner.getMaxValue(), (Double) valueSpinner.getValue(),
								(Double) stepSpinner.getValue(), sliderPrefix.getText(), sliderSuffix.getText(),
								whenSliderMoves.getSelectedProcedure());

						setEditingComponent(component);
						editor.editor.addComponent(component);
						editor.list.setSelectedValue(component, true);
						editor.editor.moveMode();
					} else {
						int idx = editor.components.indexOf(slider);
						editor.components.remove(slider);
						Slider sliderNew = new Slider(slider.getX(), slider.getY(), slider.width, slider.height,
								slider.name, rangeSpinner.getMinValue(), rangeSpinner.getMaxValue(),
								(Double) valueSpinner.getValue(), (Double) stepSpinner.getValue(),
								sliderPrefix.getText(), sliderSuffix.getText(), whenSliderMoves.getSelectedProcedure());
						editor.components.add(idx, sliderNew);
						setEditingComponent(sliderNew);
					}
				}
			}
		});

		setVisible(true);
	}

}
