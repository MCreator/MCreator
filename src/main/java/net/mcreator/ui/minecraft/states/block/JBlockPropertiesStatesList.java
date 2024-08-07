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

package net.mcreator.ui.minecraft.states.block;

import net.mcreator.element.types.Block;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JBlockPropertiesStatesList extends JEntriesList {

	private final static List<String> hardCodedProperties = List.of("axis", "face", "facing", "waterlogged");

	private final List<JBlockPropertiesListEntry> propertiesList = new ArrayList<>();

	private final JPanel propertyEntries = new JPanel();

	private final TechnicalButton addProperty = new TechnicalButton(UIRES.get("16px.add"));

	public JBlockPropertiesStatesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(0, 10), gui);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.block.custom_properties.add"));
		addProperty.addActionListener(e -> createPropertiesEntry());

		JPanel basePane = new JPanel(new GridLayout());
		basePane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.custom_properties_states"), 0, 0, basePane.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JScrollPane scrollProperties = new JScrollPane(PanelUtils.pullElementUp(propertyEntries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		scrollProperties.getVerticalScrollBar().setUnitIncrement(15);
		scrollProperties.setOpaque(false);
		scrollProperties.getViewport().setOpaque(false);
		scrollProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.custom_properties.title"), 0, 0, scrollProperties.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		JPanel left = new JPanel(new BorderLayout());
		left.setOpaque(false);
		left.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addProperty, new JEmptyBox(5, 5),
				HelpUtils.helpButton(gui.withEntry("block/block_states"))));
		left.add("Center", scrollProperties);
		basePane.add(left);

		add("Center", basePane);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);

		propertiesList.forEach(e -> e.setEnabled(enabled));

		propertyEntries.setEnabled(enabled);
	}

	private void createPropertiesEntry() {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("elementgui.block.custom_properties.add.title"),
				true);

		VTextField name = new VTextField(20);
		name.setValidator(new UniqueNameValidator(L10N.t("elementgui.block.custom_properties.add.input"), name::getText,
				() -> propertiesList.stream().map(e -> e.getPropertyData().getName()), hardCodedProperties,
				new RegistryNameValidator(name, L10N.t("elementgui.block.custom_properties.add.input"))));
		name.enableRealtimeValidation();
		JComboBox<String> type = new JComboBox<>(new String[] { "Logic", "Integer", "String" });

		JMinMaxSpinner integerBounds = new JMinMaxSpinner(0, 1, 0, Integer.MAX_VALUE, 1);
		JStringListField stringBounds = new JStringListField(mcreator, e -> new RegistryNameValidator(e,
				L10N.t("elementgui.block.custom_properties.add.input"))).setUniqueEntries(true);

		CardLayout cards = new CardLayout();
		JPanel bounds = new JPanel(cards);
		bounds.add("Logic", new JEmptyBox());
		bounds.add("Integer", PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.values"),
				integerBounds));
		bounds.add("String", PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.values"),
				stringBounds));
		type.addActionListener(e -> cards.show(bounds, (String) type.getSelectedItem()));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				String propertyName = "CUSTOM:" + name.getText().toUpperCase(Locale.ROOT);
				if ("Logic".equals(type.getSelectedItem())) {
					addPropertiesEntry(new PropertyData.LogicType(propertyName));
					dialog.setVisible(false);
				} else if ("Integer".equals(type.getSelectedItem())) {
					addPropertiesEntry(new PropertyData.IntegerType(propertyName, integerBounds.getIntMinValue(),
							integerBounds.getIntMaxValue()));
					dialog.setVisible(false);
				} else if ("String".equals(type.getSelectedItem())) {
					List<String> textList = stringBounds.getTextList();
					if (!textList.isEmpty()) {
						addPropertiesEntry(new PropertyData.StringType(propertyName,
								textList.stream().map(s -> s.toUpperCase(Locale.ROOT)).toArray(String[]::new)));
						dialog.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(dialog,
								L10N.t("elementgui.block.custom_properties.add.error_invalid_values"),
								L10N.t("elementgui.block.custom_properties.add.error_invalid_values.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		JPanel main = new JPanel(new GridLayout(0, 1, 0, 2));
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		main.add(PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.name"), name));
		main.add(PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.type"), type));
		main.add(bounds);

		dialog.getContentPane().add("Center", main);
		dialog.getContentPane().add("South", PanelUtils.join(ok, cancel));
		dialog.pack();
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private JBlockPropertiesListEntry addPropertiesEntry(PropertyData<?> data) {
		JBlockPropertiesListEntry pe = new JBlockPropertiesListEntry(this, gui, propertyEntries, propertiesList, data);
		registerEntryUI(pe);
		return pe;
	}

	void removeProperty(JBlockPropertiesListEntry entry) {
		propertiesList.remove(entry);
		propertyEntries.remove(entry);
		propertyEntries.revalidate();
		propertyEntries.repaint();
	}

	public List<Block.PropertyEntry> getProperties() {
		return propertiesList.stream().map(JBlockPropertiesListEntry::getEntry).toList();
	}

	public void setProperties(List<Block.PropertyEntry> properties) {
		properties.forEach(entry -> addPropertiesEntry(entry.property).setEntry(entry));
	}

}