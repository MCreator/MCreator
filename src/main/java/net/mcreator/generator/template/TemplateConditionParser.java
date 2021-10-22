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

package net.mcreator.generator.template;

import freemarker.template.Template;
import net.mcreator.generator.Generator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateConditionParser {

	private static final Logger LOG = LogManager.getLogger("Template condition");

	public static boolean shouldSkipTemplateBasedOnCondition(@Nonnull Generator generator,
			@Nullable Object conditionRaw, @Nullable Object conditionDataProvider, Operator operator) {
		if (conditionRaw == null) // we check for condition value if present
			return false;

		if (conditionDataProvider == null) // if no conditionDataProvider element is found, there is nothing to do
			return false;

		boolean anyConditionFailed = false;
		boolean anyConditionPassed = false;

		// get list of all conditions that need to be met for template to be included
		List<String> conditions = new ArrayList<>();
		if (conditionRaw instanceof List<?> conditionRawList) {
			for (var conditionRawObject : conditionRawList)
				conditions.add(conditionRawObject.toString());
		} else {
			conditions.add(conditionRaw.toString());
		}

		for (var condition : conditions) {
			boolean result = parseCondition(generator, condition, conditionDataProvider);
			if (!result)
				anyConditionFailed = true;
			else
				anyConditionPassed = true;
		}

		return operator == Operator.AND ? anyConditionFailed : !anyConditionPassed;
	}

	private static boolean parseCondition(@Nonnull Generator generator, @Nonnull String condition,
			@Nonnull Object conditionDataProvider) {
		try {
			if (condition.startsWith("${")) {
				return processFTLExpression(generator, condition);
			} else if (condition.contains("#?=")) { // check if value == one of the other values in list
				String[] condData = condition.split("#\\?=");
				int field;
				if (!condData[0].contains("()")) { // field
					field = (int) conditionDataProvider.getClass().getField(condData[0].trim())
							.get(conditionDataProvider);
				} else { // method
					field = (int) conditionDataProvider.getClass().getMethod(condData[0].replace("()", "").trim())
							.invoke(conditionDataProvider);
				}
				int[] values = Arrays.stream(condData[1].trim().split(",")).mapToInt(Integer::parseInt).toArray();
				for (int value : values)
					if (value == field) {
						return true;
					}
			} else if (condition.contains("#=")) { // check if value == other value
				var condData = condition.split("#=");
				int field;
				if (!condData[0].contains("()")) { // field
					field = (int) conditionDataProvider.getClass().getField(condData[0].trim())
							.get(conditionDataProvider);
				} else { // method
					field = (int) conditionDataProvider.getClass().getMethod(condData[0].replace("()", "").trim())
							.invoke(conditionDataProvider);
				}
				int value = Integer.parseInt(condData[1].trim());
				return value == field;
			} else if (condition.contains("%=")) { // compare strings
				var condData = condition.split("%=");
				String field;
				if (!condData[0].contains("()")) { // field
					field = (String) conditionDataProvider.getClass().getField(condData[0].trim())
							.get(conditionDataProvider);
				} else { // method
					field = (String) conditionDataProvider.getClass().getMethod(condData[0].replace("()", "").trim())
							.invoke(conditionDataProvider);
				}
				String value = condData[1].trim();
				return value.equals(field);
			} else if (condition.contains("()")) { // check if method return value is true
				return (boolean) conditionDataProvider.getClass().getMethod(condition.replace("()", "").trim())
						.invoke(conditionDataProvider);
			} else {
				return (boolean) conditionDataProvider.getClass().getField(condition.trim()).get(conditionDataProvider);
			}
		} catch (Exception e) {
			LOG.error("Failed to parse condition: " + condition, e);
		}

		return false;
	}

	private static boolean processFTLExpression(Generator generator, String expression) {
		try {
			Template t = new Template("INLINE EXPRESSION", new StringReader(expression),
					generator.getGeneratorConfiguration().getTemplateGeneratorConfiguration().getConfiguration());

			StringWriter stringWriter = new StringWriter();
			t.process(generator.getBaseDataModelProvider().provide(), stringWriter);

			return Boolean.parseBoolean(stringWriter.getBuffer().toString());
		} catch (Exception e) {
			LOG.error("Failed to parse FTL expression: " + expression, e);
		}
		return false;
	}

	public enum Operator {
		AND, OR
	}

}
