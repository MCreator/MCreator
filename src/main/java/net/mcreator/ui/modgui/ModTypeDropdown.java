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

package net.mcreator.ui.modgui;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.NewModElementDialog;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ModTypeDropdown extends JPopupMenu {

	public ModTypeDropdown(MCreator mcreator) {
		setBorder(BorderFactory.createEmptyBorder());

		SortedMap<ModElementType, ModElementTypeRegistry.ModTypeRegistration<?>> map = new TreeMap<>(
				Comparator.comparing(ModElementType::getReadableName));
		map.putAll(ModElementTypeRegistry.REGISTRY);

		List<Map.Entry<ModElementType, ModElementTypeRegistry.ModTypeRegistration<?>>> types = map.entrySet().stream()
				.filter(entry ->
						mcreator.getGeneratorStats().getModElementTypeCoverageInfo()
								.get(entry.getKey()) != GeneratorStats.CoverageStatus.NONE)
				.collect(Collectors.toList());

		if (types.size() > 14) {
			List<Map.Entry<ModElementType, ModElementTypeRegistry.ModTypeRegistration<?>>> typestmp = new ArrayList<>(
					types);

			int i = 0;
			for (; i < Math.ceil(types.size() / 2d); i++)
				typestmp.set(i * 2, types.get(i));
			for (int j = 0; i < types.size(); i++, j++)
				typestmp.set(j * 2 + 1, types.get(i));

			types = typestmp;
		}

		types.forEach(entry -> {
			ModElementType type = entry.getKey();
			ModElementTypeRegistry.ModTypeRegistration<?> registration = entry.getValue();
			JMenuItem modTypeButton = new JMenuItem(" " + type.getReadableName() + " ");

			modTypeButton.setToolTipText(type.getDescription());
			modTypeButton.addActionListener(actionEvent -> NewModElementDialog.showNameDialog(mcreator, type));
			modTypeButton.setOpaque(false);

			modTypeButton.setBorder(BorderFactory.createEmptyBorder());

			ComponentUtils.deriveFont(modTypeButton, 12);

			if (registration.getShortcut() != null)
				modTypeButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(registration.getShortcut()));

			modTypeButton.setIcon(
					new ImageIcon(ImageUtils.resizeAA(TiledImageCache.getModTypeIcon(type).getImage(), 32, 32)));

			add(modTypeButton);
		});

		setLayout(new GridLayout(-1, types.size() > 14 ? 2 : 1));
	}

}
