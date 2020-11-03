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

package net.mcreator.ui.dialogs;

import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TextureSelectorDialog extends MCreatorDialog {

	public JButton naprej = new JButton(UIManager.getString("OptionPane.okButtonText"));

	private final DefaultListModel<ResourcePointer> model = new DefaultListModel<>();
	public JList<ResourcePointer> list = new JList<>(model);

	public TextureSelectorDialog(Iterable<ResourcePointer> block, JFrame f) {
		super(f, "Texture selector", true);
		setIconImage(UIRES.get("icon").getImage());
		list.setCellRenderer(new Render());

		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		block.forEach(model::addElement);

		JPanel buttons = new JPanel();
		JButton naprej2 = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		buttons.add(naprej2);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.addListSelectionListener(event -> naprej.doClick());
		naprej2.addActionListener(event -> setVisible(false));

		JLabel jtf = new JLabel("Select the texture:");
		ComponentUtils.deriveFont(jtf, 17);

		add("North", PanelUtils.join(FlowLayout.LEFT, jtf));
		add("South", buttons);
		add("Center", new JScrollPane(list));

		setSize(740, 370);
		setLocationRelativeTo(f);
	}

	class Render extends JLabel implements ListCellRenderer<ResourcePointer> {
		@Override
		public Component getListCellRendererComponent(JList<? extends ResourcePointer> list, ResourcePointer ma,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(Color.blue);
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			ImageIcon icon = ImageMakerTexturesCache.CACHE.get(ma);
			if (icon != null)
				setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
			else if (!ma.isInClasspath())
				setIcon(new ImageIcon(
						ImageUtils.resize(new ImageIcon(((File) ma.identifier).getAbsolutePath()).getImage(), 32)));

			setToolTipText(ma.toString());

			setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

			return this;
		}
	}

}
