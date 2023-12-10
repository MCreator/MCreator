/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.jigsaw;

import net.mcreator.element.types.Structure;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.ComboBoxFullWidthPopup;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ResourceLocationValidator;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JJigsawPool extends JEntriesList {

	private final VTextField poolName = new VTextField();
	private final VComboBox<String> fallbackPool = new VComboBox<>();

	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final List<JJigsawPart> entryList = new ArrayList<>();
	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JJigsawPool(JJigsawPoolsList jigsawPools, IHelpContext gui, JPanel parent, List<JJigsawPool> poolList) {
		super(jigsawPools.getMCreator(), new BorderLayout(0, 5), gui);
		setOpaque(false);
		setBackground(Theme.current().getBackgroundColor().brighter());

		entries.setOpaque(false);

		poolName.setPreferredSize(new Dimension(300, 30));
		poolName.setValidator(jigsawPools.newPoolNameValidator(poolName));
		poolName.enableRealtimeValidation();

		fallbackPool.setPreferredSize(new Dimension(300, 30));
		fallbackPool.setEditable(true);
		fallbackPool.setValidator(
				new ResourceLocationValidator<>(L10N.t("elementgui.structuregen.jigsaw_fallback"), fallbackPool,
						true).setAllowEmpty(true));
		fallbackPool.addPopupMenuListener(new ComboBoxFullWidthPopup() {
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				ComboBoxUtil.updateComboBoxContents(fallbackPool,
						poolList.stream().filter(p -> !p.poolName.getText().isBlank())
								.filter(p -> p.poolName.getValidationStatus().getValidationResultType()
										== Validator.ValidationResultType.PASSED)
								.map(p -> jigsawPools.getMCreator().getWorkspace().getWorkspaceSettings().getModID()
										+ ":" + jigsawPools.getModElement().getRegistryName() + "_"
										+ p.poolName.getText()).toList(),
						fallbackPool.getEditor().getItem().toString());
				super.popupMenuWillBecomeVisible(e);
			}
		});

		ComponentUtils.deriveFont(poolName, 16);
		ComponentUtils.deriveFont(fallbackPool, 16);

		JComponent poolParams = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		poolParams.setOpaque(false);

		poolParams.add(HelpUtils.wrapWithHelpButton(gui.withEntry("structure/jigsaw_pool_name"),
				L10N.label("elementgui.structuregen.jigsaw_pool_name")));
		poolParams.add(poolName);

		poolParams.add(new JEmptyBox(15, 5));

		poolParams.add(HelpUtils.wrapWithHelpButton(gui.withEntry("structure/jigsaw_fallback_pool"),
				L10N.label("elementgui.structuregen.jigsaw_fallback")));
		poolParams.add(fallbackPool);

		final JComponent container = PanelUtils.expandHorizontally(this);

		poolList.add(this);
		parent.add(container);

		add.setText(L10N.t("elementgui.structuregen.jigsaw_add_pool_entry"));
		add.addActionListener(e -> {
			JJigsawPart part = new JJigsawPart(mcreator, entries, entryList);
			registerEntryUI(part);
		});

		remove.setText(L10N.t("elementgui.structuregen.jigsaw_remove_pool"));
		remove.addActionListener(e -> {
			poolList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});

		add("North", PanelUtils.westAndEastElement(poolParams, PanelUtils.join(add, remove)));
		add("Center", entries);

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.structuregen.jigsaw_pool"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		parent.revalidate();
		parent.repaint();
	}

	String getPoolName() {
		return poolName.getText();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		remove.setEnabled(enabled);

		poolName.setEnabled(enabled);
		fallbackPool.setEnabled(enabled);
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JJigsawPart::reloadDataLists);
	}

	public void addInitialEntry() {
		JJigsawPart part = new JJigsawPart(mcreator, entries, entryList); // initial add
		registerEntryUI(part);
	}

	public Structure.JigsawPool getPool() {
		Structure.JigsawPool pool = new Structure.JigsawPool();
		pool.poolName = poolName.getText();
		pool.fallbackPool = fallbackPool.getEditor().getItem().toString();
		pool.poolParts = entryList.stream().map(JJigsawPart::getEntry).toList();
		return pool.poolParts.isEmpty() ? null : pool;
	}

	public void setPool(Structure.JigsawPool pool) {
		poolName.setText(pool.poolName);
		fallbackPool.getEditor().setItem(pool.fallbackPool);
		if (pool.poolParts != null) {
			pool.poolParts.forEach(e -> {
				JJigsawPart entry = new JJigsawPart(mcreator, entries, entryList);
				registerEntryUI(entry);
				entry.setEntry(e);
			});
		}
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult(poolName);
		entryList.forEach(validationResult::addValidationElement);
		return validationResult;
	}

}
