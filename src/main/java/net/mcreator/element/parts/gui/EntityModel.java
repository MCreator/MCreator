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

	public int scale;
	public int rotationX;

	public boolean followMouseMovement;

	public EntityModel(int x, int y, Procedure entityModel, Procedure displayCondition, int scale, int rotationX,
			boolean followMouseMovement) {
		super(x, y);
		this.entityModel = entityModel;
		this.displayCondition = displayCondition;
		this.scale = scale;
		this.rotationX = rotationX;
		this.followMouseMovement = followMouseMovement;
	}

	public EntityModel(int x, int y, Procedure entityModel, Procedure displayCondition, int scale, int rotationX,
			boolean followMouseMovement, AnchorPoint anchorPoint) {
		this(x, y, entityModel, displayCondition, scale, rotationX, followMouseMovement);
		this.anchorPoint = anchorPoint;
	}

	@Override public String getName() {
		return "entity_model_" + RegistryNameFixer.fromCamelCase(Objects.requireNonNull(this.entityModel.getName()));
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.setColor(Color.darkGray);

		g.drawLine(cx, cy + 20, cx + 20, cy + 20);

		Stroke original = g.getStroke();
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1 }, 0));
		g.drawLine(cx + 10, cy, cx + 10, cy + 20);
		g.setStroke(original);

		g.setColor(VariableTypeLoader.BuiltInTypes.ENTITY.getBlocklyColor());

		g.fillRect(cx + 9, cy + 19, 2, 2);

		g.setFont(g.getFont().deriveFont(5f));
		int textwidth = (int) (g.getFont().getStringBounds(this.entityModel.getName(), WYSIWYG.frc).getWidth());
		g.drawString(this.entityModel.getName(), cx + 10 - textwidth / 2, cy + 17);
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
