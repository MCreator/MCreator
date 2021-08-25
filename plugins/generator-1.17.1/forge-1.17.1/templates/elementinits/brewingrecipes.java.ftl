<#-- @formatter:off -->

<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign recipes = []>
<#list w.getRecipesOfType("Brewing") as recipe>
    <#assign recipes += [recipe.getGeneratableElement()]>
</#list>

public class ${JavaModName}BrewingRecipes {

	public static void load() {
        <#list recipes as recipe>
		BrewingRecipeRegistry.addRecipe(new ${recipe.getModElement().getName()}BrewingRecipe());
        </#list>
	}

}

<#-- @formatter:on -->