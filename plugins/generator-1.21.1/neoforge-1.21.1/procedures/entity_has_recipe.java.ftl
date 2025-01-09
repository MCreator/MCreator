<#include "mcelements.ftl">
<@addTemplate file="utils/entity/entity_has_recipe.java.ftl"/>
(hasEntityRecipe(${input$entity}, ${toResourceLocation(input$recipe)}))