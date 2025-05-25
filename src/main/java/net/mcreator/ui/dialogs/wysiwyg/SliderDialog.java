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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;

import javax.annotation.Nullable;
import javax.swing.*;

public class SliderDialog extends AbstractWYSIWYGDialog<Slider> {

	public SliderDialog(WYSIWYGEditor editor, @Nullable Slider slider) {
		super(editor, slider);
		setModal(true);
		setSize(460, 390);
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
		JSpinner minValueSpinner = new JSpinner(new SpinnerNumberModel(0, -10000.0, 10000.0, 1));
		JSpinner maxValueSpinner = new JSpinner(new SpinnerNumberModel(10, -10000.0, 10000.0, 1));
		JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(5, -10000.0, 10000.0, 1));
		JSpinner stepSpinner = new JSpinner(new SpinnerNumberModel(1, -10000.0, 10000.0, 1));
		JTextField sliderSuffix = new JTextField(8);

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		if (slider == null)
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.slider_change_width")));
		else
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.slider_resize")));

		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_name"), sliderMachineName));
		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_prefix"), sliderPrefix));
		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_min"), minValueSpinner));
		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_max"), maxValueSpinner));
		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_value"), valueSpinner));
		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_step"), stepSpinner));
		options.add(PanelUtils.join(L10N.label("dialog.gui.slider_suffix"), sliderSuffix));

		ProcedureSelector whenSliderMoves = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/when_slider_moves"),
				editor.mcreator, L10N.t("dialog.gui.when_slider_moves"), ProcedureSelector.Side.CLIENT, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/value:number"));
		whenSliderMoves.refreshList();

		options.add(PanelUtils.centerInPanel(whenSliderMoves));

		add("Center", new JScrollPane(PanelUtils.centerInPanel(options)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (slider != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			sliderMachineName.setText(slider.name);
			sliderPrefix.setText(slider.prefix);
			sliderSuffix.setText(slider.suffix);
			minValueSpinner.setValue(slider.min);
			maxValueSpinner.setValue(slider.max);
			valueSpinner.setValue(slider.value);
			stepSpinner.setValue(slider.step);

			whenSliderMoves.setSelectedProcedure(slider.whenSliderMoves);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			if (sliderMachineName.getValidationStatus().getValidationResultType()
					!= Validator.ValidationResultType.ERROR) {
				setVisible(false);
				String sliderName = sliderMachineName.getText();
				if (!sliderName.isEmpty()) {
					if (slider == null) {
						String fullText = sliderPrefix.getText() + valueSpinner.getValue() + sliderSuffix.getText();
						int textWidth = (int) (WYSIWYG.fontMC.getStringBounds(fullText, WYSIWYG.frc).getWidth());

						Slider component = new Slider(0, 0, textWidth + 25, 20, sliderName,
								(Double) minValueSpinner.getValue(), (Double) maxValueSpinner.getValue(),
								(Double) valueSpinner.getValue(), (Double) stepSpinner.getValue(),
								sliderPrefix.getText(), sliderSuffix.getText(), whenSliderMoves.getSelectedProcedure());

						setEditingComponent(component);
						editor.editor.addComponent(component);
						editor.list.setSelectedValue(component, true);
						editor.editor.moveMode();
					} else {
						int idx = editor.components.indexOf(slider);
						editor.components.remove(slider);
						Slider sliderNew = new Slider(slider.getX(), slider.getY(), slider.width, slider.height,
								slider.name, (Double) minValueSpinner.getValue(), (Double) maxValueSpinner.getValue(),
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
