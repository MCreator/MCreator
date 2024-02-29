/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.component.entries;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class JEntriesList extends JPanel {

	protected final MCreator mcreator;
	protected final IHelpContext gui;

	private final List<Consumer<JComponent>> entryListeners = new ArrayList<>();

	protected final TechnicalButton add = new TechnicalButton(UIRES.get("16px.add"));

	public JEntriesList(MCreator mcreator, LayoutManager layout, IHelpContext gui) {
		super(layout);
		this.mcreator = mcreator;
		this.gui = gui;
	}

	public final void addEntryRegisterListener(Consumer<JComponent> entryListener) {
		entryListeners.add(entryListener);
	}

	protected void registerEntryUI(JComponent entry) {
		entryListeners.forEach(l -> l.accept(entry));
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		add.setEnabled(enabled);
	}

	public void reloadDataLists() {

	}

}
