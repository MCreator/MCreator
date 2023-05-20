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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TemplateExpressionParser {

	private static final Logger LOG = LogManager.getLogger("Template expression parser");

	public static boolean shouldSkipTemplateBasedOnCondition(@Nonnull Generator generator, @Nonnull Map<?, ?> template, @Nullable Object conditionDataProvider) {
		Operator operator = Operator.AND;
		Object conditionRaw = template.get("condition");
		if (conditionRaw == null) {
			conditionRaw = template.get("condition_any");
			operator = Operator.OR;
		}

		if (conditionRaw == null) // we check for condition value if present
			return false;

		if (conditionDataProvider == null) // if no conditionDataProvider element is found, there is nothing to do
			return false;

		// get list of all conditions that need to be met for template to be included
		List<String> conditions = new ArrayList<>();
		if (conditionRaw instanceof List<?> conditionRawList) {
			for (var conditionRawObject : conditionRawList)
				conditions.add(conditionRawObject.toString());
		} else {
			conditions.add(conditionRaw.toString());
		}

		if (operator == Operator.AND) {
			for (var condition : conditions) {
				if (!parseCondition(generator, condition, conditionDataProvider))
					return true; // at least one condition was false (AND logic), skip the template
			}

			return false; // all conditions were true, include the template (false = don't skip it)
		} else {
			for (var condition : conditions) {
				if (parseCondition(generator, condition, conditionDataProvider))
					return false; // at least one condition was true (OR logic), include the template
			}

			return true; // no conditions were true, skip the template (true = skip it)
		}
	}

	private static boolean parseCondition(@Nonnull Generator generator, @Nonnull String condition,
			@Nonnull Object conditionDataProvider) {
		try {
			if (condition.startsWith("${")) {
				Object processed = processFTLExpression(generator, condition.substring(2, condition.length() - 1),
						conditionDataProvider);
				return processed instanceof Boolean check && check;
			} else if (condition.contains("#?=")) { // check if value == one of the other values in list
				String[] condData = condition.split("#\\?=");
				int field = (int) getValueFrom(condData[0], conditionDataProvider);
				return Arrays.stream(condData[1].trim().split(",")).mapToInt(Integer::parseInt)
						.anyMatch(e -> e == field);
			} else if (condition.contains("#=")) { // check if value == other value
				String[] condData = condition.split("#=");
				int field = (int) getValueFrom(condData[0], conditionDataProvider);
				int value = Integer.parseInt(condData[1].trim());
				return value == field;
			} else if (condition.contains("%=")) { // compare strings
				String[] condData = condition.split("%=");
				String field = (String) getValueFrom(condData[0], conditionDataProvider);
				String value = condData[1].trim();
				return value.equals(field);
			} else {
				return (boolean) getValueFrom(condition, conditionDataProvider);
			}
		} catch (Exception e) {
			LOG.error("Failed to parse condition: " + condition, e);
		}

		return false;
	}

	private static Object getValueFrom(String field, Object conditionDataProvider) throws ReflectiveOperationException {
		if (!field.contains("()")) { // field
			return conditionDataProvider.getClass().getField(field.trim()).get(conditionDataProvider);
		} else { // method
			return conditionDataProvider.getClass().getMethod(field.replace("()", "").trim())
					.invoke(conditionDataProvider);
		}
	}

	public static Object processFTLExpression(Generator generator, String expression, Object dataHolder) {
		try {
			Map<String, Object> dataModel = new HashMap<>(generator.getBaseDataModelProvider().provide());
			AtomicReference<?> retVal = new AtomicReference<>(null);
			dataModel.put("data", dataHolder);
			dataModel.put("_retVal", retVal);

			Template t = new Template("INLINE EXPRESSION", new StringReader("${_retVal.set(" + expression + ")}"),
					generator.getGeneratorConfiguration().getTemplateGenConfigFromName("templates").getConfiguration());
			t.process(dataModel, new StringWriter(),
					generator.getGeneratorConfiguration().getTemplateGenConfigFromName("templates").getBeansWrapper());

			return retVal.get();
		} catch (Exception e) {
			LOG.error("Failed to parse FTL expression: " + expression, e);
			return null;
		}
	}

	private enum Operator {
		AND, OR
	}

}
