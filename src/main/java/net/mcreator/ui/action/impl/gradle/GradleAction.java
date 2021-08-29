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

package net.mcreator.ui.action.impl.gradle;

import net.mcreator.gradle.GradleStateListener;
import net.mcreator.gradle.GradleTaskResult;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;

import java.awt.event.ActionListener;

public class GradleAction extends BasicAction {

	private String prevTooltip;

	public GradleAction(ActionRegistry actionRegistry, String name, ActionListener listener) {
		super(actionRegistry, name, listener);
		actionRegistry.getMCreator().getGradleConsole().addGradleStateListener(new GradleStateListener() {
			@Override public void taskStarted(String taskName) {
				setEnabled(false);
			}

			@Override public void taskFinished(GradleTaskResult result) {
				setEnabled(true);
			}
		});
	}

	@Override public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (b) {
			setTooltip(prevTooltip);
		} else {
			prevTooltip = (String) getValue(SHORT_DESCRIPTION);
			setTooltip(L10N.t("action.gradle.disabled"));
		}
	}

	@Override public boolean isEnabled() {
		return actionRegistry.getMCreator().getGradleConsole().getStatus() != GradleConsole.RUNNING;
	}

}
