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

package net.mcreator.ui.views;

import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.ArmorMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

public class ArmorImageMakerView extends ViewBase {

	private final JColor col;

	private final JLabel ar1 = new JLabel();
	private final JLabel ar2 = new JLabel();
	private final JLabel arI = new JLabel();

	private final JComboBox<String> str = new JComboBox<>(ArmorMakerTexturesCache.NAMES.toArray(new String[0]));
	private final JCheckBox type1 = new JCheckBox();

	public ArmorImageMakerView(final MCreator fra) {
		super(fra);

		col = new JColor(fra);

		str.setSelectedItem("Standard");
		col.setOpaque(false);

		JPanel controls = new JPanel(new GridLayout(3, 2, 5, 10));
		controls.setOpaque(false);

		type1.setOpaque(false);

		controls.add(L10N.label("dialog.armor_image_maker.type"));
		controls.add(str);
		controls.add(L10N.label("dialog.armor_image_maker.color"));
		controls.add(PanelUtils.join(col));
		controls.add(L10N.label("dialog.armor_image_maker.saturation_lightness_lock"));
		controls.add(type1);

		col.setColorSelectedListener(event -> updateARM());
		str.addActionListener(e -> updateARM());
		type1.addActionListener(e -> updateARM());

		JPanel wrap = PanelUtils.centerInPanelPadding(controls, 10, 10);
		wrap.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("dialog.armor_image_maker.properties"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		add("Center", wrap);

		JPanel top = new JPanel();
		top.setOpaque(false);
		top.add(PanelUtils.join(ar1));
		top.add(PanelUtils.join(ar2));

		JPanel spo = new JPanel(new BorderLayout());
		spo.setOpaque(false);

		spo.add("North", PanelUtils.join(top));
		spo.add("South", PanelUtils.join(arI));

		JPanel spom = PanelUtils.totalCenterInPanel(spo);
		spom.setOpaque(true);
		spom.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		spom.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("dialog.armor_image_maker.preview"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		add("South", spom);

		JButton save = L10N.button("dialog.armor_image_maker.save");
		save.setMargin(new Insets(1, 40, 1, 40));
		save.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		save.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		save.setFocusPainted(false);
		add("North", PanelUtils.maxMargin(
				PanelUtils.westAndEastElement(new JEmptyBox(0, 0), PanelUtils.centerInPanelPadding(save, 0, 0)), 5,
				true, true, false, true));
		save.addActionListener(event -> {
			String namec = JOptionPane.showInputDialog(L10N.t("dialog.armor_image_maker.name"));
			if (namec != null && !namec.trim().equals("")) {
				namec = RegistryNameFixer.fix(namec);
				File[] armorPars = mcreator.getFolderManager().getArmorTextureFilesForName(namec);
				if (armorPars[0].isFile() || armorPars[1].isFile()) {
					JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.armor_image_maker.name_already_exists"),
							L10N.t("dialog.armor_image_maker.resource_error"), JOptionPane.ERROR_MESSAGE);
				} else {
					generateArmorImages(mcreator.getWorkspace(), namec, (String) str.getSelectedItem(), col.getColor(),
							!type1.isSelected());
				}
			}
		});

		type1.setSelected(true);
		col.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));

		updateARM();
	}

	public static void generateArmorImages(Workspace workspace, String namec, String type, Color color,
			boolean colorizeType) {
		Image[] images = getImages(type, color, colorizeType);

		Image image = images[0];
		Image image2 = images[1];

		File[] armorPars = workspace.getFolderManager().getArmorTextureFilesForName(namec);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image), armorPars[0]);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image2), armorPars[1]);
		use(workspace, images[2], namec + "_head");
		use(workspace, images[3], namec + "_body");
		use(workspace, images[4], namec + "_leggings");
		use(workspace, images[5], namec + "_boots");
	}

	private static Image[] getImages(String type, Color color, boolean colorizeType) {
		Image[] images = new Image[6];
		try {
			Image ari1 = ImageUtils.colorize(getTextureTemplate(type, "1"), color, colorizeType)
					.getImage();
			Image ari2 = ImageUtils.colorize(getTextureTemplate(type, "2"), color, colorizeType)
					.getImage();
			Image helmet = ImageUtils.colorize(getTextureTemplate(type, "H"), color, colorizeType)
					.getImage();
			Image body = ImageUtils.colorize(getTextureTemplate(type, "By"), color, colorizeType)
					.getImage();
			Image leggings = ImageUtils.colorize(getTextureTemplate(type, "L"), color, colorizeType)
					.getImage();
			Image boots = ImageUtils.colorize(getTextureTemplate(type, "Bs"), color, colorizeType)
					.getImage();
			images[0] = ari1;
			images[1] = ari2;
			images[2] = helmet;
			images[3] = body;
			images[4] = leggings;
			images[5] = boots;
		} catch (Exception ignored) {
			images[0] = new EmptyIcon.ImageIcon(380, 190).getImage();
			images[1] = new EmptyIcon.ImageIcon(380, 190).getImage();
			images[2] = new EmptyIcon.ImageIcon(16, 16).getImage();
			images[3] = new EmptyIcon.ImageIcon(16, 16).getImage();
			images[4] = new EmptyIcon.ImageIcon(16, 16).getImage();
			images[5] = new EmptyIcon.ImageIcon(16, 16).getImage();
		}
		return images;
	}

	private static ImageIcon getTextureTemplate(String type, String itemId){
		ImageIcon icon = ArmorMakerTexturesCache.getIcon(type + itemId);
		if(icon != null)
			return icon;
		else
			return ArmorMakerTexturesCache.getIcon("Standard" + itemId);
	}

	private void updateARM() {
		Image[] images = getImages((String) str.getSelectedItem(), col.getColor(), !type1.isSelected());

		Image ari1 = ImageUtils.resize(images[0], 380, 190);
		Image ari2 = ImageUtils.resize(images[1], 380, 190);

		BufferedImage resizedImage = new BufferedImage(400, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();

		g.drawImage(images[2], 0, 0, 100, 100, null);
		g.drawImage(images[3], 100, 0, 100, 100, null);
		g.drawImage(images[4], 200, 0, 100, 100, null);
		g.drawImage(images[5], 300, 0, 100, 100, null);

		g.dispose();

		ar1.setIcon(new ImageIcon(ari1));
		ar2.setIcon(new ImageIcon(ari2));
		arI.setIcon(new ImageIcon(resizedImage));
	}

	public static void use(Workspace workspace, Image image, String nam) {
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image),
				workspace.getFolderManager().getItemTextureFile(nam.toLowerCase(Locale.ENGLISH)));
	}

	@Override public String getViewName() {
		return "Armor texture maker";
	}

}
