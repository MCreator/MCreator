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

package net.mcreator.minecraft;

import net.mcreator.ui.init.EntityAnimationsLoader;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaModels {

	public static int getModelVersionAndPrepareCodeForAnimations(JavaClassSource classJavaSource) {
		int model_version = -1;
		List<MethodSource<JavaClassSource>> methods = classJavaSource.getMethods();
		for (MethodSource<JavaClassSource> method : methods) {
			if (method.getName().equals("setRotationAngles")) {
				classJavaSource.removeMethod(method);
				if (model_version == -1)
					model_version = 0;
			} else if (method.getName().equals("setupAnim")) {
				classJavaSource.removeMethod(method);
				if (model_version == -1)
					model_version = 1;
			}
		}
		return model_version;
	}

	public static List<String> getModelParts(JavaClassSource classJavaSource) {
		List<String> parts = new ArrayList<>();
		List<FieldSource<JavaClassSource>> fields = classJavaSource.getFields();
		for (FieldSource<JavaClassSource> field : fields)
			if (field.getType().getName().contains("ModelRenderer") || field.getType().getName().contains("ModelPart"))
				parts.add(field.getName());
		return parts;
	}

	public static String getAnimationsModelType0(Map<String, JComboBox<String>> animations) {
		StringBuilder anim = new StringBuilder();

		for (Map.Entry<String, JComboBox<String>> animation : animations.entrySet()) {
			String selected = (String) animation.getValue().getSelectedItem();
			if (selected != null) {
				String[] animationCodes = EntityAnimationsLoader.getAnimationCodesFromID(selected);
				for (String animationCode : animationCodes) {
					anim.append("this.").append(animation.getKey()).append(animationCode
									// @formatter:off
									.replace("xRot", "rotateAngleX")
									.replace("yRot", "rotateAngleY")
									.replace("zRot", "rotateAngleZ")
									.replace("limbSwingAmount", "f1")
									.replace("limbSwing", "f")
									.replace("ageInTicks", "f2")
									.replace("netHeadYaw", "f3")
									.replace("headPitch", "f4")
									.replace("Mth", "MathHelper")
									// @formatter:on
					).append("\n");
				}
			}
		}

		return "public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {"
				+ anim + "}";
	}

	public static String getAnimationsModelType1(Map<String, JComboBox<String>> animations) {
		StringBuilder anim = new StringBuilder();

		for (Map.Entry<String, JComboBox<String>> animation : animations.entrySet()) {
			String selected = (String) animation.getValue().getSelectedItem();
			if (selected != null) {
				String[] animationCodes = EntityAnimationsLoader.getAnimationCodesFromID(selected);
				for (String animationCode : animationCodes) {
					anim.append("this.").append(animation.getKey()).append(animationCode).append("\n");
				}
			}
		}

		return "public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {"
				+ anim + "}";
	}

}
