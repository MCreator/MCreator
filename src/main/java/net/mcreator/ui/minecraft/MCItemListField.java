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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.AddTagDialog;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.IconUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MCItemListField extends JItemListField<MItemBlock> {

	private final MCItem.ListProvider supplier;

	private final List<String> additionalTagSuggestions = new ArrayList<>() {{
		add("tag");
		add("category/tag");
	}};

	public MCItemListField(MCreator mcreator, MCItem.ListProvider supplier) {
		this(mcreator, supplier, false, false);
	}

	public MCItemListField(MCreator mcreator, MCItem.ListProvider supplier, boolean excludeButton,
			boolean supportTags) {
		super(mcreator, excludeButton);
		this.supplier = supplier;

		if (supportTags)
			allowTags();

		elementsList.setCellRenderer(new CustomListCellRenderer());
	}

	public void addAdditionalTagSuggestions(String... suggestions) {
		additionalTagSuggestions.addAll(Arrays.asList(suggestions));
	}

	@Override public List<MItemBlock> getElementsToAdd() {
		return MCItemSelectorDialog.openMultiSelectorDialog(mcreator, supplier).stream()
				.map(e -> new MItemBlock(mcreator.getWorkspace(), e.getName())).collect(Collectors.toList());
	}

	@Override protected List<MItemBlock> getTagsToAdd() {
		TagType tagType = TagType.BLOCKS;
		List<MCItem> items = supplier.provide(mcreator.getWorkspace());
		for (MCItem item : items) {
			if (item.getType().equals("item")) {
				tagType = TagType.ITEMS;
				break;
			}
		}

		List<MItemBlock> tags = new ArrayList<>();
		String tag = AddTagDialog.openAddTagDialog(mcreator, mcreator, tagType,
				additionalTagSuggestions.toArray(new String[0]));
		if (tag != null)
			tags.add(new MItemBlock(mcreator.getWorkspace(), "TAG:" + tag));
		return tags;
	}

	@Nullable @Override protected MItemBlock fromExternalToElement(String external) {
		return new MItemBlock(mcreator.getWorkspace(), NameMapper.EXTERNAL_PREFIX + external);
	}

	class CustomListCellRenderer extends JLabel implements ListCellRenderer<MItemBlock> {

		@Override
		public Component getListCellRendererComponent(JList<? extends MItemBlock> list, MItemBlock value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(true);

			setBackground(isSelected ? Theme.current().getForegroundColor() : Theme.current().getBackgroundColor());

			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 2, 0, 2, Theme.current().getBackgroundColor()),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);

			setToolTipText(value.getUnmappedValue().replace(NameMapper.MCREATOR_PREFIX, "").replace("Blocks.", "")
					.replace("Items.", ""));

			setIcon(IconUtils.resize(MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), value.getUnmappedValue()),
					25));

			if (!isSelected && value.isManaged()) {
				setBackground(Theme.current().getAltBackgroundColor());
			}

			return this;
		}
	}

}
