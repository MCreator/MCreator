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

package net.mcreator.generator;

import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.EmptyIcon;

import javax.swing.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public enum GeneratorFlavor {

	//@formatter:off
	FORGE(GamePlatform.JAVAEDITION, BaseLanguage.JAVA),
	FABRIC(GamePlatform.JAVAEDITION, BaseLanguage.JAVA),
	SPIGOT(GamePlatform.JAVAEDITION, BaseLanguage.JAVA),
	QUILT(GamePlatform.JAVAEDITION, BaseLanguage.JAVA),
	NEOFORGE(GamePlatform.JAVAEDITION, BaseLanguage.JAVA),
	DATAPACK(GamePlatform.JAVAEDITION, BaseLanguage.JSON),
	ADDON(GamePlatform.BEDROCKEDITION, BaseLanguage.JSON),
	UNKNOWN(null, null);
	//@formatter:on

	/**
	 * Official flavors are flavors that MCreator supports in its core.
	 */
	public static final List<GeneratorFlavor> OFFICIAL_FLAVORS = List.of(FORGE, DATAPACK, ADDON, NEOFORGE);

	/**
	 * Special flavors are flavors that are not full modding APIs, and therefore we assume no covered METs by default.
	 */
	public static final List<GeneratorFlavor> SPECIAL_FLAVORS = List.of(QUILT);

	private final BaseLanguage baseLanguage;

	private final GamePlatform gamePlatform;

	GeneratorFlavor(GamePlatform gamePlatform, BaseLanguage baseLanguage) {
		this.gamePlatform = gamePlatform;
		this.baseLanguage = baseLanguage;
	}

	public BaseLanguage getBaseLanguage() {
		return baseLanguage;
	}

	public GamePlatform getGamePlatform() {
		return gamePlatform;
	}

	public ImageIcon getIcon() {
		if (this == ADDON) {
			return UIRES.get("16px.bedrock");
		} else if (this == UNKNOWN) {
			return new EmptyIcon.ImageIcon(16, 16);
		}

		return UIRES.get("16px." + name().toLowerCase(Locale.ENGLISH));
	}

	public static GeneratorFlavor[] gamePlatform(GamePlatform gamePlatform) {
		return Stream.of(values()).filter(flavor -> flavor.getGamePlatform() == gamePlatform)
				.toArray(GeneratorFlavor[]::new);
	}

	public static GeneratorFlavor[] baseLanguage(BaseLanguage baseLanguage) {
		return Stream.of(values()).filter(flavor -> flavor.getBaseLanguage() == baseLanguage)
				.toArray(GeneratorFlavor[]::new);
	}

	public static GeneratorFlavor[] allBut(GeneratorFlavor... flavors) {
		return Stream.of(values()).filter(flavor -> Stream.of(flavors).noneMatch(f -> f == flavor))
				.toArray(GeneratorFlavor[]::new);
	}

	public enum BaseLanguage {
		JAVA, JSON
	}

	public enum GamePlatform {
		JAVAEDITION, BEDROCKEDITION
	}

}
