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

package net.mcreator.element.parts.gui;

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.awt.*;
import java.util.Objects;

public class EntityModel extends GUIComponent {

	public Procedure entityModel;
	public Procedure displayCondition;

	public double scale;

	public boolean followMouseMovement;

	public EntityModel(int x, int y, Procedure entityModel, Procedure displayCondition, double scale, boolean followMouseMovement) {
		super(x, y);
		this.entityModel = entityModel;
		this.displayCondition = displayCondition;
		this.scale = scale;
		this.followMouseMovement = followMouseMovement;
	}

	@Override public String getName() {
		return "entity_model_" + RegistryNameFixer.fromCamelCase(Objects.requireNonNull(this.entityModel.getName()));
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.setColor(Color.darkGray);
		g.drawLine(cx, cy, cx + 20, cy + 20);
		g.drawLine(cx + 20, cy, cx, cy + 20);

		g.setFont(g.getFont().deriveFont(5f));
		g.setColor(VariableTypeLoader.BuiltInTypes.ENTITY.getBlocklyColor());
		int textwidth = (int) (g.getFont().getStringBounds(this.entityModel.getName(), WYSIWYG.frc).getWidth());
		g.drawString(this.entityModel.getName(), cx + 10 - textwidth / 2, cy + 12);
	}

	@Override public int getWidth(Workspace workspace) {
		return 20;
	}

	@Override public int getHeight(Workspace workspace) {
		return 20;
	}

	@Override public int getWeight() {
		return -10;
	}

}
