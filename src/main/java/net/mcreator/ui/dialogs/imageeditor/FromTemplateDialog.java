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

package net.mcreator.ui.dialogs.imageeditor;

import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.dialogs.TextureSelectorDialog;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.stream.Collectors;

public class FromTemplateDialog extends MCreatorDialog {

	private static final Logger LOG = LogManager.getLogger("From Template Dialog");

	private static final String[] templateList = new String[] { "Random", "Sword", "Pickaxe", "Axe", "Shovel", "Hoe",
			"Shears", "Music disc", "Drinkable potion", "Splash potion", "Lingering potion", "Ore" };

	private static final Color[] presetColors = new Color[] { Color.red, Color.green, Color.blue,
			(Color) UIManager.get("MCreatorLAF.MAIN_TINT"), Color.magenta, Color.cyan, new Color(244, 67, 54),
			new Color(233, 30, 99), new Color(255, 235, 59), new Color(205, 220, 57), new Color(255, 87, 34),
			new Color(158, 158, 158), new Color(255, 152, 0), new Color(0, 188, 212), new Color(139, 195, 74) };

	private final JComboBox<ResourcePointer> cbs = new JComboBox<>();
	private final JComboBox<ResourcePointer> cbs2 = new JComboBox<>();
	private final JComboBox<ResourcePointer> cbs3 = new JComboBox<>();
	private final JComboBox<ResourcePointer> cbs4 = new JComboBox<>();
	private final JCheckBox type1 = L10N.checkbox("dialog.imageeditor.saturation_brightness_lock");
	private final JCheckBox type2 = L10N.checkbox("dialog.imageeditor.saturation_brightness_lock");
	private final JCheckBox type3 = L10N.checkbox("dialog.imageeditor.saturation_brightness_lock");
	private final JLabel prev = new JLabel();
	private final JColor col1;
	private final JColor col2;
	private final JColor col4;
	private final JSpinner ang1 = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));
	private final JSpinner ang2 = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));
	private final JSpinner ang3 = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));

	private static final int zoom = 2000;

	private ResourcePointer noimage;
	private final List<ResourcePointer> templatesSorted;

	private boolean disableRefresh = false;

	public FromTemplateDialog(MCreator window, Canvas canvas, VersionManager versionManager) {
		super(window, L10N.t("dialog.imageeditor.template_from_title"), true);

		templatesSorted = new ArrayList<>(ImageMakerTexturesCache.CACHE.keySet());
		templatesSorted.sort(Comparator.comparing(resourcePointer -> resourcePointer.identifier.toString()));

		col1 = new JColor(window);
		col2 = new JColor(window);
		col4 = new JColor(window);

		JPanel settings = new JPanel(new BorderLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JPanel templates = new JPanel(new GridLayout(2, 2, 5, 5));
		templates.setBorder(new EmptyBorder(5, 2, 10, 2));

		JComboBox<String> templateSelector = new JComboBox<>(templateList);
		templateSelector.setSelectedIndex(0);
		templates.add(L10N.label("dialog.imageeditor.template_generator"));
		templates.add(templateSelector);

		for (ResourcePointer template : templatesSorted) {
			if (!template.toString().contains("(no image)"))
				cbs.addItem(template);
			else
				noimage = template;
			cbs2.addItem(template);
			cbs3.addItem(template);
			cbs4.addItem(template);
		}

		TextureSelectorDialog is = new TextureSelectorDialog(templatesSorted, window);
		TextureSelectorDialog is2 = new TextureSelectorDialog(templatesSorted, window);
		TextureSelectorDialog is3 = new TextureSelectorDialog(templatesSorted, window);
		TextureSelectorDialog is4 = new TextureSelectorDialog(templatesSorted, window);

		JPanel editp = new JPanel(new BorderLayout(10, 10));
		editp.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		editp.add("Center", PanelUtils.totalCenterInPanel(prev));

		JButton bt = new JButton(UIRES.get("18px.open"));
		JButton bt2 = new JButton(UIRES.get("18px.open"));
		JButton bt3 = new JButton(UIRES.get("18px.open"));
		JButton bt4 = new JButton(UIRES.get("18px.open"));
		bt.setText(L10N.t("dialog.imageeditor.template_select_texture"));
		bt2.setText(L10N.t("dialog.imageeditor.template_select_texture"));
		bt3.setText(L10N.t("dialog.imageeditor.template_select_texture"));
		bt4.setText(L10N.t("dialog.imageeditor.template_select_texture"));
		bt.setMargin(new Insets(2, 2, 2, 2));
		bt2.setMargin(new Insets(2, 2, 2, 2));
		bt3.setMargin(new Insets(2, 2, 2, 2));
		bt4.setMargin(new Insets(2, 2, 2, 2));

		bt.setOpaque(false);
		bt2.setOpaque(false);
		bt3.setOpaque(false);
		bt4.setOpaque(false);

		ang1.setOpaque(false);
		ang2.setOpaque(false);
		ang3.setOpaque(false);

		ang1.addChangeListener(event -> refreshIcon());
		ang2.addChangeListener(event -> refreshIcon());
		ang3.addChangeListener(event -> refreshIcon());

		bt.addActionListener(event -> is.setVisible(true));
		is.naprej.addActionListener(arg01 -> {
			is.setVisible(false);
			cbs.setSelectedItem(is.list.getSelectedValue());
		});

		bt2.addActionListener(event -> is2.setVisible(true));
		is2.naprej.addActionListener(arg01 -> {
			is2.setVisible(false);
			cbs2.setSelectedItem(is2.list.getSelectedValue());
		});

		bt3.addActionListener(event -> is3.setVisible(true));
		is3.naprej.addActionListener(arg01 -> {
			is3.setVisible(false);
			cbs3.setSelectedItem(is3.list.getSelectedValue());
		});

		bt4.addActionListener(event -> is4.setVisible(true));
		is4.naprej.addActionListener(arg01 -> {
			is4.setVisible(false);
			cbs4.setSelectedItem(is4.list.getSelectedValue());
		});

		type1.setOpaque(false);
		type2.setOpaque(false);
		type3.setOpaque(false);

		type1.addActionListener(event -> refreshIcon());

		type2.addActionListener(event -> refreshIcon());

		type3.addActionListener(event -> refreshIcon());

		JPanel as = new JPanel();
		as.setOpaque(false);

		JPanel pas = new JPanel();
		pas.setLayout(new BoxLayout(pas, BoxLayout.PAGE_AXIS));

		pas.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.imageeditor.template_layer_one"), cbs, bt, as));
		pas.add(PanelUtils
				.join(FlowLayout.LEFT, L10N.label("dialog.imageeditor.template_layer_two"), cbs2, bt2, col1, type1, L10N.label("dialog.imageeditor.template_rotation"),
						ang1));
		pas.add(PanelUtils
				.join(FlowLayout.LEFT, L10N.label("dialog.imageeditor.template_layer_three"), cbs3, bt3, col2, type2, L10N.label("dialog.imageeditor.template_rotation"),
						ang2));
		pas.add(PanelUtils
				.join(FlowLayout.LEFT, L10N.label("dialog.imageeditor.template_layer_four"), cbs4, bt4, col4, type3, L10N.label("dialog.imageeditor.template_rotation"),
						ang3));

		col1.setColorSelectedListener(event -> refreshIcon());
		col2.setColorSelectedListener(event -> refreshIcon());
		col4.setColorSelectedListener(event -> refreshIcon());
		cbs.addActionListener(event -> refreshIcon());
		cbs2.addActionListener(event -> refreshIcon());
		cbs3.addActionListener(event -> refreshIcon());
		cbs4.addActionListener(event -> refreshIcon());

		col1.setOpaque(false);
		col2.setOpaque(false);
		col4.setOpaque(false);

		pas.setOpaque(false);

		editp.add("South", PanelUtils.centerInPanel(pas));
		editp.setOpaque(false);

		JButton randomize = L10N.button("dialog.imageeditor.template_randomize");
		randomize.setMargin(new Insets(1, 40, 1, 40));
		randomize.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		randomize.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		randomize.setFocusPainted(false);

		templates.add(new JLabel(""));
		templates.add(randomize);

		settings.add(templates, BorderLayout.NORTH);

		settings.add(editp, BorderLayout.CENTER);

		Timer timer = new java.util.Timer();
		final TimerTask[] task = new TimerTask[1];

		randomize.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent mouseEvent) {
				super.mousePressed(mouseEvent);
				task[0] = new TimerTask() {
					@Override public void run() {
						if (templateSelector.getSelectedItem() != null)
							generateFromTemplate((String) templateSelector.getSelectedItem());
						else
							generateFromTemplate("Random");
					}
				};
				timer.scheduleAtFixedRate(task[0], 0, 250);
			}

			@Override public void mouseReleased(MouseEvent mouseEvent) {
				super.mouseReleased(mouseEvent);
				task[0].cancel();
			}
		});

		generateFromTemplate("Random");

		JButton cancel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));
		JButton merge = L10N.button("dialog.imageeditor.template_create_and_merge");
		JButton ok = L10N.button("action.common.create");

		ok.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		ok.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		getRootPane().setDefaultButton(ok);

		cancel.addActionListener(e -> setVisible(false));

		merge.addActionListener(e -> {
			Layer first = new Layer(16, 16, 0, 0, L10N.t("dialog.imageeditor.template_merged_layer"), refreshIcon().getImage());
			canvas.add(first);
			setVisible(false);
		});

		ok.addActionListener(e -> {
			try {
				UUID changeGroup = UUID.randomUUID();
				if (cbs.getSelectedItem() != null && !cbs.getSelectedItem().toString().contains("(no image)")) {
					ImageIcon layer1 = new ImageIcon(ImageIO.read(
							((ResourcePointer) Objects.requireNonNull(cbs.getSelectedItem())).getStream()));
					Layer first = new Layer(16, 16, 0, 0, cbs.getSelectedItem().toString(), layer1.getImage());
					canvas.add(first, changeGroup);
				}
				if (cbs2.getSelectedItem() != null && !cbs2.getSelectedItem().toString().contains("(no image)")) {
					ImageIcon layer2 = ImageUtils.rotate(new ImageIcon(ImageIO.read(
							((ResourcePointer) Objects.requireNonNull(cbs2.getSelectedItem())).getStream())),
							(Integer) ang1.getValue());
					Layer second = new Layer(16, 16, 0, 0, cbs2.getSelectedItem().toString(),
							ImageUtils.colorize(layer2, col1.getColor(), !type1.isSelected()).getImage());
					canvas.add(second, changeGroup);
				}
				if (cbs3.getSelectedItem() != null && !cbs3.getSelectedItem().toString().contains("(no image)")) {
					ImageIcon layer3 = ImageUtils.rotate(new ImageIcon(ImageIO.read(
							((ResourcePointer) Objects.requireNonNull(cbs3.getSelectedItem())).getStream())),
							(Integer) ang2.getValue());
					Layer third = new Layer(16, 16, 0, 0, cbs3.getSelectedItem().toString(),
							ImageUtils.colorize(layer3, col2.getColor(), !type2.isSelected()).getImage());
					canvas.add(third, changeGroup);
				}
				if (cbs4.getSelectedItem() != null && !cbs4.getSelectedItem().toString().contains("(no image)")) {
					ImageIcon layer4 = ImageUtils.rotate(new ImageIcon(ImageIO.read(
							((ResourcePointer) Objects.requireNonNull(cbs4.getSelectedItem())).getStream())),
							(Integer) ang3.getValue());
					Layer fourth = new Layer(16, 16, 0, 0, cbs4.getSelectedItem().toString(),
							ImageUtils.colorize(layer4, col4.getColor(), !type3.isSelected()).getImage());
					canvas.add(fourth, changeGroup);
				}
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), e);
			}
			setVisible(false);
		});

		controls.add(cancel, BorderLayout.WEST);
		controls.add(PanelUtils.join(FlowLayout.CENTER, 5, 0, merge, ok), BorderLayout.EAST);
		add(PanelUtils.maxMargin(settings, 5, true, true, true, true), BorderLayout.CENTER);
		add(PanelUtils.maxMargin(controls, 5, true, true, true, true), BorderLayout.SOUTH);
		setSize(900, 650);
		setResizable(false);
		setLocationRelativeTo(window);
	}

	private void generateFromTemplate(String template) {
		disableRefresh = true;

		switch (template) {
		case "Sword":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_base_stick"))
							.collect(Collectors.toList())));
			if (Math.random() > 0.33) {
				cbs2.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().equals("tool_sword"))
								.collect(Collectors.toList())));
				cbs3.setSelectedItem(noimage);
			} else {
				cbs2.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().equals("tool_sword_blade"))
								.collect(Collectors.toList())));
				cbs3.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().equals("tool_sword_handle"))
								.collect(Collectors.toList())));
				col2.setColor(ListUtils.getRandomItem(presetColors));
				type2.setSelected(Math.random() < 0.4);
				ang2.setValue(0);
			}
			cbs4.setSelectedItem(noimage);
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);
			break;
		case "Pickaxe":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_base_stick"))
							.collect(Collectors.toList())));
			cbs2.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_pickaxe"))
							.collect(Collectors.toList())));
			cbs3.setSelectedItem(noimage);
			cbs4.setSelectedItem(noimage);
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);
			break;
		case "Axe":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_base_stick"))
							.collect(Collectors.toList())));
			cbs2.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_axe"))
							.collect(Collectors.toList())));
			cbs3.setSelectedItem(noimage);
			cbs4.setSelectedItem(noimage);
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);
			break;
		case "Shovel":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_shovel_base"))
							.collect(Collectors.toList())));
			cbs2.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_shovel_top"))
							.collect(Collectors.toList())));
			cbs3.setSelectedItem(noimage);
			cbs4.setSelectedItem(noimage);
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);
			break;
		case "Hoe":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_base_stick"))
							.collect(Collectors.toList())));
			cbs2.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("tool_hoe"))
							.collect(Collectors.toList())));
			cbs3.setSelectedItem(noimage);
			cbs4.setSelectedItem(noimage);
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);
			break;
		case "Shears":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("shears_base"))
							.collect(Collectors.toList())));
			if (Math.random() > 0.9) {
				cbs2.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().equals("shears_base_mono"))
								.collect(Collectors.toList())));
				col1.setColor(ListUtils.getRandomItem(presetColors));
				type1.setSelected(Math.random() < 0.4);
				ang1.setValue(0);
			} else
				cbs2.setSelectedItem(noimage);
			cbs3.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("shears_blades"))
							.collect(Collectors.toList())));
			cbs4.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("shears_mechanism"))
							.collect(Collectors.toList())));
			if (Math.random() > 0.20) {
				Color c = ListUtils.getRandomItem(presetColors);
				boolean ctype = Math.random() < 0.4;

				col2.setColor(c);
				type2.setSelected(ctype);
				ang2.setValue(0);

				col4.setColor(c);
				type3.setSelected(ctype);
				ang3.setValue(0);
			} else {
				col2.setColor(ListUtils.getRandomItem(presetColors));
				type2.setSelected(Math.random() < 0.4);
				ang2.setValue(0);

				col4.setColor(ListUtils.getRandomItem(presetColors));
				type3.setSelected(Math.random() < 0.4);
				ang3.setValue(0);
			}
			break;
		case "Music disc":
			if (Math.random() < 0.75) {
				cbs.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().equals("record"))
								.collect(Collectors.toList())));
			} else {
				cbs.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().equals("record_broken"))
								.collect(Collectors.toList())));
			}

			cbs2.setSelectedItem(ListUtils.getRandomItem(templatesSorted.stream()
					.filter(e -> e.toString().equals("record_mid_0") || e.toString().equals("record_mid_1"))
					.collect(Collectors.toList())));
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);

			if (Math.random() < 0.9)
				cbs3.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().startsWith("record_mid_"))
								.collect(Collectors.toList())));
			else
				cbs3.setSelectedItem(noimage);
			col2.setColor(ListUtils.getRandomItem(presetColors));
			type2.setSelected(Math.random() < 0.4);
			ang2.setValue(0);

			if (Math.random() < 0.7)
				cbs4.setSelectedItem(ListUtils.getRandomItem(
						templatesSorted.stream().filter(e -> e.toString().startsWith("record_mid_"))
								.collect(Collectors.toList())));
			else
				cbs4.setSelectedItem(noimage);
			col4.setColor(ListUtils.getRandomItem(presetColors));
			type3.setSelected(Math.random() < 0.4);
			ang3.setValue(0);
			break;
		case "Drinkable potion":
			generatePotionFluid();
			cbs3.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("potion_bottle_overlay"))
							.collect(Collectors.toList())));
			col2.setColor(Color.white);
			type2.setSelected(false);
			ang2.setValue(0);
			cbs4.setSelectedItem(noimage);
			break;
		case "Splash potion":
			generatePotionFluid();
			cbs3.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("potion_bottle_overlay_splash"))
							.collect(Collectors.toList())));
			col2.setColor(Color.white);
			type2.setSelected(false);
			ang2.setValue(0);
			cbs4.setSelectedItem(noimage);
			break;
		case "Lingering potion":
			generatePotionFluid();
			cbs3.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().equals("potion_bottle_overlay_lingering"))
							.collect(Collectors.toList())));
			col2.setColor(Color.white);
			type2.setSelected(false);
			ang2.setValue(0);
			cbs4.setSelectedItem(noimage);
			break;
		case "Ore":
			cbs.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().contains("noise")).collect(Collectors.toList())));
			cbs2.setSelectedItem(ListUtils.getRandomItem(
					templatesSorted.stream().filter(e -> e.toString().matches("ore\\d+"))
							.collect(Collectors.toList())));
			cbs3.setSelectedItem(noimage);
			cbs4.setSelectedItem(noimage);
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(ListUtils.getRandomItem(new Integer[] { 0, 90, 180, 270, 0, 0 }));
			break;
		default:
			randomizeSetup();
			break;
		}

		cbs.revalidate();
		cbs.repaint();

		cbs2.revalidate();
		cbs2.repaint();

		cbs3.revalidate();
		cbs3.repaint();

		cbs4.revalidate();
		cbs4.repaint();

		disableRefresh = false;
		refreshIcon();
	}

	private void generatePotionFluid() {
		cbs.setSelectedItem(ListUtils.getRandomItem(
				templatesSorted.stream().filter(e -> e.toString().equals("potion_fluid_water"))
						.collect(Collectors.toList())));
		if (Math.random() > 0.1) {
			cbs2.setSelectedItem(ListUtils.getRandomItem(templatesSorted.stream()
					.filter(e -> e.toString().equals("potion_fluid_bright") || e.toString().equals("potion_fluid_dark"))
					.collect(Collectors.toList())));
			col1.setColor(ListUtils.getRandomItem(presetColors));
			type1.setSelected(Math.random() < 0.4);
			ang1.setValue(0);
		} else
			cbs2.setSelectedItem(noimage);
	}

	private void randomizeSetup() {
		cbs.setSelectedItem(ListUtils.getRandomItem(templatesSorted));

		if (Math.random() < 0.8)
			cbs2.setSelectedItem(ListUtils.getRandomItem(templatesSorted.stream().filter(e ->
					!(e.toString().contains("noise") || e.toString().contains("pattern") || e.toString()
							.contains("oreblock")) || Math.random() > 0.4).collect(Collectors.toList())));
		else
			cbs2.setSelectedItem(noimage);

		if (Math.random() < 0.25)
			cbs3.setSelectedItem(ListUtils.getRandomItem(templatesSorted.stream()
					.filter(e -> !(e.toString().contains("noise") || e.toString().contains("pattern") || e.toString()
							.contains("oreblock") || Math.random() > 0.2)).collect(Collectors.toList())));
		else
			cbs3.setSelectedItem(noimage);

		if (Math.random() < 0.1)
			cbs4.setSelectedItem(ListUtils.getRandomItem(templatesSorted.stream()
					.filter(e -> !(e.toString().contains("noise") || e.toString().contains("pattern") || e.toString()
							.contains("oreblock"))).collect(Collectors.toList())));
		else
			cbs4.setSelectedItem(noimage);

		if (Math.random() > 0.8) {
			cbs2.setSelectedItem(cbs.getSelectedItem());
			cbs4.setSelectedItem(noimage);
		}

		col1.setColor(ListUtils.getRandomItem(presetColors));
		col2.setColor(ListUtils.getRandomItem(presetColors));
		col4.setColor(ListUtils.getRandomItem(presetColors));

		type1.setSelected(Math.random() < 0.4);
		type2.setSelected(Math.random() < 0.5);
		type3.setSelected(Math.random() < 0.6);

		ang1.setValue(ListUtils.getRandomItem(new Integer[] { 0, 90, 180, 270, 0, 0 }));
		ang2.setValue(ListUtils.getRandomItem(new Integer[] { 0, 90, 180, 270, 0, 0 }));
		ang3.setValue(ListUtils.getRandomItem(new Integer[] { 0, 90, 180, 270, 0, 0 }));
	}

	private ImageIcon refreshIcon() {
		if (disableRefresh)
			return new EmptyIcon.ImageIcon(16, 16);

		try {
			ImageIcon joined = new ImageIcon(
					ImageIO.read(((ResourcePointer) Objects.requireNonNull(cbs.getSelectedItem())).getStream()));
			ImageIcon pl2 = ImageUtils.rotate(new ImageIcon(
							ImageIO.read(((ResourcePointer) Objects.requireNonNull(cbs2.getSelectedItem())).getStream())),
					(Integer) ang1.getValue());
			ImageIcon pl3 = ImageUtils.rotate(new ImageIcon(
							ImageIO.read(((ResourcePointer) Objects.requireNonNull(cbs3.getSelectedItem())).getStream())),
					(Integer) ang2.getValue());
			ImageIcon pl4 = ImageUtils.rotate(new ImageIcon(
							ImageIO.read(((ResourcePointer) Objects.requireNonNull(cbs4.getSelectedItem())).getStream())),
					(Integer) ang3.getValue());

			if (cbs2.getSelectedItem() != null && !cbs2.getSelectedItem().toString().contains("(no image)"))
				joined = ImageUtils.drawOver(joined, ImageUtils.colorize(pl2, col1.getColor(), !type1.isSelected()));
			if (cbs3.getSelectedItem() != null && !cbs3.getSelectedItem().toString().contains("(no image)"))
				joined = ImageUtils.drawOver(joined, ImageUtils.colorize(pl3, col2.getColor(), !type2.isSelected()));
			if (cbs4.getSelectedItem() != null && !cbs4.getSelectedItem().toString().contains("(no image)"))
				joined = ImageUtils.drawOver(joined, ImageUtils.colorize(pl4, col4.getColor(), !type3.isSelected()));

			prev.setIcon(new ImageIcon(ImageUtils
					.resize(joined.getImage(), (int) (joined.getIconWidth() * (zoom / 100.0f) + 1),
							(int) (joined.getIconHeight() * (zoom / 100.0f)) + 1)));

			return joined;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new EmptyIcon.ImageIcon(16, 16);
		}
	}
}
